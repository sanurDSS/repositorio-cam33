package javavis.jip2d.functions;

import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*Pixelates an image. The parameter side defines the side of a window. Every pixel inside this
*window takes the average value of the original pixels, giving a pixelate effect. 
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>side: Length of smooth window<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Image processed with the same type as input image<BR><BR>
*</ul>
*/
public class FPixelate extends JIPFunction {
	private static final long serialVersionUID = 3639217021560311842L;

	public FPixelate() {
		super();
		name = "FPixelate";
		description = "Sets the average intensity mask in all pixels";
		groupFunc = FunctionGroup.Manipulation;

		JIPParamInt p1 = new JIPParamInt("side", false, true);
		p1.setDefault(2);
		p1.setDescription("Length of the side");

		addParam(p1);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("Pixelate can not be applied to this image format");
		int p1 = getParamValueInt("side");
		JIPImgBitmap imgBmp = (JIPImgBitmap)img;
		int w = img.getWidth();
		int h = img.getHeight();
		int b = imgBmp.getNumBands();
		double[] bmp, bin;
		int inicioX, inicioY, finX, finY, cont;
		double aux;
		
		JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(b, w, h, img.getType());
		for (int nb = 0; nb < b; nb++) {
			bmp = imgBmp.getAllPixels(nb);
			bin = new double[w * h];
			for (int i = 0; i < h; i += p1)
				for (int j = 0; j < w; j += p1) {
					inicioX = j - p1; finX = j + p1;
					inicioY = i - p1; finY = i + p1;
					if (inicioX < 0) inicioX = 0;
					if (finX >= w) finX = w - 1;
					if (inicioY < 0) inicioY = 0;
					if (finY >= h) finY = h - 1;
					aux = 0.0f;
					cont = 0;
					for (int y = inicioY; y <= finY; y++)
						for (int x = inicioX; x <= finX; cont++, x++)
							aux += bmp[x + y * w];
					aux /= cont;
					for (int y = inicioY; y <= finY; y++)
						for (int x = inicioX; x <= finX; x++)
							bin[x + y * w] = aux;
				}
			res.setAllPixels(nb, bin);
		}
		return res;
	}
}
