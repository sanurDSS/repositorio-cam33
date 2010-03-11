package javavis.jip2d.functions;

import java.util.ArrayList;
import java.util.Arrays;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpFloat;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;


/**
*Converts the geometric elements of a geometric image in a grey scale bitmap<BR>
*Applicable to: POINT, SEGMENT, POLY, EDGES types.<BR>
*Uses: FGrayToGray
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>gray: Type of grey: BIT, BYTE, WORD or REAL<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Image in grey scale.<BR><BR>
*</ul>
*/
public class FGeoToGray extends JIPFunction {
	private static final long serialVersionUID = -4763689691943809535L;

	public FGeoToGray() {
		super();
		name = "FGeoToGray";
		description = "Converts a geometric image into a grey scale";
		groupFunc = FunctionGroup.Geometry;

		JIPParamList p1 = new JIPParamList("gray", false, true);
		String []paux = new String[4];
		paux[0]="BYTE";
		paux[1]="BIT";
		paux[2]="SHORT";
		paux[3]="FLOAT";
		p1.setDefault(paux);
		p1.setDescription("Type of grey");

		addParam(p1);
	}
	
	public JIPImage processImg(JIPImage img) throws JIPException {
		ImageType t = img.getType();
		if (!(img instanceof JIPImgGeometric)) 
			throw new JIPException("GeoToGray can only be applied to a geometric image");

		int w = img.getWidth();
		int h = img.getHeight();
		int totalPix = w*h;
		JIPImgBitmap res = null;
		String p1 = getParamValueString("gray");

		double pix[] = new double[totalPix];
		Arrays.fill(pix,0);
		JIPImgGeometric imgGeom = (JIPImgGeometric)img;
		if (t == ImageType.SEGMENT) {
			ArrayList<Integer> aux = imgGeom.getData();
			int tam = aux.size() / 4;
			for (int i = 0; i < tam; i++) {
				int x0 = aux.get(4 * i);
				int y0 = aux.get(4 * i + 1);
				int x1 = aux.get(4 * i + 2);
				int y1 = aux.get(4 * i + 3);
				drawline(x0, y0, x1, y1, w, h, pix);
			}
		} else if (t == ImageType.POINT) {
			ArrayList<Integer> aux = imgGeom.getData();
			int tam = aux.size() / 2;
			for (int i = 0; i < tam; i++) {
				int x0 = aux.get(2 * i);
				int y0 = aux.get(2 * i + 1);
				drawline(x0, y0, x0, y0, w, h, pix);
			}
		} else if (t == ImageType.POLY) {
			ArrayList<ArrayList<Integer>> aux = imgGeom.getData();
			int xini = 0, yini = 0, x0, y0, x1, y1;
			for (int kp = 0; kp < aux.size(); kp++) {
				ArrayList<Integer> aux1 = aux.get(kp);
				int tam = aux1.size() / 2;
				for (int i = 0; i < tam - 1; i++) {
					x0 = aux1.get(2 * i);
					y0 = aux1.get(2 * i + 1);
					if (i == 0) {
						xini = x0;
						yini = y0;
					}
					x1 = aux1.get(2 * i + 2);
					y1 = aux1.get(2 * i + 3);
					drawline(x0, y0, x1, y1, w, h, pix);
				}

				x1 = aux1.get(2 * tam - 2);
				y1 = aux1.get(2 * tam - 1);
				drawline(xini, yini, x1, y1, w, h, pix);
			}
		} else if (t == ImageType.EDGES) {
			ArrayList<ArrayList<Integer>> aux = imgGeom.getData();
			int i, x0, y0, tam;
			for (int kp = 0; kp < aux.size(); kp++) {
				ArrayList<Integer> aux1 = aux.get(kp);
				tam = aux1.size() / 2;
				for (i = 0; i < tam; i++) {
					x0 = aux1.get(2 * i);
					y0 = aux1.get(2 * i + 1);
					drawline(x0, y0, x0, y0, w, h, pix);
				}
			}
		} 
		res = new JIPBmpFloat(w, h);
		res.setAllPixels(pix);

		JIPFunction conversion = new FGrayToGray();
		conversion.setParamValue("gray", p1);
		return conversion.processImg(res);
	}
	
	/** 
	 * It draws the line which coordinate is transfered by parameter,
	* by means of Bressenham algorithm.	
	*@param x0 X coordinate of initial point.
	*@param y0 Y coordinate of inicial point.
	 *@param x1 X coordinate of the last point.
	 *@param y1 Y coordinate of the last point.
	 *@param icol Column	
	*@param irow Row
	*@param pix Array of pixels.
	*/
	public static void drawline(int x0, int y0, int x1, int y1, int icol, int irow, double pix[]) {
		int xmin, xmax; /* Coordenadas de la Linea */
		int ymin, ymax;
		int dir; /* Direccion de busqueda */
		int dx, dy;

		/* Incrementos Este, Nort-Este, Sur, Sur-Este, Norte */
		int incrE, incrNE, incrSE;
		int d, x, y;
		int mpCase, done;
		
		xmin = x0;
		xmax = x1;
		ymin = y0;
		ymax = y1;

		dx = xmax - xmin;
		dy = ymax - ymin;

		if (dx * dx > dy * dy) /* busqueda horizontal */ {
			dir = 0;
			if (xmax < xmin) {
				xmin ^= xmax;
				xmax ^= xmin;
				xmin ^= xmax;
				ymin ^= ymax;
				ymax ^= ymin;
				ymin ^= ymax;
			}
			dx = xmax - xmin;
			dy = ymax - ymin;

			if (dy >= 0) {
				mpCase = 1;
				d = 2 * dy - dx;
			} else {
				mpCase = 2;
				d = 2 * dy + dx;
			}

			incrNE = 2 * (dy - dx);
			incrE = 2 * dy;
			incrSE = 2 * (dy + dx);
		} else { /* busqueda vertical */
			dir = 1;
			if (ymax < ymin) {
				xmin ^= xmax;
				xmax ^= xmin;
				xmin ^= xmax;
				ymin ^= ymax;
				ymax ^= ymin;
				ymin ^= ymax;
			}
			dx = xmax - xmin;
			dy = ymax - ymin;

			if (dx >= 0) {
				mpCase = 1;
				d = 2 * dx - dy;
			} else {
				mpCase = 2;
				d = 2 * dx + dy;
			}

			incrNE = 2 * (dx - dy);
			incrE = 2 * dx;
			incrSE = 2 * (dx + dy);
		}

		/* Comenzamos la busqueda */
		x = xmin;
		y = ymin;
		done = 0;

		while (done == 0) {
			if (x > 0 && x < icol && y > 0 && y < irow)
				pix[y * icol + x] = 1.0f;

			/* Movemos al siguiente p */
			switch (dir) {
				case 0 : /* horizontal */ {
						if (x < xmax) {
							switch (mpCase) {
								case 1 :
									if (d <= 0) {
										d += incrE;
										x++;
									} else {
										d += incrNE;
										x++;
										y++;
									}
									break;

								case 2 :
									if (d <= 0) {
										d += incrSE;
										x++;
										y--;
									} else {
										d += incrE;
										x++;
									}
									break;
							}
						} else
							done = 1;
					}
					break;

				case 1 : /* vertical */ {
						if (y < ymax) {
							switch (mpCase) {
								case 1 :
									if (d <= 0) {
										d += incrE;
										y++;
									} else {
										d += incrNE;
										y++;
										x++;
									}
									break;

								case 2 :
									if (d <= 0) {
										d += incrSE;
										y++;
										x--;
									} else {
										d += incrE;
										y++;
									}
									break;
							}
						} else
							done = 1;
					}
					break;
			}
		}
	}
}
