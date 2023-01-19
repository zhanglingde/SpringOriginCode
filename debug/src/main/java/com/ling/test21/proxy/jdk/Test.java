package com.ling.test21.proxy.jdk;

import java.lang.reflect.Proxy;

/**
 * @author zhangling
 * @date 2022/4/6 8:39 下午
 */
public class Test {
	public static void main(String[] args) {
		// 实例化目标对象
		UserService userService = new UserServiceImpl();
		// 实例化 InvocationHandler
		MyInvocationHandler invocationHandler = new MyInvocationHandler(userService);
		// 根据目标对象生成代理对象
        UserService proxy = (UserService) Proxy.newProxyInstance(Test.class.getClassLoader(),
                userService.getClass().getInterfaces(),
                new MyInvocationHandler(userService));
		// 调用代理对象的方法
		proxy.doService();
	}
}
