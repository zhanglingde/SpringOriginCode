package com.ling.test09;

import com.ling.Person;
import com.ling.test09.registryPostProcessor.Teacher;

/**
 * 自定义 BeanFactoryPostProcessor
 *
 * @author zhangling
 * @date 2022/1/27 9:49 上午
 */
public class Test09 {

	public static void main(String[] args) {
		MyClassPathXmlApplicationContext ac = new MyClassPathXmlApplicationContext("bean09.xml");
		Person person = ac.getBean(Person.class);
		System.out.println("person = " + person);
		Teacher teacher = (Teacher) ac.getBean("teacher");
		System.out.println("teacher = " + teacher);
	}
}
