package com.ling.test31.deferredImportSelector;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MyDeferredImportSelector.class)
public class TestDeferredImport {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TestDeferredImport.class);
		MyBeanDefinition bean = ac.getBean(MyBeanDefinition.class);
		System.out.println(bean);
	}
}
