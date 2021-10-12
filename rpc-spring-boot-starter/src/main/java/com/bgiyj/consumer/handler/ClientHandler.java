package com.bgiyj.consumer.handler;

import com.alibaba.fastjson.JSON;
import com.bgiyj.consumer.future.RpcFuture;
import com.bgiyj.consumer.future.RpcFutureManager;
import com.bgiyj.core.common.entity.RpcResponse;
import com.bgiyj.consumer.ConsumerClient;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
@ChannelHandler.Sharable
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private ConsumerClient consumerClient;
    @Override
    public void channelActive(ChannelHandlerContext ctx)   {
        logger.info("已连接到RPC服务器.{}",ctx.channel().remoteAddress());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().addBefore("clientHandler","heartBeat",new HeartBeatClientHandler());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)   {
        InetSocketAddress address =(InetSocketAddress) ctx.channel().remoteAddress();
        logger.info("与RPC服务器断开连接."+address);
        ctx.channel().close();
        //移除本地管理的链接 fixme
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception {
        RpcResponse response = JSON.parseObject(msg.toString(), RpcResponse.class);
        String requestId = response.getRequestId();
        logger.debug("Receive response: " + requestId);
        RpcFuture rpcFuture = RpcFutureManager.instance().getRpcFuture(requestId);
        if (rpcFuture != null) {
            RpcFutureManager.instance().removeRpcFuture(requestId);
            rpcFuture.done(response);
        } else {
            logger.warn("Can not get pending response for request id: " + requestId);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        //重连机制
        logger.info("RPC通信服务器发生异常.{}",cause);
        ctx.channel().close();
        //fixme 判断异常类型
        //重新连接远程服务
        consumerClient.startClient();
    }
}
