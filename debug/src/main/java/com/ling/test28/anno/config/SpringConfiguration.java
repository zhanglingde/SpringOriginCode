package com.ling.test28.anno.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("com.ling.test28.anno")    // 扫描由注解注入的Bean，service层、dao层的 @Autowired 、@Service
@Import({JdbcConfig.class, TransactionalConfig.class})  // 导入子配置类
@EnableTransactionManagement   // 开启spring对注解事务的支持  导入 ProxyTransactionManagementConfiguration.class  AutoProxyRegistrar.class
public class SpringConfiguration {


}
