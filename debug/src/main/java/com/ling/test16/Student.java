package com.ling.test16;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * 1. 如果有多个 Autowired 且 require = true，不管有没有默认构造方法，会报异常
 * 2. 如果只有一个 Autowired 且 require 为 false，没有默认构造方法，会报警告
 * 3. 如果没有 Autowired 注解，定义了两个及以上有参数的构造方法，没有无参构造方法，就会报错 ???
 * 4. 其他情况都可以，但是以有 Autowired 的构造方法优先，然后才是默认构造方法
 */
public class Student {

	private int id;
	private String name;

	public Student() {
	}

	public Student(int id) {
		this.id = id;
	}

	// @Autowired(required = true)
	public Student(String name) {
		this.name = name;
	}

	@Autowired(required = false)
	public Student(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Student{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
