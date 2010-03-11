package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamBool;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpColor;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*Extends a catadrioptic image as it is a cylinder.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>x: X coord of the center of the omnidirectional lens.<BR>
*<li>y: Y coord of the center of the omnidirectional lens.<BR>
*<li>rint: Internal circumference radius (in pixels).<BR>
*<li>rext: External circumference radius (in pixels).<BR>
*<li>maxw: Rectangular image width.<BR>
*<li>maxh: Rectangular image height.<BR>
*<li>sphmodel: Spherical model for height (loses resolution)<BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Output rectified image .<BR><BR>
*</ul>
* @author Boyan Ivanov Bonev
*/
public class FRectifyOmnidir extends JIPFunction {
	private static final long serialVersionUID = 7653378936800769989L;
	
	public FRectifyOmnidir() {
		super();
		name = "FRectifyOmnidir";
		description = "Transforms a catadioptric image into a rectangular one.";
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
		JIPParamInt p5 = new JIPParamInt("maxw", false, true);
		p5.setDefault(900);
		p5.setDescription("Rectangular image width");
		addParam(p5);
		JIPParamInt p6 = new JIPParamInt("maxh", false, true);
		p6.setDefault(200);
		p6.setDescription("Rectangular image height");
		addParam(p6);
		JIPParamBool p7 = new JIPParamBool("sphmodel", false, true);
		p7.setDefault(true);
		p7.setDescription("Spherical model for height (loses resolution)");
		addParam(p7);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgGeometric || img.getType()==ImageType.BIT) 
			throw new JIPException("RectifyOmnidir can not be applied to this image type");

		int cx =  getParamValueInt("x");
		int cy =  getParamValueInt("y");
		int rint =  getParamValueInt("rint");
		int rext =  getParamValueInt("rext");
		int maxw =  getParamValueInt("maxw");
		int maxh =  getParamValueInt("maxh");
		boolean sph_model = getParamValueBool("sphmodel");
		JIPImgBitmap imgrect = (JIPImgBitmap)JIPImage.newImage(maxw, maxh, img.getType());
		JIPImgBitmap imgBmp = (JIPImgBitmap)img;
		
		double anglescale = Math.PI * 2.0 / maxw;
		double distnorm = rext-rint;
		
		for(int x = 0; x < maxw; x++){
			double angle = x * anglescale;
			for(int y = 0; y < maxh; y++){
				double dist  = fh(sph_model, (maxh - y)/(double)maxh) * distnorm + rint;
				double xomni = dist * Math.sin(angle) + cx;
				double yomni = dist * Math.cos(angle) + cy;
				if (imgBmp instanceof JIPBmpColor) {
					imgrect.setPixel(0,x,y,imgBmp.getPixel(0,(int)xomni, (int)yomni));
					imgrect.setPixel(1,x,y,imgBmp.getPixel(1,(int)xomni, (int)yomni));
					imgrect.setPixel(2,x,y,imgBmp.getPixel(2,(int)xomni, (int)yomni));
				}
				else 
					imgrect.setPixel(x,y,imgBmp.getPixel((int)xomni, (int)yomni));
			}
		}

		return imgrect;
	}

	/**
	 * Models the mapping between radius and height.
	 * @param x 
	 * @return
	 */
	double fh (boolean sph_model, double x){
		if( sph_model )
			return x*x;
		else
			return x;
	}
	
}



