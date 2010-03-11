package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.bitmaps.JIPBmpByte;

/**
*It converts the input byte image in a binary image. To do that,
*a pixel in the output image is 1 if the value in the corresponding input image
* is between the range [u1, u2], 0 otherwise.<BR>
*Only applicable for BYTE type.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image.<BR>
*<li>u1: Lower bound of the range to consider as 1<BR>
*<li>u2: Upper bound of the range to consider as 1<BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Binary image.<BR><BR>
*</ul>
*/

public class FBinarize extends JIPFunction {
	private static final long serialVersionUID = -7262973524107183332L;

	public FBinarize() {
		super();
		name = "FBinarize";
		description = "Transforms a BYTE image to binary";
		groupFunc = FunctionGroup.Transform;

		JIPParamInt p1 = new JIPParamInt("u1", false, true);
		p1.setDefault(128);
		p1.setDescription("Lower bound of the range to consider as 1");
		JIPParamInt p2 = new JIPParamInt("u2", false, true);
		p2.setDefault(255);
		p2.setDescription("Upper bound of the range to consider as 1");

		addParam(p1);
		addParam(p2);
	}
	
	public JIPImage processImg(JIPImage img) throws JIPException {
		JIPBmpBit res = null;
		int p1 = getParamValueInt("u1");
		int p2 = getParamValueInt("u2");
		if (img.getType() == ImageType.BYTE) {
			int w = img.getWidth();
			int h = img.getHeight();
			int totalPix = w*h;
			int b = ((JIPBmpByte)img).getNumBands();
			res = new JIPBmpBit(b, w, h);
			long percTotal = totalPix * b;
			for (int nb = 0; nb < b; nb++) {
				double[] bmp = ((JIPBmpByte)img).getAllPixels(nb);
				boolean[] bin = new boolean[totalPix];
				for (int i = 0; i < totalPix; i++) {
					bin[i] = (bmp[i] >= p1 && bmp[i] <= p2);
					percProgress = (int)((100*((nb+1)*totalPix + i))/percTotal);
				}
				res.setAllPixelsBool(nb, bin);
			}
		}
		else
			throw new JIPException("Binarize only defined for BYTE images");
		return res;
	}
}
