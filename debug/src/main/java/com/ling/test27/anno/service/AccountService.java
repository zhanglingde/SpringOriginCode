package com.ling.test27.anno.service;


import com.ling.test27.anno.Account;

/**
 * 账户的业务层接口
 */
public interface AccountService {

    Account findAccountById(Integer accountId);

    /**
     * 转账
     */
    void transfer(String sourceName, String targetName, Float money);

    void transferImpl(String sourceName, String targetName, Float money);
}
