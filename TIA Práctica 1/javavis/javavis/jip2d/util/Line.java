package javavis.jip2d.util;

// used by FHoughLine

public class Line {
	double lTheta;
	double lRho; 
	public Line(double d1, double d2) {
		lTheta = d1;
		lRho = d2;
	}
	public double getTheta()  {return lTheta;}
	public double getRho()  {return lRho;}
}