package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*Calculates the negative of an input image.<BR>
*Applicable to: BIT, BYTE, SHORT, COLOR and FLOAT<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Image which is the negative of input image.<BR><BR>
*</ul>
*/
public class FNegate extends JIPFunction {
	private static final long serialVersionUID = -5089690362652154611L;

	public FNegate() {
		super();
		name = "FNegate";
		description = "Calculates the negative of an image";
		groupFunc = FunctionGroup.Manipulation;
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgGeometric)
			throw new JIPException("Negate can not be applied to this image format");
		
		int w = img.getWidth();
		int h = img.getHeight();
		JIPImgBitmap imgBmp = (JIPImgBitmap)img;
		int b = imgBmp.getNumBands();
		ImageType t = img.getType();
		int numPixels = w*h;
		double maximo = 1;

		switch (t) {
			case BIT :
			case FLOAT:
				maximo = 1;
				break;
			case BYTE :
			case COLOR :
				maximo = 255;
				break;
			case SHORT :
				maximo = 65535;
				break;
			default :
		}
		JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(b, w, h, t);
		double[] bmp;
		for (int nb = 0; nb < b; nb++) {
			bmp = imgBmp.getAllPixels(nb);
			for (int i = 0; i < numPixels; i++)
				bmp[i] = maximo - bmp[i];
			res.setAllPixels(nb, bmp);
		}
		return res;
	}
}
