package com.iflytek.musicsearch.toolsframework.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.iflytek.musicsearch.toolsframework.config.schema.LoopConfig;
import com.iflytek.musicsearch.toolsframework.config.schema.MultiConfig;
import com.iflytek.musicsearch.toolsframework.config.schema.QuartzConfig;
import com.iflytek.musicsearch.toolsframework.config.schema.RmiConfig;
import com.iflytek.musicsearch.toolsframework.config.schema.SingleConfig;
import com.iflytek.musicsearch.toolsframework.config.schema.ZKConfig;


public class ToolsFrameworkNameSpaceHandle extends NamespaceHandlerSupport {

	@Override
	public void init() {
        registerBeanDefinitionParser("quartz", new ToolsFrameworkBeanDefinitionParser(QuartzConfig.class));
        registerBeanDefinitionParser("zk", new ToolsFrameworkBeanDefinitionParser(ZKConfig.class));
        registerBeanDefinitionParser("rmi", new ToolsFrameworkBeanDefinitionParser(RmiConfig.class));
        registerBeanDefinitionParser("multirun", new ToolsFrameworkBeanDefinitionParser(MultiConfig.class));
        registerBeanDefinitionParser("singlerun", new ToolsFrameworkBeanDefinitionParser(SingleConfig.class));
        registerBeanDefinitionParser("loop", new ToolsFrameworkBeanDefinitionParser(LoopConfig.class));
	}

}
