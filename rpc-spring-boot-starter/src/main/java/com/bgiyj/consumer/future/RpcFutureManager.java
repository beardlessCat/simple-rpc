package com.bgiyj.consumer.future;

import java.util.concurrent.ConcurrentHashMap;

/**
 *  RpcFuture管理类
 */
public class RpcFutureManager {
    private ConcurrentHashMap<String, RpcFuture> rpcFutures = new ConcurrentHashMap<>();

    private static final RpcFutureManager instance = new RpcFutureManager();

    public static RpcFutureManager instance(){
        return instance;
    }

    public void addRpcFuture(String requestId,RpcFuture rpcFuture){
        rpcFutures.put(requestId,rpcFuture);
    }

    public void removeRpcFuture(String requestId){
        rpcFutures.remove(requestId);
    }

    public RpcFuture getRpcFuture(String requestId) {
        return rpcFutures.get(requestId);
    }
}
