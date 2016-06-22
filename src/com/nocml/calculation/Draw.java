package com.nocml.calculation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.nocml.pojo.Line;
import com.nocml.pojo.Point;

public class Draw {
	MyCanvas p = new MyCanvas();
	public static void main(String[] args) {
		
	}
	public void addLines(HashMap<Color ,List<Line>> lines){
		p.addLines(lines);
	}
	public void addPoints(Color color , List<Point> points){
		p.addPoints(color , points);
	}
	public void paintLines(){
		JFrame f = new JFrame("效果图");
		// 取屏幕分辨率的大小
		p.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		f.add(p, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		p.repaint();
	}
}



class MyCanvas extends JPanel {
	private static final long serialVersionUID = 1L;
	HashMap<Color , List<Line>> lines;
	public void addLines(HashMap<Color , List<Line>> lines) {
		if(this.lines == null)
			this.lines = new HashMap<Color, List<Line>>();
		this.lines.putAll(lines);
	}
	public void addPoints(Color color , List<Point> points) {
		if(lines == null)
			lines = new HashMap<Color, List<Line>>();
		List<Line> list = new ArrayList<Line>();
		for(int i = 0 ; i + 1 < points.size() ; i++){
			Line l = new Line(points.get(i), points.get(i+1));
			list.add(l);
		}
		if(lines.containsKey(color))
			list.addAll(lines.get(color));
		lines.put(color, list);
	}

	public void paint(Graphics g) {
		System.out.println("line size to paint : " + lines.size() );
		for (Entry<Color , List<Line>> en : lines.entrySet()) {
			g.setColor(en.getKey());
			for(Line l : en.getValue())
				g.drawLine((int)l.getS().x, (int)l.getS().y, (int)l.getE().x, (int)l.getE().y);
		}
	}
}