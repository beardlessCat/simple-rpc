package com.provider.server.initializer;

import com.common.codedc.JSONDecoder;
import com.common.codedc.JSONEncoder;
import com.provider.server.handler.ServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.springframework.stereotype.Component;

@Component
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new JSONEncoder());
        pipeline.addLast(new JSONDecoder());
        pipeline.addLast("serverHandler",new ServerHandler());
    }
}
