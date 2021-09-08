package com.provider.handler;

import com.common.entity.RpcRequest;
import com.google.gson.Gson;
import com.provider.client.ConsumerClient;

public class RpcMethodHandler implements MethodHandler{
    private RpcRequest request ;

    public RpcMethodHandler(RpcRequest request) {
        this.request = request;
    }
    private ConsumerClient consumerClient ;
    @Override
    public Object invoke(Object[] args) throws Throwable {
        String msg = new Gson().toJson(request);
        //执行远程通讯
        ConsumerClient.getInstance().send(msg+"/");
        //等待消息反馈 fixme
        return null;
    }
}
