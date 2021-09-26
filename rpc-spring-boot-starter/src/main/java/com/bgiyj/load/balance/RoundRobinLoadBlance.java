package com.bgiyj.load.balance;


import com.bgiyj.core.common.entity.ServerNode;

import java.util.List;

/**
 *轮询调用
 */
public class RoundRobinLoadBlance extends AbstractLoadBalance{

	@Override
	protected ServerNode doSelect(List<ServerNode> serverNodes) {
		return null;
	}
}
