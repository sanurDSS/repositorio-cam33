package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamBool;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*Swoop all points in the image in relation to the input angle and one of its sides.<BR>
*Applicable to: BYTE, WORD, COLOR and REAL<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input iamge<BR>
*<li>angle: Angle of swooped, > 0, it swoops to the right, <0 on left. Maximuns (-89, +89)<BR>
*<li>clipping: If true, mantains the original dimensions.<BR>
*<li>side: Side to swoop: BOTTOM, UP, LEFT, RIGHT<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Swooped image with the same type as the input image.<BR><BR>
*</ul>
*/
public class FSkew extends JIPFunction {
	private static final long serialVersionUID = -2431379428816308525L;

	public FSkew() {
		super();
		name = "FSkew";
		description =
			"Swoops all the points of the image in relation to the input angle and one of its sides.";
		groupFunc = FunctionGroup.Manipulation;

		JIPParamInt p1 = new JIPParamInt("angle", false, true);
		p1.setDefault(15);
		p1.setDescription("Swooping angle. <0 swoops on left, >0 on right.");

		JIPParamBool p2 = new JIPParamBool("clipping", false, true);
		p2.setDefault(false);
		p2.setDescription("Mantains the original dimensions.");
		
		JIPParamList p3 = new JIPParamList("side", false, true);
		String []paux = new String[4];
		paux[0]="BOTTOM";
		paux[1]="UP";
		paux[2]="LEFT";
		paux[3]="RIGHT";
		p3.setDefault(paux);
		p3.setDescription("Side to apply the skew");

		addParam(p1);
		addParam(p2);
		addParam(p3);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		ImageType t = img.getType();		
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("Skew can not be applied to this image format");

		JIPImgBitmap res = null;
		int p1 =  getParamValueInt("angle");
		if (Math.abs(p1) > 89 || Math.abs(p1) < -89) 
			throw new JIPException("Angle is incorrect");

		boolean p2 =  getParamValueBool("clipping");
		String a =  getParamValueString("side");
		boolean p3=false, p4=false, p5=false, p6=false;
		if (a.equals("BOTTOM")) p3=true;
		if (a.equals("LEFT")) p4=true;
		if (a.equals("RIGHT")) p5=true;
		if (a.equals("UP")) p6=true;
		JIPImgBitmap imgBmp = (JIPImgBitmap)img;

		int w = img.getWidth();
		int h = img.getHeight();
		int b = imgBmp.getNumBands();
		double ang = Math.toRadians(Math.abs(p1));
		double sa = Math.sin(ang);
		int ntam = 0;
		
		if (p3 || p6) {
			ntam = (int) (w + h * sa);
			if (!p2) res = (JIPImgBitmap)JIPImage.newImage(b, ntam, h, t);
			else res = (JIPImgBitmap)JIPImage.newImage(b, w, h, t);
		}
		else {
			ntam = (int) (h + w * sa);
			if (!p2) res = (JIPImgBitmap)JIPImage.newImage(b, w, ntam, t);
			else res = (JIPImgBitmap)JIPImage.newImage(b, w, h, t);
		}
		
		double[] bmpflo, binflo;
		for (int nb = 0; nb < b; nb++) {
			bmpflo = imgBmp.getAllPixels(nb);
			binflo = new double[ntam * h];
			int pos=0;
			for (int y = 0; y < h; y++)
				for (int x = 0; x < w; x++) {
					if (p1 > 0) {
						if (p3) pos = (int) (x + (h - y) * sa) + (y * ntam);
						if (p4) pos = (int) (y + (w - (w - x)) * sa) * w + x;
						if (p5) pos = (int) (y + (w - x) * sa) * w + x;
						if (p6) pos = (int) (x + (h - (h - y)) * sa) + (y * ntam);
					}
					else {
						if (p3) pos = (int) (x + (h - (h - y)) * sa) + (y * ntam);
						if (p4) pos = (int) (y + (w - x) * sa) * w + x;
						if (p5) pos = (int) (y + (w - (w - x)) * sa) * w + x;
						if (p6) pos = (int) (x + (h - y) * sa) + (y * ntam);
					}
					binflo[pos] = bmpflo[x + y * w];
				}
			if (p2) {
				double[] auxflo = new double[w * h];
				for (int x = 0; x < w; x++)
					for (int y = 0; y < h; y++) {
						if (p1 > 0) {
							auxflo[x + y * w] = binflo[x + y * ntam];
						}
						else {
							if (p3 || p6) 
								auxflo[x + y * w] = binflo[(int) (x + h * sa) + (y * ntam)];
							if (p4 || p5) 
								auxflo[x + y * w] = binflo[(int) (y + w * sa) * w + x];
						}
					}
				res.setAllPixels(nb, auxflo);
			} 
			else {
				res.setAllPixels(nb, binflo);
			}
		}
		return res;
	}
}
