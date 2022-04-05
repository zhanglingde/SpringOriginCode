package com.ling.test16;

import com.ling.Person;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test16 {
	public static void main(String[] args) {
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("bean16.xml");
		// Person person1 = (Person) ac.getBean("person");
		// Person person2 = ac.getBean("person", Person.class);

		// Autowire
		// Student bean = ac.getBean(Student.class);
		// System.out.println("bean = " + bean);

		// 生命周期
		Life bean = ac.getBean(Life.class);
		System.out.println("bean = " + bean);
		ac.close();



	}
}
