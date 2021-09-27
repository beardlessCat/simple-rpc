package com.bgiyj.consumer;


import com.bgiyj.consumer.initializer.ClientInitializer;
import com.bgiyj.core.common.entity.ServerNode;
import com.bgiyj.core.holder.ConnectedHolder;
import com.bgiyj.core.zk.ZkService;
import com.bgiyj.load.balance.LoadBalance;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;

/**
 * netty 服务端启动类
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "rpc",name = "role",havingValue = "consumer")
public class ConsumerClient {
    private static final ConsumerClient instance = new ConsumerClient() ;

    public static ConsumerClient getInstance(){
        return instance;
    }
    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap ;
    private Channel channel ;
    @Autowired
    private LoadBalance loadBalance;
    @Autowired
    private ZkService zkService;
    public static final String MANAGE_PATH = "/rpc/nodes";
    public static final String PATH_PREFIX_NO_STRIP =  "seq-";

    public void startClient(){
        List<ServerNode> workers = zkService.getServerNodes(MANAGE_PATH, PATH_PREFIX_NO_STRIP);
        ServerNode serverNode = loadBalance.selectNode(workers);
        Channel channel = null;
        String host =serverNode.getHost() ;
        int port= serverNode.getPort() ;
        try {
            bootstrap = new Bootstrap();
            eventLoopGroup = new NioEventLoopGroup(1);
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());
            channel = bootstrap.connect(host, port).sync().channel();
            this.channel =  channel ;
            //管理channel
            ConnectedHolder.getInstance().init(channel);
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
}
