package com.iflytek.musicsearch.toolsframework.spi;

import java.util.List;

import org.apache.log4j.Logger;

public class Demo_TimerSingleRun implements BusinessRun<Object> {

	Logger logger = Logger.getLogger(SmsDemoBusinessRun.class);
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		logger.info("哈哈");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("哈哈哈哈哈");
	}

	@Override
	public List<Object> divide(int index, int len) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(Object item) {
		// TODO Auto-generated method stub
		
	}

}
