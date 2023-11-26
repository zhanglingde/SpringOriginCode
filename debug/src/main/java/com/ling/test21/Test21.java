package com.ling.test21;

import com.ling.test21.circleAop.service.AccountService;
import com.ling.test21.circleAop.service.BService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test21 {

	public static void main(String[] args) {
		// xml
		// ApplicationContext ac = new ClassPathXmlApplicationContext("bean21-xml.xml");
		// IAccountService as = (IAccountService) ac.getBean("accountService");
		// as.saveAccount();

		//	注解
		// ApplicationContext ac = new ClassPathXmlApplicationContext("bean21-aop.xml");
		// AccountService as = (AccountService) ac.getBean("accountService");
		// as.saveAccount();


		// 提前 AOP（三级缓存循环依赖）
		ApplicationContext ac = new ClassPathXmlApplicationContext("bean21-circleaop.xml");
		AccountService as = (AccountService) ac.getBean("accountService");
		as.saveAccount();
		BService bService = (BService) ac.getBean("BService");
		bService.saveAccount();

	}

}
