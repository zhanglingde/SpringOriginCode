package com.ling.test09;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring 初始化时，初始化自己的环境变量
 */
public class MyClassPathXmlApplicationContext extends ClassPathXmlApplicationContext {

	public MyClassPathXmlApplicationContext(String... configLocations) {
		super(configLocations);
	}

	/**
	 * 设置 bean 允许被覆盖，允许循环依赖，重写方法
	 */
	@Override
	protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
		beanFactory.setAllowBeanDefinitionOverriding(false);
		beanFactory.setAllowCircularReferences(false);
		// 自己添加 beanFactoryPostProcessor,添加作为参数中的 BFPP
		super.addBeanFactoryPostProcessor(new MyBeanFactoryPostProcessor());
		super.customizeBeanFactory(beanFactory);
	}

	@Override
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		System.out.println("1. 扩展重写 AbstractApplicationContext 中的 postProcessBeanFactory(), MyClassPathXmlApplicationContext#postProcessBeanFactory 方法");
	}
}
