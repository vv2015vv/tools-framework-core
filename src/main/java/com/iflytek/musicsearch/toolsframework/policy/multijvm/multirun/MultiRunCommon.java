package com.iflytek.musicsearch.toolsframework.policy.multijvm.multirun;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.zookeeper.ZooKeeper;

import com.iflytek.musicsearch.toolsframework.policy.multijvm.TimerRunEntity;



public class MultiRunCommon {
	
	private static Map<String, TimerRunEntity> mapEntity = new HashMap<String, TimerRunEntity>();
	
	private static Map<String, ZooKeeper> mapZK = new HashMap<String, ZooKeeper>();
	
	private static Map<String, ExecutorService> mapExecutorService = new HashMap<String, ExecutorService>();
	
	/**
	 * 任务名称与对应的实体信息的对应关系
	 */
	public static Map<String, TimerRunEntity> getMapEntity() {
		return mapEntity;
	}

	public static void setMapEntity(Map<String, TimerRunEntity> mapEntity) {
		MultiRunCommon.mapEntity = mapEntity;
	}
	
	/**
	 * 任务名称与对应的zk子节点对应关系
	 */
	public static Map<String, ZooKeeper> getMapZK() {
		return mapZK;
	}

	public static void setMapZK(Map<String, ZooKeeper> mapZK) {
		MultiRunCommon.mapZK = mapZK;
	}

	/**
	 * 任务名称与线程池的对应关系
	 */
	public static Map<String, ExecutorService> getMapExecutorService() {
		return mapExecutorService;
	}

	public static void setMapExecutorService(Map<String, ExecutorService> mapExecutorService) {
		MultiRunCommon.mapExecutorService = mapExecutorService;
	}
	
}
