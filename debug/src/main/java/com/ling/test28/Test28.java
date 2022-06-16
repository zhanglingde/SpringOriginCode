package com.ling.test28;

import com.ling.test28.anno.config.SpringConfiguration;
import com.ling.test28.anno.service.AccountService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;

public class Test28 {

	public static void main(String[] args) {
		ApplicationContext ac = new AnnotationConfigApplicationContext(SpringConfiguration.class);
		AccountService accountService = (AccountService) ac.getBean("accountService");
		accountService.transfer("lisi", "zhang", 50f);

	}

}
