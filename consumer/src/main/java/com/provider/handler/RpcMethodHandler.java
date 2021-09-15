package com.provider.handler;

import com.alibaba.fastjson.JSONArray;
import com.common.entity.RpcRequest;
import com.provider.holder.ConnectedHolder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.SynchronousQueue;

@Slf4j
public class RpcMethodHandler implements MethodHandler{

    @Override
    public Object invoke(Method method ,Object[] args) throws Throwable {
        RpcRequest request = method.getParameterTypes().length>0?
                new RpcRequest(method.getDeclaringClass().getName(),method.getName(), method.getParameterTypes(),args, RpcRequest.RequestType.CONTENT)
                :
                new RpcRequest(method.getDeclaringClass().getName(),method.getName(), RpcRequest.RequestType.CONTENT);
        //执行远程通讯. 等待消息反馈
        SynchronousQueue<Object> queue = ConnectedHolder.getInstance().send(request);
        Object result = queue.take();
        return JSONArray.toJSONString(result);
    }
}
