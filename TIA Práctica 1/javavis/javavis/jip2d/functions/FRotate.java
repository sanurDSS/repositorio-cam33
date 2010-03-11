package javavis.jip2d.functions;

import java.util.ArrayList;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamBool;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;


/**
*Image rotates with a given angle.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>angle: Rotation angle expressed in degrees. Positive 
*corresponds clockwise, negative anticlockwise.<BR>
*<li>clipping: If true, the output image will have the same size of the
*input image.<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Rotated image.<BR><BR>
*</ul>
*/

public class FRotate extends JIPFunction {
	private static final long serialVersionUID = -6309640810930939257L;

	public FRotate() {
		super();
		name = "FRotate";
		description =
			"Rotate the image with the angle value. (>0 clockwise, <0 anticlockwise)";
		groupFunc = FunctionGroup.Manipulation;

		JIPParamInt p1 = new JIPParamInt("angle", false, true);
		p1.setDefault(30);
		p1.setDescription("Rotation angle in degrees");
		JIPParamBool p2 = new JIPParamBool("clipping", false, true);
		p2.setDefault(false);
		p2.setDescription("Make clipping");

		addParam(p1);
		addParam(p2);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		int ang_g = getParamValueInt("angle");
		boolean clip = getParamValueBool("clipping");
		int w = img.getWidth();
		int h = img.getHeight();
		ImageType t = img.getType();
		int cx = w / 2;
		int cy = h / 2;
		double sin = Math.sin(-Math.toRadians(ang_g));
		double cos = Math.cos(-Math.toRadians(ang_g));
		int ind;
		int xs, ys;

		if (ang_g % 360 == 0) ang_g = 0;
		if (ang_g == 0) return img;

		int wid, hei;

		if (!clip) {
			wid = (int) (Math.abs(w * cos) + Math.abs(h * sin));
			hei = (int) (Math.abs(w * sin) + Math.abs(h * cos));
			cx = (wid / 2);
			cy = (hei / 2);
		} 
		else {
			wid = w;
			hei = h;
		}
		if (img instanceof JIPImgBitmap) {
			JIPImgBitmap imgBmp = (JIPImgBitmap)img;
			int b = imgBmp.getNumBands();
			JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(b, wid, hei, t);

			double dtemp11 = cy * sin - cx * cos;
			double dtemp21 = -cx * sin - cy * cos;
			
			double[] bmp, bin;

			for (int nb = 0; nb < b; nb++) {
				bmp = imgBmp.getAllPixels(nb);
				bin = new double[wid * hei];
				for (int y = 0; y < hei; y++) {
					ind = y * wid;
					double dtemp31 = (dtemp11 - y * sin) + (w / 2.0);
					double dtemp41 = (dtemp21 + y * cos) + (h / 2.0);
					for (int x = 0; x < wid; x++) {
						xs = (int) ((x * cos) + dtemp31);
						ys = (int) ((x * sin) + dtemp41);
						if ((xs >= 0) && (xs < w) && (ys >= 0) && (ys < h))
							bin[ind++] = bmp[w * ys + xs];
						else
							bin[ind++] = 0;
					}
				}
				res.setAllPixels(nb, bin);
			}
			return res;
		} else {
			JIPImgGeometric res = (JIPImgGeometric)JIPImage.newImage(wid, hei, t);
			if (img.getType() == ImageType.POINT || img.getType() == ImageType.SEGMENT) {
				ArrayList<Integer> geodata = new ArrayList<Integer>(((JIPImgGeometric)img).getData());
				for (int i = 0; i < geodata.size(); i += 2) {                      
					int xAct = geodata.get(i);
					int yAct = geodata.get(i + 1);
					int xDes = xAct - cx;
					int yDes = cy - yAct;
					int x = (int) (xDes*cos + yDes*sin) + cx;
					int y = cy - (int) (yDes*cos - xDes*sin);
					if (x >= 0 && x <= wid && y >= 0 && y <= hei) {
						geodata.set(x, i);
						geodata.set(y, i + 1);
					}
					else {
						geodata.remove(i);
						geodata.remove(i+1);
						i -= 2; // I know, I know, bad thing, but neccesary
					}
				}
				res.setData(geodata);
			} else if (img.getType() == ImageType.POLY || img.getType() == ImageType.EDGES) {
				double dtemp1 = cy * sin - cx * cos;
				double dtemp2 = -cx * sin - cy * cos;

				ArrayList<ArrayList<Integer>> geodata = 
					new ArrayList<ArrayList<Integer>>(((JIPImgGeometric)img).getData());
				for (int j = 0; j < geodata.size(); j++) {
					ArrayList<Integer> aux = geodata.get(j);
					for (int i = 0; i < aux.size(); i += 2) { 
						int xAct = aux.get(i);
						int yAct =aux.get(i+1);
						for (int y = 0; y < hei; y++) {
							double dtemp3 = (dtemp1 - y * sin) + (w / 2.0);
							double dtemp4 = (dtemp2 + y * cos) + (h / 2.0);
							for (int x = 0; x < wid; x++) {
								xs = (int) (x * cos + dtemp3);
								ys = (int) (x * sin + dtemp4);
								if (xs >= (xAct - 1) && xs <= (xAct + 1)
									&& ys >= (yAct - 1) && ys <= (yAct + 1)) {
									aux.set(i, x);
									aux.set(i+1, y);
								}
							}
						}
					}
				}
				res.setData(geodata);
			}
			return res;
		}
	}
}
