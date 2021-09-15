package com.provider.factory;

import com.common.entity.RpcRequest;
import com.provider.handler.MethodHandler;
import com.provider.handler.RpcMethodHandler;
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
