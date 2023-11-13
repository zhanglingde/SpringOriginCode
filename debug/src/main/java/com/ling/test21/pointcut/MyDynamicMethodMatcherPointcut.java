package com.ling.test21.pointcut;

import org.springframework.aop.support.DynamicMethodMatcherPointcut;

import java.lang.reflect.Method;

public class MyDynamicMethodMatcherPointcut extends DynamicMethodMatcherPointcut {

	// 方法匹配器中两个参数的方法返回 true，三个参数的方法才会匹配
	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		return method.getName().startsWith("set");
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass, Object... args) {
		return method.getName().startsWith("set") && args.length == 1 && Integer.class.isAssignableFrom(args[0].getClass());
	}
}
