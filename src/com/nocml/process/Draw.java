package com.nocml.process;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import com.nocml.pojo.Line;
import com.nocml.pojo.Point;

public class Draw {
	MyCanvas p = new MyCanvas();
	public static void main(String[] args) {
		
	}
	public void addLines(List<Line> lines){
		p.addLines(lines);
	}
	public void addPoints(List<Point> points){
		p.addPoints(points);
	}
	public void paintLines(){
		JFrame f = new JFrame("画线段");
		// 不知道你的屏多大，所以直接取屏幕分辨率的大小了
		p.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		f.add(p, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		p.repaint();
	}
}



class MyCanvas extends JPanel {
	ArrayList<Line> lines;
	public void addLines(List<Line> lines) {
		if(this.lines == null)
			this.lines = new ArrayList<Line>();
		this.lines.addAll(lines);
	}
	public void addPoints(List<Point> points) {
		if(lines == null)
			lines = new ArrayList<Line>();
		for(int i = 0 ; i + 1 < points.size() ; i++){
			Line l = new Line(points.get(i), points.get(i+1));
			lines.add(l);
		}
	}

	public void paint(Graphics g) {
		System.out.println("line size to paint : " + lines.size() );
		for (Line l : lines) {
			g.setColor(new Color(255, 0, 255));
			g.drawLine((int)l.getS().x, (int)l.getS().y, (int)l.getE().x, (int)l.getE().y);
		}
	}
}