package com.provider.holder;

import com.common.entity.RpcRequest;
import io.netty.channel.Channel;

import java.util.concurrent.SynchronousQueue;

public class ConnectedHolder {
    private Channel channel ;
    private static final  ConnectedHolder instance = new ConnectedHolder();
    public static ConnectedHolder getInstance(){
        return instance;
    }

    public void init(Channel channel){
        this.channel = channel ;
    }

    public SynchronousQueue<Object>  send(RpcRequest request){
        SynchronousQueue<Object> queue = new SynchronousQueue<>();
        QueueHolder.addQueue(request.getId(), queue);
        channel.writeAndFlush(request);
        return queue;
    }
}
