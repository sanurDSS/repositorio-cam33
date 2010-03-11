package javavis.jip2d.functions;

import java.util.Random;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.geometrics.JIPGeomSegment;

/**
*Adds a number of random segments into the image. 
*Image must be of type segment.<BR>
*Applicable to: SEGMENT<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>number: Number of segments to add.<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Output image with the new segments added.<BR><BR>
*</ul>
*/
public class FRandomSegment extends JIPFunction {
	private static final long serialVersionUID = 4452325343561072193L;

	public FRandomSegment() {
		super();
		name = "FRandomSegment";
		description = "Adds random segments";
		groupFunc = FunctionGroup.Geometry;

		JIPParamInt p1 = new JIPParamInt("number", false, true);
		p1.setDescription("Number of segments to add");
		p1.setDefault(100);

		addParam(p1);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		int num = getParamValueInt("number");
		int w = img.getWidth();
		int h = img.getHeight();
		if (img.getType() != ImageType.SEGMENT) 
			throw new JIPException("RandomSegment can not be applied to this image format");
		if (num <= 0) 
			throw new JIPException("Number of segments incorrect");

		Random rnd = new Random();
		for (int i = 0; i < num; i++) 
			((JIPGeomSegment)img).addSegment(Math.abs(rnd.nextInt() % w), Math.abs(rnd.nextInt() % w), 
					Math.abs(rnd.nextInt() % h), Math.abs(rnd.nextInt() % h));
		return img.clone();
	}
}
