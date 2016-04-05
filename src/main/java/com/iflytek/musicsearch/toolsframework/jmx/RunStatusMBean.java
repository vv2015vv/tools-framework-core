package com.iflytek.musicsearch.toolsframework.jmx;

public interface RunStatusMBean {
	public String getTaskName();
	public boolean isTaskRunning();
	public boolean isTaskSchedule();
	public void stopRunning();
	public void startRunning();
}
