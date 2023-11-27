package com.ling.test21.preproxy;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 提前创建对象（BeanPostProcessor）
 */
public class PreProxyTest {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("bean21-preproxy.xml");
		BookService bookService = ctx.getBean(BookService.class);
		System.out.println("bs.getClass() = " + bookService.getClass());
		bookService.hello();

		// AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(LogAspect.class);
		// LogAspect bean = ac.getBean(LogAspect.class);
		// System.out.println("bean = " + bean);
	}
}
