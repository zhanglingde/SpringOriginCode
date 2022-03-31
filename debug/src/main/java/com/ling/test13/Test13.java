package com.ling.test13;

import com.ling.test13.factorybean.MyFactoryBean;
import com.ling.test13.factorybean.Student;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring 中默认的对象都是单例的，Spring 会在一级缓存中持有该对象，方便下次直接获取；
 * 如果 bean 是原型作用域的话，会创建一个新的对象
 * 如果想在一个单例模式的 bean 下引用一个原型模式的 bean，就需要使用 lookup-method 标签
 *
 * 解决了单例引用原型
 *
 * 通过拦截器的方式，每次需要的时候都去创建新的对象，而不会把原型对象缓存起来
 */
public class Test13 {
	public static void main(String[] args) {
		// 改成原型模式 prototype
		// ApplicationContext ac = new ClassPathXmlApplicationContext("bean13.xml");
		// FruitPlate fruitPlate1 = (FruitPlate) ac.getBean("fruitplate1");
		// fruitPlate1.getFruit();
		// FruitPlate fruitPlate2 = (FruitPlate) ac.getBean("fruitplate2");
		// fruitPlate2.getFruit();
		//

		// factory-bean
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("bean13.xml");

		// 获取 FactoryBean 对象，transformedBeanName 将 beanName 加上 &
		MyFactoryBean bean1 = (MyFactoryBean) ac.getBean("&myFactoryBean");
		System.out.println("bean1 = " + bean1);
		// MyFactoryBean bean2 = ac.getBean(MyFactoryBean.class);
		// System.out.println("bean2 = " + bean2);

		Student student1 = (Student) ac.getBean("myFactoryBean");
		System.out.println("student1 = " + student1);
		// 获取 FactoryBean 创建的对象
		Student student = ac.getBean(Student.class);
		System.out.println("student = " + student);
	}
}
