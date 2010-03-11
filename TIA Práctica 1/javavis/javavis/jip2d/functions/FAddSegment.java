package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.JIPParameter;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.geometrics.JIPGeomSegment;

/**
*It adds a segment with indicated coordinates as parameter
*in a SEGMENT type image.<BR>
*Only applicable to a SEGMENT type image.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>x0: X Coordinate of origin<BR>
*<li>y0: Y Coordinate of origin<BR>
*<li>x1: X Coordinate of destiny<BR>
*<li>y1: Y Coordinate of destiny<BR><BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Segment type image with a new segment with
*coordinates corresponding to input parameters.<BR><BR>
*</ul>
*/

public class FAddSegment extends JIPFunction {
	private static final long serialVersionUID = 4303209767641719076L;

	public FAddSegment() {
		super();
		name = "FAddSegment";
		description = "Adds a segment in a segment type image.";
		groupFunc = FunctionGroup.Geometry;

		JIPParameter p1 = new JIPParamInt("x0", true, true);
		p1.setDescription("X origin");
		JIPParameter p2 = new JIPParamInt("y0", true, true);
		p2.setDescription("Y origin");
		JIPParameter p3 = new JIPParamInt("x1", true, true);
		p3.setDescription("X destiny");
		JIPParameter p4 = new JIPParamInt("y1", true, true);
		p4.setDescription("Y destiny");

		addParam(p1);
		addParam(p2);
		addParam(p3);
		addParam(p4);

	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		int x0 = getParamValueInt("x0");;
		int y0 = getParamValueInt("y0");;
		int x1 = getParamValueInt("x1");;
		int y1 = getParamValueInt("y1");;

		int w = img.getWidth();
		int h = img.getHeight();
		ImageType t = img.getType();
		if (t != ImageType.SEGMENT) 
			throw new JIPException("The image must be segment type");
		if (x0 < 0 || y0 < 0 || x1 < 0 || y1 < 0 || x0 > w - 1 || y0 > h - 1
			|| x1 > w - 1 || y1 > h - 1) 
			throw new JIPException("Dimensions exceed image size");
		((JIPGeomSegment)img).addSegment(x0, y0, x1, y1);
		return img;
	}
}
