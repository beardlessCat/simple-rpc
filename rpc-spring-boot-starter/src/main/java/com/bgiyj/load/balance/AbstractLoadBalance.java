package com.bgiyj.load.balance;


import com.bgiyj.core.common.entity.ServerNode;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {
	@Override
	public ServerNode selectNode(List<ServerNode> serverNodes) {
		if(serverNodes.isEmpty()){
			return null;
		}
		return doSelect(serverNodes);
	}

	protected abstract ServerNode doSelect(List<ServerNode> serverNodes);
}
