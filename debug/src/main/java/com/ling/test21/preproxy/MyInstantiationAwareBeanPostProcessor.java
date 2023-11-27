package com.ling.test21.preproxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		if (beanClass == BookService.class) {
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(beanClass);
			enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
				String name = method.getName();
				System.out.println(name + " 方法开始执行了...");
				Object invoke = proxy.invokeSuper(obj, args);
				System.out.println(name + " 方法执行结束了...");
				return invoke;
			});
			BookService bookService = (BookService) enhancer.create();
			return bookService;
		}
		return InstantiationAwareBeanPostProcessor.super.postProcessBeforeInstantiation(beanClass, beanName);
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("bean.getClass() ========= " + bean.getClass());
		return InstantiationAwareBeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}

}
