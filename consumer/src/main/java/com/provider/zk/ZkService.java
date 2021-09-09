package com.provider.zk;


import com.common.entity.ServerNode;

import java.util.List;

public interface ZkService {
	List<ServerNode> getWorkers(String path, String prefix);
}
