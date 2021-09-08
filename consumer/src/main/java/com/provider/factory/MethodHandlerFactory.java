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
        RpcRequest request = method.getParameterTypes().length>0?
            RpcRequest.builder(method.getName(), type.getName()).setParameterTypes(method.getParameterTypes()).setParameter(method.getTypeParameters()).build()
                :
            RpcRequest.builder(method.getName(), type.getName()).build();
        return new RpcMethodHandler(request);
    }
}
