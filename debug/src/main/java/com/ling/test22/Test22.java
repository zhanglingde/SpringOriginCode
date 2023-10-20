package com.ling.test22;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test22 {
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:bean22.xml");
		UserBean user = (UserBean) applicationContext.getBean("userBean");
		((AbstractApplicationContext) applicationContext).close();
	}
}
