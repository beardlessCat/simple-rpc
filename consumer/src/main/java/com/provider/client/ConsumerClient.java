package com.provider.client;

import com.common.entity.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
@Slf4j
public class ConsumerClient {
    private static final ConsumerClient instance = new ConsumerClient() ;
    public static ConsumerClient getInstance(){
        return instance;
    }
    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap ;
    private Channel channel ;
    public void startClient(){
        Channel channel = null;
        String hosts ="" ;
        int port= 1000 ;
        try {
            bootstrap = new Bootstrap();
            eventLoopGroup = new NioEventLoopGroup(1);
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(null);
            channel = bootstrap.connect(hosts, port).sync().channel();
            this.channel =  channel ;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroy(){
        logger.info("RPC客户端退出,释放资源!");
        close();
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

    public void send(RpcRequest msg) {
        this.channel.writeAndFlush(msg);
    }
}
