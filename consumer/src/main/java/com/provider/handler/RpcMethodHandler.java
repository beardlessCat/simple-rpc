package com.provider.handler;

import com.alibaba.fastjson.JSONPObject;
import com.common.entity.RpcRequest;
import com.google.gson.Gson;
import com.provider.client.ConsumerClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcMethodHandler implements MethodHandler{
    private RpcRequest request ;

    public RpcMethodHandler(RpcRequest request) {
        this.request = request;
    }
    private ConsumerClient consumerClient ;
    @Override
    public Object invoke(Object[] args) throws Throwable {
        //执行远程通讯
        ConsumerClient.getInstance().send(request);
        //等待消息反馈 fixme
        return null;
    }
}
