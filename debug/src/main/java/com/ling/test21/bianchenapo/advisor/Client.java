package com.ling.test21.bianchenapo.advisor;

import com.ling.test21.bianchenapo.jdk.CalculatorImpl;
import com.ling.test21.bianchenapo.jdk.ICalculator;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactory;

import java.lang.reflect.Method;

public class Client {
	public static void main(String[] args) {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(new CalculatorImpl());
		proxyFactory.addInterface(ICalculator.class);
		// Advisor 中包含 Pointcut + Advice
		proxyFactory.addAdvisor(new PointcutAdvisor() {
			@Override
			public Pointcut getPointcut() {
				AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
				pointcut.setExpression("execution(* com.ling.test21.bianchenapo.jdk.ICalculator.add(..))");
				return pointcut;
			}

			@Override
			public Advice getAdvice() {
				return new MethodInterceptor() {
					@Override
					public Object invoke(MethodInvocation invocation) throws Throwable {
						Method method = invocation.getMethod();
						String name = method.getName();
						System.out.println("before...");
						Object proceed = invocation.proceed();
						System.out.println("after...");
						return proceed;
					}
				};
			}

			@Override
			public boolean isPerInstance() {
				return true;
			}
		});

		ICalculator calculator = (ICalculator) proxyFactory.getProxy();
		calculator.add(3, 4);
		calculator.minus(3, 4);
	}
}
