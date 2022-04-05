package com.ling.test16;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

public class Life {

	@Resource
	private int id;
	@Resource
	private String name;

	public Life() {
	}

	public Life(int id, String name) {
		this.id = id;
		this.name = name;
	}

	@PostConstruct
	public void init(){
		System.out.println("init......");
	}

	@PreDestroy
	public void destroy(){
		System.out.println("destroy...");
	}


	public int getId() {
		return id;
	}

	@Resource
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
		return "Life{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
