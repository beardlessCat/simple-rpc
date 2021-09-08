package com.provider.handler;

public interface MethodHandler {
    Object invoke(Object[] args) throws Throwable;
}
