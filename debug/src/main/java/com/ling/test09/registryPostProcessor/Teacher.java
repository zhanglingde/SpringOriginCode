package com.ling.test09.registryPostProcessor;

/**
 * @author zhangling
 * @date 2022/1/28 11:06 上午
 */
public class Teacher {

	private Long id;
	private String name;

	public Teacher() {
		System.out.println("8. 执行 bean 的创建，创建 teacher 对象");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Teacher{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
