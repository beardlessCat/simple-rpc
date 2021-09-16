package com.provider.zk;

import com.alibaba.fastjson.JSONObject;
import com.common.entity.ServerNode;
import com.common.utils.NodeUtil;
import com.provider.holder.RemoteServerHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class ZkServiceImpl implements ZkService {
	@Autowired
	CuratorFramework curatorFramework;

	@Override
	public CopyOnWriteArrayList<ServerNode> getWorkers(String path, String prefix) {
		CopyOnWriteArrayList<ServerNode> serverList = RemoteServerHolder.getServerList();
		if(serverList.isEmpty()){
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
}
