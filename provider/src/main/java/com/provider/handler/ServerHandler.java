package com.provider.handler;

import com.provider.holder.ServiceHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@ChannelHandler.Sharable
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端连接成功!"+ctx.channel().remoteAddress());
        //将心跳handler加入通道
        ctx.pipeline().addBefore("serverHandler","heartbeat",new HeartBeatServerHandler());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)   {
        logger.info("客户端断开连接!{}",ctx.channel().remoteAddress());
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Map<String, Object> serviceMap = ServiceHolder.serviceMap;
        super.channelRead(ctx, msg);
    }
}
