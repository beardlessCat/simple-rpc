package com.bgiyj.annotation;

import com.bgiyj.config.ClientBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ClientBeanDefinitionRegistrar.class)
public @interface RpcComponentScan {
}
