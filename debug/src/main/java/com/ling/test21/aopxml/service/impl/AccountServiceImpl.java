package com.ling.test21.aopxml.service.impl;


import com.ling.test21.aopxml.service.IAccountService;

public class AccountServiceImpl implements IAccountService {

    @Override
    public void saveAccount() {
        System.out.println("业务方法保存...");
    }

    @Override
    public void updateAccount(int i) {
        System.out.println("业务方法更新"+i);

    }

    @Override
    public int deleteAccount() {
        System.out.println("业务方法删除");
        return 0;
    }
}
