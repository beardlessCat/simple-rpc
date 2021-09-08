package com.common.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class RpcRequest {
    private final String id;
    private final String className;// 类名
    private final String methodName;// 函数名称
    private final Class<?>[] parameterTypes;// 参数类型
    private final Object[] parameters;// 参数列表

    private RpcRequest(Builder builder){
        this.id=builder.id;
        this.className=builder.className;
        this.methodName=builder.methodName;
        this.parameterTypes=builder.parameterTypes;
        this.parameters=builder.parameters;
    }
    public static Builder builder(String className,String methodName) {
        return new Builder(className,methodName);
    }

    public static class Builder{
        private String id;
        private String className;// 类名
        private String methodName;// 函数名称
        private Class<?>[] parameterTypes;// 参数类型
        private Object[] parameters;// 参数列表

        public Builder(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
            this.id = UUID.randomUUID().toString();
        }

        public Builder setParameterTypes(Class<?>[] parameterTypes){
            this.parameterTypes =parameterTypes;
            return this;
        }

        public Builder setParameter(Object[] parameters){
            this.parameters =parameters;
            return this;
        }
        public RpcRequest build(){
            return new RpcRequest(this);
        }
    }

}
