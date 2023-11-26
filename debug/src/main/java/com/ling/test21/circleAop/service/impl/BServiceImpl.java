package com.ling.test21.circleAop.service.impl;

import com.ling.test21.circleAop.service.AccountService;
import com.ling.test21.circleAop.service.BService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("BService")
public class BServiceImpl implements BService {

	@Autowired
	AccountService accountService;

	@Override
	public void saveAccount() {
		System.out.println("BServiceImpl 执行了保存");
	}
}
