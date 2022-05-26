package com.ling.test28.anno.dao;


import com.ling.test28.anno.Account;

/**
 * 账户的持久层接口
 */
public interface AccountDao {

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
