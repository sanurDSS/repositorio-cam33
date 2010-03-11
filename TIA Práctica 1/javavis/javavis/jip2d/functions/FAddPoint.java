package javavis.jip2d.functions;

import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.base.JIPParameter;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.geometrics.JIPGeomPoint;

/**
*It adds a point, with coordinates indicated as parameter
*in a POINT type image.<BR>
*Only applicable for POINT type images.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>x: X Coordinate<BR>
*<li>y: Y Coordinate<BR><BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Point image with a new point with
*coordinates corresponding to input parameters.<BR><BR>
*</ul>
*/
public class FAddPoint extends JIPFunction {
	private static final long serialVersionUID = 667525781927539405L;

	public FAddPoint() {
		super();
		name = "FAddPoint";
		description = "Adds a point in a point type image.";
		groupFunc = FunctionGroup.Geometry;

		JIPParameter p1 = new JIPParamInt("x", true, true);
		p1.setDescription("X origin");
		JIPParameter p2 = new JIPParamInt("y", true, true);
		p2.setDescription("Y origin");

		addParam(p1);
		addParam(p2);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		int x0 = getParamValueInt("x"); 
		int y0 = getParamValueInt("y"); 

		int w = img.getWidth();
		int h = img.getHeight();
		if (img.getType() != ImageType.POINT)
			throw new JIPException("The image must be POINT type");
		if (x0 < 0 || y0 < 0 || x0 > w - 1 || y0 > h - 1) 
			throw new JIPException("Dimensions exceed image size");
		((JIPGeomPoint)img).addPoint(x0, y0);
		return img.clone();
	}
}
