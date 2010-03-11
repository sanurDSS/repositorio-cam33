package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*The input image is equalized. 
*This function can not be used with geometry data.<BR>
*Applicable for Bitmap images<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR><BR>
*</ul>
<ul><B>Output parametes:</B><BR>
*<li>Processed image with its histogram equalized<BR><BR>
*</ul>
*/
public class FEqualize extends JIPFunction {
	private static final long serialVersionUID = 5081225558906799271L;

	public FEqualize() {
		super();
		name = "FEqualize";
		description = "Histogram equalization";
		groupFunc = FunctionGroup.Adjustment;
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		ImageType t = img.getType();
		if (img instanceof JIPImgGeometric || t == ImageType.BIT) 
			throw new JIPException("Equalize can not be applied to this image type");

		int w = img.getWidth();
		int h = img.getHeight();
		int totalPix = w*h;
		JIPImgBitmap imgBmp = (JIPImgBitmap)img;
		int b = imgBmp.getNumBands();
		double[] bmp, bin = new double[totalPix];
		double max=0;
		JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(b, w, h, t);
		switch (t) {
			case BYTE: 
			case COLOR: max=255; break;
			case FLOAT:
			case BIT: max=1; break;
			case SHORT: max=65535; break;
		}

		for (int nb = 0; nb < b; nb++) {
			double maxi = 0, mini = 70000;
			bmp = imgBmp.getAllPixels(nb);

			for (int k = 0; k < totalPix; k++) {
				if (bmp[k] < mini)
					mini = bmp[k];
				else if (bmp[k] > maxi)
					maxi = bmp[k];
			}

			for (int k = 0; k < totalPix; k++) {
				bin[k] = max*(bmp[k] - mini) / (maxi - mini);
			}

			res.setAllPixels(nb, bin);
		}
		return res;
	}
}
