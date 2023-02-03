package com.ling.test30.mapper;


import com.ling.test30.Account;

public interface AccountMapper {

    /**
     * 根据Id查询账户
     */
    Account findAccountById(Integer accountId);

    /**
     * 根据名称查询账户
     *
     * @param accountName
     * @return
     */
    Account findAccountByName(String accountName);

    /**
     * 更新账户
     */
    void updateAccount(Account account);

}
