package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpColor;

/**
*Adjusts the color bands brightness in a input image.<BR>
*Only applicable to COLOR type<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>r: Red percentage<BR>
*<li>g: Green percentage<BR>
*<li>b: Blue percentage<BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Processed image with a new brightness values for the color bands<BR><BR>
*</ul>
*/
public class FGamma extends JIPFunction {
	private static final long serialVersionUID = 7924490703944874892L;

	public FGamma() {
		super();
		name = "FGamma";
		description = "Adjusts the color bands intensity in a COLOR image.";
		groupFunc = FunctionGroup.Adjustment;

		JIPParamInt p1 = new JIPParamInt("r", false, true);
		p1.setDefault(100);
		p1.setDescription("Red percentage");
		JIPParamInt p2 = new JIPParamInt("g", false, true);
		p2.setDefault(100);
		p2.setDescription("Green percentage");
		JIPParamInt p3 = new JIPParamInt("b", false, true);
		p3.setDefault(100);
		p3.setDescription("Blue percentage");

		addParam(p1);
		addParam(p2);
		addParam(p3);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img.getType() != ImageType.COLOR) 
			throw new JIPException("Image must be COLOR type");

		JIPBmpColor res = null;
		int[] color = new int[3];
		color[0] = getParamValueInt("r");
		color[1] = getParamValueInt("g");
		color[2] = getParamValueInt("b");

		int w = img.getWidth();
		int h = img.getHeight();
		int totalPix = w*h;

		res = new JIPBmpColor(w,h);
		for (int nb = 0; nb < 3; nb++) {
			double[] bmp = ((JIPBmpColor)img).getAllPixels(nb);
			double[] bin = new double[totalPix];
			for (int i = 0; i < totalPix; i++)
				bin[i] = Math.min((int) (bmp[i] * color[nb] / 100.0f), 255);
			res.setAllPixels(nb, bin);
		}
		return res;
	}
}
