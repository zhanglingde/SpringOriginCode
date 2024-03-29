package com.ling.test30.service;


import com.ling.test30.Account;

/**
 * 账户的业务层接口
 */
public interface AccountService {

    Account findAccountById(Integer accountId);
    Account findAccountByName(String name);

    /**
     * 转账
     */
    void transfer(String sourceName, String targetName, Float money);

    void transferImpl(String sourceName, String targetName, Float money,boolean flag);
}
