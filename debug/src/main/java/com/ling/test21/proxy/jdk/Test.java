package com.ling.test21.proxy.jdk;

/**
 * @author zhangling
 * @date 2022/4/6 8:39 下午
 */
public class Test {
	public static void main(String[] args) {
		Calculator proxy = CalculatorProxy.getProxy(new MyCalculator());
		proxy.add(1,1);
		System.out.println(proxy.getClass());
		//
		// // 实例化目标对象
		// UserService userService = new UserServiceImpl();
		// // 实例化 InvocationHandler
		// MyInvocationHandler invocationHandler = new MyInvocationHandler(userService);
		// // 根据目标对象生成代理对象
		// UserService proxy = (UserService) invocationHandler.getProxy();
		// // 调用代理对象的方法
		// proxy.add();
	}
}
