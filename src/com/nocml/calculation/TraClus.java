package com.nocml.calculation;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import cn.nocml.FileTool;

import com.nocml.pojo.Line;
import com.nocml.pojo.Point;
import com.nocml.pojo.Trajectory;
import com.nocml.process.Draw;

public class TraClus {

	private ArrayList<Line> lines = new ArrayList<Line>();
	private HashMap<Integer , Trajectory> trajectorys = new HashMap<Integer, Trajectory>();
	int minLines = 10;
	double eps = 20;
	public ArrayList<Line> getLines() {
		return lines;
	}

	public HashMap<Integer, Trajectory> getTrajectorys() {
		return trajectorys;
	}
	
	public void setParameter(int minLines , double eps){
		this.minLines = minLines;
		this.eps = eps;
	}
	
	public void load(String filename){
		try{
			Scanner scan = new Scanner(new File(filename));
			while (scan.hasNext()) {
				int order = scan.nextInt();
				int lineNum = scan.nextInt();
				double x = scan.nextDouble();
				double y = scan.nextDouble();
				Point point = new Point(x , y);
				point.setNum(lineNum);
				Trajectory tra= new Trajectory();
				if(trajectorys.containsKey(lineNum))
					tra = trajectorys.get(lineNum);
				tra.insert(point);
				trajectorys.put(lineNum, tra);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void partition(){
		for(Entry<Integer,Trajectory> en : trajectorys.entrySet()){
			List<Line> ls = partition(en.getValue().getPoints());
			lines.addAll(ls);
		}
		System.out.println("lines " + lines.size());
	}

	public List<Line> partition(List<Point> points){
		try{
			int start = 0;
			int end = 1;
			List<Line> list = new ArrayList<Line>();
			double cost_par , cost_nopar;
			cost_par = cost_nopar = 0.0;
			while(end < points.size()){
				cost_par = Distance.distance_mdl_par(points, start, end);
				cost_nopar = Distance.distance_mdl_nopar(points, start, end);
//				  System.out.printf("%f  %f\n", cost_par, cost_nopar);
				if(cost_par > cost_nopar + 10){
//					System.out.printf("segment:%d",start);
					Line line = new Line(points.get(start), points.get(end - 1));
					line.setNum(points.get(0).getNum());
					list.add(line);
					start = end - 1 ;
				}else{
					end++;
				}
			}
			if(start<end){
				//如果之前的轨迹已经划分出去
				if(cost_par > cost_nopar + 10){
					Line line = new Line(points.get(start), points.get(end - 1));
					line.setNum(points.get(0).getNum());
					list.add(line);
				}else{
					list.remove(list.size() -1);
					Line line = new Line(points.get(start), points.get(end - 1));
					line.setNum(points.get(0).getNum());
					list.add(line);
				}
			}
			return list;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public void cluster(){
		int end = lines.size();
		for(int i = 0 ; i < end ; i++){
			Line l = lines.get(i);
			if(l.getClassifiy() == 0){
				ArrayList<Integer> neighbor = getNeighbor(i);
				if(neighbor.size() > minLines){
					l.addCluster(neighbor);
					lines.remove(i);
					lines.add(i, l);
				}
			}
		}
	} 
	private void ExpandCluster(int clusterId , ArrayList<Integer> neighbor){
		for(int index : neighbor){
			ArrayList<Integer> sub_neighbor = getNeighbor(index);
			for(int nb : sub_neighbor){
				if(lines.get(nb).getClassifiy() == 0 || lines.get(nb).getClassifiy() == 1){
					
				}
			}
			
		}
	}
	private ArrayList<Integer> getNeighbor(int index){
		ArrayList<Integer> ndxs = new ArrayList<Integer>();
		Line l = lines.get(index);
		Line llong = new Line();
		Line lshort = new Line();
		for(int i = 0 ; i < lines.size() ;i++){
			Line ltemp = lines.get(i);
			if (Distance.distance(l) >= Distance.distance(ltemp)) {
				llong = l ; lshort = ltemp;
			}else{
				llong = ltemp ; lshort = l;
			}
			if (Distance.dist(llong.getS(), llong.getE(), lshort.getS(),	lshort.getE()) > eps) {
				ndxs.add(i);
			}
		}
		return ndxs;
	}
	public void ouputLines(String ofile){
		List<String> ls = new ArrayList<String>();
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		int order = 0;
		for(Line l : lines){
			l.setOrder(order++);
			ls.add(l.toString());
			if(map.containsKey(l.getNum())){
				map.put(l.getNum(), map.get(l.getNum()) + 1);
			}else{
				map.put(l.getNum() , 1);
			}
		}
		try {
			FileTool.SaveListToFile(ls, ofile, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(Entry<Integer , Integer> en : map.entrySet()){
			System.out.println(en.getKey() + "\t" + en.getValue());
		}
	}
	
	public static void main(String[] args) {
		try{
			TraClus traClus = new TraClus();
			String root = System.getProperty("user.dir") + "/";
			String filename = "Copydeer95.txt";
			traClus.load(root + filename);
			Draw draw = new Draw();
			traClus.partition();
			traClus.ouputLines("ls.txt");
			System.out.println(traClus.getLines().size());
			draw.addLines(traClus.getLines());
			draw.paintLines();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
