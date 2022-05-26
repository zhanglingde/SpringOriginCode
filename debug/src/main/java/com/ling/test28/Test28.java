package com.ling.test28;

import com.ling.test28.anno.config.SpringConfiguration;
import com.ling.test28.anno.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfiguration.class)
public class Test28 {

	@Autowired
	private AccountService accountService;

	@Test
	public void test(){
		accountService.transfer("lisi", "zhang", 50f);
	}
}
