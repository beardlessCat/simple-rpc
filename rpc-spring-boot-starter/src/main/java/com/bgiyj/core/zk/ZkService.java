package com.bgiyj.core.zk;



import com.bgiyj.core.common.entity.ServerNode;

import java.util.concurrent.CopyOnWriteArrayList;

public interface ZkService {
	CopyOnWriteArrayList<ServerNode> getServerNodes(String path, String prefix);

	boolean checkNodeExists(String path) throws Exception;

	String createPersistentNode(String path) throws Exception;

	String createNode(String prefix , ServerNode serverNode) throws Exception;
}
