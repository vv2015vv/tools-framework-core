package com.iflytek.musicsearch.toolsframework.config.schema;

import org.springframework.beans.factory.InitializingBean;

import com.iflytek.musicsearch.toolsframework.common.ToolsFrameWorkContext;
import com.iflytek.musicsearch.toolsframework.config.TaskContext;

public class LoopConfig extends BaseSchema implements InitializingBean {
	
	private int loopnum;

	public int getLoopnum() {
		return loopnum;
	}
	public void setLoopnum(int loopnum) {
		this.loopnum = loopnum;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		ToolsFrameWorkContext.getListContext().add(new TaskContext(this.getClass(), this));
	}
	
}
