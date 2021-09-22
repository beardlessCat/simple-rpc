package com.provider.holder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceHolder {
    public static Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    public static void addService(String interfaceName,Object bean){
        serviceMap.put(interfaceName,bean);
    }

    public static Object getService(String interfaceName){
        return  serviceMap.get(interfaceName);
    }
}
