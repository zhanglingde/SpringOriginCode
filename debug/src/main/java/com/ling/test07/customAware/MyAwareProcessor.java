package com.ling.test07.customAware;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.security.AccessControlContext;

/**
 * @author zhangling  2021/12/27 22:41
 */
public class MyAwareProcessor implements BeanPostProcessor {

	private final ConfigurableApplicationContext applicationContext;

	public MyAwareProcessor(ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		AccessControlContext acc = null;
		if (System.getSecurityManager() != null) {
			acc = this.applicationContext.getBeanFactory().getAccessControlContext();
		}

		((ApplicationContextAware)bean).setApplicationContext(this.applicationContext);
		return bean;
	}
}
