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
            new RpcRequest(type.getName(),method.getName(), method.getParameterTypes(),method.getParameters(), RpcRequest.RequestType.CONTENT)
            :
            new RpcRequest(type.getName(),method.getName(), RpcRequest.RequestType.CONTENT);
        return new RpcMethodHandler(request);
    }
}
