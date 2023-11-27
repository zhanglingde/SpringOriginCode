package com.ling.test21.notProxy.service;

import org.springframework.stereotype.Service;

@Service("com.ling.test21.notProxy.service.BookService.ORIGINAL")
public class BookService {

	public void read(){
		System.out.println("read book...");
	}
}
