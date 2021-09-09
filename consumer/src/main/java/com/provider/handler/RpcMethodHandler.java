package com.provider.handler;

import com.common.entity.RpcRequest;
import com.provider.holder.ConnectedHolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcMethodHandler implements MethodHandler{
    private RpcRequest request ;

    public RpcMethodHandler(RpcRequest request) {
        this.request = request;
    }
    @Override
    public Object invoke(Object[] args) throws Throwable {
        //执行远程通讯
        ConnectedHolder.getInstance().send(request);
        //等待消息反馈 fixme
        return null;
    }
}
