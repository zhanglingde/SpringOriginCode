package com.ling.test06.customtag;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Animal 命名空间处理
 */
public class AnimalNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		// 遇到自定义标签 <cat:aaa 这样以 cat 开头的元素，将该元素交给 CatBeanDefinitionParser 解析
		registerBeanDefinitionParser("cat", new CatBeanDefinitionParser());
		registerBeanDefinitionParser("dog", new DogBeanDefinitionParser());
	}
}
