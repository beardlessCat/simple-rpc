package com.provider.annotation;

import com.provider.config.ClientBeanDefinitionRegistrar;
import com.provider.config.RpcReferenceAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ ClientBeanDefinitionRegistrar.class, RpcReferenceAnnotationBeanPostProcessor.class})
public @interface EnableRpc {
    /**
     * 客户端扫描根路径
     * @return
     */
    String[] basePackages() default {};
}
