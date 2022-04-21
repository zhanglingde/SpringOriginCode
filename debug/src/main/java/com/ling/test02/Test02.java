package com.ling.test02;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhangling  2021/9/1 17:35
 */
public class Test02 {
	public static void main(String[] args) {

		MyClassPathXmlApplicationContext ac = new MyClassPathXmlApplicationContext("bean02.xml");
		Person person = ac.getBean("person", Person.class);
		System.out.println("person = " + person);

		// ApplicationContext ac = new ClassPathXmlApplicationContext("spring-${username}.xml");
		// Person bean = (Person) ac.getBean("person");
		// System.out.println(bean);

	}
}
