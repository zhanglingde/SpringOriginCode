package com.ling.test15.resolveBeforeInstantiation;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cglib.proxy.Enhancer;


/**
 * registerBeanPostProcessors： 注册 bean 的后置处理器
 *
 * 给 bean 创建一个代理对象，而不是本身的对象；doCreateBean 并不一定会执行，而是取决于是否包含提前创建对象的 BeanPostProcessor
 *
 * {@link org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation(String, RootBeanDefinition)}
 */
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {


	/**
	 * 实例化前
	 */
	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		System.out.println("beanName:"+beanName+"---------执行 postProcessBeforeInitialization");
		if (beanClass == BeforeInstantiation.class) {
			Enhancer enhancer =new Enhancer();
			enhancer.setSuperclass(beanClass);
			enhancer.setCallback(new MyMethodInterceptor());
			BeforeInstantiation beforeInstantiation = (BeforeInstantiation) enhancer.create();
			System.out.println("创建代理对象："+beforeInstantiation);
			return beforeInstantiation;
		}
		return null;
	}

	/**
	 * 实例化后
	 */
	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		System.out.println("beanName:"+beanName+"---------执行 postProcessAfterInstantiation");
		return false;
	}

	/**
	 * 初始化前
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("beanName:"+beanName+"---------执行 postProcessBeforeInitialization");
		return bean;
	}

	/**
	 * 初始化后
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("beanName:"+beanName+"---------执行 postProcessBeforeInitialization");
		return bean;
	}

	/**
	 * 属性值处理
	 */
	@Override
	public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
		System.out.println("beanName:"+beanName+"---------执行 postProcessProperties");
		return pvs;
	}
}
