package com.provider.server.handler;

public interface MethodHandler {
    Object invoke(Object[] args) throws Throwable;
}
