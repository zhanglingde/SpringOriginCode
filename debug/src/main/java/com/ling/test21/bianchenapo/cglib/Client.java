package com.ling.test21.bianchenapo.cglib;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;

public class Client {
	public static void main(String[] args) {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(new UserService());
		proxyFactory.addAdvice(new MethodInterceptor() {
			@Override
			public Object invoke(MethodInvocation invocation) throws Throwable {
				String name = invocation.getMethod().getName();
				System.out.println(name + " 方法开始执行了...");
				Object proceed = invocation.proceed();
				System.out.println(name + " 方法执行结束了...");
				return proceed;
			}
		});
		UserService us = (UserService) proxyFactory.getProxy();
		us.hello();
	}
}
