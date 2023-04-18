package com.ling.test30;

import com.ling.test30.service.AccountService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test30 {

	// Mybatis 事务控制不生效
	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext("spring-mybatis.xml");
		AccountService accountService = ac.getBean("accountService", AccountService.class);
		Account account = accountService.findAccountById(2);
		System.out.println("account = " + account);

		// lisi -> zhang 转账 50
		accountService.transfer("lisi", "zhang", 50f);
	}
}
