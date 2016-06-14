package com.nocml.pojo;

public class Point {
	public double x;
	public double y;
	int num = -1;
	public Point(){
		
	}
	public Point(double x , double y){
		this.x = x;
		this.y = y;
	}
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	@Override
	public String toString() {
		return x + "\t" + y;
	}
	
}
