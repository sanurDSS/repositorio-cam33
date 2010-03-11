package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*Calculates the phase of a gradient image. To do that, the image
*should be a gradient, that is, each image has to be two bands.
*For example, we can use the output of FGrad. The phase is calculated
*using atan2.<BR>
*Applicable to: BIT, BYTE, WORD and REAL<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Processed image of real type, with the half bands that input image.<BR><BR>
*</ul>
*Additional notes: The input image should have a pair number of bands.<BR>
*/
public class FPhase extends JIPFunction {
	private static final long serialVersionUID = 7341222887994655211L;

	public FPhase() {
		super();
		name = "FPhase";
		description = "Calculates the phase of the complex image.";
		groupFunc = FunctionGroup.Edges;
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		ImageType t=img.getType();
    	JIPImgBitmap imgBmp = (JIPImgBitmap)img;
    	double[] bmp, bmp2;
    	double pp, pp2;

    	if (img instanceof JIPImgGeometric || t==ImageType.COLOR) 
    		throw new JIPException("Phase can not be applied to this image format");
    	int w = img.getWidth();
		int h = img.getHeight();
		int b = imgBmp.getNumBands();
		JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(b / 2, w, h, ImageType.FLOAT);

		double[] bin = new double[w*h];

		for (int z = 0; z < b; z = z + 2) { 
			bmp = imgBmp.getAllPixels(z);
			bmp2 = imgBmp.getAllPixels(z + 1);
			for (int i = 0; i < w*h; i++) { 
				pp = bmp[i];
				pp2 = bmp2[i];
				if (pp == 0.0 && pp2 == 0.0)
					bin[i] = 0;
				else
					bin[i] = Math.atan2(pp, pp2);
			}
			res.setAllPixels(z/2, bin);
		}
		return res;
	} 
}
