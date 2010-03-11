package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFloat;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.bitmaps.JIPBmpFloat;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;

/**
*The input image must be a HSB image (at least 3 bands of float type).
*It takes the h, s and b parameters and using the corresponding error, binarizes the
*image.
*<ul><B>Input parameters:</B><BR>
*<li>img: Image to segment.<BR>
*<li>h: Hue value to binarize.<BR>
*<li>herror: Error admitted in hue band.<BR><BR> 
*<li>s: Saturation value to binarize.<BR>
*<li>serror: Error admitted in saturation band.<BR><BR>
*<li>b: Brightness value to binarize.<BR>
*<li>berror: Error admitted in brightness band.<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>BIT binarize image. <BR>
*</ul>
*/
public class FSegmentHSB extends JIPFunction {
	private static final long serialVersionUID = -2759433399980478199L;

	public FSegmentHSB() {
		super();
		name = "FSegmentHSB";
		description = "Color segment";
		groupFunc = FunctionGroup.Others;

		JIPParamFloat p1 = new JIPParamFloat("h", false, true);
		p1.setDefault(0.5f);
		p1.setDescription("Hue value");

		JIPParamFloat p2 = new JIPParamFloat("herror", false, true);
		p2.setDefault(0.01f);
		p2.setDescription("Margin of hue error");

		JIPParamFloat p3 = new JIPParamFloat("s", false, true);
		p3.setDefault(0.5f);
		p3.setDescription("Saturation value");

		JIPParamFloat p4 = new JIPParamFloat("serror", false, true);
		p4.setDefault(0.5f);
		p4.setDescription("Margin of saturation error");

		JIPParamFloat p5 = new JIPParamFloat("b", false, true);
		p5.setDefault(0.5f);
		p5.setDescription("Brightness value");

		JIPParamFloat p6 = new JIPParamFloat("berror", false, true);
		p6.setDefault(0.5f);
		p6.setDescription("Margin of brightness error");

		addParam(p1);
		addParam(p2);
		addParam(p3);
		addParam(p4);
		addParam(p5);
		addParam(p6);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img.getType() != ImageType.FLOAT || ((JIPImgBitmap)img).getNumBands() <3) 
			throw new JIPException("SegmentHSB can only be applied to a REAL image and" +
					" its band number must be at least 3");

		float valorH =  getParamValueFloat("h");
		float errorH =  getParamValueFloat("herror");
		float valorS =  getParamValueFloat("s");
		float errorS =  getParamValueFloat("serror");
		float valorB =  getParamValueFloat("b");
		float errorB =  getParamValueFloat("berror");
		int w = img.getWidth();
		int h = img.getHeight();
		boolean []todo = new boolean[w*h];
		float[] H = ((JIPBmpFloat)img).getAllPixelsFloat(0);
		float[] S = ((JIPBmpFloat)img).getAllPixelsFloat(1);
		float[] B = ((JIPBmpFloat)img).getAllPixelsFloat(2);
		float max=valorH+errorH;
		float min=valorH-errorH;
		
		for (int i = 0; i < w*h; i++) {
			if (min < 0.0 && H[i] > 0.5) 
				H[i] -= 1.0;
			if (max > 1.0 && H[i] < 0.5) 
				H[i] += 1.0;
			todo[i] = (H[i] >= min) && (H[i] <= max)
					&& (S[i] >= valorS - errorS) && (S[i] <= valorS + errorS) 
					&& (B[i] >= valorB - errorB) && (B[i] <= valorB + errorB);
		}
		JIPBmpBit res = (JIPBmpBit)JIPImage.newImage(w, h, ImageType.BIT);
		res.setAllPixelsBool(todo);
		return res;
	}
}
