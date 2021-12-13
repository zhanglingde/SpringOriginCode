package com.ling.test02;

import com.ling.test.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhangling  2021/9/1 17:35
 */
public class Test {
	public static void main(String[] args) {

		// MyClassPathXmlApplicationContext ac = new MyClassPathXmlApplicationContext("bean.xml");

		ApplicationContext ac = new ClassPathXmlApplicationContext("spring-${username}.xml");
		// Person bean = (Person) ac.getBean("person");
		// System.out.println(bean);

	}
}
