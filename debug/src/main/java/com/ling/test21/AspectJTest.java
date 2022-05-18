package com.ling.test21;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

/**
 * 创建 Advisor
 */
@Aspect
public class AspectJTest {

	@Pointcut("execution(* com.ling.test21.TestBean.test())")
	public void test() {

	}

	@Before("test()")
	public void beforeTest() {
		System.out.println("before test...");
	}

	@After("test()")
	public void afterTest() {
		System.out.println("after test...");
	}

	@Around("test()")
	public Object aroundTest(ProceedingJoinPoint p) {
		System.out.println("around before...");
		Object o = null;
		try {
			o = p.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println("around after...");
		return o;
	}
}
