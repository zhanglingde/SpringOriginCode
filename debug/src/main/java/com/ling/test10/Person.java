package com.ling.test10;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author zhangling  2021/9/2 0:08
 */
@Component
public class Person {

	private Integer id;
	@Value("${myconfig2.name}")
    private String name;

	public Person() {
	}

	public Person(Integer id) {
		this.id = id;
	}

	public Person(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Person(String name, Integer id) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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
		return "Person{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
