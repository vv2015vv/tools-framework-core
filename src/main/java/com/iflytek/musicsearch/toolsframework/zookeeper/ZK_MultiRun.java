package com.iflytek.musicsearch.toolsframework.zookeeper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.iflytek.musicsearch.toolsframework.common.CommonFunc;
import com.iflytek.musicsearch.toolsframework.policy.multijvm.TimerRunEntity;
import com.iflytek.musicsearch.toolsframework.policy.multijvm.multirun.MultiRunCommon;
import com.iflytek.musicsearch.toolsframework.zookeeper.watcher.ChildNodeChangedWatcher;

public class ZK_MultiRun extends ZK_Base {

	private static Logger logger = Logger.getLogger(ZK_MultiRun.class);
	private static Map<String, ChildNodeChangedWatcher> map_ChildWatcher = 
			new HashMap<String, ChildNodeChangedWatcher>();
	private static Object obj = new Object();
	
	/**
	 * 创建任务节点
	 * @throws Exception
	 */
	public static void createTaskPath(final String taskName) throws Exception {
		//1、调用基类方法创建根节点
		createPath(taskName);
		//2、创建任务节点
		ZooKeeper zk = MultiRunCommon.getMapZK().get(taskName);
		String taskPath = rootPath + "/" + taskName;
		if(zk.exists(taskPath, false) == null){
			zk.create(taskPath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		//3、判断当前实例子节点是否存在，如存在则先删除
		String childNode = String.format("%s_%s", taskName, CommonFunc.getLocalMac());
		String childNodePath = String.format("%s/%s", taskPath, childNode);
		if (zk.exists(childNodePath, false) != null) {
			zk.delete(childNodePath, -1);
		}
		zk.create(childNodePath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		
		//3、创建任务对应的watcher
		ChildNodeChangedWatcher wc = new ChildNodeChangedWatcher(taskName);
		map_ChildWatcher.put(taskName, wc);
		
		//4、确定当前实例的一些状态信息
		List<String> childList = getNodeList(taskName, taskPath, map_ChildWatcher.get(taskName));
		int index = childList.indexOf(childNode);
		TimerRunEntity entity = MultiRunCommon.getMapEntity().get(taskName);
		entity.setValue(false, childList.size(), index, childList.size(), index);
		entity.getLastNodeList().addAll(childList);
		if(childList.size() > 1){
			entity.setNeedDelayExec(true);
		}
	}
	
	/**
	 * 清除标记
	 * 最多尝试三次，如果不成功，则记录异常标志
	 */
	public static void removeChildPathData(String taskName){
		TimerRunEntity entity = MultiRunCommon.getMapEntity().get(taskName);
		int index = 0;
		while(index < 3){
			try {
				ZooKeeper zk = MultiRunCommon.getMapZK().get(taskName);
				String childPath = String.format("%s/%s/%s_%s", rootPath, taskName, taskName, CommonFunc.getLocalMac());
				zk.setData(childPath, "0".getBytes(), -1);
				entity.setInException(false);
				return;
			} catch (Exception e) {
				//出现异常时默认不作额外处理，这种情况下，如果实例未能更新节点下的数据，则需要人工手动去参与
				logger.error(e);
				entity.setInException(true);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					logger.error(e1);
				}
			}
			index++;
		}
	}
	
	/**
	 * 检查其他实例的数据处理情况，处理完成后当前实例才能运行
	 * @return
	 */
	public static Boolean judgeCurrBeanCanRun(String taskName){
		TimerRunEntity entity = MultiRunCommon.getMapEntity().get(taskName);
		int index = 0;
		while(index < 3){
			try{
				ZooKeeper zk = MultiRunCommon.getMapZK().get(taskName);
				String taskPath = String.format("%s/%s", rootPath, taskName);
				List<String> childList = getNodeList(taskName, taskPath, map_ChildWatcher.get(taskName));
				String childPath = String.format("%s/%s_%s", taskPath, taskName, CommonFunc.getLocalMac());
				for(String child : childList){
					String path = String.format("%s/%s", taskPath, child);
					if(!path.equals(childPath)){
						byte[] bytes = zk.getData(path, null, null);
						if(bytes != null && bytes.length > 0){
							String val = new String(bytes);
							if("1".equals(val)){
								logger.info(String.format("任务：【%s】  发现节点%s的值为1，当前实例不能进行数据拆分工作。。。", taskName, child));
								return false;
							}
						}
					}
				}
				entity.setInException(false);
				return true;
			}catch(Exception e){
				logger.error(e);
				entity.setInException(true);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					logger.error(e1);
				}
			}
			index++;
		}
		return false;
	}
	
	/**
	 * 由zk的watcher触发的一个检查机制
	 * 当检查节点出现异常时，对内存中缓存的节点数量和索引信息不作任何变更
	 * 维持原状处理
	 */
	public static void checkChildNodes(String taskName){
		synchronized (obj) {
			TimerRunEntity entity = null;
			int loopIndex = 0;
			while(loopIndex < 3){
				try {
					ZooKeeper zk = MultiRunCommon.getMapZK().get(taskName);
					entity = MultiRunCommon.getMapEntity().get(taskName);
					
					String taskPath = String.format("%s/%s", rootPath, taskName);			
					String childNode = String.format("%s_%s", taskName, CommonFunc.getLocalMac());
					String childPath = String.format("%s/%s", taskPath, childNode);
					List<String> childList = getNodeList(taskName, taskPath, map_ChildWatcher.get(taskName));
					
					int originalLen = entity.getOriginalNodeCnt();
					if(childList.size() != originalLen){
						if(childList != null && childList.size() > 0){
							int currLen = childList.size();
							int index = getNodeIndex(taskName, taskPath, childNode, map_ChildWatcher.get(taskName));
							System.out.println(String.format("任务：【%s】  节点数量发生变化，当前节点列表为：%s", taskName, childList.toString()));
							//使用对象中的curr覆盖old
							entity.setOriginalValue(entity.getCurrNodeCnt(), entity.getCurrIndex());
							//使用新获取到的数据覆盖curr
							entity.setCurrValue(currLen, index, true);
							entity.setLastNodeList(childList);
							//当发现有新增节点时，置该节点的value为 1 ：表示通知新增几点其他节点还在处理数据中，处理完成之前不能开始进行数据拆分工作
							if(currLen > originalLen && originalLen > 0){
								logger.info(String.format("任务：【%s】  发现新增节点，修改当前实例临时节点的值为1：%s", taskName, childPath));
								zk.setData(childPath, "1".getBytes(), -1);
							}
							if(currLen < originalLen){
								logger.info(String.format("任务：【%s】  发现节点消失，修改当前实例临时节点的值为1：%s", taskName, childPath));
								zk.setData(childPath, "1".getBytes(), -1);
							}
						}else{
							logger.info(String.format("任务：【%s】  未获取到节点列表", taskName));
						}
					}					
					entity.setInException(false);
					return;
				} catch (Exception e) {
					logger.error(e);
					entity.setInException(true);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						logger.error(e1);
					}
				}
				loopIndex++;
			}
		}
	}

	/**
	 * 当任务被强制终止成功后，需要将节点移除
	 * @param name
	 */
	public static void removeChildNode(String taskName) {
		TimerRunEntity entity = MultiRunCommon.getMapEntity().get(taskName);
		int index = 0;
		while(index < 3){
			try {
				ZooKeeper zk = MultiRunCommon.getMapZK().get(taskName);
				String childNode = String.format("%s/%s_%s", 
						rootPath + "/" + taskName, taskName, CommonFunc.getLocalMac());
				zk.delete(childNode, -1);
				entity.setInException(false);
				return;
			} catch (Exception e) {
				entity.setInException(true);
				logger.error(e);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					logger.error(e1);
				}
			}
			index++;
		}
	}
}
