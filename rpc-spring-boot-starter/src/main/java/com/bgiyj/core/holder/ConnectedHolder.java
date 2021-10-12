package com.bgiyj.core.holder;

import com.bgiyj.consumer.future.RpcFuture;
import com.bgiyj.consumer.future.RpcFutureManager;
import com.bgiyj.core.common.entity.RpcRequest;
import io.netty.channel.Channel;

public class ConnectedHolder {
    private Channel channel ;
    private static final  ConnectedHolder instance = new ConnectedHolder();
    public static ConnectedHolder getInstance(){
        return instance;
    }

    public void init(Channel channel){
        this.channel = channel ;
    }

    public RpcFuture  send(RpcRequest request){
        RpcFuture rpcFuture = new RpcFuture(request);
        RpcFutureManager.instance().addRpcFuture(request.getId(), rpcFuture);
        channel.writeAndFlush(request);
        return rpcFuture;
    }
}
