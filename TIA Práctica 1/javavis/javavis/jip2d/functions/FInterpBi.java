package javavis.jip2d.functions;

import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFloat;
import javavis.jip2d.base.*;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.bitmaps.JIPBmpByte;
import javavis.jip2d.base.bitmaps.JIPBmpColor;
import javavis.jip2d.base.bitmaps.JIPBmpFloat;
import javavis.jip2d.base.bitmaps.JIPBmpShort;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
* 
*Rerender an image using a bilinear interpolation.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>step: The resizing ratio.<BR><BR>
*</ul>
*/
public class FInterpBi extends JIPFunction {
	private static final long serialVersionUID = -1232282732231182119L;

	public FInterpBi() {
		super();
		name = "FInterpBi";
		description = "Rerendering an image using a bilinear interpolation.";
		groupFunc = FunctionGroup.Manipulation;
		
		JIPParamFloat p1 = new JIPParamFloat("step", false, true);
		p1.setDescription("The resizing ratio.");
		p1.setDefault(1.5f);
		
		addParam(p1);
	}



	public JIPImage processImg(JIPImage img) throws JIPException{
		int width, height, bands;
		JIPImgBitmap res=null, imgBmp = (JIPImgBitmap)img;
		int f1, c1;
		double x, y, paso, pix;
		
		paso = getParamValueFloat("step");
		width = (int)Math.floor(img.getWidth()/paso);
		height = (int)Math.floor(img.getHeight()/paso);
		bands = imgBmp.getNumBands();
		
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("FInterBi can not be applied to Geometric image types.");
		switch ( img.getType() ){
		case BYTE :
			res = new JIPBmpByte(bands, width,height);
			break;
		case SHORT :
			res = new JIPBmpShort(bands, width,height);
			break;
		case FLOAT :
			res = new JIPBmpFloat(bands, width,height);
			break;
		case BIT :
			res = new JIPBmpBit(bands, width,height);
			break;
		case COLOR :
			res = new JIPBmpColor(width,height);
			break;
		}
		
		for (int b=0; b<bands; b++) {
			for(int f=0; f<height; f++)
				for(int c=0; c<width; c++) {
					f1 = (int)(f*paso);
					c1 = (int)(c*paso);
					x = (c*paso) - c1;
					y = (f*paso) - f1;
					if (c1==img.getWidth()-1) c1--; // Controls the limits
					if (f1==img.getHeight()-1) f1--; // Controls the limits
					pix = calcInterp(imgBmp, b, f1, c1, x, y);
					res.setPixel(b, c, f, pix);				
				}
		}
					
		return res;		
	
	}
	
	/**
	 * Performs a bilinear interpolation.
	 * @param img image
	 * @param b band number
	 * @param f1 fila en la esquina superior izquierda de los 4 pixeles que se interpolan
	 * @param c1 columna en la esquina superior izquierda de los 4 pixeles que se interpolan
	 * @param x desplazamiento del punto donde queremos la interpolación con respecto a c1
	 * @param y desplazamiento del punto donde queremos la interpolación con respecto a f1
	 * @return interpolated pixel value
	 * @throws JIPException 
	 */
	private double calcInterp(JIPImgBitmap img, int b, int f1, int c1, double x, double y) throws JIPException {
		double p00,p10,p01,p11;
		p00 = img.getPixel(b, c1,f1);
		p10 = img.getPixel(b, c1+1, f1);
		p01 = img.getPixel(b, c1,f1+1);
		p11 = img.getPixel(b, c1+1,f1+1);
		return (p10-p00)*x +(p01-p00)*y + (p11+p00-p01-p10)*x*y + p00;	
	}
}
