package com.iflytek.musicsearch.toolsframework.policy.multijvm;

import java.util.ArrayList;
import java.util.List;

import com.iflytek.musicsearch.toolsframework.spi.BusinessRun;
import com.iflytek.musicsearch.toolsframework.zookeeper.ZK_StatusCheck;

public class TimerRunEntity {
	
	/**
	 * 任务对象
	 */
	private BusinessRun runnable;
	
	/**
	 * 任务名称
	 */
	private String taskName;
	
	public BusinessRun getRunnable() {
		return runnable;
	}

	public void setRunnable(BusinessRun runnable) {
		this.runnable = runnable;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	/**
	 * 处理该任务需要的线程数
	 */
	private int threadCnt;
	
	/**
	 * 第一次启动时，检查是否有其他节点已存在，如果有的话，当前实例需要延迟执行
	 */
	private Boolean needDelayExec = false;
	
	/**
	 * 第一次启动延迟执行后，判断其他节点是否已经执行完成上一个周期，若未执行完，则继续等待
	 */
	private Boolean needDelayAndCheckCurrCanRun = false;
	
	/**
	 * 第一次启动时，如果其他节点已存在，需要延迟执行，延迟后第一个周期需要判断其他节点实例是否已经把上一个周期的数据处理完成
	 * 如果其他节点实例未处理完成，当前实例需要继续等待，直到其他节点设置了完成的标志方可参与集群一起进行数据分片
	 */
	private Boolean needWaitForOtherNodeFinish = false;
	
	/**
	 * zk下子节点节点发生变更前的节点数量
	 */
	private int originalNodeCnt = 0;
	
	/**
	 * zk下子节点发生变更前当前实例节点所在位置
	 */
	private int originalIndex = 0;
	
	/**
	 * zk下子节点变化后节点数量
	 */
	private int currNodeCnt = 0;
	
	/**
	 * zk下子节点变化后当前实例节点所在位置
	 */
	private int currIndex = 0;
	
	/**
	 * zk中是否有节点发生变化
	 */
	private Boolean hasChanged = false;
	
	/**
	 * 当hasChanged为true时，第一次循环过后，该变量被置为true
	 * 下一次循环时，判断该值为true，开始以 currNodeCnt 来拆分数据
	 */
	private Boolean needReDivide = false;
	
	//private Boolean runStatus = false;
	
	private List<String> lastNodeList = new ArrayList<String>();
	
	/**
	 * 标志zk操作是否出现了异常
	 * 当一个周期开始时，如果该标志为true，则该次循环结束
	 * 
	 */
	private boolean inException;

	public Boolean getHasChanged() {
		return hasChanged;
	}
	
	public void setHasChanged(Boolean changed) {
		hasChanged = changed;
	}

	public int getOriginalNodeCnt() {
		return originalNodeCnt;
	}
	
	public int getCurrNodeCnt() {
		return currNodeCnt;
	}
	
	public Boolean getNeedReDivide() {
		return needReDivide;
	}

	public void setNeedReDivide(Boolean needReDivide) {
		this.needReDivide = needReDivide;
	}

	public int getThreadCnt() {
		return threadCnt;
	}

	public void setThreadCnt(int threadCnt) {
		this.threadCnt = threadCnt;
	}

	public void setOriginalNodeCnt(int originalNodeCnt) {
		this.originalNodeCnt = originalNodeCnt;
	}

	public void setOriginalIndex(int originalIndex) {
		this.originalIndex = originalIndex;
	}

	public void setCurrNodeCnt(int currNodeCnt) {
		this.currNodeCnt = currNodeCnt;
	}

	public void setCurrIndex(int currIndex) {
		this.currIndex = currIndex;
	}

	public int getOriginalIndex() {
		return originalIndex;
	}

	public int getCurrIndex() {
		return currIndex;
	}
	
	public Boolean getNeedDelayExec() {
		return needDelayExec;
	}

	public void setNeedDelayExec(Boolean needDelayExec) {
		this.needDelayExec = needDelayExec;
	}
	
	public void setValue(Boolean changed, int originalLen, int originalIdx, int currLen, int currIdx){
		hasChanged = changed;
		originalNodeCnt = originalLen;
		currNodeCnt = currLen;
		originalIndex = originalIdx;
		currIndex = currIdx;
	}
	
	public void setOriginalValue(int originalLen, int originalIdx){
		originalNodeCnt = originalLen;
		originalIndex = originalIdx;
	}
	
	public void setCurrValue(int currLen, int currIdx, Boolean changed){
		currNodeCnt = currLen;
		currIndex = currIdx;
		hasChanged = changed;
	}

	public Boolean getNeedWaitForOtherNodeFinish() {
		return needWaitForOtherNodeFinish;
	}

	public void setNeedWaitForOtherNodeFinish(
			Boolean needWaitForOtherNodeFinish) {
		this.needWaitForOtherNodeFinish = needWaitForOtherNodeFinish;
	}
	
	public TimerRunEntity(){}
	
	public TimerRunEntity(BusinessRun run, String taskName, int threadCnt){
		this.runnable = run;
		this.taskName = taskName;
		this.threadCnt = threadCnt;
	}

	public List<String> getLastNodeList() {
		return lastNodeList;
	}

	public void setLastNodeList(List<String> lastNodeList) {
		this.lastNodeList = lastNodeList;
	}

	public Boolean getNeedDelayAndCheckCurrCanRun() {
		return needDelayAndCheckCurrCanRun;
	}

	public void setNeedDelayAndCheckCurrCanRun(
			Boolean needDelayAndCheckCurrCanRun) {
		this.needDelayAndCheckCurrCanRun = needDelayAndCheckCurrCanRun;
	}

	public boolean isInException() {
		return inException;
	}

	public void setInException(boolean inException) {
		this.inException = inException;
		if(this.inException){
			Thread th = new Thread(new ZK_StatusCheck(this.taskName));
			th.start();
		}
	}

}
