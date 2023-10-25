package com.ling.test21.bianchenapo.jdk;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;

import java.lang.reflect.Method;

public class Client {
	public static void main(String[] args) {
		ProxyFactory proxyFactory = new ProxyFactory();
		// 设置真正的代理对象
		proxyFactory.setTarget(new ICalculatorImpl());
		// 设置代理对象的接口
		proxyFactory.setInterfaces(ICalculator.class);
		// 添加增强/通知
		proxyFactory.addAdvice(new MethodInterceptor() {
			@Override
			public Object invoke(MethodInvocation invocation) throws Throwable {
				Method method = invocation.getMethod();
				String name = method.getName();
				System.out.println(name+"方法执行之前...");
				Object proceed = invocation.proceed();
				System.out.println(name+"方法执行完毕...");
				return proceed;
			}
		});
		// 创建代理对象
		ICalculator calculator = (ICalculator) proxyFactory.getProxy();
		calculator.add(3, 4);
		int res = calculator.minus(5, 3);
	}
}
