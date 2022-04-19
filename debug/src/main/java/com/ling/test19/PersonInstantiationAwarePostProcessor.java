package com.ling.test19;

import com.ling.test19.populateBean.Person;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * BeanPostProcessor 中对属性赋值
 *
 * 调用 postProcessAfterInstantiation 方法来完成属性的赋值工作，可以直接终止后续的值处理工作，也可以让后续的属性完成覆盖操作，返回 true 覆盖，返回 false 不覆盖
 */
@Component
public class PersonInstantiationAwarePostProcessor implements InstantiationAwareBeanPostProcessor {

	/**
	 * 对象实例化后，也可以进行赋值
	 *
	 * @return 返回 true，表示后续会覆盖；返回 false 表示属性值不会被后续覆盖
	 */
	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		System.out.println("执行 PersonInstantiationAwarePostProcessor#postProcessAfterInstantiation... ");
		Person person = null;
		if (bean instanceof Person) {
			person = (Person) bean;
			person.setName("ling");
			return false;
		} else {
			return true;
		}
	}
}
