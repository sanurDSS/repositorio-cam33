package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*It implements the Canny method to edge detection. It applies
*an optimum filter which is convolutioned with the image causing a local maximum 
*where an edge is located, reducing the noise effect at the same time. To get the 
*best filter two criteria are defined which should be maximized.<BR>
*It is applicable for: BYTE, SHORT, COLOR y FLOAT (Color images are converted to Gray)<BR>
*Uses: FEqualize and FBrightness <BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image.<BR>
*<li>sigma: Level of Gaussian smoothed<BR>
*<li>brightness: Adjustment of brightness in the result image<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Image processed with the same type that the input image<BR><BR>
*</ul>
*Additional notes: Since a suppression of non-maximum values is applied
*then the edges have a width of a pixel. This makes difficult to visualize
*and it is recomendable to increase the brightness.<BR>
*To normalize the result, FEqualize is called.
*/

public class FCanny extends JIPFunction {
	private static final long serialVersionUID = 3914477486873913843L;

	public FCanny() {
		super();
		name = "FCanny";
		description = "Detects edge using the Canny's method";
		groupFunc = FunctionGroup.Edges;
		
		JIPParamFloat p1 = new JIPParamFloat("sigma", false, true);
		p1.setDefault(1.0f);
		p1.setDescription("Level of gaussian smoothed");
		JIPParamInt p2 = new JIPParamInt("brightness", false, true);
		p2.setDefault(100);
		p2.setDescription("Brightness adjustment");

		addParam(p1);
		addParam(p2);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("Canny algorithm can not be applied to this image type");
		JIPImgBitmap res, aux, aux2;
		ImageType t = img.getType();
		int w = img.getWidth();
		int h = img.getHeight();
		int totalPix = w*h;

		switch (t) {
			case COLOR :
				t = ImageType.BYTE;
				JIPFunction ctg = new FColorToGray();
				ctg.setParamValue("gray", "FLOAT");
				aux2 = (JIPImgBitmap)ctg.processImg(img);
				break;
			case BYTE :
			case SHORT :
				JIPFunction gtg = new FGrayToGray();
				gtg.setParamValue("gray","FLOAT");
				aux2 = (JIPImgBitmap)gtg.processImg(img);
				break;
			case FLOAT :
				aux2 = (JIPImgBitmap)img;
				break;
			default :
				throw new JIPException("Canny algorithm can not be applied to this image type");
		}

		res = (JIPImgBitmap)JIPImage.newImage(aux2.getNumBands(), w, h, t);
		float p1 = getParamValueFloat("sigma");
		int p2 = getParamValueInt("brightness");

		if (p1 > 0) {
			JIPFunction suavizado = new FSmoothGaussian();
			suavizado.setParamValue("sigma", p1);
			suavizado.setParamValue("method", "PAD");
			aux = (JIPImgBitmap)suavizado.processImg(aux2);
			if (suavizado.isInfo())
				info = "Canny info: "+suavizado.getInfo();
		}
		else aux=aux2;
		
		aux2=(JIPImgBitmap)JIPImage.newImage(aux.getNumBands(), w, h, ImageType.FLOAT);

		for (int i = 0; i < aux.getNumBands(); i++) {
			double Dx, Dy;
			double[] bmp, gradX = new double[totalPix], gradY = new double[totalPix];
			double[] mag = new double[totalPix], bin = new double[totalPix];
			bmp = aux.getAllPixels(i);

			for (int x = 1; x < w - 1; x++) {
				for (int y = 1; y < h - 1; y++) {
					Dx = (bmp[(y + 1) * w + x + 1] + 2 * bmp[y * w + x + 1]
							+ bmp[(y - 1) * w + x + 1] - bmp[(y + 1) * w + x - 1]
							- 2 * bmp[y * w + x - 1] - bmp[(y - 1) * w + x - 1]) / 8;
					Dy = (bmp[(y + 1) * w + x + 1] + 2 * bmp[(y + 1) * w + x]
							+ bmp[(y + 1) * w + x - 1] - bmp[(y - 1) * w + x + 1]
							- 2 * bmp[(y - 1) * w + x] - bmp[(y - 1) * w + x - 1]) / 8;
					gradX[y * w + x] = Dx;
					gradY[y * w + x] = Dy;
					mag[y * w + x] = Math.sqrt(Dx * Dx + Dy * Dy);
				}
			}
			double g; // Magnitud de gradiente del pixel central
			double g1, g2; // Magnitudes de gradiente interpoladas
			double ga, gb, gc, gd;
			// Magnitudes de gradiente de los pixels mas cercanos
			double ux, uy;
			double dx, dy;

			// Para cada pixel, se comprueba si es maximo local en una
			// vecindad de 3x3 en la orientacion del gradiente
			for (int x = 1; x < w - 1; x++) {
				for (int y = 1; y < h - 1; y++) {
					dx = gradX[y * w + x];
					dy = gradY[y * w + x];
					if (dx == 0.0 && dy == 0.0)
						continue;
					if (Math.abs(dy) > Math.abs(dx)) {
						ux = dx / dy;
						uy = 1.0;
						gb = mag[(y - 1) * w + x];
						gd = mag[(y + 1) * w + x];
						if (dx * dy < 0) {
							ga = mag[(y - 1) * w + x - 1];
							gc = mag[(y + 1) * w + x + 1];
						} else {
							ga = mag[(y - 1) * w + x + 1];
							gc = mag[(y + 1) * w + x - 1];
						}
					} else {
						ux = dy / dx;
						uy = 1.0;
						gb = mag[y * w + x + 1];
						gd = mag[y * w + x - 1];
						if (dx * dy < 0) {
							ga = mag[(y + 1) * w + x + 1];
							gc = mag[(y - 1) * w + x - 1];
						} else {
							ga = mag[(y - 1) * w + x + 1];
							gc = mag[(y + 1) * w + x - 1];
						}
					}
					g1 = (ux * ga) + (uy - ux) * gb;
					g2 = (ux * gc) + (uy - ux) * gd;
					g = Math.sqrt(dx * dx + dy * dy);
					if (g > g1 && g >= g2)
						bin[y * w + x] = mag[y * w + x];
					else
						bin[y * w + x] = 0;
				}
			}
			aux2.setAllPixels(bin);
		}

		if (t!=ImageType.FLOAT) {
			JIPFunction gtg = new FGrayToGray();
			gtg.setParamValue("gray",t.toString());
			res = (JIPImgBitmap)gtg.processImg(aux2);
		}
		else res = aux2;
		
		JIPFunction equ = new FEqualize();
		res = (JIPImgBitmap)equ.processImg(res); 

		JIPFunction brillo = new FBrightness();
		brillo.setParamValue("perc", p2);
		res = (JIPImgBitmap)brillo.processImg(res); 
		if (brillo.isInfo())
			info = "Canny info: "+brillo.getInfo();
		
		return res;
	}
}
