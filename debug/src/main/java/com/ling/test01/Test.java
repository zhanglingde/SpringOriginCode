package com.ling.test01;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhangling  2021/9/1 17:35
 */
public class Test {
	public static void main(String[] args) {
		// 使用 Spring 提供的类加载资源
		// Resource resource = new ClassPathResource("bean01.xml");
		// InputStream inputStream;
		// try {
		// 	inputStream = resource.getInputStream();
		// } catch (IOException e) {
		// 	e.printStackTrace();
		// }

		// ApplicationContext ac = new ClassPathXmlApplicationContext("bean01.xml");
		// Person bean = (Person) ac.getBean("person");
		// System.out.println(bean);

		// 读取多个配置文件
		AbstractApplicationContext aac = new ClassPathXmlApplicationContext("bean01.xml", "bean01-two.xml");
		Person person = (Person) aac.getBean("person");
		System.out.println("person = " + person);
		User user = aac.getBean(User.class);
		System.out.println("user = " + user);

		// BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("bean01.xml"));
		// Person person = (Person) beanFactory.getBean("person");
		// System.out.println("person = " + person);

	}
}
