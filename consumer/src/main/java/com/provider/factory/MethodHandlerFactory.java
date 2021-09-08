package com.provider.factory;

import com.provider.handler.MethodHandler;
import com.provider.handler.RpcMethodHandler;

import java.lang.reflect.Method;

public class MethodHandlerFactory {
    public MethodHandler fromMethodHandler(Method method) {

        return new RpcMethodHandler();
    }
}
