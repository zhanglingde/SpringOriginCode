package com.ling.test10;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 递归处理内部类 	{@link ConfigurationClassParser.doProcessConfigurationClass}
 * 内部类有注解，会在 doProcessConfigurationClass 方法中进行递归处理
 * @ComponentScan 属性将被扫描到进行处理
 */
@Configuration
@ComponentScan("com.ling.test10")
public class MyComponentScan {


	@Configuration
	@ComponentScan("com.ling.test10")
	class InterClass{

	}
}
