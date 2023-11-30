package com.ling.test30;

import com.ling.test30.service.AccountService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test30 {
	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext("bean30-mybatis.xml");
		AccountService accountService = ac.getBean("accountService", AccountService.class);
		Account account = accountService.findAccountByName("lisi");
		System.out.println("account = " + account);
		// lisi -> zhang 转账 50
		accountService.transferImpl("lisi", "zhangsan", 50f, true);
		Account account2 = accountService.findAccountByName("lisi");
		System.out.println("account = " + account2);
	}
}
