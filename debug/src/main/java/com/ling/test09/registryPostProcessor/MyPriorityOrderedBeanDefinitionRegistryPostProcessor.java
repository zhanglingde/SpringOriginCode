package com.ling.test09.registryPostProcessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.PriorityOrdered;

/**
 * 实现 PriorityOrder,Ordered 和不实现接口，执行顺序不一样
 */
public class MyPriorityOrderedBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("6. 优先执行 PriorityOrder 的 PriorityOrderedRegistryPostProcessor#postProcessBeanFactory()");
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		// 在此处可能会添加进新的 BeanDefinition
		System.out.println("3. 执行 PriorityOrdered 的 postProcessBeanDefinitionRegistry(), PriorityOrderedRegistryPostProcessor#postProcessBeanDefinitionRegistry");
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
