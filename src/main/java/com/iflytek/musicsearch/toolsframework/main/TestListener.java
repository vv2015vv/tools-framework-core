package com.iflytek.musicsearch.toolsframework.main;

import javax.management.Notification;
import javax.management.NotificationListener;

public class TestListener implements NotificationListener {

	@Override
	public void handleNotification(Notification n, Object handback) {
		// TODO Auto-generated method stub
		System.out.println("【jmx notification start】");
		System.out.println("\ttype=" + n.getType());
		System.out.println("\tsource=" + n.getSource());
		System.out.println("\tseq=" + n.getSequenceNumber());
		System.out.println("\tsend time=" + n.getTimeStamp());
		System.out.println("\tmessage=" + n.getMessage());
		System.out.println("【jmx notification end】");
	}

}
