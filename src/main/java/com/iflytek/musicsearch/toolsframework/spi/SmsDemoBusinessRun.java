package com.iflytek.musicsearch.toolsframework.spi;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class SmsDemoBusinessRun implements BusinessRun{
	
	Logger logger = Logger.getLogger(SmsDemoBusinessRun.class);
	
	private String[] arr = new String[]{"1", "2", "3", "4"};

	public void init() {
		logger.info("开始初始化SMS业务样例");
		
	}

	

	public void run(){
//		logger.info(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")+"开始运行SMS业务样例");
//		new File("sss").delete();
//		try {
//			//Thread.sleep(60000L); //设置等待0.6S
////			logger.info(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")+"结束运行SMS业务样例");
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		System.out.println("run");
		
	}



	@Override
	public void execute(String taskid) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public List<String> divide(int index, int len) {
		//System.out.println(String.format("index: %d  len: %d", index, len));
		List<String> list = new ArrayList<String>();
		for(String s : arr){
			int num = Integer.parseInt(s);
			if(num % len == index){
				list.add(s);
			}
		}
		return list;
	}


}
