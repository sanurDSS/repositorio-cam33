package javavis.jip2d.functions;

import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamObject;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;


/**
*This function applies a filter color to an image color. If the image is a RGB one, converts it into
*HSB format. If not, it assumes that the input image is HSB format (3 float bands). The first two parameters
*indicate the median (the Hue value) and variance of a PDF. So the result is an image which values indicate
*the probability of belonging (each pixel) to that PDF. It returns an array with the histogram (normalize and 
*accumulated)
*It is only applicable for: COLOR types (RGB and HSB).<BR>  
*<ul>Input parameters:
*<li>img1: input image
*<li>hmean: mean of Hue value
*<li>hvar: variance of Hue value 
*</ul>
*<ul>Output parameters:
*<li>output image.
*<li>histo: Histogram vector
*</ul>
*/
public class FFilterColor extends JIPFunction {
	private static final long serialVersionUID = -2021033445346710154L;

	public FFilterColor() {
		super();
	    name="FFilterColor";
	    description="Applies a filter color to an image";

		JIPParamFloat p1=new JIPParamFloat("hmean", false, true);
		p1.setDefault(0.2f);
		p1.setDescription("Median of Hue value");
		JIPParamFloat p2=new JIPParamFloat("hvar",false, true);
		p2.setDefault(0.02f);
		p2.setDescription("Variance of Hue value");
		JIPParamObject p3=new JIPParamObject("histo",false, false);
		p3.setDescription("Histogram of the result image");

		addParam(p1);
		addParam(p2);
		addParam(p3);
    }

    public JIPImage processImg(JIPImage img) throws JIPException {
    	if (!(img instanceof JIPImgBitmap) || 
    			(img.getType() != ImageType.COLOR && !(((JIPImgBitmap)img).getNumBands() == 3 && 
    			 img.getType() == ImageType.FLOAT))) 
    		throw new JIPException("FilterColor can be only applied to RGB or HSB images");
		
    	JIPImgBitmap imgBmp = null;
    	if (img.getType() == ImageType.COLOR) {
    		FRGBToColor func = new FRGBToColor();
    		func.setParamValue("format", "HSB");
    		imgBmp = (JIPImgBitmap)func.processImg(img);
    	}
    	else imgBmp = (JIPImgBitmap)img;
    	
		float mean=getParamValueFloat("hmean");
		float var=getParamValueFloat("hvar");
		double DOSPIVAR=1.0/(Math.sqrt(2.0f*Math.PI)*var);
		double var2 = 2.0*var*var;
		int size = imgBmp.getWidth()*imgBmp.getHeight();
    	JIPImgBitmap res=(JIPImgBitmap)JIPImage.newImage(1, imgBmp.getWidth(), imgBmp.getHeight(), ImageType.FLOAT);
    	double[] values = imgBmp.getAllPixels();
    	double[] resul = new double[size];
    	double value;
    	int histo[]=new int[100]; // Histogram
    	
    	// We have to consider that Hue color is circular, i.e., 1.0==0.0
    	for (int i=0; i<size; i++) {
    		value = values[i];
    		if (value<3*var && mean>1.0-3*var) value += 1.0;
    		if (value>1.0-3*var && mean<3*var) value -= 1.0;
    		resul[i] = DOSPIVAR*Math.exp(-Math.pow(value-mean,2.0f)/var2);
    		histo[(int)(99*resul[i]/DOSPIVAR)]++;
    	}
    	
    	res.setAllPixels(resul);
    	setParamValue("histo", histo);
    	
		return res;
	}
}