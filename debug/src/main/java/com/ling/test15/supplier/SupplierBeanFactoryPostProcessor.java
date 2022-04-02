package com.ling.test15.supplier;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * BeanDefinition 设置属性值：
 */
public class SupplierBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		BeanDefinition user = beanFactory.getBeanDefinition("user");
		// 原生读取时生成 GenericBeanDefinition，里面有 Supplier 属性，可以对其属性值进行设置
		GenericBeanDefinition genericBeanDefinition = (GenericBeanDefinition) user;
		// 函数式接口
		genericBeanDefinition.setInstanceSupplier(CreateSupplier::createUser);
		genericBeanDefinition.setBeanClass(User.class);

	}
}
