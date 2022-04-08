package com.ling.test21.proxy.cglib;

import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.Enhancer;

/**
 * @author zhangling
 * @date 2022/4/8 2:27 下午
 */
public class MyTest {
	public static void main(String[] args) {
		// 动态代理创建的 class 文件存储到本地
		System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "/Users/ling/codes");
		// 通过 cglib 动态代理获取代理对象的过程，创建调用的对象
		Enhancer enhancer = new Enhancer();
		// 设置 enhancer 对象的父类
		enhancer.setSuperclass(MyCalculator.class);
		// 设置 enhancer 的回调对象
		enhancer.setCallback(new MyCglib());
		// 创建代理对象
		MyCalculator myCalculator = (MyCalculator) enhancer.create();
		// 通过代理对象调用目标方法
		myCalculator.add(1, 1);
		System.out.println(myCalculator.getClass());
	}
}
