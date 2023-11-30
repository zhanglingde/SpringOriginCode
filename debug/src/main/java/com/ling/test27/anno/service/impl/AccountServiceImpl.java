package com.ling.test27.anno.service.impl;


import com.ling.test27.anno.Account;
import com.ling.test27.anno.dao.AccountDao;
import com.ling.test27.anno.service.AccountService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账户的业务层实现类
 * <p>
 * 事务控制应该都是在业务层
 */
@Service("accountService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true) //只读型事务的配置
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao accountDao;

    @Override
    public Account findAccountById(Integer accountId) {
        return accountDao.findAccountById(accountId);
    }

    /**
     * 转账
     */
    @Override
    // @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void transfer(String sourceName, String targetName, Float money) {
        ((AccountService) AopContext.currentProxy()).transferImpl(sourceName, targetName, money);
        // this.transferImpl(sourceName, targetName, money);
    }

    @Transactional(rollbackFor = Exception.class)
    public void transferImpl(String sourceName, String targetName, Float money) {
        System.out.println("before transfer....");
        Account source = accountDao.findAccountByName(sourceName);
        Account target = accountDao.findAccountByName(targetName);
        source.setMoney(source.getMoney() - money);
        target.setMoney(target.getMoney() + money);
        accountDao.updateAccount(source);

        int i = 10;
        if (i == 10) {
            throw new RuntimeException("自定义异常");
        }
        accountDao.updateAccount(target);
    }
}
