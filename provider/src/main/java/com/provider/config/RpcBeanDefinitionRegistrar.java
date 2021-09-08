package com.provider.config;

import com.common.annotation.RpcService;
import com.provider.holder.ServiceHolder;
import com.provider.server.ProviderServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
@Slf4j
public class RpcBeanDefinitionRegistrar implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext ;
    @Override
    public void afterPropertiesSet() throws Exception {
        //启动服务器
        ProviderServer server = applicationContext.getBean(ProviderServer.class);
        //异步启动
        new Thread(()->{
            server.startServer();
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext ;
        this.initService();
    }

    private void initService() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RpcService.class);
        for(Object serviceBean:beans.values()){
            Class<?> clazz = serviceBean.getClass();
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> inter : interfaces){
                String interfaceName = inter.getName();
                logger.info("加载服务类: {}", interfaceName);
                ServiceHolder.serviceMap.put(interfaceName, serviceBean);
            }
        }
        logger.info("已加载全部服务接口:{}", ServiceHolder.serviceMap);
    }
}
