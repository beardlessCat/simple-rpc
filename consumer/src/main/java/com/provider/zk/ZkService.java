package com.provider.zk;


import com.common.entity.ServerNode;

import java.util.concurrent.CopyOnWriteArrayList;

public interface ZkService {
	CopyOnWriteArrayList<ServerNode> getServerNodes(String path, String prefix);
}
