package com.iflytek.musicsearch.toolsframework.config.schema;

import org.springframework.beans.factory.InitializingBean;

import com.iflytek.musicsearch.toolsframework.common.ToolsFrameWorkContext;
import com.iflytek.musicsearch.toolsframework.config.TaskContext;

public class MultiConfig extends BaseSchema implements InitializingBean {

	private String cron;
	private int threadcnt;
	
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	public int getThreadcnt() {
		return threadcnt;
	}
	public void setThreadcnt(int threadcnt) {
		this.threadcnt = threadcnt;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		ToolsFrameWorkContext.getListContext().add(new TaskContext(this.getClass(), this));
	}
	
}
