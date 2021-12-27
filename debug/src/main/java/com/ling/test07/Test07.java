package com.ling.test07;

import com.ling.test07.customEditor.Customer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhangling  2021/12/18 17:21
 */
public class Test07 {
	public static void main(String[] args) {
		// ApplicationContext ac = new ClassPathXmlApplicationContext("bean07.xml");
		// 自定义编辑器
		ApplicationContext ac = new ClassPathXmlApplicationContext("selfEditor.xml");
		Customer bean = ac.getBean(Customer.class);
		System.out.println(bean);


	}
}
