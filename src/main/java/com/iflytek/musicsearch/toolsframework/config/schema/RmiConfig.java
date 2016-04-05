package com.iflytek.musicsearch.toolsframework.config.schema;

import org.springframework.beans.factory.InitializingBean;

import com.iflytek.musicsearch.toolsframework.jmx.MBeanContext;


public class RmiConfig implements InitializingBean {
	
	private String id;
	private int port;

	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		MBeanContext.setPortNum(port);
		MBeanContext.init();
	}
	
}
