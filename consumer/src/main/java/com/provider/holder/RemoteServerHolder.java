package com.provider.holder;

import com.common.entity.ServerNode;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 本地远程服务表（服务发现时不需要每次都去zk获取，减少网络消耗）
 */
public class RemoteServerHolder {
    private static CopyOnWriteArrayList<ServerNode> serverList = new CopyOnWriteArrayList<>();

    /**
     * 获取服务节点列表
     * @return
     */
    public static CopyOnWriteArrayList<ServerNode> getServerList(){
        return serverList;
    }

    /**
     * 增加服务节点
     * @param serverNode
     */
    public static void addRemoteServer(ServerNode serverNode) {
        serverList.addIfAbsent(serverNode);
    }
}
