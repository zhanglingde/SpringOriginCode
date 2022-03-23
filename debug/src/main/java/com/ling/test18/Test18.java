package com.ling.test18;

import com.ling.test18.populateBean.Person;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test18 {

	public static void main(String[] args) {
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("bean18.xml");
		Person person = ac.getBean("person", Person.class);
		System.out.println("person = " + person);
	}
}
