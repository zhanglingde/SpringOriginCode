package com.ling.test21.circleAop.service.impl;

import com.ling.test21.circleAop.service.AccountService;
import com.ling.test21.circleAop.service.BService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

	@Autowired
	BService bService;

    @Override
    public void saveAccount() {
        System.out.println("AccountService 执行了保存");
        // int i = 1/0;
    }

    @Override
    public void updateAccount(int i) {
        System.out.println("AccountService 执行了更新"+i);

    }

    @Override
    public int deleteAccount() {
        System.out.println("AccountService 执行了删除");
        return 0;
    }
}
