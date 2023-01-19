package com.ling.test21.proxy.cglib;


/**
 * 业务方法
 *
 * @author zhangling
 * @date 2022/4/6 8:36 下午
 */
public class MyCalculator {

	public int add(int i, int j) {
		int result = i + j;
		System.out.println("result = " + result);
		return result;
	}


}
