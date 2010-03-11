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
*Input parameters indicate the coordinates of the crop.<BR>
*If the width or height are greater than the image size, a zero region is
*added. It is only applicable for bitmap images.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>x: X coordinate of the upper left corner in the clipping<BR>
*<li>y: Y coordinate of the upper left corner in the clipping<BR>
*<li>w: Width of the clipping rectangle<BR>
*<li>h: Height of the clipping rectangle<BR><BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Frame of the image with the same type of the input image<BR><BR>
*</ul>
*/

public class FCrop extends JIPFunction {
	private static final long serialVersionUID = 6824908906482994407L;

	public FCrop() {
		super();
		name = "FCrop";
		description = "Cuts out a rectangular area in a image";
		groupFunc = FunctionGroup.Manipulation;

		JIPParamInt p1 = new JIPParamInt("x", false, true);
		p1.setDefault(0);
		p1.setDescription(
			"X coordinate of the upper left corner in the clipping del recorte");
		JIPParamInt p2 = new JIPParamInt("y", false, true);
		p2.setDefault(0);
		p2.setDescription(
			"Y coordinate of the upper left corner in the clipping del recorte");
		JIPParamInt p3 = new JIPParamInt("w", false, true);
		p3.setDescription("Width of rectangle");
		JIPParamInt p4 = new JIPParamInt("h", false, true);
		p4.setDescription("Height of rectangle");

		addParam(p1);
		addParam(p2);
		addParam(p3);
		addParam(p4);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		ImageType t = img.getType();
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("Crop can not be applied to this image type");
		int xrec = getParamValueInt("x");
		int yrec = getParamValueInt("y");
		int wrec = getParamValueInt("w");
		int hrec = getParamValueInt("h");
		double fpix;
			
		JIPImgBitmap imgBmp = (JIPImgBitmap)img;
		int w = imgBmp.getWidth();
		int h = imgBmp.getHeight();
		int b = imgBmp.getNumBands();
		JIPImgBitmap res = null;
		if (xrec >= 0 && yrec >= 0 && xrec < w && yrec < h && wrec > 0 && hrec > 0) {
			res = (JIPImgBitmap)JIPImage.newImage(b, wrec, hrec, t);
			for (int nb = 0; nb < b; nb++)
				for (int y = 0; y < hrec; y++)
					for (int x = 0; x < wrec; x++) {
						fpix = (xrec + x < w && yrec + y < h)
							 ? imgBmp.getPixel(nb, xrec + x, yrec + y) : 0;
						res.setPixel(nb, x, y, fpix);
					}
		}
		else 
			throw new JIPException("Dimensions exceed image size");
			
		return res;
	}
}
