package com.ling.test21.pointcut;

import com.ling.test21.bianchenapo.jdk.CalculatorImpl;
import com.ling.test21.bianchenapo.jdk.ICalculator;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.ControlFlowPointcut;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Method;

public class ControlFlowPointcutDemo {
	public static void main(String[] args) {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(new CalculatorImpl());
		proxyFactory.addInterface(ICalculator.class);
		proxyFactory.addAdvisor(new PointcutAdvisor() {
			@Override
			public Pointcut getPointcut() {
				return new ControlFlowPointcut(ControlFlowPointcutDemo.class, "evl");
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
		ICalculator calculator = (ICalculator) proxyFactory.getProxy();
		calculator.add(3, 4);
		calculator.minus(3, 4);
		evl(calculator);

	}

	public static void evl(ICalculator iCalculator) {
		iCalculator.add(3, 4);
	}
}
