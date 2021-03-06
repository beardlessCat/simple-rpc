package com.bgiyj.provider;

import com.bgiyj.core.common.entity.ServerNode;
import com.bgiyj.core.common.utils.NodeUtil;
import com.bgiyj.core.zk.ZkService;
import com.bgiyj.provider.initializer.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
/**
 * netty 客户端启动类
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "rpc",name = "role",havingValue = "provider")
public class ProviderServer {
    @Value("${provider.port}")
    private int PORT ;
    @Autowired
    private ZkService zkService ;
    @Autowired
    private ServerInitializer serverInitializer ;
    private EventLoopGroup bossGroup ;
    private EventLoopGroup workerGroup ;
    private static final String MANAGE_PATH ="/rpc/nodes";
    public static final String PATH_PREFIX = MANAGE_PATH + "/seq-";
    public void startServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(serverInitializer)
                    .localAddress(new InetSocketAddress(PORT));
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            logger.info(
                    "服务启动, 端口 " +
                            channelFuture.channel().localAddress());
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        logger.error("服务端启动成功");
                        //注册到zookeeper
                        //判断根节点是否存在
                        if (zkService.checkNodeExists(MANAGE_PATH)) {
                            zkService.createPersistentNode(MANAGE_PATH);
                        }
                        ServerNode serverNode = new ServerNode("127.0.0.1",PORT);
                        String pathRegistered =  zkService.createNode(PATH_PREFIX, serverNode);
                        //为node 设置id
                        serverNode.setId(NodeUtil.getIdByPath(pathRegistered,PATH_PREFIX));
                        logger.info("zk注册成功, path={}, id={}", pathRegistered, serverNode.getId());
                    } else {
                        logger.error("服务端启动成失败");
                    }
                }
            });
            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
