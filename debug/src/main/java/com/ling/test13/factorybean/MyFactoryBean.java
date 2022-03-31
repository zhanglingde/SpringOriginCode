package com.ling.test13.factorybean;

import org.springframework.beans.factory.FactoryBean;

/**
 * 需要交给 Spring 控制
 */
public class MyFactoryBean implements FactoryBean<Student> {

	@Override
	public Student getObject() throws Exception {
		return new Student("zhangling");
	}

	@Override
	public Class<?> getObjectType() {
		return Student.class;
	}

	@Override
	public boolean isSingleton() {
		return FactoryBean.super.isSingleton();
	}
}
