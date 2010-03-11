package javavis.jip2d.util;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import javavis.base.JIPException;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;

/**
* Auxiliary class to manage blobs.<BR>
*/
public class Blob {
	private static Logger logger = Logger.getLogger(Blob.class);
	
	public ArrayList<Integer> lista_x;
	
	public ArrayList<Integer> lista_y;

	public int centro_x;
	public int centro_y;
	public int minx;
	public int miny;
	public int maxx;
	public int maxy;
	public int maxDistX;
	public int maxDistY;
	public int xsize;
	public int ysize;
	

	public boolean valido=false;

	public Blob() {
		lista_x = new ArrayList<Integer>();
		lista_y = new ArrayList<Integer>();
	}
	
	public void calcEverything () {
		calcCentroid();
		xSize();
		ySize();
		calcDistMax();
	}
	
	public JIPImgBitmap getImage () {
		calcEverything();
		try {
			JIPBmpBit img = new JIPBmpBit(xsize, ysize);
			for (int i=0; i<lista_x.size(); i++) 
				img.setPixelBool(lista_x.get(i)-minx, lista_y.get(i)-miny, true);
			return img;
		}catch (JIPException e){logger.error("Blob: "+e); return null;}
	}

	public void calcCentroid() {
		int l = lista_x.size();

		if (l == 0) 
			valido = false;
		else {
			for (int i=0; i<l; i++) {
				centro_x += lista_x.get(i);
				centro_y += lista_y.get(i);
			}
			valido = true;
			centro_x /= l;
			centro_y /= l;
		}
	}
	
	public int length() {
		return lista_x.size();
	}
	
	/**
	 * Calculates the maximum distance from the centroid to
	 * one pixel in the blob  
	 */
	private void calcDistMax () {
		maxDistX=0;
		maxDistY=0;
		calcCentroid();
		for (int i=0; i<lista_x.size(); i++) {
			maxDistX = Math.max(maxDistX, Math.abs(centro_x - lista_x.get(i)));
			maxDistY = Math.max(maxDistY, Math.abs(centro_y - lista_y.get(i)));
		}
	}
	
	/**
	 * It returns the x size of the blob
	 */
	private void xSize () {
		minx=100000;
		maxx=0;
		int aux;

		for (int i=0; i<lista_x.size(); i++) {
			aux=lista_x.get(i);
			if (minx > aux) minx=aux;
			if (maxx < aux) maxx=aux;
		}
		
		xsize = maxx+1-minx;
	}
	
	/**
	 * It returns the y size of the blob
	 */
	private void ySize () {
		miny=100000;
		maxy=0;
		int aux;

		for (int i=0; i<lista_y.size(); i++) {
			aux=lista_y.get(i);
			if (miny > aux) miny=aux;
			if (maxy < aux) maxy=aux;
		}
		
		ysize = maxy+1-miny;
	}
}
