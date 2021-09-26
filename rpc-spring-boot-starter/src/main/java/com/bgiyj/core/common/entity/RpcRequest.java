package com.bgiyj.core.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class RpcRequest implements Serializable {
    private String id;
    private String className;// 类名
    private String methodName;// 函数名称
    private Class<?>[] parameterTypes;// 参数类型
    private Object[] parameters;// 参数列表
    private RequestType requestType;
    public RpcRequest(String className, String methodName,RequestType requestType) {
        this.id = UUID.randomUUID().toString();
        this.className = className;
        this.methodName = methodName;
        this.requestType = requestType ;
    }

    public RpcRequest() {
    }

    public RpcRequest(String className, String methodName, Class<?>[] parameterTypes, Object[] parameters, RequestType requestType) {
        this.className = className;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        this.id = UUID.randomUUID().toString();
        this.requestType = requestType ;

    }
    public enum RequestType{
        HEATBEAT,CONTENT
    }
}
