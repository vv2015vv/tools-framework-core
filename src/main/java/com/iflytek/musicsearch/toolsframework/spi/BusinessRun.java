package com.iflytek.musicsearch.toolsframework.spi;

import java.util.List;

/**
 * 业务接口，根据业务场景实现具体的业务逻辑。
 * @author rlyu
 *
 */
public interface BusinessRun extends Runnable{
	/**
	 * 业务实现初始化，
	 * @param config
	 */
	public void init();
	
	/**
	 * 需要在特定策略下执行的业务逻辑。
	 */
	public void run();
	
	/**
	 * 任务拆分
	 * @param index 根据zk节点的索引位置进行拆分
	 * @param len zk中节点的总数
	 */
	public List<String> divide(int index, int len);
	
	/**
	 * 拆分任务后，多线程调用的方法
	 * @param taskid
	 */
	public void execute(String taskid);
}
