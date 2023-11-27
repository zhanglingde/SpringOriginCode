package com.ling.test21.notProxy;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Aspect
@EnableAspectJAutoProxy
@ComponentScan
public class LogAspect {


	@Pointcut("execution(* com.ling.test21.notProxy.service.*.*(..))")
	public void pc() {

	}

	@Before("pc()")
	public void before(JoinPoint jp) {
		String name = jp.getSignature().getName();
		System.out.println(name + " 方法开始执行了...");
	}
}
