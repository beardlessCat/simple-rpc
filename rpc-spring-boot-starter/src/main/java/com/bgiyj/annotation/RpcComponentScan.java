package com.bgiyj.annotation;

import com.bgiyj.config.ClientBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 扫描使用RpcReference注解的类进行依赖注入
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ClientBeanDefinitionRegistrar.class)
public @interface RpcComponentScan {
}
