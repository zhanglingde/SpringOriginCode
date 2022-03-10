package com.ling.test11;

import com.ling.test.Person;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test11 {
	public static void main(String[] args) {
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("bean10.xml");
		Person person = ac.getBean(Person.class);
		System.out.println("person = " + person);
	}
}
