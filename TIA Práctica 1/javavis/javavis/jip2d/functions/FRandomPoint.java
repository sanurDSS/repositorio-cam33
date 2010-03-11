package javavis.jip2d.functions;

import java.util.Random;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.geometrics.JIPGeomPoint;

/**
*Adds a number of random points in the image.
*Image must be of type point.<BR>
*Applicable to: POINT<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>number: Number of points to generate<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Input image with the new points added.<BR><BR>
*</ul>
*/

public class FRandomPoint extends JIPFunction {
	private static final long serialVersionUID = 5093291346461720543L;

	public FRandomPoint() {
		super();
		name = "FRandomPoint";
		description = "Adds some random points";
		groupFunc = FunctionGroup.Geometry;

		JIPParamInt p1 = new JIPParamInt("number", false, true);
		p1.setDescription("Number of points to add");
		p1.setDefault(100);

		addParam(p1);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		int num = getParamValueInt("number");
		int w = img.getWidth();
		int h = img.getHeight();
		if (img.getType() != ImageType.POINT)
			throw new JIPException("RandomPoint can not be applied to this image format");
		if (num <= 0) 
			throw new JIPException("Number of points incorrect");

		Random rnd = new Random();
		int x0, y0;
		for (int i = 0; i < num; i++) {
			x0 = Math.abs(rnd.nextInt() % w);
			y0 = Math.abs(rnd.nextInt() % h);
			((JIPGeomPoint)img).addPoint(x0, y0);
		}

		return img.clone();
	}
}
