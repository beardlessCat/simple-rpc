package com.bgiyj.config.handler;

import com.alibaba.fastjson.JSONObject;
import com.bgiyj.consumer.future.RpcFuture;
import com.bgiyj.core.common.entity.RpcRequest;
import com.bgiyj.core.common.entity.RpcResponse;
import com.bgiyj.core.holder.ConnectedHolder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class RpcMethodHandler implements MethodHandler {

    @Override
    public Object invoke(Method method ,Object[] args) throws Throwable {
        RpcRequest request = method.getParameterTypes().length>0?
                new RpcRequest(method.getDeclaringClass().getName(),method.getName(), method.getParameterTypes(),args, RpcRequest.RequestType.CONTENT)
                :
                new RpcRequest(method.getDeclaringClass().getName(),method.getName(), RpcRequest.RequestType.CONTENT);

        //执行远程通讯. 等待消息反馈
        RpcFuture rpcFuture = ConnectedHolder.getInstance().send(request) ;
        RpcResponse result = (RpcResponse) rpcFuture.get();
        //fixme 处理接口状态
        Class<?> returnType = method.getReturnType();
        Object data = result.getResult();
        return JSONObject.parseObject(data.toString(), returnType);
    }
}
