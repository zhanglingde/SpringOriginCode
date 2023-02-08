package com.ling.test30.service.impl;


import com.ling.test30.Account;
import com.ling.test30.mapper.AccountMapper;
import com.ling.test30.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账户的业务层实现类
 * <p>
 * 事务控制应该都是在业务层
 */
@Service("accountService")
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    public void setAccountMapper(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Override
    public Account findAccountById(Integer accountId) {
        return accountMapper.findAccountById(accountId);
    }

    /**
     * 转账
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transfer(String sourceName, String targetName, Float money) {
        System.out.println("before transfer....");
        Account source = accountMapper.findAccountByName(sourceName);
        Account target = accountMapper.findAccountByName(targetName);
        source.setMoney(source.getMoney() - money);
        target.setMoney(target.getMoney() + money);
        accountMapper.updateAccount(source);

        int i = 10;
        if (i == 10) {
            throw new RuntimeException("自定义异常");
        }
        accountMapper.updateAccount(target);
    }
}
