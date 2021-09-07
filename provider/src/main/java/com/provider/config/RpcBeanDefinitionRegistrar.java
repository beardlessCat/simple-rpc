package com.provider.config;

import com.provider.annotation.RpcService;
import com.provider.holder.ServiceHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
@Slf4j
public class RpcBeanDefinitionRegistrar implements ApplicationContextAware, InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        //启动服务器
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
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
