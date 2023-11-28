package com.ling.test27.bianchenTx;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TxTest {
	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext("bean27-biancheng.xml");
		TransferService2 bean = ac.getBean(TransferService2.class);
		bean.transfer(false);
	}
}
