package com.ling.test21.proxy.cglib;


import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author zhangling
 * @date 2022/4/8 2:25 下午
 */
public class MyCglib implements MethodInterceptor {
	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		System.out.println("代理前...");
		Object o1 = methodProxy.invokeSuper(o, objects);
		System.out.println("代理后...");
		return o1;
	}
}
