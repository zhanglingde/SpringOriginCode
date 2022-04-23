package com.ling.test06.customtag;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class DogBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	/**
	 * Element 对应的类
	 * @param element the {@code Element} that is being parsed
	 * @return
	 */
	@Override
	protected Class<?> getBeanClass(Element element) {
		return Dog.class;
	}

	/**
	 * 从 element 中解析并提取对应的元素
	 *
	 * @param element the XML element being parsed
	 * @param builder used to define the {@code BeanDefinition}
	 */
	@Override
	protected void doParse(Element element, BeanDefinitionBuilder bean) {
		String weight = element.getAttribute("weight");
		String name = element.getAttribute("name");
		String color = element.getAttribute("color");

		// 将提取的数据放入 BeanDefinitionBuilder 中，待到完成所有 bean 的解析后统一注册到 beanFactory 中
		if (StringUtils.hasText(weight)) {
			bean.addPropertyValue("weight", weight);
		}
		if (StringUtils.hasText(name)) {
			bean.addPropertyValue("name", name);
		}
		if (StringUtils.hasText(color)) {
			bean.addPropertyValue("color", color);
		}
	}
}
