package com.nocml.process;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import javax.swing.*;

import com.nocml.pojo.Line;

public class CopyOfDraw {
	@SuppressWarnings("unchecked")
	// 去掉泛型的警告信息
	public static void main(String[] args) throws Exception {
		File file = new File("data.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String str = "";
		JFrame f = new JFrame("画线段");
		MyCanvas p = new MyCanvas();
		// 不知道你的屏多大，所以直接取屏幕分辨率的大小了
		p.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		f.add(p, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ArrayList<int[]> array = new ArrayList();
		// 把每行数字转为数组加入ArrayList
		while ((str = br.readLine()) != null) {
			// 文件中的数字是用空格分的吧
			String[] temp = str.split(" ");
			int[] i = new int[temp.length];
			int n = 0;
			for (String s : temp) {
				i[n++] = Integer.parseInt(s);
			}
			array.add(i);
		}
		p.setMyCanvas(array);// 把array传给画图对象
		p.repaint();// 开始画图
	}
	public static void paintLines(ArrayList<Line> lines){
		MyCanvas pen = new MyCanvas();
		JFrame f = new JFrame("画线段");
		MyCanvas p = new MyCanvas();
		// 不知道你的屏多大，所以直接取屏幕分辨率的大小了
		p.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		f.add(p, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		for(Line l : lines){
			
		}
	}
}



class MyCanvas extends JPanel {
	ArrayList<int[]> array;
	
	public void setMyCanvas(ArrayList<int[]> array) {
		this.array = array;
	}

//	public void paint(Graphics g) {
//		for (int[] i : array) {
//			g.setColor(new Color(i[4], i[5], i[6]));
//			g.drawLine(i[0], i[1], i[2], i[3]);
//		}
//	}
	public void paint(Graphics g) {
		for (int[] i : array) {
			g.setColor(new Color(i[4], i[5], i[6]));
			g.drawLine(i[0], i[1], i[2], i[3]);
		}
	}
}