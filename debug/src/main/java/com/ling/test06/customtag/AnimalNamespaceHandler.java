package com.ling.test06.customtag;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Animal 命名空间初始化
 */
public class AnimalNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		// 解析 animal 命名空间下的 cat 标签 <animal:cat 标签
		registerBeanDefinitionParser("cat", new CatBeanDefinitionParser());
		registerBeanDefinitionParser("dog", new DogBeanDefinitionParser());
	}
}
