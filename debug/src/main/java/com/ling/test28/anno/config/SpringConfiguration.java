package com.ling.test28.anno.config;

import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("com.ling.test28.anno")    // 扫描由注解注入的Bean，service层、dao层的 @Autowired 、@Service
@Import({JdbcConfig.class, TransactionalConfig.class})  // 导入子配置类
// 开启spring对注解事务的支持  导入 ProxyTransactionManagementConfiguration.class  AutoProxyRegistrar.class
@EnableTransactionManagement
@EnableAspectJAutoProxy(exposeProxy = true)
public class SpringConfiguration {


}
