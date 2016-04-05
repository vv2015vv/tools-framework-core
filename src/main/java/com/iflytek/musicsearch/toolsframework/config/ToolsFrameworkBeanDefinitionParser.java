package com.iflytek.musicsearch.toolsframework.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.iflytek.musicsearch.toolsframework.config.schema.LoopConfig;
import com.iflytek.musicsearch.toolsframework.config.schema.MultiConfig;
import com.iflytek.musicsearch.toolsframework.config.schema.QuartzConfig;
import com.iflytek.musicsearch.toolsframework.config.schema.RmiConfig;
import com.iflytek.musicsearch.toolsframework.config.schema.SingleConfig;
import com.iflytek.musicsearch.toolsframework.config.schema.ZKConfig;

public class ToolsFrameworkBeanDefinitionParser implements BeanDefinitionParser {
	
	private final Class<?> beanClass;
	
	public ToolsFrameworkBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }
	
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(beanClass);

		String p_id = element.getAttribute("id");
		beanDefinition.getPropertyValues().addPropertyValue("id", p_id);
		
		if(QuartzConfig.class.equals(beanClass)){
			String p_workerNum = element.getAttribute("workerNum");
			beanDefinition.getPropertyValues().addPropertyValue("workerNum", p_workerNum);			
		}else if(ZKConfig.class.equals(beanClass)){
			String p_url = element.getAttribute("url");
			beanDefinition.getPropertyValues().addPropertyValue("url", p_url);
			String p_root = element.getAttribute("root");
			beanDefinition.getPropertyValues().addPropertyValue("root", p_root);
			String p_timeout = element.getAttribute("timeout");
			beanDefinition.getPropertyValues().addPropertyValue("timeout", p_timeout);
		}else if(RmiConfig.class.equals(beanClass)){
			String p_port = element.getAttribute("port");
			beanDefinition.getPropertyValues().addPropertyValue("port", p_port);			
		}else{
			String p_taskname = element.getAttribute("taskname");
			beanDefinition.getPropertyValues().addPropertyValue("taskname", p_taskname);
			
			String p_handle = element.getAttribute("handle");
			beanDefinition.getPropertyValues().addPropertyValue("handle", p_handle);
			
			String p_execonstart = element.getAttribute("execonstart");
			beanDefinition.getPropertyValues().addPropertyValue("execonstart", p_execonstart);
			
			if(MultiConfig.class.equals(beanClass)){
				String p_cron = element.getAttribute("cron");
				beanDefinition.getPropertyValues().addPropertyValue("cron", p_cron);
				String p_threadcnt = element.getAttribute("threadcnt");
				beanDefinition.getPropertyValues().addPropertyValue("threadcnt", 
						StringUtils.isBlank(p_threadcnt) ? 1 : Integer.parseInt(p_threadcnt));
			}else if(SingleConfig.class.equals(beanClass)){
				String p_cron = element.getAttribute("cron");
				beanDefinition.getPropertyValues().addPropertyValue("cron", p_cron);
			}else if(LoopConfig.class.equals(beanClass)){
				String p_loopnum = element.getAttribute("loopnum");
				beanDefinition.getPropertyValues().addPropertyValue("loopnum", 
						StringUtils.isBlank(p_loopnum) ? 1 : Integer.parseInt(p_loopnum));
			}
		}

		parserContext.getRegistry().registerBeanDefinition(p_id, beanDefinition);
		return beanDefinition;
	}

}