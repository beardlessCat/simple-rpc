package com.provider.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Data
public class ClientInvocationHandler implements InvocationHandler {
    Map<Method, MethodHandler> dispatch = new ConcurrentHashMap<>();
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = dispatch.get(method).invoke(args);
        return result;
    }
}
