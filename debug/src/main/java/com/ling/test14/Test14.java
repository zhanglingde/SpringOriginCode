package com.ling.test14;

import com.ling.test14.factoryMethod.Person;
import com.ling.test14.resolveBeforeInstantiation.BeforeInstantiation;
import com.ling.test14.supplier.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test14 {

	public static void main(String[] args) {
		// 1. resolveBeforeInstantiation BeanPostProcessor 创建代理 bean
		// ApplicationContext ac = new ClassPathXmlApplicationContext("resolveBeforeInstantiation.xml");
		// BeforeInstantiation bean = ac.getBean(BeforeInstantiation.class);
		// bean.doSomeThing();

		// 2. supplier 创建 bean
		// ApplicationContext ac = new ClassPathXmlApplicationContext("supplier.xml");
		// User user = ac.getBean(User.class);
		// System.out.println("user = " + user);

		// 3. factoryMethod 创建 bean
		ApplicationContext ac = new ClassPathXmlApplicationContext("supplier.xml");
		Person person1 = ac.getBean("person01",Person.class);
		System.out.println("person = " + person1);

		Object person02 = ac.getBean("person02");
		System.out.println("person02 = " + person02);

	}
}
