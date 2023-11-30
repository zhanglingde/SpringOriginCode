package com.ling.test30.service.impl;


import com.ling.test30.Account;
import com.ling.test30.mapper.AccountMapper;
import com.ling.test30.service.AccountService;
import org.springframework.aop.framework.AopContext;
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

	@Override
	public Account findAccountByName(String name) {
		return accountMapper.findAccountByName(name);
	}

	@Autowired
	AccountService accountService;

	/**
	 * 转账
	 */
	@Override
	public void transfer(String sourceName, String targetName, Float money) {
		((AccountService) AopContext.currentProxy()).transferImpl(sourceName, targetName, money, true);
		// accountService.transferImpl(sourceName, targetName, money);
	}

	@Transactional(rollbackFor = Exception.class)
	public void transferImpl(String sourceName, String targetName, Float money, boolean flag) {
		System.out.println("before transfer....");
		Account source = accountMapper.findAccountByName(sourceName);
		Account target = accountMapper.findAccountByName(targetName);
		source.setMoney(source.getMoney() - money);
		target.setMoney(target.getMoney() + money);
		accountMapper.updateAccount(source);
		if (flag) {
			throw new RuntimeException("自定义异常");
		}
		accountMapper.updateAccount(target);
	}
}
