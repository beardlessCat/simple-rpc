package com.provider.server.handler;

import com.alibaba.fastjson.JSON;
import com.common.entity.RpcRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@ChannelHandler.Sharable
public class HeartBeatServerHandler extends IdleStateHandler {
    private static final int READ_IDLE_GAP = 150;

    public HeartBeatServerHandler() {
        super(READ_IDLE_GAP, 0, 0, TimeUnit.SECONDS);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        RpcRequest request = JSON.parseObject(msg.toString(),RpcRequest.class);
        //判断消息实例
        if (null == request || request.getRequestType()!=RpcRequest.RequestType.HEATBEAT) {
            super.channelRead(ctx, msg);
            return;
        }
        //接受到心跳，直接回复给客户端
        //logger.info("pone.......");
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        logger.info(READ_IDLE_GAP + "秒内未读到数据，关闭连接");
    }
}
