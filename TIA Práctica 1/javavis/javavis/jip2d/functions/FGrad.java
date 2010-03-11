package javavis.jip2d.functions;

import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpFloat;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;

/**
*Calculates the gradient of an image. To do that, for each pixel 
*it uses gradx=I(x+1,y)-I(x-1,y) and the same for Y axis.
*The output is another image but with the double bands than the 
*original image.<BR>.
*Only applicable to: BIT, BYTE, SHORT, and REAL types<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR><BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Image which has double bands than the original image. The added bands have
*the X gradient and the even bands the Y. The result is always real.<BR><BR>
*/

public class FGrad extends JIPFunction {
	private static final long serialVersionUID = -1264860928372025034L;

	public FGrad() {
		super();
		name = "FGrad";
		description = "Calculates image gradient";
		groupFunc = FunctionGroup.Edges;
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img.getType() == ImageType.BIT || img.getType() == ImageType.BYTE 
				|| img.getType() == ImageType.SHORT || img.getType() == ImageType.FLOAT) {
			int w = img.getWidth();
			int h = img.getHeight();
			int b = ((JIPImgBitmap)img).getNumBands();
			JIPBmpFloat res = new JIPBmpFloat(2 * b, w, h);
			double[] binflo = new double[w * h];
			double[] binflo2 = new double[w * h];
			double[] bmpflo;
			
			JIPImgBitmap img2;
			if (img.getType() != ImageType.FLOAT) {
				FGrayToGray fgtg = new FGrayToGray();
				fgtg.setParamValue("gray","FLOAT");
				img2 = (JIPImgBitmap)fgtg.processImg(img);
			}
			else img2=(JIPBmpFloat)img;

			for (int z = 0; z < b; z++) {
				bmpflo = img2.getAllPixels(z);
				for (int x = 1; x < w - 1; x++)  
					for (int y = 1; y < h - 1; y++) { 
						binflo[x + y * w] = bmpflo[x + 1 + y * w] - bmpflo[x - 1 + y * w];
						binflo2[x + y * w] = bmpflo[x + (y - 1) * w] - bmpflo[x
								+ (y + 1) * w];
					}
				res.setAllPixels(2*z, binflo);
				res.setAllPixels(2*z + 1, binflo2);
			}
			return res;
		}
		else 
			throw new JIPException("Grad can not be applied to this image type");
	}
}
