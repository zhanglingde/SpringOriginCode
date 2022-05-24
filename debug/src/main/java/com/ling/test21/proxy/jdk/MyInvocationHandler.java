package com.ling.test21.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 创建自定义的 InvocationHandler，用于对提供的方法进行增强
 *
 * InvocationHandler的创建是最为核心的，在自定义 InvocationHandler 中需要重写 3 个函数
 * 1. 构造函数，将代理的对象传入
 * 2. invoke 方法，此方法中实现了 AOP 增强的所有逻辑
 * 3. getProxy 方法，此方法千篇一律，但是必不可少
 *
 */
public class MyInvocationHandler implements InvocationHandler {

	/**
	 * 目标对象
	 */
	private Object target;

	public MyInvocationHandler(Object target) {
		super();
		this.target = target;
	}

	/**
	 * 执行目标对象的方法
	 *
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 目标对象方法执行之前
		System.out.println("before...");
		// 执行目标对象方法
		Object result = method.invoke(target, args);
		// 目标对象方法执行之后
		System.out.println("after...");
		return result;
	}

	/**
	 * 获取目标对象的代理对象
	 * @return 代理对象
	 */
	public Object getProxy(){
		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				target.getClass().getInterfaces(), this);
	}
}
