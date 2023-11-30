package com.ling.test29;

import com.ling.test27.xml.service.AccountService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Mybatis 持久层
 */
public class Test29 {
	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext("bean27.xml");
		AccountService accountService = ac.getBean("accountService", AccountService.class);
		// Account account = accountService.findAccountById(2);
		// System.out.println("account = " + account);

		// lisi -> zhang 转账 50
		accountService.transfer("lisi", "zhang", 50f,true);
	}
}
