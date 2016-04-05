package com.iflytek.musicsearch.toolsframework.jmx;

import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

public class MBeanContext {
	
	private static int portNum;
	private static MBeanServer mbserver;
	private static boolean hasInit = false;
	
	public static void setPortNum(int portNum) {
		MBeanContext.portNum = portNum;
	}
	
	public static void init(){
		try {
			mbserver = ManagementFactory.getPlatformMBeanServer();
			LocateRegistry.createRegistry(portNum);
	        JMXConnectorServer cserver = JMXConnectorServerFactory.newJMXConnectorServer(new JMXServiceURL(  
	                        "service:jmx:rmi:///jndi/rmi://localhost:" + portNum + "/toolserver"),  
	                        null, mbserver);
	        cserver.start();
	        hasInit = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void registerMBean(RunStatus mbean, ObjectName name) 
			throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException{
		if(hasInit){
			mbserver.registerMBean(mbean, name);
        	mbean.addNotificationListener(new RunStatusListener(), null, null);
		}
	}

}
