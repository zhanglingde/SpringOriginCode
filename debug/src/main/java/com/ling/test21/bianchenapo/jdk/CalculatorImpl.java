package com.ling.test21.bianchenapo.jdk;

import com.ling.test21.pointcut.MyAction;

@MyAction
public class CalculatorImpl implements ICalculator {
	@Override
	public void add(int a, int b) {
		System.out.println(a + "+" + b + "=" + (a + b));
	}

	@MyAction
	@Override
	public int minus(int a, int b) {
		System.out.println(a + "-" + b + "=" + (a - b));
		return a - b;
	}

	@Override
	public void setA() {
		System.out.println("setA...");
	}
}
