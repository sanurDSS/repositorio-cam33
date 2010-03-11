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
* Adjusts the contrast in an input image.<BR>
*It is only applicable for BYTE, SHORT, COLOR and FLOAT types.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image.<BR>
*<li>perc: Percentage (If it is 100% the image is not changed)<BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Processed image with the same type of the input image but it has the
*new contrast values.<BR><BR>
*</ul>
*/

public class FContrast extends JIPFunction {
	private static final long serialVersionUID = 8452052380513155834L;

	public FContrast() {
		super();
		name = "FContrast";
		description = "Adjusts the contrast of the image";
		groupFunc = FunctionGroup.Adjustment;

		JIPParamInt p1 = new JIPParamInt("perc", false, true);
		p1.setDefault(100);
		p1.setDescription("Percentage (If it is 100% the image is not changed)");

		addParam(p1);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		ImageType t = img.getType();
		if (t==ImageType.BIT || img instanceof JIPImgGeometric) 
			throw new JIPException("Contrast can not be applied to this image format");
		JIPImgBitmap imgBmp = (JIPImgBitmap)img;
		float p1 = getParamValueInt("perc") / 100.0f;
		int totalPix = img.getWidth()*img.getHeight();
		int b = imgBmp.getNumBands();
		double max;
		
		switch (t) {
			case BYTE :
			case COLOR :
				max = 255;
				break;
			case SHORT :
				max = 65535;
				break;
			case FLOAT :
				max = 1;
				break;
			default :
				return img;
		}
		
		JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(b, img.getWidth(), img.getHeight(), t);
		for (int nb = 0; nb < b; nb++) {
			double[] bmp = imgBmp.getAllPixels(nb);
			double[] bin = new double[totalPix];

			for (int k = 0; k < totalPix; k++) {
				bin[k] = bmp[k];
				bin[k] = bin[k] - (max / 2);
				bin[k] = (int) (bin[k] * p1 + 0.5);
				if (bin[k] < - (max / 2))
					bin[k] = -max / 2;
				if (bin[k] > (max / 2))
					bin[k] = max / 2;
				bin[k] += max / 2.0;
			}
			res.setAllPixels(nb, bin);
		}
		return res;
	}
}
