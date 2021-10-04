package com.bgiyj.consumer.future;

public interface AsyncRPCCallback {
    void success(Object result);
    void fail(Exception e);
}
