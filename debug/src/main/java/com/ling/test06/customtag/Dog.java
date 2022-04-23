package com.ling.test06.customtag;

public class Dog {
	private String weight;
	private String name;
	private String color;

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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "Dog{" +
				"weight='" + weight + '\'' +
				", name='" + name + '\'' +
				", color='" + color + '\'' +
				'}';
	}
}
