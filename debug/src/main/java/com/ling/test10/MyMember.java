package com.ling.test10;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


/**
 * 递归处理内部类 	{@link ConfigurationClassParser.doProcessConfigurationClass}
 * 内部类有注解，会在 doProcessConfigurationClass 方法中进行递归处理
 */
@Component
@Configuration
public class MyMember {

	@Component
	@Configuration
	@ComponentScan
	class innerClass{

	}
}
