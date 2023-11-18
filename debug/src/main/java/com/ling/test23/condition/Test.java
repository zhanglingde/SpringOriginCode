package com.ling.test23.condition;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		// 先加载 BConfig，后加载 AConfig；加载 BConfig 的时候，AConfig 还不存在，因此 BConfig 不会被加载
		ctx.register(BConfig.class, AConfig.class);
		ctx.refresh();
		String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
		for (String beanDefinitionName : beanDefinitionNames) {
			System.out.println(beanDefinitionName);
		}
	}
}
