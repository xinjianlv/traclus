package com.nocml.pojo;

public class Utils {
	public static double log(double num, double base) {
		try {
			return (Math.log(num) / Math.log(base));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1.0D;
	}
}
