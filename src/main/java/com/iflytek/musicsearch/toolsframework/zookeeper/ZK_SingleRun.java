package com.iflytek.musicsearch.toolsframework.zookeeper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.iflytek.musicsearch.toolsframework.common.CommonFunc;
import com.iflytek.musicsearch.toolsframework.policy.multijvm.multirun.MultiRunCommon;

public class ZK_SingleRun extends ZK_Base {

	private static Logger logger = Logger.getLogger(ZK_SingleRun.class);
	
	/**
	 * 尝试获取锁
	 * @param taskName
	 * @return
	 */
	public static boolean getLock(String taskName){
		try {
			ZooKeeper zk = MultiRunCommon.getMapZK().get(taskName);
			
			//1、先判断任务节点是否存在，不存在则创建
			String taskPath = rootPath + "/" + taskName;
			String macStr = CommonFunc.getLocalMac(); 
			if(zk.exists(taskPath, false) == null){
			 	String createdPath = zk.create(taskPath, macStr.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			 	return StringUtils.isNotBlank(createdPath);
			}else{
				byte[] bytes = zk.getData(taskPath, null, null);
				String value = new String(bytes);
				return macStr.equals(value);
			}
		}catch(NodeExistsException ex){
			logger.info(String.format("任务节点  %s 已存在", taskName));
		} catch (Exception e) {
			logger.error(e);
		}
		return false;
	}

	/**
	 * 当任务被强制终止成功后，需要移除节点的值
	 */
	public static void removeNode(String taskName) {
		try {
			ZooKeeper zk = MultiRunCommon.getMapZK().get(taskName);
			zk.delete(rootPath + "/" + taskName, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
