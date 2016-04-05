package com.iflytek.musicsearch.toolsframework.config;

import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.iflytek.musicsearch.toolsframework.config.schema.BaseSchema;
import com.iflytek.musicsearch.toolsframework.config.schema.MultiConfig;
import com.iflytek.musicsearch.toolsframework.config.schema.SingleConfig;
import com.iflytek.musicsearch.toolsframework.jmx.MBeanContext;
import com.iflytek.musicsearch.toolsframework.jmx.RunStatus;
import com.iflytek.musicsearch.toolsframework.policy.RunPolicy;
import com.iflytek.musicsearch.toolsframework.policy.multijvm.multirun.MultiRunPolicy;
import com.iflytek.musicsearch.toolsframework.policy.multijvm.singlerun.SingleRunPolicy;
import com.iflytek.musicsearch.toolsframework.policy.singlejvm.LoopPolicy;
import com.iflytek.musicsearch.toolsframework.spi.BusinessRun;

public class TaskContext {

	private static Logger logger = Logger.getLogger(TaskContext.class);
	
	private boolean scheduleStart;
	private boolean taskStart;
	private Object taskConf;
	private RunPolicy runPolicy;
	public RunPolicy getRunPolicy() {
		return runPolicy;
	}

	public void setRunPolicy(RunPolicy runPolicy) {
		this.runPolicy = runPolicy;
	}

	private BusinessRun bRun;	
	
	public TaskContext(){
	}
	
	public TaskContext(Class<?> schemaClass, Object obj){
		try {
			this.setTaskConf(obj);
			BaseSchema schema = (BaseSchema)obj;
			this.bRun = (BusinessRun)Class.forName(schema.getHandle()).newInstance();
			if(MultiConfig.class.equals(schemaClass)){
				runPolicy = MultiRunPolicy.class.newInstance();
			}else if(SingleConfig.class.equals(schemaClass)){
				runPolicy = SingleRunPolicy.class.newInstance();
			}else{
				runPolicy = LoopPolicy.class.newInstance();
			}
			
			this.runPolicy.init(this);
			
			RunStatus mbean = new RunStatus(schema.getTaskname(), this);
			ObjectName objName = new ObjectName("com.iflytek.ms50:name=" + this.bRun.getClass().getSimpleName());
			MBeanContext.registerMBean(mbean, objName);
			logger.info(String.format("注册mbean：%s 成功！", objName));
			
			if(schema.isExeconstart()){
				logger.info(String.format("任务【%s】配置为启动执行，开始运行...", schema.getTaskname()));
				this.bRun.run();
				logger.info(String.format("任务【%s】启动执行结束...", schema.getTaskname()));
			}
			
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public boolean isScheduleStart() {
		return scheduleStart;
	}
	public void setScheduleStart(boolean scheduleStart) {
		this.scheduleStart = scheduleStart;
	}
	public boolean isTaskStart() {
		return taskStart;
	}
	public void setTaskStart(boolean taskStart) {
		this.taskStart = taskStart;
	}
	public BusinessRun getbRun() {
		return bRun;
	}
	public void setbRun(BusinessRun bRun) {
		this.bRun = bRun;
	}

	public Object getTaskConf() {
		return taskConf;
	}

	public void setTaskConf(Object taskConf) {
		this.taskConf = taskConf;
	}
	
}
