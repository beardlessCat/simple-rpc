package com.bgiyj.config.processor;

import com.bgiyj.core.common.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;

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
