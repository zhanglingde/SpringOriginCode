package com.ling.test27.xml.service.impl;


import com.ling.test27.xml.Account;
import com.ling.test27.xml.dao.AccountDao;
import com.ling.test27.xml.service.AccountService;

/**
 * 账户的业务层实现类
 * <p>
 * 事务控制应该都是在业务层
 */
public class AccountServiceImpl implements AccountService {

	private AccountDao accountDao;

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	@Override
	public Account findAccountById(Integer accountId) {
		return accountDao.findAccountById(accountId);
	}

	/**
	 * 转账
	 */
	@Override
	public void transfer(String sourceName, String targetName, Float money,boolean flag) {
		System.out.println("before transfer....");
		Account source = accountDao.findAccountByName(sourceName);
		Account target = accountDao.findAccountByName(targetName);
		source.setMoney(source.getMoney() - money);
		target.setMoney(target.getMoney() + money);
		accountDao.updateAccount(source);
		if (flag) {
			throw new RuntimeException("自定义异常");
		}
		accountDao.updateAccount(target);
	}
}
