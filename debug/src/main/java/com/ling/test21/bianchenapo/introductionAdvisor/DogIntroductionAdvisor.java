package com.ling.test21.bianchenapo.introductionAdvisor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.stereotype.Component;

/**
 * 定义一个 Advisor
 */
@Component
public class DogIntroductionAdvisor implements IntroductionAdvisor {

	// 返回拦截下来后执行的通知
	@Override
	public Advice getAdvice() {
		return new AnimalIntroductionInterceptor();
	}

	@Override
	public boolean isPerInstance() {
		return true;
	}

	// 返回被拦截的类
	@Override
	public ClassFilter getClassFilter() {
		return new ClassFilter() {
			@Override
			public boolean matches(Class<?> clazz) {
				return Dog.class.isAssignableFrom(clazz);
			}
		};
	}

	@Override
	public void validateInterfaces() throws IllegalArgumentException {

	}

	/**
	 * 生成代理对象的时候，代理对象需要实现哪些接口，在这里定义了接口，代理对象就能调用就口中的方法
	 * @return
	 */
	@Override
	public Class<?>[] getInterfaces() {
		return new Class<?>[]{Animal.class};
	}
}
