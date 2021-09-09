package com.provider.client;

import com.common.entity.RpcRequest;
import com.common.entity.ServerNode;
import com.provider.load.balance.LoadBalance;
import com.provider.zk.ZkService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;

@Slf4j
@Component
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
    public static final String MANAGE_PATH = "/im/nodes";
    public static final String PATH_PREFIX_NO_STRIP =  "seq-";

    public void startClient(){
        List<ServerNode> workers = zkService.getWorkers(MANAGE_PATH, PATH_PREFIX_NO_STRIP);
        ServerNode serverNode = loadBalance.selectNode(workers);
        Channel channel = null;
        String host =serverNode.getHost() ;
        int port= serverNode.getPort() ;
        try {
            bootstrap = new Bootstrap();
            eventLoopGroup = new NioEventLoopGroup(1);
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(null);
            channel = bootstrap.connect(host, port).sync().channel();
            this.channel =  channel ;
            //管理channel
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
