package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*Changes the intensity value for the smaller value in a 
*neighborhood window with a specified size.<BR>
*Applicable to: image bitmaps<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: image input<BR>
*<li>radius: Radius of the neighborhood window<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Processed image with the same type that input image.<BR><BR>
*</ul>
**/
public class FMinimum extends JIPFunction {
	private static final long serialVersionUID = 2686998645864431044L;

	public FMinimum() {
		super();
		name = "FMinimum";
		description =
			"Changes the intensity value for the smaller value in a neighbourhood window with a specified size.";
		groupFunc = FunctionGroup.Manipulation;

		JIPParamInt p1 = new JIPParamInt("radius", false, true);
		p1.setDefault(2);
		p1.setDescription("Radius");

		addParam(p1);

	}
	
	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgGeometric)	
			throw new JIPException("Minimum can not be applied to this image format");
		JIPImgBitmap res = null, imgBmp = (JIPImgBitmap)img;
		int p1 = getParamValueInt("radius");
		ImageType t = img.getType();
		int w = img.getWidth();
		int h = img.getHeight();
		int b = imgBmp.getNumBands();

		res = (JIPImgBitmap)JIPImage.newImage(b, w, h, t);
		for (int nb = 0; nb < b; nb++) {
			double[] bmp = imgBmp.getAllPixels(nb);
			double[] bin = new double[w * h];
			for (int i = 0; i < h; i++)
				for (int j = 0; j < w; j++) {
					int inicioX = j - p1, finX = j + p1;
					int inicioY = i - p1, finY = i + p1;
					if (inicioX < 0) inicioX = 0;
					if (finX >= w) finX = w - 1;
					if (inicioY < 0) inicioY = 0;
					if (finY >= h) finY = h - 1;
					double aux = 65535;
					for (int y = inicioY; y <= finY; y++)
						for (int x = inicioX; x <= finX; x++)
							if (aux > bmp[x + y * w])
								aux = bmp[x + y * w];
					bin[j + i * w] = aux;
				}
			res.setAllPixels(nb, bin);
		}
		return res;
	}
}
