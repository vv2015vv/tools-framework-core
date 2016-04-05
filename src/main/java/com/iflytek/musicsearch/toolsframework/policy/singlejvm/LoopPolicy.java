package com.iflytek.musicsearch.toolsframework.policy.singlejvm;

import com.iflytek.musicsearch.toolsframework.config.TaskContext;
import com.iflytek.musicsearch.toolsframework.config.schema.LoopConfig;
import com.iflytek.musicsearch.toolsframework.policy.RunPolicy;


/**
 * 循环执行策略实现
 * @author rlyu
 *
 */
public class LoopPolicy implements RunPolicy {
	
	//循环次数
	private int loopNum = 0;
	private TaskContext taskContext;
	
	@Override
	public void init(TaskContext taskContext) {
		this.taskContext = taskContext;
		this.loopNum = ((LoopConfig)taskContext.getTaskConf()).getLoopnum();
	}
	
	public void run() {
		for (int i = 0; i < loopNum; i++) {
			System.out.println("loop times: " + i);
			this.taskContext.getbRun().run();
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

}
