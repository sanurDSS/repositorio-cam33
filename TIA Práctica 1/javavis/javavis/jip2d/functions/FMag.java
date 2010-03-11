package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

    /**
    *Calculates the magnitude in a gradient image. To do that,
    *it calculates the square root of the sum of the square of the pixels of 
    *the even & odd bands.<BR>
    *Applicable to: BYTE, SHORT, and REAL<BR>
    *<ul><B>Input parameters:</B><BR>
    *<li>img: Input image to process with a even number of bands.<BR><BR>
    *</ul>
    *<ul><B>Output parameters:</B><BR>
    *<li>The result is an image with the same type than the original but it has half number
    *of bands.<BR><BR>
    *</ul>
    */
public class FMag extends JIPFunction {
	private static final long serialVersionUID = 5577393934194722663L;

	public FMag() {
		super();
	    name="FMag";
	    description= "Calculates the magnitude in a gradient image";
	    groupFunc = FunctionGroup.Edges;
    }

    public JIPImage processImg(JIPImage img) throws JIPException {
    	ImageType t=img.getType();
    	if (img instanceof JIPImgGeometric || t==ImageType.COLOR|| t==ImageType.BIT) 
    		throw new JIPException("Mag can not be applied to this image format");

		int totalPix = img.getWidth()*img.getHeight();
		JIPImgBitmap imgBmp = (JIPImgBitmap)img;
		int b=imgBmp.getNumBands();
		JIPImgBitmap res=(JIPImgBitmap)JIPImage.newImage(b/2, img.getWidth(), img.getHeight(), t);
		
		double[] bmp, bmp2, bin=new double[totalPix];
		double pp, pp2;
		for (int z=0; z<b; z=z+2) { 
			bmp=imgBmp.getAllPixels(z);
			bmp2=imgBmp.getAllPixels(z+1);
		  	for (int i=0; i<totalPix; i++) {
		  		pp=bmp[i];
	  			pp2=bmp2[i];
	  			bin[i]=Math.sqrt(pp*pp+pp2*pp2);
		  	}  
		  	res.setAllPixels(z/2, bin);
		}
		return res;
	}
}
