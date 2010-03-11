package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpColor;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;

/**
*FgrayToColor changes a BYTE, SHORT, BIT into a REAL type. To do that, a conversion between original ranges
*(e.g. 0..65556) into color range (0..255) must be done.
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image with type BYTE, BIT or REAL<BR>
<BR> 
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Color image.<BR><BR>
*</ul>
*/
public class FGrayToColor extends JIPFunction {
	private static final long serialVersionUID = -520184657904679850L;

	public FGrayToColor() {
		super();
		name = "FGrayToColor";
		description = "Converts a BYTE, SHORT, BIT or REAL image into a COLOR image.";
		groupFunc = FunctionGroup.Transform;
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		ImageType tipo = img.getType();
		JIPBmpColor res = null;

		if (tipo == ImageType.FLOAT || tipo == ImageType.BYTE || tipo == ImageType.BIT || tipo == ImageType.SHORT) {
			res = new JIPBmpColor(img.getWidth(), img.getHeight());
			double[] temp = ((JIPImgBitmap)img).getAllPixels();
			// In case of float, we assume that values in the float type are between 0.0 and 1.0
			if (tipo == ImageType.FLOAT || tipo == ImageType.BIT) { 
				for (int i = 0; i < temp.length; i++) {
					temp[i] *= 255;
					if (temp[i]<0.0) temp[i]=0.0;
					if (temp[i]>255) temp[i]=255;
				}
			}
			else if (tipo == ImageType.SHORT) { 
				for (int i = 0; i < temp.length; i++) {
					temp[i] = 255*temp[i]/65535;
					if (temp[i]<0.0) temp[i]=0.0;
					if (temp[i]>255) temp[i]=255;
				}
			}
			res.setAllPixels(temp);
		}
		else 
			throw new JIPException("GrayToColor can not be applied to this image format");
		
		return res;
	} 
}
