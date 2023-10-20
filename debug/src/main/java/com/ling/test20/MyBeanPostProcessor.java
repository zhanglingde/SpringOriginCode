package com.ling.test20;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

// 自定义 BeanPostProcessor 生成了新的 AService ，导致循环依赖 BService 生成的 AService 错误
// @Component
public class MyBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (beanName.equals("AService")) {
			System.out.println(bean);
			AService aService = new AService();
			return aService;
		}

		return bean;
	}
}