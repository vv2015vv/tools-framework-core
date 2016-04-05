package com.iflytek.musicsearch.toolsframework.config.schema;

import org.springframework.beans.factory.InitializingBean;

import com.iflytek.musicsearch.toolsframework.common.ToolsFrameWorkContext;
import com.iflytek.musicsearch.toolsframework.config.TaskContext;

public class SingleConfig extends BaseSchema implements InitializingBean {

	private String cron;
	
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ToolsFrameWorkContext.getListContext().add(new TaskContext(this.getClass(), this));
	}
	
}
