package com.ling.test02;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring 初始化时，初始化自己的环境变量
 *
 * @author zhangling  2021/12/13 21:28
 */
public class MyClassPathXmlApplicationContext extends ClassPathXmlApplicationContext {

	public MyClassPathXmlApplicationContext(String... configLocations) {
		super(configLocations);
	}


	@Override
	protected void initPropertySources() {
		System.out.println("扩展 initPropertySources");
		// 自定义必须的环境变量
		// getEnvironment().setRequiredProperties("username");
		getEnvironment().validateRequiredProperties();
	}

	/**
	 * 设置 bean 允许被覆盖，允许循环依赖，重写方法
	 *
	 * @param beanFactory the newly created bean factory for this context
	 */
	@Override
	protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
		beanFactory.setAllowBeanDefinitionOverriding(false);
		beanFactory.setAllowCircularReferences(false);
		super.customizeBeanFactory(beanFactory);
	}


}
