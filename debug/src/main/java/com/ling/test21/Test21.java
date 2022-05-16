package com.ling.test21;

import com.ling.test21.aopxml.service.IAccountService;
// import com.ling.test21.aopannotation.service.IAccountService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test21 {

	public static void main(String[] args) {
		// xml
		ApplicationContext ac = new ClassPathXmlApplicationContext("bean21.xml");
		IAccountService as = (IAccountService) ac.getBean("accountService");
		as.saveAccount();

		//	注解
		// ApplicationContext ac = new ClassPathXmlApplicationContext("bean21.xml");
		// IAccountService as = (IAccountService) ac.getBean("accountService");
		// as.saveAccount();
	}

}
