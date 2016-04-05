package com.iflytek.musicsearch.toolsframework.config.schema;

import org.springframework.beans.factory.InitializingBean;

import com.iflytek.musicsearch.toolsframework.zookeeper.ZK_Base;

public class ZKConfig implements InitializingBean {

	private String id;
	private String url;
	private String root;
	private int timeout;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		ZK_Base.setZkConfig(this);
		ZK_Base.init();
	}
}
