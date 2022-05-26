package com.ling.test28.anno.dao.impl;


import com.ling.test28.anno.Account;
import com.ling.test28.anno.dao.AccountDao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 账户的持久层实现类
 */
@Repository("accountDao")
public class AccountDaoImpl extends JdbcDaoSupport implements AccountDao {

	@Override
	public Account findAccountById(Integer accountId) {
		List<Account> accounts = super.getJdbcTemplate().query("select * from account where id = ?", new BeanPropertyRowMapper<Account>(Account.class), accountId);
		return accounts.isEmpty() ? null : accounts.get(0);
	}

	@Override
	public Account findAccountByName(String accountName) {
		List<Account> accounts = super.getJdbcTemplate().query("select * from account where name = ?", new BeanPropertyRowMapper<Account>(Account.class), accountName);
		if (accounts.isEmpty()) {
			return null;
		}
		if (accounts.size() > 1) {
			throw new RuntimeException("结果集不唯一");
		}
		return accounts.get(0);
	}

	@Override
	public void updateAccount(Account account) {
		super.getJdbcTemplate().update("update account set name=?,money=? where id=?", account.getName(), account.getMoney(), account.getId());
	}
}
