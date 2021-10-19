# netty实现RPC框架

## 一、RPC
### 1.介绍
RPC（Remote Procedure Call）：远程过程调用，是一种通过网络从远程计算机上请求服务，而不需要了解底层网络技术的协议。在 OSI 网络通信模型中，RPC 跨越了传输层（第四层，传输协议 TCP/UDP，即通过 ip+port 进行通信）和应用层（第七层，传输协议有 HTTP、HTTPS、FTP 等）。RPC 使得开发分布式系统应用变得更加容易。

RPC 采用 C/S 模式。请求程序就是 Client，而服务提供程序就是 Server。首先，Client 发送一个带有请求参数的调用请求到 Server，然后等待响应。在 Server 端，进程一直处于睡眠状态直到接收到 Client 的调用请求。当一个调用请求到达，Server 会根据请求参数进行计算，并将计算结果发送给 Client，然后等待下一个调用请求。Client 接收到响应信息，即获取到调用结果，然后根据情况继续发出下一次调用。（摘自网上、侵删）
### 2.执行流程
![执行流程](https://img-blog.csdnimg.cn/2d63601929dc49a98883f2ea47a672d6.png)
## 二、功能简介
### 1.实现
- 支持多种序列化协议，配置即用。
- 增加多种负载均衡算法（随机、轮询、一致性HASH等）。
- 客户端增加本地服务列表缓存，提高性能。
- 通过netty建立长链接进行通讯。
- 服务提供者使用线程池进行异步处理。
### 2.技术栈
- springBoot starter进行依赖包开发
- 使用zookeeper作为注册中心
- 使用netty作为通信框架
- 消息编解码使用protostuff、java
- 使用SPI来根据配置动态选择负载均衡算法等
## 三、框架使用
### 1.引入rpc依赖包starter
```
dependencies {
    implementation 'com.bigyj:rpc-spring-boot-starter:1.0.0-SNAPSHOT'
}
```
### 2.启动rpc功能
```java
@SpringBootApplication
@EnableRpc(basePackages = "com.api")
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
```
### 3.编写接口
```java
public interface UserService {
    UserDto getUser(String id);
}
```
### 4.编写provider

实现UserService接口，并在该类中使用@RpcService注解
```java
@Component
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public UserDto getUser(String id) {
        UserDto userDto = new UserDto("小明",10,"男","山东济南！");
        return userDto;
    }
}
```
### 5.编写调用接口
接口变量UserService通过@RpcReference注解（bean注入、依赖注入及代理对象生成），直接调用接口中的getUser方法。
```java
@RestController
@RequestMapping("/user")
public class TestControler {
    @RpcReference
    private UserService userService ;
    @RequestMapping("/getUser")
    private UserDto getUser(){
        UserDto user = userService.getUser("1");
        return user;
    }
}
```
### 6.功能测试
调用接口http://127.0.0.1:8090/user/getUser后，返回结果
```json
{"name":"小明","age":10,"sex":"男","address":"山东济南！"}
```
## 四、实现原理
### 1.@EnableRpc注解开启rpc功能
通过EnableRpc，引入RpcReferenceAnnotationBeanPostProcessor配置及ClientBeanDefinitionRegistrar配置
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RpcComponentScan
@EnableRpcConfig
public @interface EnableRpc {
    /**
     * 客户端扫描根路径
     * @return
     */
    String[] basePackages() default {};
}
```
```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({RpcReferenceAnnotationBeanPostProcessor.class})
public @interface EnableRpcConfig {
}
```
```java
/**
 * 扫描使用RpcReference注解的类进行依赖注入
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ClientBeanDefinitionRegistrar.class)
public @interface RpcComponentScan {
}
```
### 2.@RpcService声明远程服务
通过@RpcService声明远程服务，配合RpcProviderRegistrar配置类，将相关相关实现信息存放至ConcurrentHashMap中，以备后续节后到请求后，根据相关实现信息通过反射处理相关逻辑。key为接口名，value为spring bean对象
```java
public class RpcProviderRegistrar implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext ;
    @Override
    public void afterPropertiesSet() throws Exception {
        //启动服务器
        ProviderServer server = applicationContext.getBean(ProviderServer.class);
        //服务启动，并注册值zk
        server.startServer();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext ;
        this.initService();
    }

    /**
     *初始化服务实现，
     */
    private void initService() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RpcService.class);
        for(Object serviceBean:beans.values()){
            Class<?> clazz = serviceBean.getClass();
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> inter : interfaces){
                String interfaceName = inter.getName();
                logger.info("加载服务类: {}", interfaceName);
                ServiceHolder.addService(interfaceName, serviceBean);
            }
        }
        logger.info("已加载全部服务接口:{}", ServiceHolder.serviceMap);
    }
}
```
### 3.@RpcReference实现spring bean注入、依赖注入及动态代理生成
**(1)ClientBeanDefinitionRegistrar**
通过ClientBeanDefinitionRegistrar扫描所有变量，通过jdk动态代理生成代理类并注入spring容器中。生成代理类时，通过netty客户端发起远程请求，进行远程长链接通讯。
```java
public class ClientBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar{
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Set<String> basePackages = getBasePackages(importingClassMetadata);
        basePackages.stream().forEach(basePackage->{
            //fixme 寻找更加优雅的方式出合理
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackages(basePackage)
                    .addScanners(new SubTypesScanner())
                    .addScanners(new FieldAnnotationsScanner()));
            Set<Field> fieldsAnnotatedWith = reflections.getFieldsAnnotatedWith(RpcReference.class);
            fieldsAnnotatedWith.stream().forEach(field -> {
                Class<?> type = field.getType();
                registClientBean(type.getName(),registry);
            });
        });
    }

    /**
     * 注册客户端bean
     * @param className
     * @param registry
     */
    private void registClientBean(String className,BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(InvokeClientFactoryBean.class);
        builder.addPropertyValue("type", className);
        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        beanDefinition.setPrimary(true);
        String alias = "rpcClient" + className.substring(className.lastIndexOf(".") + 1);
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[]{alias});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    /**
     * 获取包扫描路径
     * @param importingClassMetadata
     * @return
     */
    private Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableRpc.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        //无论是否维护包名，均获取当前启动类所在的目录
        basePackages.add(
            ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        return basePackages;
    }
}
```
- 动态代理实现：
```java
public class InvokeClientFactoryBean implements FactoryBean<Object>, ApplicationContextAware {
	private ApplicationContext applicationContext ;
	@Setter
	private Class<?> type;
	/**
	 * 生成代理对象
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object getObject() {
		MethodHandlerFactory methodHandlerFactory = applicationContext.getBean(MethodHandlerFactory.class);
		methodHandlerFactory.setType(type);
		Map<Method, MethodHandler> dispatch = new ConcurrentHashMap<>();
		for (Method method : type.getMethods()) {
			//通过method的注解信息，获取远程调用的信息
			dispatch.put(method, methodHandlerFactory.fromMethodHandler(method));
		}
		ClientInvocationHandler invocationHandler = new ClientInvocationHandler(dispatch);
		return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, invocationHandler);
	}

	@Override
	public Class<?> getObjectType() {
		return this.type;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
```
- 代理方法的实现（netty客户端执行远程调用）
```java
public class RpcMethodHandler implements MethodHandler {

    @Override
    public Object invoke(Method method ,Object[] args) throws Throwable {
        RpcRequest request = method.getParameterTypes().length>0?
                new RpcRequest(method.getDeclaringClass().getName(),method.getName(), method.getParameterTypes(),args, RpcRequest.RequestType.CONTENT)
                :
                new RpcRequest(method.getDeclaringClass().getName(),method.getName(), RpcRequest.RequestType.CONTENT);

        //执行远程通讯. 等待消息反馈
        RpcFuture rpcFuture = ConnectedHolder.getInstance().send(request) ;
        RpcResponse result = (RpcResponse) rpcFuture.get();
        //fixme 处理接口状态
        Class<?> returnType = method.getReturnType();
        Object data = result.getResult();
        return JSONObject.parseObject(data.toString(), returnType);
    }
}

```
**(2)RpcReferenceAnnotationBeanPostProcessor**
RpcReferenceAnnotationBeanPostProcessor主要是实现依赖注入，类似与@Autowire或@Resource的作用
```java
public class RpcReferenceAnnotationBeanPostProcessor implements BeanPostProcessor , ApplicationContextAware {
    private ApplicationContext applicationContext ;
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        /**
         * 利用Java反射机制注入属性
         */
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RpcReference annotation = declaredField.getAnnotation(RpcReference.class);
            if (null == annotation) {
                continue;
            }
            declaredField.setAccessible(true);
            Class<?> type = declaredField.getType();
            Object object = applicationContext.getBean(type);
            try {
                declaredField.set(bean, object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
```
### 4.服务注册与发现
使用zookeeper作为在注册中心，进行服务注册与发现。同时消费者拉取远程服务列表进行本地缓存，减少网络开销，通过zookeeper的事件监听机制进行本地服务列表的更新。
```java
private void addWatch(CuratorFramework client) throws Exception {
    //订阅节点的增加和删除事件
        PathChildrenCache childrenCache = new PathChildrenCache(client, MANAGE_PATH, true);
        PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client,
                                   PathChildrenCacheEvent event) throws Exception {
                ChildData data = event.getData();
                switch (event.getType()) {
                    case CHILD_ADDED:
                        logger.info("CHILD_ADDED : " + data.getPath());
                        processAdd(data);
                        break;
                    case CHILD_REMOVED:
                        logger.info("CHILD_REMOVED : " + data.getPath());
                        processChange();
                        break;
                    case CHILD_UPDATED:
                        logger.info("CHILD_UPDATED : " + data.getPath());
                        processChange();
                        break;
                    default:
                        logger.debug("[PathChildrenCache]节点数据为空, path={}", data == null ? "null" : data.getPath());
                        break;
                }
            }
        };
        //todo 自定义线程池
        childrenCache.getListenable().addListener(childrenCacheListener);
        logger.info("Register zk watcher successfully!");
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
    }

    /**
     * 节点删除事件
     */
    private void processChange() {
        RemoteServerHolder.clear();
        zkService.getServerNodes(MANAGE_PATH, PATH_PREFIX_NO_STRIP);
    }

    /**
     * 节点增加事件
     * @param data
     */
    private void processAdd(ChildData data) {
        long id = NodeUtil.getIdByPath(data.getPath(), PATH_PREFIX);
        ServerNode serverNode = JSONObject.parseObject(data.getData(), ServerNode.class);
        serverNode.setId(id);
        RemoteServerHolder.addRemoteServer(serverNode);

    }
```
### 5.远程调用原理
远程调用通过netty长链接进行通信，客户端启动时从zk获取远程服务节点列表（缓存至本地服务列表地），通过负载均衡策略选择服务端进行连接。
发起请求后通过组装RpcRequest对象与远程服务进行通讯。服务接收到请求后解析RpcRequest对象，进行本地服务调用，返回RpcResponse对象。
```java
 private Object handleRequest(RpcRequest request) throws Exception {
        String className = request.getClassName();
        Map<String, Object> serviceMap = ServiceHolder.serviceMap;
        Object serviceBean = serviceMap.get(className);
        if (serviceBean!=null){
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object invoke = method.invoke(serviceBean, getParameters(parameterTypes, parameters));
            return invoke ;
        }else{
            throw new Exception("未找到服务接口");
        }
    }
```
### 6.异步结果接受处理
由于netty远程调用是异步处理，无法直接获取相关处理结果
- 通过SynchronousQueue实现
```java
    //执行远程通讯. 等待消息反馈
    SynchronousQueue<Object> queue = ConnectedHolder.getInstance().send(request);
    RpcResponse result = (RpcResponse) queue.take();
    Class<?> returnType = method.getReturnType();
    Object data = result.getResult();
```
- 通过Future实现
```java
public class RpcFuture implements Future<Object> {
    private RpcRequest rpcRequest ;
    private RpcResponse response;
    private long startTime;
    private long responseTimeThreshold = 1;
    private ConsumerSynchronizer consumerSynchronizer;

    public RpcFuture(RpcRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
        consumerSynchronizer = new ConsumerSynchronizer();
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean isDone() {
        return consumerSynchronizer.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        consumerSynchronizer.acquire(ConsumerSynchronizer.STATUS_DONE);
        if (this.response != null) {
            return this.response;
        } else {
            return null;
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = consumerSynchronizer.tryAcquireNanos(1, unit.toNanos(timeout));
        if (success) {
            if (this.response != null) {
                return this.response.getResult();
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("Timeout exception. Request id: " + this.rpcRequest.getId()
                    + ". Request class name: " + this.rpcRequest.getClassName()
                    + ". Request method: " + this.rpcRequest.getMethodName());
        }
    }

    /**
     * 任务调用完成
     * @param response
     */
    public void done(RpcResponse response) {
        consumerSynchronizer.release(ConsumerSynchronizer.STATUS_DONE);
        this.response = response;
        //计算执行时间
        long responseTime = System.currentTimeMillis() - startTime;
        //打印执行时间过长的请求
        if (responseTime > this.responseTimeThreshold) {
            logger.warn("Service response time is too slow. Request id = " + response.getRequestId() + ". Response Time = " + responseTime + "ms");
        }
    }

    static class ConsumerSynchronizer extends AbstractQueuedSynchronizer {
        private static final int STATUS_DONE = 1 ;
        private static final int STATUS_PENDING = 0;

        private static final long serialVersionUID = 1L;


        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == STATUS_DONE;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == STATUS_PENDING) {
                if (compareAndSetState(STATUS_PENDING, STATUS_DONE)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        protected boolean isDone() {
            return getState() == STATUS_DONE;
        }
    }
}
```
## 五、不足与后续规划
- 服务端增加线程池提高消息处理能力

- 通讯流转

- 客户端发送消息后如何如何将收到的结果返回

- provider中服务的注册与管理

- consumer中客户端的注册与动态代理

- 本地服务列表

- 服务注册监听机制

- 客户端请求异步处理的支持，不需要同步等待：发送一个异步请求，返回Feature，通过Feature的callback机制获取结果。

- @RpcReference 的依赖注入及springBean创建

- 如何优雅的区分启动服务是consumer还是provider(客户端与服务端bean注册问题)

- dubbo源码阅读及参考

- 改造，是有用Future接受远程执行结果,进行异步处理
