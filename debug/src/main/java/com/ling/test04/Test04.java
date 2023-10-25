package com.ling.test04;

import com.ling.test05.Person;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test04 {

	public static void main(String[] args) {
		// AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		//
		// RootBeanDefinition pbd = new RootBeanDefinition();
		// MutablePropertyValues pValues = new MutablePropertyValues();
		// pValues.add("name", "小黄");
		// pbd.setBeanClass(Animal.class);
		// pbd.setPropertyValues(pValues);
		//
		// GenericBeanDefinition cbd = new GenericBeanDefinition();
		// cbd.setBeanClass(Dog.class);
		// cbd.setParentName("parent");
		// MutablePropertyValues cValues = new MutablePropertyValues();
		// cValues.add("name", "小强");
		// cbd.setPropertyValues(cValues);
		//
		// ctx.registerBeanDefinition("parent", pbd);
		// ctx.registerBeanDefinition("child", cbd);
		// // 将 BeanDefinition 加载成对象放入容器中
		// ctx.refresh();
		// Dog child = (Dog) ctx.getBean("child");
		// System.out.println("child = " + child);

		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("bean04.xml");
		Dog bean = (Dog) ac.getBean("dog");
		System.out.println("bean = " + bean);

	}
}
