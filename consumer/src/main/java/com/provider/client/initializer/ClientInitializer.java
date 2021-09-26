package com.provider.client.initializer;

import com.common.codedc.JSONDecoder;
import com.common.codedc.JSONEncoder;
import com.provider.client.handler.ClientHandler;
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
