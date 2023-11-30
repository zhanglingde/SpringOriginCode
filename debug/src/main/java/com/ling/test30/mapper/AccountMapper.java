package com.ling.test30.mapper;


import com.ling.test30.Account;

public interface AccountMapper {


    Account findAccountById(Integer accountId);

    Account findAccountByName(String accountName);

	// 更新账号金额
    void updateAccount(Account account);

}
