package com.iflytek.musicsearch.toolsframework.common;

import java.util.ArrayList;
import java.util.List;

import com.iflytek.musicsearch.toolsframework.config.TaskContext;

public class ToolsFrameWorkContext {
	
	private static List<TaskContext> listContext = new ArrayList<TaskContext>();
	
	public static List<TaskContext> getListContext() {
		return listContext;
	}

	public static void setListContext(List<TaskContext> listContext) {
		ToolsFrameWorkContext.listContext = listContext;
	}

	public static void start(){
		for(TaskContext context : listContext){
			Thread t = new Thread(context.getRunPolicy());			
			t.run();
		}
	}	
}
