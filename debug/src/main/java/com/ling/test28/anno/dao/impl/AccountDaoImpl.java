package com.ling.test28.anno.dao.impl;


import com.ling.test28.anno.Account;
import com.ling.test28.anno.dao.AccountDao;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 账户的持久层实现类
 */
@Repository("accountDao")
public class AccountDaoImpl implements AccountDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Account findAccountById(Integer accountId) {
		List<Account> accounts = jdbcTemplate.query("select * from account where id = ?",new BeanPropertyRowMapper<Account>(Account.class),accountId);
		return accounts.isEmpty()?null:accounts.get(0);
	}

	@Override
	public Account findAccountByName(String accountName) {
		List<Account> accounts = jdbcTemplate.query("select * from account where name = ?",new BeanPropertyRowMapper<Account>(Account.class),accountName);
		if(accounts.isEmpty()){
			return null;
		}
		if(accounts.size()>1){
			throw new RuntimeException("结果集不唯一");
		}
		return accounts.get(0);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateAccount(Account account) {
		jdbcTemplate.update("update account set name=?,money=? where id=?",account.getName(),account.getMoney(),account.getId());
	}
}

