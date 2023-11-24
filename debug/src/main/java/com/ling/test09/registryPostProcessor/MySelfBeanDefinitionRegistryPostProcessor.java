package com.ling.test09.registryPostProcessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.PriorityOrdered;

/**
 * BeanFactoryPostProcessor 中 添加的 BeanDefinition
 */
public class MySelfBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("7. BFPP 中添加的 BFPP 的 postProcessBeanFactory(), MySelfBeanDefinitionRegistryPostProcessor#postProcessBeanFactory()");
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println("4. BFPP 中添加的 BFPP 的 postProcessBeanDefinitionRegistry(), MySelfBeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry()... ");
	}

	@Override
	public int getOrder() {
		return 5;
	}
}
