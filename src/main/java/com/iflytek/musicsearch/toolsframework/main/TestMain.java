package com.iflytek.musicsearch.toolsframework.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.iflytek.musicsearch.toolsframework.common.ToolsFrameWorkContext;

public class TestMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ClassPathXmlApplicationContext context = 
				new ClassPathXmlApplicationContext("applicationContext-tfc.xml");
		context.start();
		System.out.println("****************************************");
		System.out.println("启动结束，任务开始执行！！！");
		System.out.println("****************************************");
		
		ToolsFrameWorkContext.start();
		
	}

}
