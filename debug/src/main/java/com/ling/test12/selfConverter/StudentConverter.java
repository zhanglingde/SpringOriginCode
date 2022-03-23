package com.ling.test12.selfConverter;

import org.springframework.core.convert.converter.Converter;

public class StudentConverter implements Converter<String,Student> {

	/**
	 * 将   1_ling 字符串格式转换成 Student
	 * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
	 * @return
	 */
	@Override
	public Student convert(String source) {
		System.out.println("====== String --> Student ========");
		Student student = new Student();
		String[] splits = source.split("_");
		student.setId(Integer.parseInt(splits[0]));
		student.setName(splits[1]);
		return student;
	}
}
