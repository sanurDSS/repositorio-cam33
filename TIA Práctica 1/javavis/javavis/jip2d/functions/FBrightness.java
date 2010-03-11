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
*It adjusts the brightness of the image. If a value is greater/lower than 
*the maximum/minimum value of the type (e.g., 255) it keeps the maximum/minimum value.<BR>
*Apply to BYTE, WORD, COLOR and FLOAT types.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>perc: Percentage (when 100% the image is not modified)<BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Processed image, with the same type that the input image.<BR>
*</ul>
*/

public class FBrightness extends JIPFunction {
	private static final long serialVersionUID = 2124203301892703158L;

	public FBrightness() {
		super();
		name = "FBrightness";
		description = "Adjusts the brightness of the image.";
		groupFunc = FunctionGroup.Adjustment;

		JIPParamInt p1 = new JIPParamInt("perc", false, true);
		p1.setDefault(100);
		p1.setDescription("Percentage (when 100% the image is not modified)");

		addParam(p1);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		JIPImgBitmap res = null;
		float p1 = (float) getParamValueInt("perc") / 100.0f;
		ImageType t = img.getType();
		if (t==ImageType.BIT || img instanceof JIPImgGeometric) 
			throw new JIPException ("Brightness can not be applied to this image format");
		JIPImgBitmap imgBmp = (JIPImgBitmap)img;
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

		res = (JIPImgBitmap)imgBmp.clone();
		for (int nb = 0; nb < b; nb++) {
			double[] bmp = imgBmp.getAllPixels(nb);
			double[] bin = new double[totalPix];
			for (int i = 0; i < totalPix; i++)
				bin[i] = Math.min(bmp[i] * p1, max);
			res.setAllPixels(nb, bin);
		}
		return res;
	}
}
