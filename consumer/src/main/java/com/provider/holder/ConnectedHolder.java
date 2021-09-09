package com.provider.holder;

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

    public void send(Object request){
        channel.writeAndFlush(request);
    }
}
