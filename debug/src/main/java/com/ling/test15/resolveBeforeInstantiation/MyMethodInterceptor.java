package com.ling.test15.resolveBeforeInstantiation;


import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 代理对象
 */
public class MyMethodInterceptor implements MethodInterceptor {

	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		System.out.println("目标方法执行之前："+method);
		// 获得代理对象
		Object o1 = methodProxy.invokeSuper(o, objects);
		System.out.println("目标方法执行之后："+method);
		return o1;
	}
}
