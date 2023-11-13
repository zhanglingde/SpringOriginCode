package com.ling.test21.bianchenapo.introductionAdvisor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 引介增强案例
 */
public class Client {
	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext("introduction.xml");
		Dog dog = ac.getBean(Dog.class);
		dog.run();
		System.out.println("Animal.class.isAssignableFrom(dog.getClass()) = " + Animal.class.isAssignableFrom(dog.getClass()));
		Animal animal = (Animal) dog;
		animal.eat();
	}
}
