package com.ling.test19;

import com.ling.test19.populateBean.Person;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Test19 {
	public static void main(String[] args) {
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("bean19.xml");
		Person person = ac.getBean("person", Person.class);
		System.out.println("person = " + person);
	}
}
