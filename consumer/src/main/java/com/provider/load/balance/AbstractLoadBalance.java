package com.provider.load.balance;


import com.common.entity.ServerNode;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {
	@Override
	public ServerNode selectNode(List<ServerNode> serverNodes) {
		if(serverNodes.isEmpty()){
			return null;
		}
		return serverNodes.get(0);
	}

	protected abstract ServerNode doSelect(List<ServerNode> serverNodes);
}
