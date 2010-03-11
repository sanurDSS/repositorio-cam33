package javavis.jip2d.functions;

import java.util.ArrayList;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;


/**
*Scales the input image. The scale can be of two types: Expand or reduce.
*Expand makes the image bigger. Two methods can be used:
*Expand using expand: The image is expanded, giving the same value for the corresponded
*area in the output image. Expand using scale: the values in the corresponded area are interpolated.<BR>    
*Reduce makes the image smaller. Two methods can be used:
*Reduce using reduce: an area in the input image corresponds with a pixel in the output image.
*The value of the output pixel is calculated with an average of the pixel in the area.
*Reduce using sample: the output value is taken from the central pixel of the input area.
*Sample and Expand methods have a worse graphical results.
*Reduce and Scale methods have a better graphical results.
*That method works with a geometrical frames. The results of scale are indepedents with
*the choosen method.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: input image<BR>
*<li>FE: Percentage of scale factor. 100 doesn´t change the image..<BR>
*<li>method: Reduce or expand by one of the previous methods<br>
*<li>EX: Percentage scale factor in the X axis. The method is applied only in X axis, when FE=-1<BR>
*<li>EX: Percentage scale factor in the Y axis. The method is applied only in Y axis, when FE=-1<BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Scaled image<BR><BR>
*</ul> 
*/

public class FScale extends JIPFunction {
	private static final long serialVersionUID = -323208875505182117L;

	public FScale() {
		super();
		name = "FScale";
		description = "Scales an image.";
		groupFunc = FunctionGroup.Manipulation;

		JIPParamInt p1 = new JIPParamInt("FE", false, true);
		p1.setDescription("Percentage of scale factor");
		p1.setDefault(100);

		JIPParamList p2 = new JIPParamList("method", false, true);
		String []paux = new String[4];
		paux[0]="Reduce/Reduce";
		paux[1]="Reduce/Sample";
		paux[2]="Expand/Expand";
		paux[3]="Expand/Scale";
		p2.setDefault(paux);
		p2.setDescription("Reduce or Extend by one of these methods.");

		JIPParamInt p6 = new JIPParamInt("EX", false, true);
		p6.setDescription( "Scale factor for X axis.Only applied if FE<0");
		p6.setDefault(0);

		JIPParamInt p7 = new JIPParamInt("EY", false, true);
		p7.setDescription( "Scale factor for Y axis.Only applied if FE<0");
		p7.setDefault(0);

		addParam(p1);
		addParam(p2);
		addParam(p6);
		addParam(p7);
	}

	/**
	*Method to reduce images.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>method: the reduction method<BR>
	*<li>img: numFrame to work.<BR>
	*<li>p6: Image width.<BR>
	*<li>p7: Image height.<BR>
	*<li>Xstep: Size of the area in function of FE for Xs.<BR>
	*<li>Ystep: Size of the area in function of FE for Ys.<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>res: Result image of the algorithm.<BR><BR>
	*</ul>
	*/
	private JIPImage reduce(int metodo, JIPImgBitmap img, int p6, int p7, 
			double Xstep, double Ystep) throws JIPException {
		int b = img.getNumBands();
		JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(b, p6, p7, img.getType());
		int denom, xs, ys;
		float media;

		for (int nb = 0; nb < b; nb++)
			for (int Y = 0; Y < p7; Y++)
				for (int X = 0; X < p6; X++) {
					xs = (int) (X * Xstep);
					ys = (int) (Y * Ystep);
					if (metodo == 1) {
						media = 0.0f;
						denom = 0;
						for (int x = xs; x < xs + Xstep - 1; x++)
							for (int y = ys; y < ys + Ystep - 1; y++) {
								media += img.getPixel(nb, x, y);
								denom++;
							}
						if (denom == 0)
							res.setPixel(nb, X, Y, img.getPixel(nb, xs, ys));
						else 
							res.setPixel(nb, X, Y, media / denom);
					} else
						res.setPixel(nb, X, Y, img.getPixel(nb, xs, ys));
				}
		return res;
	}

	/**
	*Method to reduce an image.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>method: reduction method, (RS,RR)<BR>
	*<li>img: numFrame to work.<BR>
	*<li>b: Number of bands of the numFrame.<BR>
	*<li>p6: Image width.<BR>
	*<li>p7: Image height.<BR>
	*<li>Xstep: Size of the area in function of FE for Xs.<BR>
	*<li>Ystep: Size of the area in function of FE for Ys.<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>res: Result image of the algorithm.<BR><BR>
	*</ul>
	*/	
	private JIPImage extend(int method, JIPImgBitmap img, int p6b, 
			float p7b, double Xstep, double Ystep) throws JIPException {
		int b = img.getNumBands();
		int xs, ys, nb, X, Y;
		int w = img.getWidth();
		int h = img.getHeight();
		int p6=(int)p6b, p7=(int)p7b;
		double intO, intFX, intFY, intDifX, intDifY, incX, incY;
		double Xinv = 1 / Xstep, Yinv = 1 / Ystep;
		JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(b, p6, p7, img.getType());

		if (method == 1) {
			double[] imgaux = new double[p6 * p7];
			for (nb = 0; nb < b; nb++) {
				double[] bmp = img.getAllPixels(nb);
				for (Y = 0; Y < p7; Y++)
					for (X = 0; X < p6; X++) {
						xs = (int) (X * Xstep);
						ys = (int) (Y * Ystep);
						imgaux[X + Y * p6] = bmp[xs + ys * w];
					}
				res.setAllPixels(nb, imgaux);
			}
		} else {
			double[][] pixels = new double[p6][p7];
			for (nb = 0; nb < b; nb++) {
				for (Y = 0; Y < p7; Y++)
					for (X = 0; X < p6; X++) 
						pixels[X][Y] = -1;

				for (Y = 0; Y < h; Y++) //  todas las filas y columnas menos la ultima
					for (X = 0; X < w; X++) {
						xs = (int) (X * (Xinv));
						ys = (int) (Y * (Yinv));
						pixels[xs][ys] = img.getPixel(nb, X, Y);
					}

				for (X = 0; X < w; X++) { // ultima columna
					xs = (int) (X * (Xinv));
					pixels[xs][p7 - 1] = img.getPixel(nb, X, h - 1);
				}

				for (Y = 0; Y < h; Y++) { // ultima fila
					ys = (int) (Y * (Yinv));
					pixels[p6 - 1][ys] = img.getPixel(nb, w - 1, Y);
				}

				pixels[p6 - 1][p7 - 1] = img.getPixel(nb, w - 1, h - 1);
				//	ultimo pixel

				int rx = 0, ry = 0, fin = 0;

				// calculo de malla de degradado hasta la penultima fila y columna

				for (X = 0; X < p6;) {
					for (Y = 0; Y < p7;) {
						rx = 0;
						fin = 0;
						while (fin == 0) {
							if (rx + X + 1 < p6)
								if (pixels[rx + X + 1][Y] == -1) rx++;
								else fin = 1;
							else fin = 1;
						}
						ry = 0;
						fin = 0;
						while (fin == 0) {
							if (ry + Y + 1 < p7)
								if (pixels[X][ry + Y + 1] == -1) ry++;
								else fin = 1;
							else fin = 1;
						}

						intO = pixels[X][Y];

						if (rx > 0) {
							intFX = pixels[X + rx + 1][Y];
							intDifX = java.lang.Math.abs(intO - intFX);
							incX = intDifX / (rx + 1);
							if (intO > intFX) incX = -incX;
							for (int m = X + 1; m <= X + rx; m++)
								pixels[m][Y] = intO + (incX * (m - X));
						}

						if (ry > 0) {
							intFY = pixels[X][Y + ry + 1];
							intDifY = java.lang.Math.abs(intO - intFY);
							incY = intDifY / (ry + 1);
							if (intO > intFY) incY = -incY;
							for (int m = Y + 1; m <= Y + ry; m++)
								pixels[X][m] = intO + (incY * (m - Y));
						}

						Y += ry + 1;
						if (Y == p7 - 1) Y = p7;
					}
					X += rx + 1;
					if (X == p6 - 1) X = p6;
				}

				// ultima fila de la malla	

				for (X = 0; X < p6;) {
					rx = 0;
					fin = 0;
					while (fin == 0) {
						if (rx + X + 1 < p6)
							if (pixels[rx + X + 1][p7 - 1] == -1) rx++;
							else fin = 1;
						else fin = 1;
					}

					if (rx > 0) {
						intO = pixels[X][p7 - 1];
						intFX = pixels[X + rx + 1][p7 - 1];
						intDifX = java.lang.Math.abs(intO - intFX);
						incX = intDifX / (rx + 1);
						if (intO > intFX) incX = -incX;
						for (int m = X + 1; m <= X + rx; m++)
							pixels[m][p7 - 1] = intO + (incX * (m - X));
					}

					X += rx + 1;
					if (X == p6 - 1) X = p6;
				}

				// ultima columna de la malla

				for (Y = 0; Y < p7;) {
					ry = 0;
					fin = 0;
					while (fin == 0) {
						if (ry + Y + 1 < p7)
							if (pixels[p6 - 1][ry + Y + 1] == -1) ry++;
							else fin = 1;
						else
							fin = 1;
					}

					if (ry > 0) {
						intO = pixels[p6 - 1][Y];
						intFY = pixels[p6 - 1][Y + ry + 1];
						intDifY = java.lang.Math.abs(intO - intFY);
						incY = intDifY / (ry + 1);

						if (intO > intFY) incY = -incY;

						for (int m = Y + 1; m <= Y + ry; m++)
							pixels[p6 - 1][m] = intO + (incY * (m - Y));
					}

					Y += ry + 1;
					if (Y == p7 - 1) Y = p7;
				}

				// Primera interpolacion sobre la malla en eje X

				for (Y = 1; Y < p7 - 1; Y++) {
					for (X = 0; X < p6;) {
						fin = 0;
						while (fin == 0) {
							if (X + 1 < p6)
								if (pixels[X + 1][Y] != -1) X++;
								else fin = 1;
							else {
								fin = 1;
								X = p6;
							}
						}

						if (X < p6) {
							rx = 0;
							fin = 0;
							while (fin == 0) {
								if (rx + X + 1 < p6)
									if (pixels[rx + X + 1][Y] == -1) rx++;
									else fin = 1;
								else fin = 1;
							}

							if (rx > 0) {
								intO = pixels[X][Y];
								intFX = pixels[X + rx + 1][Y];
								intDifX = java.lang.Math.abs(intO - intFX);
								incX = intDifX / (rx + 1);
								if (intO > intFX) incX = -incX;
								if (rx == 0) rx = 1;
								for (int m = X + 1; m <= X + rx; m++)
									pixels[m][Y] = intO + (incX * (m - X));
							}

							X += rx + 1;
							if (X == p6 - 1) X = p6;
						}
					}
				}

				// Segunda interpolacion sobre la malla en eje Y

				for (X = 1; X < p6 - 1; X++) {
					for (Y = 0; Y < p7;) {
						fin = 0;
						while (fin == 0) {
							if (Y + 1 < p7)
								if (pixels[X][Y + 1] != -1)Y++;
								else fin = 1;
							else {
								fin = 1;
								Y = p7;
							}
						}

						if (Y < p7) {
							ry = 0;
							fin = 0;
							while (fin == 0) {
								if (ry + Y + 1 < p7)
									if (pixels[X][ry + Y + 1] == -1) ry++;
									else fin = 1;
								else fin = 1;
							}

							if (ry != 0) {
								intO = pixels[X][Y];
								intFY = pixels[X][Y + ry + 1];
								intDifY = java.lang.Math.abs(intO - intFY);
								incY = intDifY / (ry + 1);
								if (intO > intFY)incY = -incY;
								for (int m = Y + 1; m <= Y + ry; m++) 
									pixels[X][m] = (pixels[X][m] + intO + (incY * (m - Y))) / 2;
							}

							Y += ry + 1;
							if (Y == p7 - 1)
								Y = p7;
						}
					}
				} 
				for (Y = 0; Y < p7; Y++)
					for (X = 0; X < p6; X++)
						res.setPixel(nb, X, Y, pixels[X][Y]);
			}
		}
		return (res);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		float p1 = getParamValueInt("FE") / 100.0f;
		boolean p2=false,p3=false,p4=false,p5=false;
		String a = getParamValueString("method");
		if (a.equals("Reduce/Reduce")) p2 = true;
		if (a.equals("Reduce/Sample")) p3 = true;
		if (a.equals("Expand/Expand")) p4 = true;
		if (a.equals("Expand/Scale")) p5 = true;
		float p6 = getParamValueInt("EX") / 100.0f;
		float p7 = getParamValueInt("EY") / 100.0f;

		JIPImage res = null;
		int w = img.getWidth();
		int h = img.getHeight();
		ImageType t = img.getType();
		if (p1 == 0) 
			throw new JIPException("Scale factor must be greater than 0");
		
		if (!(p2 || p3 || p4 || p5)) 
			throw new JIPException ("Scale not applied. Please, indicate the method to use");

		if (p1 >= 0.01) {
			p6 = w * p1;
			p7 = h * p1;
		} else {
			p6 *= w;
			p7 *= h;
		}
		double Xstep = w / p6;
		double Ystep = h / p7;

		if (img instanceof JIPImgBitmap ) {
			if (p2) res = reduce(1, (JIPImgBitmap)img, (int)p6, (int)p7, Xstep, Ystep);
			else if (p3) res = reduce(2, (JIPImgBitmap)img, (int)p6, (int)p7, Xstep, Ystep);
			else if (p4) res = extend(1, (JIPImgBitmap)img, (int)p6, (int)p7, Xstep, Ystep);
			else if (p5) res = extend(2, (JIPImgBitmap)img, (int)p6, (int)p7, Xstep, Ystep);
		} else { 
			double xmul = p6 / w;
			double ymul = p7 / h;
			if (t == ImageType.POINT || t == ImageType.SEGMENT) {
				ArrayList<Integer> puntos = new ArrayList<Integer>(((JIPImgGeometric)img).getData());
				for (int i = 0; i < puntos.size(); i++) {
					if (i % 2 != 0)
						puntos.set((int)(ymul * puntos.get(i)), i);
					else 
						puntos.set((int)(xmul * puntos.get(i)), i);
				}
				res = JIPImage.newImage((int)p6, (int)p7, t);
				((JIPImgGeometric)res).setData(puntos);
			} else if (t == ImageType.POLY || t == ImageType.EDGES) {
				ArrayList<ArrayList<Integer>> poligonos = 
					new ArrayList<ArrayList<Integer>>(((JIPImgGeometric)img).getData());
				
				for (int j = 0; j < poligonos.size(); j++) {
					int size = poligonos.get(j).size();
					ArrayList<Integer> auxVec = poligonos.get(j);
					for (int i = 0; i < size; i++) {
						if (i % 2 != 0)
							auxVec.set(i, (int) (ymul * auxVec.get(i)));
						else
							auxVec.set(i, (int) (xmul * auxVec.get(i)));
					}
				}
				res = JIPImage.newImage((int)p6, (int)p7, t);
				((JIPImgGeometric)res).setData(poligonos);
			} 
		}
		return res;
	}
}
