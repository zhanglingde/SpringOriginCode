package com.ling.test21.bianchenapo.introductionAdvisor;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.IntroductionInterceptor;


/**
 * 创建一个 Advice 增强
 */
public class AnimalIntroductionInterceptor implements IntroductionInterceptor, Animal {
	@Override
	public void eat() {
		System.out.println("animal eat...");
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// 拦截下来的方法是 Animal 中的方法返回 true
		if (implementsInterface(invocation.getMethod().getDeclaringClass())) {
			// 反射调用 method 方法，会调用 eat() 方法
			return invocation.getMethod().invoke(this, invocation.getArguments());
		}
		return invocation.proceed();
	}

	@Override
	public boolean implementsInterface(Class<?> intf) {
		return intf.isAssignableFrom(this.getClass());
	}
}
