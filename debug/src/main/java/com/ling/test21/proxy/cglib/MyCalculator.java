package com.ling.test21.proxy.cglib;

import com.ling.test21.proxy.jdk.Calculator;

/**
 * 业务方法
 *
 * @author zhangling
 * @date 2022/4/6 8:36 下午
 */
public class MyCalculator implements Calculator {
	@Override
	public int add(int i, int j) {
		int result = i + j;
		System.out.println("result = " + result);
		return result;
	}

	@Override
	public int sub(int i, int j) {
		int result = i - j;
		return result;
	}

	@Override
	public int mul(int i, int j) {
		int result = i * j;
		return result;
	}

	@Override
	public int div(int i, int j) {
		int result = i / j;
		return result;
	}
}
