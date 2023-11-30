package com.ling.test27.anno;

import com.ling.test27.anno.config.SpringConfig;
import com.ling.test27.anno.service.AccountService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TxTest {

	public static void main(String[] args) {
		ApplicationContext ac = new AnnotationConfigApplicationContext(SpringConfig.class);
		AccountService accountService = (AccountService) ac.getBean("accountService");
		accountService.transfer("lisi", "zhangsan", 50f);

	}

}
