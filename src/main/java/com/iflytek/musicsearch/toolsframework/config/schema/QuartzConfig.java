package com.iflytek.musicsearch.toolsframework.config.schema;

import org.springframework.beans.factory.InitializingBean;

import com.iflytek.musicsearch.toolsframework.policy.ScheduleContext;


public class QuartzConfig implements InitializingBean {
	
	private String id;
	private String workerNum;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWorkerNum() {
		return workerNum;
	}

	public void setWorkerNum(String workerNum) {
		this.workerNum = workerNum;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ScheduleContext.setQuartzConfig(this);
		//ScheduleContext.init();
	}
	
}
