package com.provider.load.balance;


import com.common.entity.ServerNode;

import java.util.List;

public interface LoadBalance {
	ServerNode selectNode(List<ServerNode> serverNodes);
}
