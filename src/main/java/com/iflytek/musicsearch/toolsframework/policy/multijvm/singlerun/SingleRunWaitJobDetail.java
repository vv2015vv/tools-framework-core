package com.iflytek.musicsearch.toolsframework.policy.multijvm.singlerun;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.iflytek.musicsearch.toolsframework.config.TaskContext;
import com.iflytek.musicsearch.toolsframework.config.schema.SingleConfig;
import com.iflytek.musicsearch.toolsframework.policy.multijvm.TimerRunEntity;
import com.iflytek.musicsearch.toolsframework.policy.multijvm.multirun.MultiRunCommon;
import com.iflytek.musicsearch.toolsframework.zookeeper.ZK_SingleRun;

public class SingleRunWaitJobDetail implements Job {
	
	private static Logger logger = Logger.getLogger(SingleRunWaitJobDetail.class);

	@Override
	public void execute(JobExecutionContext arg) throws JobExecutionException {
		String taskName = "";
		TimerRunEntity entity = null;
		TaskContext context = null;
		try{
			JobDataMap dataMap = arg.getJobDetail().getJobDataMap();
			context = (TaskContext)dataMap.get("JobContext");
			taskName = ((SingleConfig)context.getTaskConf()).getTaskname();
			
			//判断任务是否正在运行（在多个worker情况下，可能会有多个worker重复来运行，所以需要运行前检查任务的运行状态）
			entity = MultiRunCommon.getMapEntity().get(taskName);
			if(context.isTaskStart()){
				logger.info(String.format("任务：【%s】 正在运行过程中...", taskName));
			}else{
				logger.info(String.format("任务：【%s】 开始运行...", taskName));
				context.setTaskStart(true);

				if(ZK_SingleRun.getLock(taskName)){
					entity.getRunnable().run();
				}else{
					logger.info(String.format("任务：【%s】 获取zk分布式锁失败...", taskName));
				}
				
				//任务运行结束
				context.setTaskStart(false);
				logger.info(String.format("任务：【%s】 本次运行结束...", taskName));
			}
		}catch(Exception e){			
			if(entity != null){
				context.setTaskStart(false);
			}
			// 遇到异常
			logger.error(String.format("任务：【%s】 本次运行出现异常...", taskName));
			logger.error(e);
			// 制造一个异常，让调度器知道，这样写有的噁心，暂时没有想到其他办法。
			String s = null;
			s.toString();
		}
	}
}
