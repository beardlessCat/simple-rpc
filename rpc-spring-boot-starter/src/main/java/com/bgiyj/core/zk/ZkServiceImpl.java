package com.bgiyj.core.zk;

import com.alibaba.fastjson.JSONObject;
import com.bgiyj.core.common.entity.ServerNode;
import com.bgiyj.core.common.utils.NodeUtil;
import com.bgiyj.core.holder.RemoteServerHolder;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class ZkServiceImpl implements ZkService {
	@Autowired
	CuratorFramework curatorFramework;

	@Override
	public CopyOnWriteArrayList<ServerNode> getServerNodes(String path, String prefix) {
		CopyOnWriteArrayList<ServerNode> serverList = RemoteServerHolder.getServerList();
		if(!serverList.isEmpty()){
			return serverList;
		}
		List<String> children = null;
		try
		{
			children = curatorFramework.getChildren().forPath(path);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

		for (String child : children)
		{
			logger.info("child:", child);
			byte[] payload = null;
			try
			{
				payload = curatorFramework.getData().forPath(path + "/" + child);

			} catch (Exception e)
			{
				e.printStackTrace();
			}
			if (null == payload)
			{
				continue;
			}
			ServerNode serverNode = JSONObject.parseObject(payload, ServerNode.class);
			serverNode.setId(NodeUtil.getIdByPath(child,prefix));
			RemoteServerHolder.addRemoteServer(serverNode);
		}
		return RemoteServerHolder.getServerList();
	}
	@Override
	public boolean checkNodeExists(String path) throws Exception {
		Stat stat = curatorFramework.checkExists().forPath(path);
		return stat==null?false:true;
	}

	@Override
	public String createPersistentNode(String path) throws Exception {
		String pathRegistered = curatorFramework.create()
				.creatingParentsIfNeeded()
				.withProtection()
				.withMode(CreateMode.PERSISTENT)
				.forPath(path);
		return pathRegistered;
	}

	@Override
	public String createNode(String prefix, ServerNode serverNode) throws Exception {
		byte[] payload = new Gson().toJson(serverNode).getBytes(StandardCharsets.UTF_8);
		String pathRegistered = curatorFramework.create()
				.creatingParentsIfNeeded()
				.withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
				.forPath(prefix, payload);
		return pathRegistered;
	}
}
