package com.ling.test20;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test20 {

	public static void main(String[] args) {
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("bean20.xml");
		AService aService = ac.getBean("AService", AService.class);
		System.out.println("aService = " + aService);
	}
}
