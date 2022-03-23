package com.ling.test10;

import com.ling.Person;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test10 {
	public static void main(String[] args) {

		// WindowCondition 了解
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("bean10.xml");
		Person person = ac.getBean(Person.class);
		System.out.println("person = " + person);

		// AnnotationConfigApplicationContext ac1 = new AnnotationConfigApplicationContext();
		// ac1.getBean(Person.class);
	}
}
