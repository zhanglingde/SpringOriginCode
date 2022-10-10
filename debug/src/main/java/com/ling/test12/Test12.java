package com.ling.test12;

import com.ling.Person;
import com.ling.test12.selfConverter.Student;
import com.ling.test12.selfConverter.StudentConverter;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Date;

public class Test12 {

	public static void main(String[] args) {
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("bean12.xml");
		Person person = ac.getBean(Person.class);
		System.out.println("person = " + person);

		DefaultConversionService conversionService = new DefaultConversionService();
		conversionService.addConverter(new StudentConverter());
		String strStudent = "24515_zhangling";
		Student student = conversionService.convert(strStudent, Student.class);
		System.out.println("student = " + student);
		// String strDate = "2022-10-10";
		// Date date = conversionService.convert(strDate, Date.class);
		// System.out.println("date = " + date);
	}
}
