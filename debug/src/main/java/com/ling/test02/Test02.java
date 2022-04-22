package com.ling.test02;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @author zhangling  2021/9/1 17:35
 */
public class Test02 {
	public static void main(String[] args) {

		// MyClassPathXmlApplicationContext ac = new MyClassPathXmlApplicationContext("bean02.xml");
		// Person person = ac.getBean("person", Person.class);
		// System.out.println("person = " + person);

		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("bean02.xml");
		Person bean = (Person) ac.getBean("person");
		System.out.println("bean = " + bean);

		ResourceLoader resourceLoader = new PathMatchingResourcePatternResolver();
		Resource resource = resourceLoader.getResource("bean02.xml");


	}
}
