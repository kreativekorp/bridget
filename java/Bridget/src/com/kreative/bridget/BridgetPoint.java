package com.kreative.bridget;

import java.io.Serializable;

// identical to java.awt.Point, with one important difference:
// it doesn't make Mac OS X bring up the GUI
public class BridgetPoint implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	public int x;
	public int y;
	
	public BridgetPoint() {
		x = 0; y = 0;
	}
	
	public BridgetPoint(double x, double y) {
		this.x = (int)x; this.y = (int)y;
	}
	
	public BridgetPoint(int x, int y) {
		this.x = x; this.y = y;
	}
	
	public BridgetPoint(java.awt.Point p) {
		x = p.x; y = p.y;
	}
	
	public BridgetPoint(java.awt.geom.Point2D p) {
		x = (int)p.getX(); y = (int)p.getY();
	}
	
	public BridgetPoint(BridgetPoint p) {
		x = p.x; y = p.y;
	}
	
	public Object clone() {
		return new BridgetPoint(x,y);
	}
	
	public double distance(double x, double y) {
		return Math.sqrt((this.x - x) * (this.x - x) + (this.y - y) * (this.y - y));
	}
	
	public double distance(int x, int y) {
		return Math.sqrt((this.x - x) * (this.x - x) + (this.y - y) * (this.y - y));
	}
	
	public double distance(java.awt.Point p) {
		return Math.sqrt((this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y));
	}
	
	public double distance(java.awt.geom.Point2D p) {
		return Math.sqrt((this.x - p.getX()) * (this.x - p.getX()) + (this.y - p.getY()) * (this.y - p.getY()));
	}
	
	public double distance(BridgetPoint p) {
		return Math.sqrt((this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y));
	}
	
	public double distanceSq(double x, double y) {
		return (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y);
	}
	
	public int distanceSq(int x, int y) {
		return (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y);
	}
	
	public int distanceSq(java.awt.Point p) {
		return (this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y);
	}
	
	public double distanceSq(java.awt.geom.Point2D p) {
		return (this.x - p.getX()) * (this.x - p.getX()) + (this.y - p.getY()) * (this.y - p.getY());
	}
	
	public int distanceSq(BridgetPoint p) {
		return (this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y);
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof BridgetPoint) {
			BridgetPoint p = (BridgetPoint)obj;
			return (x == p.x && y == p.y);
		}
		else if (obj instanceof java.awt.Point) {
			java.awt.Point p = (java.awt.Point)obj;
			return (x == p.x && y == p.y);
		}
		else if (obj instanceof java.awt.geom.Point2D) {
			java.awt.geom.Point2D p = (java.awt.geom.Point2D)obj;
			return (x == p.getX() && y == p.getY());
		}
		else return false;
	}
	
	public java.awt.Point getLocation() {
		return new java.awt.Point(x,y);
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public int hashCode() {
		return x ^ Integer.reverse(y);
	}
	
	public void move(int x, int y) {
		this.x = x; this.y = y;
	}
	
	public void setLocation(double x, double y) {
		this.x = (int)x; this.y = (int)y;
	}
	
	public void setLocation(int x, int y) {
		this.x = x; this.y = y;
	}
	
	public void setLocation(java.awt.Point p) {
		x = p.x; y = p.y;
	}
	
	public void setLocation(java.awt.geom.Point2D p) {
		x = (int)p.getX(); y = (int)p.getY();
	}
	
	public void setLocation(BridgetPoint p) {
		x = p.x; y = p.y;
	}
	
	public String toString() {
		return x + "," + y;
	}
	
	public void translate(int x, int y) {
		this.x += x; this.y += y;
	}
}
