package com.ling.test12;

import com.ling.test.Person;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test12 {

	public static void main(String[] args) {
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("bean12.xml");
		Person person = ac.getBean(Person.class);
		System.out.println("person = " + person);
	}
}
