package com.ling.test21.proxy.jdk;

/**
 * @author zhangling
 * @date 2022/5/24 10:16 上午
 */
public class UserServiceImpl implements UserService{

	@Override
	public void doService() {
		System.out.println("业务方法...");
	}
}
