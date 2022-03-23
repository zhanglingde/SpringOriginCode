package com.ling.test15;

import com.ling.test15.factoryMethod.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test14 {

	public static void main(String[] args) {
		// 1. resolveBeforeInstantiation BeanPostProcessor 创建代理 bean
		// ApplicationContext ac = new ClassPathXmlApplicationContext("bean15.xml");
		// BeforeInstantiation bean = ac.getBean(BeforeInstantiation.class);
		// bean.doSomeThing();

		// 2. supplier 创建 bean
		// ApplicationContext ac = new ClassPathXmlApplicationContext("bean15.xml");
		// User user = ac.getBean(User.class);
		// System.out.println("user = " + user);

		// 3. factoryMethod 创建 bean
		ApplicationContext ac = new ClassPathXmlApplicationContext("bean15.xml");
		Person person1 = ac.getBean("person01",Person.class);
		System.out.println("person = " + person1);

		Object person02 = ac.getBean("person02");
		System.out.println("person02 = " + person02);

	}
}
