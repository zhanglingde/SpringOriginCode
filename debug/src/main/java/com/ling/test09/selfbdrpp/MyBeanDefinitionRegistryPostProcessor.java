package com.ling.test09.selfbdrpp;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.PriorityOrdered;

/**
 * 实现 PriorityOrder,Ordered 和不实现接口，执行顺序不一样
 *
 * @author zhangling
 * @date 2022/1/28 10:40 上午
 */
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("执行 MyBeanDefinitionRegistryPostProcessor#postProcessBeanFactory");
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		// 在此处可能会添加进新的 BeanDefinition
		System.out.println("执行 MyBeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry");
		// 查看两种方式 Bean 是什么时候创建的  ？
		// 方式一
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(Teacher.class);
		builder.addPropertyValue("id", 18L);
		builder.addPropertyValue("name", "zhangling");
		registry.registerBeanDefinition("teacher", builder.getBeanDefinition());

		// 方式二
		// registry.registerBeanDefinition("customBeanName", new RootBeanDefinition(Teacher.class));

		// 执行添加了新的 BeanDefinition,后序需要再次扫描执行
		BeanDefinitionBuilder builder2 = BeanDefinitionBuilder.rootBeanDefinition(MySelfBeanDefinitionRegistryPostProcessor.class);
		builder2.addPropertyValue("name", "MySelfBeanDefinitionRegistry");
		registry.registerBeanDefinition("mySelfBeanDefinition", builder2.getBeanDefinition());
	}

	// 执行顺序，数字越小越先执行
	@Override
	public int getOrder() {
		return 10;
	}
}
