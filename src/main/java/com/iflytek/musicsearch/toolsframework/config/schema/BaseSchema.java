package com.iflytek.musicsearch.toolsframework.config.schema;

public class BaseSchema {

	private String id;
	private String taskname;
	private String handle;
	private boolean execonstart;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTaskname() {
		return taskname;
	}
	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public boolean isExeconstart() {
		return execonstart;
	}
	public void setExeconstart(boolean execonstart) {
		this.execonstart = execonstart;
	}
	
}
