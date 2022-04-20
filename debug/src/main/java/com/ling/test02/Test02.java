package com.ling.test02;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhangling  2021/9/1 17:35
 */
public class Test02 {
	public static void main(String[] args) {

		MyClassPathXmlApplicationContext ac = new MyClassPathXmlApplicationContext("bean01.xml");

		// ApplicationContext ac = new ClassPathXmlApplicationContext("spring-${username}.xml");
		// Person bean = (Person) ac.getBean("person");
		// System.out.println(bean);

	}
}
