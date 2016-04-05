package com.iflytek.musicsearch.toolsframework.zookeeper.watcher;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * 创建连接时的监听器
 * @author weiluo
 *
 */
public class ConnectedWatcher implements Watcher {
	
	private static Logger logger = Logger.getLogger(ConnectedWatcher.class);
	 
    private CountDownLatch connectedLatch;

    public ConnectedWatcher(CountDownLatch connectedLatch) {
        this.connectedLatch = connectedLatch;
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getState() == KeeperState.SyncConnected) {
        	logger.info("zk已连接上");
            connectedLatch.countDown();
        }else if (event.getState() == KeeperState.Disconnected) {
        	logger.info("*************************zk链接已断开*************************");
        }
    }
}
