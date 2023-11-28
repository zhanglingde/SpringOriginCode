package com.ling.test27.bianchenTx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
public class TransferService {
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	PlatformTransactionManager txManager;

	public void transfer(boolean flag) {
		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		TransactionStatus status = txManager.getTransaction(definition);
		try {
			jdbcTemplate.update("update account set money = money + 100 where name = 'zhangsan'");
			if (flag) {
				throw new RuntimeException("自定义异常");
			}
			jdbcTemplate.update("update account set money = money - 100 where name = 'lisi'");
			txManager.commit(status);
		} catch (DataAccessException e) {
			e.printStackTrace();
			txManager.rollback(status);
		}
	}
}