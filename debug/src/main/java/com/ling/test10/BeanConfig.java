package com.ling.test10;

import com.ling.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


@Conditional({WindowsCondition.class})
@Configuration
public class BeanConfig {

	@Bean(name = "zhang")
	public Person person1(){
		return new Person(18,"zhang");
	}

	@Bean(name = "ling")
	public Person person2(){
		return new Person(26,"ling");
	}
}
