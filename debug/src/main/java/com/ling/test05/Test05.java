package com.ling.test05;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class Test05 {
	public static void main(String[] args) {

		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("bean05.xml");
		Person bean = (Person) ac.getBean("person");
		System.out.println("bean = " + bean);
		Person person2 = (Person) ac.getBean("person2");
		System.out.println("person2 = " + person2);


	}
}
