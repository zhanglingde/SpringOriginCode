package com.ling.test06.customtag;

public class Cat {
	private String weight;
	private String name;

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Cat{" +
				"weight=" + weight +
				", name='" + name + '\'' +
				'}';
	}
}
