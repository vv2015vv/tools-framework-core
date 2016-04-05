package com.iflytek.musicsearch.toolsframework.policy.multijvm.singlerun;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

import com.iflytek.musicsearch.toolsframework.config.TaskContext;
import com.iflytek.musicsearch.toolsframework.config.schema.SingleConfig;
import com.iflytek.musicsearch.toolsframework.policy.RunPolicy;
import com.iflytek.musicsearch.toolsframework.policy.ScheduleContext;
import com.iflytek.musicsearch.toolsframework.policy.multijvm.TimerRunEntity;
import com.iflytek.musicsearch.toolsframework.policy.multijvm.multirun.MultiRunCommon;
import com.iflytek.musicsearch.toolsframework.zookeeper.ZK_SingleRun;

/**
 * 多实例数据分片定时器策略实现。
 * @author weiluo
 *
 */
public class SingleRunPolicy implements RunPolicy {

	private static Logger logger = Logger.getLogger(SingleRunPolicy.class);
	private TaskContext taskContext;
	private SingleConfig taskConf;
	private String taskName;
	
	private JobDetail jobDetail;
	private CronTrigger trigger;
	
	@Override
	public void init(TaskContext taskContext) {
		try {
			this.taskContext = taskContext;
			this.taskContext.getbRun().init();
			this.taskConf = (SingleConfig)taskContext.getTaskConf();
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
			//使用deletejob而不是shutdown的原因，参见TimerMultiRunPolicy中该方法的注释
			if(taskContext.isScheduleStart()){
				ScheduleContext.getScheduler().deleteJob(this.taskName, this.taskName);
				ZK_SingleRun.removeNode(this.taskName);
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
			entity.setTaskName(this.taskName);
			MultiRunCommon.getMapEntity().put(this.taskName, entity);
		}
		//3、创建zk的路径信息
		ZK_SingleRun.createPath(this.taskName);
		//4、设置job参数信息和调度器
		jobDetail = new JobDetail(this.taskName, this.taskName, SingleRunWaitJobDetail.class);	
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
