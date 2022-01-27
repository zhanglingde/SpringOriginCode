package com.ling.test08;

import com.ling.test.Person;
import com.ling.test07.customEditor.Customer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhangling
 * @date 2022/1/27 9:49 上午
 */
public class Test08 {

	public static void main(String[] args) {
		MyClassPathXmlApplicationContext ac = new MyClassPathXmlApplicationContext("bean08.xml");
		Person bean = ac.getBean(Person.class);
		System.out.println(bean);
	}
}
