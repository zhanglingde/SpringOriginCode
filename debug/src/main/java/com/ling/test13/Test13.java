package com.ling.test13;

import com.ling.test13.factorybean.MyFactoryBean;
import com.ling.test13.factorybean.Student;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * FactoryBean
 */
public class Test13 {
	public static void main(String[] args) {
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("bean13.xml");

		// 获取 FactoryBean 对象，transformedBeanName 将 beanName 加上 &
		MyFactoryBean bean1 = (MyFactoryBean) ac.getBean("&myFactoryBean");
		System.out.println("bean1 = " + bean1);
		// MyFactoryBean bean2 = ac.getBean(MyFactoryBean.class);
		// System.out.println("bean2 = " + bean2);

		// 获取 Student 对象，使用 FactoryBean#getObject 创建对象
		Student student1 = (Student) ac.getBean("myFactoryBean");
		System.out.println("student1 = " + student1);
		// 如果 Student 是单例，则从缓存中获取；如果不是单例，则每次创建
		Student student2 = (Student) ac.getBean("myFactoryBean");
		System.out.println("student2 = " + student2);

		// 获取 FactoryBean 创建的对象
		// Student student = ac.getBean(Student.class);
		// System.out.println("student = " + student);
	}
}
