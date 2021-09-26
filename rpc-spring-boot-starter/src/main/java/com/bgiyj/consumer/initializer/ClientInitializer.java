package com.bgiyj.consumer.initializer;

import com.bgiyj.consumer.handler.ClientHandler;
import com.bgiyj.core.common.codedc.JSONDecoder;
import com.bgiyj.core.common.codedc.JSONEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new JSONEncoder());
        pipeline.addLast(new JSONDecoder());
        pipeline.addLast("clientHandler",new ClientHandler());
    }

}
