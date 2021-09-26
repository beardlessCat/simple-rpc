package com.bgiyj.load.balance;


import com.bgiyj.core.common.entity.ServerNode;

import java.util.List;

/**
 * 一致性HASH
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {
	@Override
	protected ServerNode doSelect(List<ServerNode> serverNodes) {
		String remoteIp = "127.0.0.1";
		int hashCode = remoteIp.hashCode();
		int serverListSize = serverNodes.size();
		int serverPos = hashCode % serverListSize;
		return serverNodes.get(serverPos);
	}
}
