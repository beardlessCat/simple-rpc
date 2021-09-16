package com.provider.discovery;

import com.alibaba.fastjson.JSONObject;
import com.common.entity.ServerNode;
import com.common.utils.NodeUtil;
import com.provider.client.ConsumerClient;
import com.provider.holder.RemoteServerHolder;
import com.provider.zk.ZkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class ServiceDiscovery {
    @Autowired
    private ConsumerClient consumerClient ;
    private static final String MANAGE_PATH ="/rpc/nodes";
    public static final String PATH_PREFIX_NO_STRIP =  "seq-";
    public static final String PATH_PREFIX = MANAGE_PATH + "/seq-";

    @Autowired
    private CuratorFramework client ;
    @Autowired
    private ZkService zkService ;
    @PostConstruct
    public void init() throws Exception {
        //监听节点变化
        this.addWatch(client);
        consumerClient.startClient();
    }

    private void addWatch(CuratorFramework client) throws Exception {
    //订阅节点的增加和删除事件
        PathChildrenCache childrenCache = new PathChildrenCache(client, MANAGE_PATH, true);
        PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client,
                                   PathChildrenCacheEvent event) throws Exception {
                ChildData data = event.getData();
                switch (event.getType()) {
                    case CHILD_ADDED:
                        logger.info("CHILD_ADDED : " + data.getPath());
                        processAdd(data);
                        break;
                    case CHILD_REMOVED:
                        logger.info("CHILD_REMOVED : " + data.getPath());
                        processChange();
                        break;
                    case CHILD_UPDATED:
                        logger.info("CHILD_UPDATED : " + data.getPath());
                        processChange();
                        break;
                    default:
                        logger.debug("[PathChildrenCache]节点数据为空, path={}", data == null ? "null" : data.getPath());
                        break;
                }
            }
        };
        //fixme 自定义线程池
        childrenCache.getListenable().addListener(childrenCacheListener);
        logger.info("Register zk watcher successfully!");
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
    }

    /**
     * 节点删除事件
     */
    private void processChange() {
        RemoteServerHolder.clear();
        zkService.getServerNodes(MANAGE_PATH, PATH_PREFIX_NO_STRIP);
    }

    /**
     * 节点增加事件
     * @param data
     */
    private void processAdd(ChildData data) {
        long id = NodeUtil.getIdByPath(data.getPath(), PATH_PREFIX);
        ServerNode serverNode = JSONObject.parseObject(data.getData(), ServerNode.class);
        serverNode.setId(id);
        RemoteServerHolder.addRemoteServer(serverNode);

    }
}

