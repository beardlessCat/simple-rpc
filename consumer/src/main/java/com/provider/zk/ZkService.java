package com.provider.zk;


import com.common.entity.ServerNode;

import java.util.concurrent.CopyOnWriteArrayList;

public interface ZkService {
	CopyOnWriteArrayList<ServerNode> getWorkers(String path, String prefix);
}
