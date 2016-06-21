package com.nocml.pojo;

public class CalcBean {
	private int count = 0;
	private double sumy = 0;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getSumy() {
		return sumy;
	}
	public void setSumy(double sumy) {
		this.sumy = sumy;
	}
	public void incrementCount(){
		count++;
	}
	public void incrementSumY(double sumy){
		this.sumy += sumy;
	}
}
