package com.ling.test14.factoryMethod;


import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * 静态工厂：可能有多个 getPerson 方法，而配置文件中只配置了  getPerson,所以会有许多判断
 *
 * {@link org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#instantiateUsingFactoryMethod(String, RootBeanDefinition, Object[])}
 */
public class PersonStaticFactory {

	public static Person getPerson(String name) {
		Person person = new Person();
		person.setId(1);
		person.setName(name);
		return person;
	}

	public static Person getPerson(int age) {
		return new Person();
	}

	public static Person getPerson(int age,String name) {
		Person person = new Person();
		person.setAge(age);
		person.setName(name);
		return person;
	}

}
