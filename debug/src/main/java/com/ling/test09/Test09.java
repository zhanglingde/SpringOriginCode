package com.ling.test09;

import com.ling.Person;

/**
 * @author zhangling
 * @date 2022/1/27 9:49 上午
 */
public class Test09 {

	public static void main(String[] args) {
		MyClassPathXmlApplicationContext ac = new MyClassPathXmlApplicationContext("bean09.xml");
		Person bean = ac.getBean(Person.class);
		System.out.println(bean);
	}
}
