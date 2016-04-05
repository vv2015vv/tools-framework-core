package com.iflytek.musicsearch.toolsframework.policy;

import com.iflytek.musicsearch.toolsframework.config.TaskContext;



/**
 * 
 * @author Administrator
 *
 */
public interface RunPolicy extends Runnable{

	/**
	 * 初始化子进程
	 * @param taskContext
	 */
	public void init(TaskContext taskContext);
	
	/**
	 * 启动子进程
	 * @param context
	 * @param config
	 */
	public void run();
	
	/**
	 * 关闭调度
	 */
	public void stop();
	
	/**
	 * 恢复调度
	 */
	public void start();
	
}
