package com.iflytek.musicsearch.toolsframework.zookeeper;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

import com.iflytek.musicsearch.toolsframework.config.schema.ZKConfig;
import com.iflytek.musicsearch.toolsframework.policy.multijvm.multirun.MultiRunCommon;
import com.iflytek.musicsearch.toolsframework.zookeeper.watcher.ConnectedWatcher;

public class ZK_Base {

	private static Logger logger = Logger.getLogger(ZK_Base.class);
	private static ZKConfig zkConfig;
	public static String zkConnStr = "";
	public static String rootPath = "";
	public static int timeout = 0;
	
	public static void setZkConfig(ZKConfig zkConfig) {
		ZK_Base.zkConfig = zkConfig;
	}

	public static void init(){
		try {
			zkConnStr = zkConfig.getUrl();
			timeout = zkConfig.getTimeout();
			rootPath = zkConfig.getRoot();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 创建根节点
	 * @param taskName
	 * @throws Exception
	 */
	public static void createPath(final String taskName) throws Exception {
		CountDownLatch connectedSignal = new CountDownLatch(1);	
		ZooKeeper zk = new ZooKeeper(zkConnStr, timeout, new ConnectedWatcher(connectedSignal));
		if(States.CONNECTING == zk.getState()){
        	logger.info("zk还处于正在连接的状态中。。。");
			connectedSignal.await();
		}
		MultiRunCommon.getMapZK().put(taskName, zk);
		if(zk.exists(rootPath, false) == null){
			zk.create(rootPath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
	}
	
	/**
	 * 获取某个node在所有任务子node列表中的位置
	 * @param taskName 任务名称
	 * @param path 要检查的路径
	 * @param childNode 子节点名称
	 * @param wc 是否注册观察者
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public static int getNodeIndex(String taskName, String path, String childNode, Watcher wc) throws KeeperException, InterruptedException, SocketException, UnknownHostException{
		ZooKeeper zk = MultiRunCommon.getMapZK().get(taskName);
		List<String> nodes = new ArrayList<String>();
		if(wc == null){
			nodes = zk.getChildren(path, null);
		}else{
			nodes = zk.getChildren(path, wc);
		}
		return nodes.indexOf(childNode);
	}
	
	/**
	 * 获取所有的子节点列表
	 * @param taskName
	 * @param path
	 * @param wc
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public static List<String> getNodeList(String taskName, String path, Watcher wc) throws KeeperException, InterruptedException{
		ZooKeeper zk = MultiRunCommon.getMapZK().get(taskName);
		if(wc == null){
			return zk.getChildren(path, null);
		}else{
			return zk.getChildren(path, wc);
		}
	}
}
