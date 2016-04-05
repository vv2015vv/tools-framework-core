package com.iflytek.musicsearch.toolsframework.policy;

import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.iflytek.musicsearch.toolsframework.config.schema.QuartzConfig;

public class ScheduleContext {
	
	private static QuartzConfig quartzConfig;
	private static SchedulerFactory sf; 
	private static Scheduler scheduler;
	
	public static Scheduler getScheduler() {
		return scheduler;
	}
	public static void setScheduler(Scheduler scheduler) {
		ScheduleContext.scheduler = scheduler;
	}
	public static void setQuartzConfig(QuartzConfig q) {
		quartzConfig = q;
	}

	private static boolean hasInited;
	
	public static boolean isHasInited() {
		return hasInited;
	}
	public static void setHasInited(boolean flag) {
		hasInited = flag;
	}
	
	public static void init(){
		try {
			Properties p = new Properties();
			p.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
			p.setProperty("org.quartz.threadPool.threadCount", quartzConfig.getWorkerNum());
			sf = new StdSchedulerFactory(p);
			scheduler = sf.getScheduler();
			scheduler.start();
			hasInited = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
