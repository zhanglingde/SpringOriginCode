package com.ling.test21.bianchenapo.jdk;

public class CalculatorImpl implements ICalculator {
	@Override
	public void add(int a, int b) {
		System.out.println(a + "+" + b + "=" + (a + b));
	}

	@Override
	public int minus(int a, int b) {
		System.out.println(a + "-" + b + "=" + (a - b));
		return a - b;
	}
}
