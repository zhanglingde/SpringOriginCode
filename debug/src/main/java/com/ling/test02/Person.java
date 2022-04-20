package com.ling.test02;

/**
 * @author zhangling  2021/9/2 0:08
 */
public class Person {

	private Integer id;
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
