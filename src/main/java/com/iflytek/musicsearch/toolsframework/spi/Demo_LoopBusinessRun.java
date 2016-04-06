package com.iflytek.musicsearch.toolsframework.spi;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Demo_LoopBusinessRun implements BusinessRun<String>{
	
	Logger logger = Logger.getLogger(Demo_LoopBusinessRun.class);
	
	private String[] arr = new String[]{"1", "2", "3", "4"};

	public void init() {
		logger.info("开始初始化loop业务样例");
		
	}

	

	public void run(){
		System.out.println("run");
	}



	@Override
	public void execute(String item) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public List<String> divide(int index, int len) {
		System.out.println(String.format("index: %d  len: %d", index, len));
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
