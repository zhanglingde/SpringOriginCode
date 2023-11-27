package com.ling.test21.notProxy;

import com.ling.test21.notProxy.service.BookService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class NotProxyTest {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(LogAspect.class);
		BookService bookService = ac.getBean(BookService.class);
		bookService.read();
		System.out.println("bookService = " + bookService);
	}
}
