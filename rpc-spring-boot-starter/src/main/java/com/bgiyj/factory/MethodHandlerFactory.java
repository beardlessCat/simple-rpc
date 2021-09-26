package com.bgiyj.factory;

import com.bgiyj.config.handler.MethodHandler;
import com.bgiyj.config.handler.RpcMethodHandler;
import lombok.Setter;

import java.lang.reflect.Method;

public class MethodHandlerFactory {
    @Setter
    private Class<?> type ;

    public MethodHandler fromMethodHandler(Method method) {
        return this.buildHandler(method);
    }

    private MethodHandler buildHandler(Method method) {
        return new RpcMethodHandler();
    }
}
