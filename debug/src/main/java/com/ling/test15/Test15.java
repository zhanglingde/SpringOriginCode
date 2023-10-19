package com.ling.test15;

import com.ling.test15.factoryMethod.Person;
import com.ling.test15.resolveBeforeInstantiation.BeforeInstantiation;
import com.ling.test15.supplier.User;
import com.ling.test15.supplier.UserCreateSupplier;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test15 {

	public static void main(String[] args) {
		// 1. resolveBeforeInstantiation BeanPostProcessor 创建代理 bean
		// ApplicationContext ac = new ClassPathXmlApplicationContext("bean15.xml");
		// BeforeInstantiation bean = ac.getBean(BeforeInstantiation.class);
		// bean.doSomeThing();

		// 2. supplier 创建 bean
		// User user = (User) ac.getBean("user");
		// System.out.println("user = " + user);

		// 3. factoryMethod 创建 bean
		// Person person1 = ac.getBean("person01",Person.class);
		// System.out.println("person = " + person1);
		// //
		// Object person02 = ac.getBean("person02");
		// System.out.println("person02 = " + person02);
		//
        // // 构造方法创建 bean(没有设置属性值，是 MyInstantiationAwareBeanPostProcessor#postProcessAfterInstantiation() 返回 false,属性赋值未执行)
        // User user2 =  ac.getBean("user2",User.class);
        // System.out.println("user2 = " + user2);
		//
        // User user3 = (User) ac.getBean("user3");
        // System.out.println("user3 = " + user3);

		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		GenericBeanDefinition definition = new GenericBeanDefinition();
		definition.setBeanClass(User.class);
		definition.setInstanceSupplier(UserCreateSupplier::createUser);
		ctx.registerBeanDefinition("user", definition);
		ctx.refresh();
		User user2 = ctx.getBean("user", User.class);
		System.out.println("user = " + user2);

    }
}
