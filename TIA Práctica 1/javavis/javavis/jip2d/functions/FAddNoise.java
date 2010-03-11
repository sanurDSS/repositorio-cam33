package javavis.jip2d.functions;

import java.util.Random;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpColor;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
* 
*Introduces random noise depending on the noise level indicated
*by the user.<BR>
*Applicable to bitmap images<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>level: Noise level (If 0 the image does not change)<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Original image with random noise.<BR><BR>
*</ul>
*/

public class FAddNoise extends JIPFunction {
	private static final long serialVersionUID = -5543080812213828342L;

	public FAddNoise() {
		super();
		name = "FAddNoise";
		description = "Introduces random noise";
		groupFunc = FunctionGroup.Manipulation;

		JIPParamInt p1 = new JIPParamInt("level", false, true);
		p1.setDefault(20);
		p1.setDescription("Noise level [0..100]");

		addParam(p1);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("Noise can not be applied to this image format");
		float p1 = getParamValueInt("level")/100.0f;
		Random rnd = new Random();
		ImageType t = img.getType();
		int w = img.getWidth();
		int h = img.getHeight();
		int totalPix = w*h;
		JIPImgBitmap imgBmp = (JIPImgBitmap)img;
		int b = imgBmp.getNumBands();
		int max=0;
		JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(b, w, h, t);
		switch (t) {
			case BYTE :
			case COLOR :
				max = 255;
				break;
			case SHORT :
				max = 65535;
				break;
			case FLOAT :
			case BIT :
				max = 1;
				break;
		}
		if (t!=ImageType.COLOR) {
			for (int nb = 0; nb < b; nb++) {
				double[] bmp = imgBmp.getAllPixels(nb);
				for (int k = 0; k < totalPix; k++)
					if (rnd.nextFloat() < p1) {	
						bmp[k] += rnd.nextGaussian()*max;
						if (bmp[k] > max) bmp[k]=max;
						if (bmp[k] < 0) bmp[k]=0.0;
					}
				res.setAllPixels(nb, bmp);
			}
		}
		else {
			double[] R, G, B;
			R = ((JIPBmpColor)imgBmp).getAllPixelsRed();
			G = ((JIPBmpColor)imgBmp).getAllPixelsGreen();
			B = ((JIPBmpColor)imgBmp).getAllPixelsBlue();
			int cont=0;
			for (int k = 0; k < totalPix; k++)
				if (rnd.nextFloat() < p1) {
					cont++;
					R[k] = Math.abs(rnd.nextGaussian())*max;
					if (R[k] > max) R[k]=max;
					G[k] = Math.abs(rnd.nextGaussian())*max;
					if (G[k] > max) G[k]=max;
					B[k] = Math.abs(rnd.nextGaussian())*max;
					if (B[k] > max) B[k]=max;
				}
			res.setAllPixels(0, R);
			res.setAllPixels(1, G);
			res.setAllPixels(2, B);
		}
		return res;
	}
}
