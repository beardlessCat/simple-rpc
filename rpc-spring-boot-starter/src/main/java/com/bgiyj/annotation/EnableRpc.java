package com.bgiyj.annotation;

import java.lang.annotation.*;

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
