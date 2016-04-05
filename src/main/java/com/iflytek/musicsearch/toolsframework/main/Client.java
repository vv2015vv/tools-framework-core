package com.iflytek.musicsearch.toolsframework.main;

import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.MemoryMXBean;
import java.util.Iterator;
import java.util.Set;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class Client {

	public static void main(String[] args) {
		
		try {
			//JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://192.168.58.53:8889/toolserver");
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:8899/toolserver");
	        JMXConnector conn = JMXConnectorFactory.connect(url, null);  
	        MBeanServerConnection mbsc = conn.getMBeanServerConnection();  
	        
	        System.out.println("Domains:---------------");  
	        String domains[] = mbsc.getDomains();  
	        for (int i = 0; i < domains.length; i++) {         
	            System.out.println("\tDomain[" + i +"] = " + domains[i]);
	            Set<ObjectInstance> set = mbsc.queryMBeans(new ObjectName(domains[i] + ":*"), null);
	            for (Iterator<ObjectInstance> it = set.iterator(); it.hasNext();) {
		            ObjectInstance oi = (ObjectInstance)it.next();
		            System.out.println("\t\t" + oi.getObjectName());
		            if(domains[i].equals("com.iflytek.ms50")){
				        mbsc.addNotificationListener(oi.getObjectName(), new TestListener(), null, 2);
		            }
//		            RunStatusMBean mbean = JMX.newMBeanProxy(mbsc, oi.getObjectName(), RunStatusMBean.class);
//		            mbean.stopRunning();
//		            Thread.sleep(2000);
//		            mbean.startRunning();
//		            Thread.sleep(2000);
		        }
	        }
	        System.out.println("");
	        
	        //1、获取 java.lang 中的信息
	        //1.1 classloader
	        ClassLoadingMXBean cBean = JMX.newMXBeanProxy(mbsc, new ObjectName("java.lang:type=ClassLoading"), ClassLoadingMXBean.class);
	        System.out.println("Loaded Class Count: " + cBean.getLoadedClassCount());
	        
	        //1.2 memory
	        MemoryMXBean mBean = JMX.newMXBeanProxy(mbsc, new ObjectName("java.lang:type=Memory"), MemoryMXBean.class);
	        System.out.println("Memory Init: " + mBean.getHeapMemoryUsage().getInit());
	        System.out.println("Memory Commited: " + mBean.getHeapMemoryUsage().getCommitted());
	        System.out.println("Memory Max: " + mBean.getHeapMemoryUsage().getMax());
	        System.out.println("Memory Used: " + mBean.getHeapMemoryUsage().getUsed());
	        
	        
	        
	        
	        //conn.close(); 
	        
		} catch (Exception e) {
			e.printStackTrace();
		}	
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
