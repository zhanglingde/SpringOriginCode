package com.ling.test05;


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

	public void initMethod() {
		System.out.println("person init ...");
	}


	public void destroyMethod() {
		System.out.println("person destroy ...");
	}
}
