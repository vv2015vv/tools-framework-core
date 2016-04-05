package com.iflytek.musicsearch.toolsframework.zookeeper;

import org.apache.log4j.Logger;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

import com.iflytek.musicsearch.toolsframework.policy.multijvm.TimerRunEntity;
import com.iflytek.musicsearch.toolsframework.policy.multijvm.multirun.MultiRunCommon;

/**
 * 目前是通过zk操作的异常次数来决定是否暂停任务（目前这种处理方式，如果异常的时间大于配置的会话超时时间，那么路径需要重建，这个步骤暂时没有）
 * 后续考虑使用curator客户端来完成zk的各种操作
 * 并且使用curator自带的zk状态监控
 * @author weiluo
 *
 */
public class ZK_StatusCheck implements Runnable {

	private static Logger logger = Logger.getLogger(ZK_StatusCheck.class);
	private String taskName;

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public ZK_StatusCheck(String taskName){
		this.taskName = taskName;
	}

	@Override
	public void run() {
		try {
			TimerRunEntity entity = MultiRunCommon.getMapEntity().get(taskName);
			ZooKeeper zk = MultiRunCommon.getMapZK().get(this.taskName);
			while(true){
				if(zk.getState() == States.CONNECTED){
					entity.setInException(false);
					//为防止因之前的异常而导致当前组件在新一轮循环时使用了错误的len和index
					//在恢复链接之后，需要触发获取节点列表的方法
					ZK_MultiRun.checkChildNodes(taskName);
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error(e);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
