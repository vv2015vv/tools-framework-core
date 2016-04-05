package com.iflytek.musicsearch.toolsframework.zookeeper.watcher;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

import com.iflytek.musicsearch.toolsframework.zookeeper.ZK_MultiRun;

/**
 * 任务子节点列表变更后的监听器
 * @author weiluo
 *
 */
public class ChildNodeChangedWatcher implements Watcher {
	
	private static Logger logger = Logger.getLogger(ChildNodeChangedWatcher.class);
	 
	private String taskName;
	 
	public ChildNodeChangedWatcher(String taskName) {
        this.taskName = taskName;
    }
	
    @Override
    public void process(WatchedEvent event) {
    	logger.info("已经触发了" + event.getType() + "事件, path为：" + event.getPath());
	       if(event.getType() != EventType.None){
	    	   ZK_MultiRun.checkChildNodes(taskName);
	       }
    }
}
