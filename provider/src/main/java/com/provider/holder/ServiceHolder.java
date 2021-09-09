package com.provider.holder;

import java.util.HashMap;
import java.util.Map;

public class ServiceHolder {
    public static Map<String, Object> serviceMap = new HashMap<>();

    public static void addService(String interfaceName,Object bean){
        serviceMap.put(interfaceName,bean);
    }

    public static Object getService(String interfaceName){
        return  serviceMap.get(interfaceName);
    }
}
