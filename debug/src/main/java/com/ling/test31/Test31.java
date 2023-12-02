package com.ling.test31;

import com.ling.test31.importSelector.TestSelector;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test31 {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TestSelector.class);
		// Cat bean = ac.getBean(Cat.class);
		// System.out.println(bean);
	}
}
