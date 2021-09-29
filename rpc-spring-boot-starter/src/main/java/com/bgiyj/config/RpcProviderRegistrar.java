package com.bgiyj.config;

import com.bgiyj.annotation.RpcService;
import com.bgiyj.core.holder.ServiceHolder;
import com.bgiyj.provider.ProviderServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "rpc",name = "role",havingValue = "provider")
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
