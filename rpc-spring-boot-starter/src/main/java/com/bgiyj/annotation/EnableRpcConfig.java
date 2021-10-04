package com.bgiyj.annotation;

import com.bgiyj.config.processor.RpcReferenceAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({RpcReferenceAnnotationBeanPostProcessor.class})
public @interface EnableRpcConfig {
}
