package javavis.jip2d.functions;

import java.util.ArrayList;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.geometrics.JIPGeomPoly;
import javavis.jip2d.functions.FBinarize;
import javavis.jip2d.functions.FCanny;
import javavis.jip2d.functions.FGrayToGray;
import javavis.jip2d.functions.FHoughCirc;
import javavis.jip2d.functions.FRGBToColor;
import javavis.jip2d.functions.FSegmentHSB;
import javavis.jip2d.util.Circumference;


/**
* It uses Canny function to find edges in the original image. This image
* is changed to grey scale and before it is changed to binary then it is passed to FHoughCirc
* function, which detect circumferences which have some restriction. Next, the color of the 
* original image is segmented, for it, we have to use FRGToHSB, and in the result of H 
* band we apply FSegmentHSB to can mark the pixels which are from the desired color.
* Finally, CuentaMonedas has to decide that circumferences are valid. It finds which is the
* circumference that the pixels are marked with 1.
* A circumference will be coin if the number of pixels in it is mare than 40 per cent
* of its area.        
*<ul><B>Input parameters:</B><BR>
*<li>Image to process.<BR>
*<li>thres: minimum percentage of votes which a circumference has to own to be accepted.
*It will be admitted when the number of votes are more than or equal to '(umbral/100) * 2* Pi * r'. <BR>
*<li>Rmin: minimum radius we allow to owner circumference<BR>
*<li>Rmax: maximum radius we allow to owner circumference<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Image with the circumferences which have been recogniced as coin.<BR>
*</ul>
*/

public class FCountCoins extends JIPFunction {
	private static final long serialVersionUID = -5337180104629350329L;

	public FCountCoins() {
		super();
		name = "FCountCoins";
		description = "Application which counts the number of coins in a image.";
		groupFunc = FunctionGroup.Applic;
		JIPParamInt p7 = new JIPParamInt("thres", false, true);
		p7.setDefault(25);
		p7.setDescription("Minimum percentage of votes");
		JIPParamInt p8 = new JIPParamInt("Rmin", false, true);
		p8.setDefault(10);
		p8.setDescription("Minimum radius");
		JIPParamInt p9 = new JIPParamInt("Rmax", false, true);
		p9.setDefault(80);
		p9.setDescription("Maximum radius");
		JIPParamFloat p1 = new JIPParamFloat("h", false, true);
		p1.setDefault(0.17f);
		p1.setDescription("Value of Hue");
		JIPParamFloat p2 = new JIPParamFloat("herror", false, true);
		p2.setDefault(0.03f);
		p2.setDescription("Value of Hue error");
		JIPParamFloat p3 = new JIPParamFloat("s", false, true);
		p3.setDefault(0.35f);
		p3.setDescription("Value of Saturation");
		JIPParamFloat p4 = new JIPParamFloat("serror", false, true);
		p4.setDefault(0.1f);
		p4.setDescription("Value of Saturation error");
		
		addParam(p1);
		addParam(p2);
		addParam(p3);
		addParam(p4);
		addParam(p7);
		addParam(p8);
		addParam(p9);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img.getType() != ImageType.COLOR) 
			throw new JIPException("CountCoins only applied to COLOR images");
			
		int umbral = getParamValueInt("thres");
		int rmin =  getParamValueInt("Rmin");
		int rmax =  getParamValueInt("Rmax");
		int w = img.getWidth();
		int h = img.getHeight();

		/* Applies Canny method */
		JIPFunction canny = new FCanny();
		canny.setParamValue("brightness", 200);
		JIPImage res = canny.processImg(img);

		/* Converts the image in Byte type.*/
		JIPFunction gray = new FGrayToGray();
		gray.setParamValue("gray", "BYTE");
		JIPImage aux = gray.processImg(res);

		/* Binarizes.*/
		JIPFunction binarizar = new FBinarize();
		binarizar.setParamValue("u1", 110);
		binarizar.setParamValue("u2", 255);
		res = binarizar.processImg(aux);

		/* Converts from RGB to HSB.*/
		JIPFunction rgbToH  = new FRGBToColor();
		rgbToH.setParamValue("format", "HSB");
		aux = rgbToH.processImg(img);

		/* Segments the HSB image in order to get the coins pixels.*/
		JIPFunction segmentar = new FSegmentHSB();
		segmentar.setParamValue("h", getParamValueFloat("h"));
		segmentar.setParamValue("herror", getParamValueFloat("herror"));
		segmentar.setParamValue("s", getParamValueFloat("s"));
		segmentar.setParamValue("serror", getParamValueFloat("serror"));
		JIPBmpBit hsbbin = (JIPBmpBit)segmentar.processImg(aux);

		/* Obtains the Hough transform of the binary image*/
		FHoughCirc hough = new FHoughCirc();
		hough.setParamValue("thres", umbral);
		hough.setParamValue("Rmin", rmin);
		hough.setParamValue("Rmax", rmax);
		hough.processImg(res);

		int num = hough.getParamValueInt("ncirc");

		/* Array with votes from pixels in the image (a pixel votes for a
		 * circumference if it is included in it.*/
		int miArray[] = new int[num];
		for (int i = 0; i < num; i++)
			miArray[i] = 0;
		ArrayList<Circumference> vecCirc = (ArrayList<Circumference>)hough.getParamValueObj("circum");
		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++)
				if (hsbbin.getPixelBool(i, j))
					for (int cont = 0; cont < num; cont++)
						if (pertenece(i, j, (Circumference) vecCirc.get(cont)))
							miArray[cont]++;

		int monedas = 0;
		ArrayList<ArrayList<Integer>> puntos_d_circ = new ArrayList<ArrayList<Integer>>();
		/* We accept circumferences with a number of votes more than 40 percent.*/
		for (int i = 0; i < num; i++) {
			Circumference c2 = (Circumference) vecCirc.get(i);
			if (miArray[i] > (0.5 * Math.PI * c2.radio * c2.radio)) {
				puntos_d_circ.add(Circumference.getPoints((Circumference) vecCirc.get(i)));
				monedas++;
			}
		}

		info = "Number of Coins found: " + monedas;
		JIPGeomPoly resFin = new JIPGeomPoly(w, h);
		resFin.setData(puntos_d_circ);
		return resFin;
	}

	/**
	  * It decides if a (x,y) point belong to a circumference.<BR>
	  *<ul><B>Input parameters:</B><BR>
	  *<li>x: 'x' coordinate of the study point.<BR>
	  *<li>y: 'y' coordinate of the study point.<BR>
	  	  *<li>circ: Circumference where we check if the recibed point is included.<BR><BR>
	  *</ul>
	  *<ul><B>Output parameters:</B><BR>
	  *<li>It returns true if the point belongs to circunference, else return false.<BR><BR>
	  *</ul>
	  */
	public boolean pertenece(int x, int y, Circumference circ) {
		int c = x - circ.centroX;
		int d = y - circ.centroY;
		int r = (int) Math.sqrt(c * c + d * d);
		if (r <= circ.radio) return true;
		else return false;
	}
}
