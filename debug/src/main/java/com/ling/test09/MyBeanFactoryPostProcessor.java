package com.ling.test09;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.PriorityOrdered;

/**
 * 参数中的 BFPP
 */
public class MyBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("5. 执行入参中 BFPP 中的 MyBeanFactoryPostProcessor#postProcessBeanFactory()");
	}

	// 该 BFPP 通过重写 ApplicationContext#customizeBeanFactory 方法添加， order 顺序无效？
	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println("2. 执行入参 BFPP 中的 postProcessBeanDefinitionRegistry() 方法");
	}
}
