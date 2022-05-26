package com.ling.test28.anno.service;


import com.ling.test28.anno.Account;

/**
 * 账户的业务层接口
 */
public interface AccountService {

    Account findAccountById(Integer accountId);

    /**
     * 转账
     */
    void transfer(String sourceName, String targetName, Float money);
}
