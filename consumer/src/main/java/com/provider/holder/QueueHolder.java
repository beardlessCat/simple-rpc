package com.provider.holder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

public class QueueHolder {
    public static Map<String, Object> serviceMap = new HashMap<>();
    private static ConcurrentHashMap<String, SynchronousQueue<Object>> queueMap = new ConcurrentHashMap<>();

    public static void addQueue(String requestId,SynchronousQueue<Object> objectSynchronousQueue){
        queueMap.put(requestId,objectSynchronousQueue);
    }

    public static SynchronousQueue<Object> getQueue(String requestId){
        return  queueMap.get(requestId);
    }
    public static void remove(String requestId){
        queueMap.remove(requestId);
    }
}
