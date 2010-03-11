package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpColor;

/**
*Eliminates the exterior area of an omnidirectional image.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>x: X coord of the center of the omnidirectional lens.<BR>
*<li>y: Y coord of the center of the omnidirectional lens.<BR>
*<li>rint: Internal circumference radius (in pixels).<BR>
*<li>rext: External circumference radius (in pixels).<BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Output rectified image .<BR><BR>
* @author Boyan Ivanov Bonev
*/
public class FCleanOmnidir extends JIPFunction {
	private static final long serialVersionUID = -6647025430380827941L;

	public FCleanOmnidir() {
		super();
		name = "FCleanOmnidir";
		description = "Paints in black the useless circular zones of an Omnidirectional Image";
		groupFunc = FunctionGroup.RingProjection;

		JIPParamInt p1 = new JIPParamInt("x", false, true);
		p1.setDefault(241);
		p1.setDescription("X coord of the center of the omnidirectional lens");
		addParam(p1);
		JIPParamInt p2 = new JIPParamInt("y", false, true);
		p2.setDefault(197);
		p2.setDescription("Y coord of the center of the omnidirectional lens");
		addParam(p2);
		JIPParamInt p3 = new JIPParamInt("rint", false, true);
		p3.setDefault(25);
		p3.setDescription("Internal circumference radius (in pixels)");
		addParam(p3);
		JIPParamInt p4 = new JIPParamInt("rext", false, true);
		p4.setDefault(151);
		p4.setDescription("External circumference radius (in pixels)");
		addParam(p4);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if ( img.getType() != ImageType.COLOR)
			throw new JIPException("FCleanOmnidir can not be applied to this image format, but only to COLOR (rgb)");

		JIPBmpColor imgCol = (JIPBmpColor)img;
		int cx =  getParamValueInt("x");
		int cy =  getParamValueInt("y");
		double rint =  getParamValueInt("rint");
		double rext =  getParamValueInt("rext");
		
		for(int w = 0; w < img.getWidth(); w++){
			for(int h = 0; h < img.getHeight(); h++){
				double d = distance(w,h, cx,cy); 
				if( d < rint || d > rext ){
					//Set pixel to Black.
					imgCol.setPixelRed(w, h, 0);
					imgCol.setPixelGreen(w, h, 0);
					imgCol.setPixelBlue(w, h, 0);
				}
			}
		}
			
		return img;
	}

	double distance(int x1, int y1, int x2, int y2){
		double r1 = x1 - x2;
		double r2 = y1 - y2;
		return Math.sqrt(r1*r1 + r2*r2);
	}
}



