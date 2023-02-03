package com.ling.test29.mapper;

import com.ling.test27.xml.Account;

public interface AccountMapper {

    /**
     * 根据Id查询账户
     */
    Account findAccountById(Integer accountId);

    /**
     * 根据名称查询账户
     * @param accountName
     * @return
     */
    Account findAccountByName(String accountName);

    /**
     * 更新账户
     */
    void updateAccount(Account account);

}
