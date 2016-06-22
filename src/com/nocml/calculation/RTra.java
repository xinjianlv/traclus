package com.nocml.calculation;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.nocml.pojo.CalcBean;
import com.nocml.pojo.Line;
import com.nocml.pojo.Point;
import com.nocml.pojo.Trajectory;

public class RTra {
	private int minlines = 1;
	private double radius = 0.0;
	private double sinv ;
	private double cosv ;
	private ArrayList<Point> points = new ArrayList<Point>();
	private ArrayList<Line> cluster = new ArrayList<Line>();
	private ArrayList<Trajectory> rtrajectory = new ArrayList<Trajectory>();
	public void setParameter(int minlines , double radius){
		this.minlines = minlines;
		this.radius = radius;
	}
	public void sortPoints(){
		try{
			Collections.sort(points, new Comparator<Point>(){
				@Override
				public int compare(Point p1, Point p2) {
					if( p1.getX() >  p2.getX())
						return 1;
					if( p1.getX() < p2.getX())
						return -1;
					return 0;
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void sortLine(){
		try{
			Collections.sort(cluster, new Comparator<Line>(){
				@Override
				public int compare(Line l1, Line l2) {
					double v1 = getMinX(l1.getS(), l1.getE());
					double v2 = getMinX(l2.getS(), l2.getE());
					if(v1 > v2)
						return 1;
					if(v1 < v2)
						return -1;
					return 0;
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private double getMinX(Point p1 , Point p2){
		return Math.min(p1.getX(), p2.getX());
	}
	public ArrayList<Line> getCluster() {
		return cluster;
	}
	public void setCluster(ArrayList<Line> cluster) {
		this.cluster = cluster;
	}
	public void clear(){
		this.cluster.clear();
		this.points.clear();
	}

	public void getRTra(){
		compute_avgv();
		rotate_axes();
		sweepline();
	}
	public ArrayList<Trajectory> getRTrajectory(){
		return this.rtrajectory;
	}
	/**
	 * @description 计算所有线段的平均夹角
	 */
	private void compute_avgv(){
		//所有线段的平均x轴长度
	    double px=0;
	    //所有线段的平均y轴长度
		double py=0;
		for (Line l : cluster) {
			if (l.getS().getOrder() > l.getE().getOrder()) {
				px += (l.getS().x - l.getE().x);
				py += (l.getS().y - l.getE().y);
			} else {
				px += (l.getE().x - l.getS().x);
				py += (l.getE().y - l.getS().y);
			}
		}
		px /= cluster.size();
		py /= cluster.size();
		
		//平均线段长度
		double l=Math.sqrt(px*px+py*py);
		//cos夹角
		cosv=px/l;
		//sin夹角
		sinv=py/l;
	}
	/**
	 * @description 旋转坐标系（点不动，坐标系动）
	 */
	private void rotate_axes(){
		for(int i = 0 ; i < cluster.size() ; i++){
			Line l = cluster.get(i);
			Point s = l.getS();
			Point stemp = new Point();
			stemp.setX(s.x * cosv + s.y * sinv);
			stemp.setY(-s.x * sinv + s.y * cosv );
			points.add(stemp);
			Point e = l.getE();
			Point etemp = new Point();
			etemp.setX(e.x * cosv + e.y * sinv);
			etemp.setY(-e.x * sinv + e.y * cosv );
			points.add(etemp);
			cluster.get(i).setS(stemp);
			cluster.get(i).setE(etemp);
		}
	}

	private void sweepline(){
		sortPoints();
		sortLine();
		Trajectory tra = new Trajectory();
		double preX = 0.0;
		boolean mark = false;
		for(int i = 0 ; i < points.size() ; i++){
			Point p = points.get(i);
			CalcBean neighbor = getNeighbor(p);
			if(!mark && neighbor.getCount() > minlines){
				double avgy = neighbor.getSumy() / neighbor.getCount();
				//取旋转前的坐标
				double x = p.x * cosv-avgy*sinv;
				double y=(p.x + avgy*sinv * cosv-p.x *cosv*cosv)/sinv;
				tra.insert(new Point(x , y));
				preX = p.x;
				mark = true;
			}
			else if(neighbor.getCount() > minlines && mark){
				if(p.x - preX >= radius){
					double avgy = neighbor.getSumy() / neighbor.getCount();
					//取旋转前的坐标
					double x = p.x * cosv-avgy*sinv;
					double y=(p.x + avgy*sinv * cosv-p.x *cosv*cosv)/sinv;
					tra.insert(new Point(x , y));
					preX = p.x;
				}
			}
		}
		this.rtrajectory.add(tra);
	}
	
	private CalcBean getNeighbor(Point p){
		CalcBean bean = new CalcBean();
		for(int i = 0 ; i < cluster.size() ; i++){
			Line l = cluster.get(i);
			double max = l.getS().x;
			double min = l.getE().x;
			if(max < min){
				double temp = max ;
				max = min;
				min = temp;
			}
			if(min > p.getX())
				break;
			if(min < p.getX() && max > p.getX()){
				bean.incrementCount();
				bean.incrementSumY( getY(l , p));
			}
		}
		return bean;
	}
	private double getY(Line l , Point p){
		double disX = (l.getS().x - l.getE().x);
		if(disX == 0){
			return (l.getE().y - l.getS().y) / 2;
		}
		//斜率
		double k = (l.getS().y - l.getE().y) / disX;
		return (l.getS().y + k * (p.getX() - l.getS().x));
	}
}
