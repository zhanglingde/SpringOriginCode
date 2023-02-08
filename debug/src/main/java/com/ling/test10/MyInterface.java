package com.ling.test10;

import org.springframework.context.annotation.Bean;

/**
 * 接口中定义 @Bean 注解处理
 *
 * {@link ConfigurationClassParser.processInterfaces}
 */
public interface MyInterface {

	@Bean
	default String show(){
		System.out.println("show...");
		return "";
	}

}
