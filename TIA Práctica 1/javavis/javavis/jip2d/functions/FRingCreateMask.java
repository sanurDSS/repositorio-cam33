package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.JIPSequence;
import javavis.jip2d.base.bitmaps.JIPBmpColor;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*Creates the Ring Mask necessary for Transformation-Ring-Projection operations.
*It returns nrings+1 images. Each image corresponds with a concentric ring of the 
*omnidirectional image and it is separated in a numFrame. The first numFrame is a mask, indicating
*for each pixel to which ring owns.
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>x: X coord of the center of the omnidirectional lens.<BR>
*<li>y: Y coord of the center of the omnidirectional lens.<BR>
*<li>rint: Internal circumference radius (in pixels).<BR>
*<li>rext: External circumference radius (in pixels).<BR>
*<li>nrings: Number of rings.<BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>nring frames and a mask image.<BR><BR>
* @author Boyan Ivanov Bonev
*/
public class FRingCreateMask extends JIPFunction {
	private static final long serialVersionUID = 6965913568509743576L;

	public FRingCreateMask() {
		super();
		name = "FRingCreateMask";
		description = "Creates the Ring Mask necessary for Transformation-Ring-Projection operations.";
		groupFunc = FunctionGroup.RingProjection;
		
		JIPParamInt p1 = new JIPParamInt("x", false, true);
		p1.setDefault(241);
		p1.setDescription("X coord of the center of the rings");
		addParam(p1);
		JIPParamInt p2 = new JIPParamInt("y", false, true);
		p2.setDefault(197);
		p2.setDescription("Y coord of the center of the rings");
		addParam(p2);
		JIPParamInt p3 = new JIPParamInt("rint", false, true);
		p3.setDefault(25);
		p3.setDescription("Internal circumference radius (in pixels)");
		addParam(p3);
		JIPParamInt p4 = new JIPParamInt("rext", false, true);
		p4.setDefault(151);
		p4.setDescription("External circumference radius (in pixels)");
		addParam(p4);
		JIPParamInt p5 = new JIPParamInt("nrings", false, true);
		p5.setDefault(4);
		p5.setDescription("Number of rings");
		addParam(p5);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		throw new JIPException("Please, apply to complete sequence");
	}
	
	public JIPSequence processSeq(JIPSequence seq) throws JIPException {
		JIPImage img = seq.getFrame(0);
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("RingCreateMask can only be applied to bitmaps images");
		
		int cx =  getParamValueInt("x");
		int cy =  getParamValueInt("y");
		int nrings = getParamValueInt("nrings");
		double rint =  getParamValueInt("rint");
		double rext =  getParamValueInt("rext");
		double ri[] = new double[nrings+1];
		JIPImgBitmap maskimg = (JIPImgBitmap)JIPImage.newImage(1, img.getWidth(), img.getHeight(), ImageType.BYTE);
		JIPImgBitmap imgcuts[] = new JIPImgBitmap[nrings];
		for(int i=0; i<nrings; i++)
			imgcuts[i] = (JIPImgBitmap)JIPImage.newImage(1, img.getWidth(), img.getHeight(), img.getType());
		
		for(int i=0; i<=nrings; i++)
			ri[i] = (rext-rint)*i/nrings + rint;
		
		for(int w = 0; w < img.getWidth(); w++){
			for(int h = 0; h < img.getHeight(); h++){
				double d = distance(w,h, cx,cy);
				for(int i=0; i<=nrings;i++){
					if( ri[i] > d  ){
						maskimg.setPixel(w,h,i);
						if(i>0){
							if (img.getType() == ImageType.COLOR) {
								((JIPBmpColor)imgcuts[i-1]).setPixelRed(w,h,((JIPBmpColor)img).getPixelRed(w,h));
								((JIPBmpColor)imgcuts[i-1]).setPixelGreen(w,h,((JIPBmpColor)img).getPixelGreen(w,h));
								((JIPBmpColor)imgcuts[i-1]).setPixelBlue(w,h,((JIPBmpColor)img).getPixelBlue(w,h));
							}
							else
								imgcuts[i-1].setPixel(w, h, ((JIPImgBitmap)img).getPixel(w,h));
						}
						break;
					}
				}
			}
		}
			
		seq = new JIPSequence(maskimg);
		for(int i=0; i<nrings; i++)
			seq.addFrame(imgcuts[i]);
		return seq;
	}

	double distance(int x1, int y1, int x2, int y2){
		double r1 = x1 - x2;
		double r2 = y1 - y2;
		return Math.sqrt(r1*r1 + r2*r2);
	}

}




