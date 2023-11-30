package com.ling.test27.anno.config;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * 和事务相关的配置类
 */
public class TransactionalConfig {

	/**
	 * 用于创建事物对象管理器
	 *
	 * @param dataSource
	 * @return
	 */
	@Bean(name = "transactionManager")
	public PlatformTransactionManager createTransactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}
