package com.bgiyj.config.handler;

import java.lang.reflect.Method;

public interface MethodHandler {
    Object invoke(Method method , Object[] args) throws Throwable;
}
