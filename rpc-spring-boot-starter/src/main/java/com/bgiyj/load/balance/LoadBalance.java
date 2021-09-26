package com.bgiyj.load.balance;



import com.bgiyj.core.common.entity.ServerNode;

import java.util.List;

public interface LoadBalance {
	ServerNode selectNode(List<ServerNode> serverNodes);
}
