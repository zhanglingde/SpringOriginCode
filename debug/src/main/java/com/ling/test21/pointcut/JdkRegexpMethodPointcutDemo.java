package com.ling.test21.pointcut;

import com.ling.test21.bianchenapo.jdk.CalculatorImpl;
import com.ling.test21.bianchenapo.jdk.ICalculator;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.JdkRegexpMethodPointcut;

import java.lang.reflect.Method;

public class JdkRegexpMethodPointcutDemo {
	public static void main(String[] args) {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setInterfaces(ICalculator.class);
		proxyFactory.setTarget(new CalculatorImpl());
		proxyFactory.addAdvisor(new PointcutAdvisor() {
			@Override
			public Pointcut getPointcut() {
				JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
				pointcut.setPattern("com.ling.test21.bianchenapo.jdk.ICalculator.set.*");
				pointcut.setExcludedPattern("com.ling.test21.bianchenapo.jdk.ICalculator.setA");
				return pointcut;
			}

			@Override
			public Advice getAdvice() {
				return new MethodInterceptor() {
					@Override
					public Object invoke(MethodInvocation invocation) throws Throwable {
						Method method = invocation.getMethod();
						String name = method.getName();
						System.out.println(name + " 方法开始执行了...");
						Object proceed = invocation.proceed();
						System.out.println(name + " 方法执行结束了...");
						return proceed;
					}
				};
			}

			@Override
			public boolean isPerInstance() {
				return true;
			}
		});

	}
}
