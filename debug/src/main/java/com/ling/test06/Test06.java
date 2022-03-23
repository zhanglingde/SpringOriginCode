package com.ling.test06;

import com.ling.test06.selftag.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhangling  2021/12/16 21:16
 */
public class Test06 {
	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext("bean06.xml");
		// Person person = (Person) ac.getBean("person");
		// System.out.println("person = " + person);
		User ling = (User) ac.getBean("user");
		System.out.println("user = " + ling.getPassword());
	}
}
