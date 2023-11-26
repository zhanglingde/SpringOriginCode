package com.ling.test21.preAop;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class PreAopTest {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(JavaConfig.class);
		UserService userService = ac.getBean(UserService.class);
		System.out.println(userService);
	}
}
