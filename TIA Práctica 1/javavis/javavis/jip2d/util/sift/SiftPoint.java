package javavis.jip2d.util.sift;

import java.io.Serializable;

public class SiftPoint implements Serializable{
	private static final long serialVersionUID = 12348932421112321L;
	
	//coords. del punto
	public double x,y;
	//Referencia a la imagen DoG de donde salió el punto SIFT
	public int numDoG;
	//sigma de la DoG de donde salio
	public double sigma;
	//coords. del punto en la imagen DoG de donde salio. Son doubles porque en teoria deberiamos tener precision subpixel
	public double xDog, yDog;
	//orientacion asignada al punto: direccion donde el gradiente en la zona de
	//alrededor del punto es máximo
	public double orientation;
	//descriptor de la vecindad
	public double[] descriptor;
	//nivel del punto (ratio entre el tama�o original de la imagen y el de la DoG de donde salio)
	public double level;
	
	public SiftPoint(double x, double y, int xDog, int yDog, int numDoG, double sigma, double nivel) {
		this.x = x;
		this.y = y;
		this.yDog = yDog;
		this.xDog = xDog;
		this.numDoG = numDoG;
		this.sigma = sigma;
		this.level = nivel;
	}
	
	public SiftPoint(SiftPoint p) {
		x = p.x;
		y = p.y;
		yDog = p.yDog;
		xDog = p.xDog;
		numDoG = p.numDoG;
		orientation = p.orientation;
		sigma = p.sigma;
	}
	
	public double calcDif(SiftPoint p2) {		
		double[] d2;	
		double dist = 0.0;
			
		d2 = p2.descriptor;	
		for(int i=0; i<descriptor.length; i++)
			dist += descriptor[i]*d2[i];
		return Math.acos(dist);	
			
	}
}