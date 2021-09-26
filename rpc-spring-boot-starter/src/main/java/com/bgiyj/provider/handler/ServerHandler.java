package com.bgiyj.provider.handler;

import com.alibaba.fastjson.JSON;
import com.bgiyj.core.common.entity.RpcRequest;
import com.bgiyj.core.common.entity.RpcResponse;
import com.bgiyj.core.holder.ServiceHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
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
        //处理远程连接，执行本地方法
        RpcRequest request = JSON.parseObject(msg.toString(),RpcRequest.class);
        Object result = this.handleRequest(request);
        logger.info(result.toString());
        RpcResponse response = new RpcResponse(request.getId(), 200, "请求成功", result);
        ctx.channel().writeAndFlush(response);
    }

    private Object handleRequest(RpcRequest request) throws Exception {
        String className = request.getClassName();
        Map<String, Object> serviceMap = ServiceHolder.serviceMap;
        Object serviceBean = serviceMap.get(className);
        if (serviceBean!=null){
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object invoke = method.invoke(serviceBean, getParameters(parameterTypes, parameters));
            return invoke ;
        }else{
            throw new Exception("未找到服务接口");
        }
    }
    /**
     * 获取参数列表
     * @param parameterTypes
     * @param parameters
     * @return
     */
    private Object[] getParameters(Class<?>[] parameterTypes,Object[] parameters){
        if (parameters==null || parameters.length==0){
            return parameters;
        }else{
            Object[] new_parameters = new Object[parameters.length];
            for(int i=0;i<parameters.length;i++){
                new_parameters[i] = JSON.parseObject(parameters[i].toString(),parameterTypes[i]);
            }
            return new_parameters;
        }
    }
}
