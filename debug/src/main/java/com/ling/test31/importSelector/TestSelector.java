package com.ling.test31.importSelector;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AnimalImportSelector.class)
public class TestSelector {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TestSelector.class);
		Cat bean = ac.getBean(Cat.class);
		System.out.println(bean);
	}
}
