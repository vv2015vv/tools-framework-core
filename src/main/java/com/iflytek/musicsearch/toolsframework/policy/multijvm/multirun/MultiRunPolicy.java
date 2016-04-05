package com.iflytek.musicsearch.toolsframework.policy.multijvm.multirun;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

import com.iflytek.musicsearch.toolsframework.config.TaskContext;
import com.iflytek.musicsearch.toolsframework.config.schema.MultiConfig;
import com.iflytek.musicsearch.toolsframework.policy.RunPolicy;
import com.iflytek.musicsearch.toolsframework.policy.ScheduleContext;
import com.iflytek.musicsearch.toolsframework.policy.multijvm.TimerRunEntity;
import com.iflytek.musicsearch.toolsframework.zookeeper.ZK_MultiRun;

/**
 * 多实例数据分片定时器策略实现。
 * @author weiluo
 *
 */
public class MultiRunPolicy implements RunPolicy {

	private static Logger logger = Logger.getLogger(MultiRunPolicy.class);
	private TaskContext taskContext;
	private MultiConfig taskConf;
	private String taskName;
	
	private JobDetail jobDetail;
	private CronTrigger trigger;
	
	@Override
	public void init(TaskContext taskContext) {
		try {
			this.taskContext = taskContext;
			this.taskContext.getbRun().init();
			this.taskConf = (MultiConfig)taskContext.getTaskConf();
			this.taskName = this.taskConf.getTaskname();
			initSchedule();
			
			logger.info("增加调度时间串设置。。。");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	public void stop() {
		try {
			//因为全局只有一个默认的 Schdule，所以如果在此 shutdown 的话会影响到所有的任务
			//最简单的办法就是deletejob了
			//也可以考虑为每一个taskName创建一个Schedule，不过好像没有必要
			if(taskContext.isScheduleStart()){
				ScheduleContext.getScheduler().deleteJob(this.taskName, this.taskName);
				ZK_MultiRun.removeChildNode(this.taskName);
				taskContext.setScheduleStart(false);
				logger.info(String.format("任务【%s】已被终止运行。。。", this.taskName));
			}else{
				logger.info(String.format("任务【%s】处于停止状态中，不能再次停止。。。", this.taskName));
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		try {
			if(!taskContext.isScheduleStart()){
				initSchedule();
				run();
			}else{
				logger.info(String.format("任务【%s】正处于被调度中，不能再次启动。。。", this.taskName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始调度
	 * @throws Exception
	 */
	private void initSchedule() throws Exception{
		//1、开始调度
		if(!ScheduleContext.isHasInited()){
			ScheduleContext.init();
		}
		//2、初始化任务状态信息
		if(!MultiRunCommon.getMapEntity().containsKey(this.taskName)){
			TimerRunEntity entity = new TimerRunEntity();
			entity.setRunnable(taskContext.getbRun());
			entity.setThreadCnt(this.taskConf.getThreadcnt());
			entity.setTaskName(this.taskName);
			MultiRunCommon.getMapEntity().put(this.taskName, entity);
		}
		//3、创建zk的路径信息、初始化处理线程
		ZK_MultiRun.createTaskPath(this.taskName);
		ExecutorService executor = Executors.newFixedThreadPool(this.taskConf.getThreadcnt());
		MultiRunCommon.getMapExecutorService().put(this.taskName, executor);
		//4、设置job参数信息和调度器
		jobDetail = new JobDetail(this.taskName, this.taskName, MultiRunJobDetail.class);		
		jobDetail.getJobDataMap().put("JobContext", taskContext);
		trigger = new CronTrigger(this.taskName, this.taskName);	
		trigger.setCronExpression(this.taskConf.getCron());
		//ScheduleContext.getScheduler().scheduleJob(jobDetail, trigger);
		taskContext.setScheduleStart(true);
	}
	
	public void run() {
		try {
			ScheduleContext.getScheduler().scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
}
