package com.nocml.calculation;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import cn.nocml.FileTool;

import com.nocml.pojo.Line;
import com.nocml.pojo.Point;
import com.nocml.pojo.Trajectory;

public class TraClus {
	
	private ArrayList<Line> lines = new ArrayList<Line>();
	private HashMap<Integer , Trajectory> trajectorys = new HashMap<Integer, Trajectory>();
	HashMap<Integer , ArrayList<Line>> cluster = new HashMap<Integer, ArrayList<Line>>();
	RTra rtra = new RTra();
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
				point.setOrder(order);
				Trajectory tra= new Trajectory();
				if(trajectorys.containsKey(lineNum))
					tra = trajectorys.get(lineNum);
				tra.insert(point);
				trajectorys.put(lineNum, tra);
			}
			scan.close();
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

	private List<Line> partition(List<Point> points){
		try{
			int start = 0;
			int end = 1;
			List<Line> list = new ArrayList<Line>();
			double cost_par , cost_nopar;
			cost_par = cost_nopar = 0.0;
			while(end < points.size()){
				cost_par = Distance.distance_mdl_par(points, start, end);
				cost_nopar = Distance.distance_mdl_nopar(points, start, end);
				if(cost_par > cost_nopar + 10){
					Line line = new Line(points.get(start), points.get(end - 1));
					line.setNum(points.get(0).getNum());
					list.add(line);
					start = end - 1 ;
				}else{
					end++;
				}
			}
			//如果只剩一个点，则加入到之前的的线段中
			if ((points.size() - 1) - start == 1) {
				list.get(list.size() - 1).setE(points.get(end - 1));
			}
			//剩多个点，则加入到最后
			else{
				Line temp = new Line(points.get(start), points.get(end - 1));
				temp.setNum(points.get(0).getNum());
				list.add(temp);
			}

			return list;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void cluster(){
		int end = lines.size();
		int clusterId = 1;
		for(int i = 0 ; i < end ; i++){
			if(i % 100 == 0)
				System.out.println("processed " + i + "...");
			Line l = lines.get(i);
			if(l.getClassifiy() == 0){
				ArrayList<Integer> neighbor = getNeighbor(i);
				if(neighbor.size() + 1 >= minLines){
					lines.get(i).setClassifiy(2);
					lines.get(i).addCluster(neighbor);
					lines.get(i).addCluster(i);
					lines.get(i).setClusterId(clusterId);
					for(int ndx : neighbor){
						lines.get(ndx).setClassifiy(2);
					}
					ExpandCluster(i , neighbor);
					clusterId++;
				}else{
					l.setClassifiy(1);
				}
			}
		}
		

		for(int i = 0 ; i < lines.size() ; i++){
			if(lines.get(i).getClusterId() > 0){
				ArrayList<Line> ls = new ArrayList<Line>();
				clusterId = lines.get(i).getClusterId();
				for(int j : lines.get(i).getCluster()){
					ls.add(lines.get(j));
				}
				cluster.put(clusterId, ls);
			}
		}
		System.out.println("cluster end...");
	} 
	public void sortLine(){
		try{
			Collections.sort(lines, new Comparator<Line>(){
				@Override
				public int compare(Line l1, Line l2) {
					return l1.getNum() - l2.getNum();
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public HashMap<Integer , ArrayList<Line>> getCluster(){
		return cluster;
	}
	public void outputCluster(String ofile){
		ArrayList<String> save = new ArrayList<String>();
		for(Entry<Integer , ArrayList<Line>> en : this.cluster.entrySet()){
			for(Line l : en.getValue()){
				save.add(en.getKey() + "\t" + l.toString());
			}
			System.out.printf("==clusterId : %d \t count:%d\n", en.getKey() , en.getValue().size());
		}
		try {
			FileTool.SaveListToFile(save, ofile, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void ExpandCluster(int center , ArrayList<Integer> neighbor){
		while(neighbor.size() > 0){
			int index = neighbor.get(0);
			ArrayList<Integer> sub_neighbor = getNeighbor(index);
			if (sub_neighbor.size() + 1>= minLines) {
				for (int ndx : sub_neighbor) {
					if (lines.get(ndx).getClassifiy() == 0 || lines.get(ndx).getClassifiy() == 1) {
						lines.get(center).addCluster(ndx);
						lines.get(ndx).setClassifiy(2);
					}
					if(lines.get(ndx).getClassifiy() == 0){
						neighbor.add(ndx);
					}
				}
			}
			neighbor.remove(0);
		}
	}
	
	private ArrayList<Integer> getNeighbor(int index){
		ArrayList<Integer> ndxs = new ArrayList<Integer>();
		Line l = lines.get(index);
		Line llong = new Line();
		Line lshort = new Line();
		for(int i = 0 ; i < lines.size() ;i++){
			if(i == index)
				continue;
			Line ltemp = lines.get(i);
			if (Distance.distance(l) >= Distance.distance(ltemp)) {
				llong = l ; lshort = ltemp;
			}else{
				llong = ltemp ; lshort = l;
			}
			double dis = Distance.dist(llong,lshort);
			if (dis <= eps) {
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
	
	public ArrayList<Trajectory> getRTrajectory( int min  , int radius){
		rtra.setParameter(min, radius);
		for(Entry<Integer, ArrayList<Line>> en : cluster.entrySet()){
			rtra.setCluster(en.getValue());
			rtra.getRTra();
			rtra.clear();
		}
		return rtra.getRTrajectory();
	}
	public void saveRtrajectory(String filepath){
		ArrayList<Trajectory> rTrajectory  = rtra.getRTrajectory();
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0 ; i < rTrajectory.size() ; i++){
			Trajectory rt = rTrajectory.get(i);
			for(Point p : rt.getPoints()){
				list.add(i + "\t" + p.toString());
			}
		}
		try {
			FileTool.SaveListToFile(list, filepath, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void overWriteLines(String ifile , int p1x , int p1y , int p2x , int p2y){
		try{
			lines.clear();
			ArrayList<String> list = FileTool.LoadListFromFile(ifile, 0, Charset.forName("utf-8"));
			for(String line : list) {
				String [] array = line.split("\t");
				Point s = new Point(Double.parseDouble(array[p1x]) , Double.parseDouble(array[p1y]));
				Point e = new Point(Double.parseDouble(array[p2x]) , Double.parseDouble(array[p2y]));
				Line l = new Line(s, e);
				l.setNum(Integer.parseInt(array[1]));
				l.setOrder(Integer.parseInt(array[0]));
				lines.add(l);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void check_tra_num(int n){
		HashMap<Integer , ArrayList<Line>> tempCluster = new HashMap<Integer, ArrayList<Line>>(cluster);
		for(Entry<Integer , ArrayList<Line>> en : tempCluster.entrySet()){
			HashSet<Integer> set = new HashSet<Integer>();
			for(Line l : en.getValue()){
				set.add(l.getNum());
			}
			if(set.size() < n){
				cluster.remove(en.getKey());
			}
		}
	}
	
	public static void main(String[] args) {
		try{
			TraClus traClus = new TraClus();
			String root = System.getProperty("user.dir") + "/";
			String filename = "Copydeer95.txt";
			traClus.load(root + filename);
			Draw draw = new Draw();
			traClus.setParameter(10, 25);
			traClus.partition();
			traClus.sortLine();
			traClus.ouputLines("ls_my.txt");
			traClus.cluster();
			traClus.outputCluster("cluster_my.txt");
			traClus.check_tra_num(10);
			traClus.outputCluster("cluster_my_check.txt");
			System.out.println(traClus.getLines().size());
			HashMap<Color , List<Line>> toDraw = new HashMap<Color, List<Line>>();
//			toDraw.put(new Color(100 , 100 ,100), traClus.getLines());
//			HashMap<Integer , ArrayList<Line>>  clusters = traClus.getCluster();
//			for(Entry<Integer , ArrayList<Line>> en : clusters.entrySet()){
//				System.out.println("cluster line " + en.getValue().size());
//				toDraw.put(new Color(0,255,0), en.getValue());
//			}
//			
			HashMap<Integer, Trajectory> trajecotrys = traClus.getTrajectorys();
			for(Entry<Integer,Trajectory> en : trajecotrys.entrySet()){
				draw.addPoints(new Color(100,100,100), en.getValue().getPoints());
			}
			
			
			draw.addLines(toDraw);
			
			ArrayList<Trajectory> rTrajectory = traClus.getRTrajectory(5,25);
			traClus.saveRtrajectory("RTra.txt");
			for(int i = 0 ; i < rTrajectory.size() ; i++){
				draw.addPoints(new Color(255,0,255), rTrajectory.get(i).getPoints());
			}
			draw.paintLines();
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
