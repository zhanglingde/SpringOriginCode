package com.ling.test27.bianchenTx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class TransferService2 {
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	TransactionTemplate tranTemplate;
	public void transfer(boolean flag) {
		tranTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					jdbcTemplate.update("update account set money = money + 100 where name = 'zhangsan'");
					if (flag) {
						throw new RuntimeException("自定义异常");
					}
					jdbcTemplate.update("update account set money = money - 100 where name = 'lisi'");
				} catch (DataAccessException e) {
					status.setRollbackOnly();
					e.printStackTrace();
				}
			}
		});
	}
}