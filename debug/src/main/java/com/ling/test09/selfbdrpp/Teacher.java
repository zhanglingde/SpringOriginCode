package com.ling.test09.selfbdrpp;

/**
 * @author zhangling
 * @date 2022/1/28 11:06 上午
 */
public class Teacher {
	private String name;

	public Teacher() {
		System.out.println("创建 teacher 对象");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Teacher{" +
				"name='" + name + '\'' +
				'}';
	}
}
