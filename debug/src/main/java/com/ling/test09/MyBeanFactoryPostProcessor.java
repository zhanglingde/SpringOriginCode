package com.ling.test09;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.PriorityOrdered;

/**
 *
 *
 * @author zhangling
 * @date 2022/1/28 10:20 上午
 */
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor, PriorityOrdered {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("自定义 MyBeanFactoryPostProcessor#postProcessBeanFactory");
	}

	// 该 BFPP 通过重写 ApplicationContext#customizeBeanFactory 方法， order 顺序无效？
	@Override
	public int getOrder() {
		return 0;
	}
}
