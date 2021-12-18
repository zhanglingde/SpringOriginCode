package com.ling.test06.selftag;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author zhangling  2021/12/17 21:20
 */
public class UserNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		// 一个标签对应一个解析器
		registerBeanDefinitionParser("user",new UserBeanDefinitionParser());
	}
}
