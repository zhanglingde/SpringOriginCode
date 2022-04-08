package com.ling.test21.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zhangling
 * @date 2022/4/6 8:37 下午
 */
public class CalculatorProxy {

	public static Calculator getProxy(final Calculator calculator){
		ClassLoader loader = calculator.getClass().getClassLoader();
		Class<?>[] interfaces = calculator.getClass().getInterfaces();
		InvocationHandler h = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Object result = null;
				try {
					result = method.invoke(calculator, args);
				} catch (Exception e) {
				} finally {
				}
				return result;
			}
		};
		Object proxy = Proxy.newProxyInstance(loader, interfaces, h);
		return (Calculator) proxy;
	}

}
