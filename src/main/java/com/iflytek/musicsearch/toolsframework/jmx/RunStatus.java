package com.iflytek.musicsearch.toolsframework.jmx;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

import com.iflytek.musicsearch.toolsframework.config.TaskContext;

public class RunStatus extends NotificationBroadcasterSupport implements RunStatusMBean {

	private String taskName;
	private TaskContext context;
	private int seq = 0;
	
	public RunStatus() { }
	
	public RunStatus(String taskName, TaskContext context){
		this.taskName = taskName;
		this.context = context;
	}

	@Override
	public String getTaskName() {
		return this.taskName;
	}
	
	@Override
	public boolean isTaskRunning() {
		return this.context.isTaskStart();
	}
	
	@Override
	public boolean isTaskSchedule() {
		return this.context.isScheduleStart();
	}
	
	@Override
	public void stopRunning(){
		context.getRunPolicy().stop();
		Notification n = new Notification(this.getClass().getName(), this, 
				seq++, System.currentTimeMillis(), this.taskName + " stopped...");
		super.sendNotification(n);
	}
	
	@Override
	public void startRunning(){
		context.getRunPolicy().start();
		Notification n = new Notification(this.getClass().getName(), this, 
				seq++, System.currentTimeMillis(), this.taskName + " started...");
		super.sendNotification(n);
	}

}
