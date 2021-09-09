package com.provider.load.balance;


import com.common.entity.ServerNode;

import java.util.List;

/**
 * 最少活跃数
 */
public class LeastActiveLoadBlance extends AbstractLoadBalance{

	@Override
	protected ServerNode doSelect(List<ServerNode> serverNodes) {
		return null;
	}
}
