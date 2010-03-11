package javavis.jip2d.functions;


import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFloat;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
* 
*Calculates the entropy of an image. It returns the same image and a float, being this last the entropy value 
*for the input image. <BR>
*Applicable to bitmap images<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Original image.</li>
*<li>entropy: entropy value of the input image
*</ul>
*/

public class FEntropy extends JIPFunction {
	private static final long serialVersionUID = 5002721718109542932L;

	public FEntropy() {
		super();
		name = "FEntropy";
		description = "Calculates the entropy of the image";
		groupFunc = FunctionGroup.Others;

		JIPParamFloat p1 = new JIPParamFloat("entropy", false, false);

		addParam(p1);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("Entropy can not be applied to this image format");
		
		double[] pixels;
		int[] histo;
		float[] histoNorm;
		float entropy;
		JIPImgBitmap imgBmp;
		
		//First, it converts all possible formats into BYTE
		if (img.getType()==ImageType.COLOR) {
			FColorToGray fcg = new FColorToGray();
			fcg.setParamValue("gray", "BYTE");
			imgBmp=(JIPImgBitmap)fcg.processImg(img);
		}
		else if (img.getType() != ImageType.BYTE) {
			FGrayToGray fgg = new FGrayToGray();
			fgg.setParamValue("gray", "BYTE");
			imgBmp=(JIPImgBitmap)fgg.processImg(img);
		}
		else
			imgBmp = (JIPImgBitmap)img;
		
		pixels=imgBmp.getAllPixels();
		histo = new int[256];
		histoNorm = new float[256];
		
		for (double d:pixels) histo[(int)d]++;
		entropy = 0.0f;
		for (int i=0; i<256; i++) {
			if (histo[i]!=0) {
				histoNorm[i] = histo[i]/(float)pixels.length;
				entropy -= histoNorm[i]*Math.log(histoNorm[i]);
			}
		}
		
		
		// Trying gradient
		double mag;
		int[] histog = new int[181];
		float[] histogNorm = new float[181];
		double max=0.0f;
		for (int c=1; c<imgBmp.getWidth(); c++)
			for (int r=1; r<imgBmp.getHeight(); r++) {
				mag=Math.sqrt(Math.pow((imgBmp.getPixel(c, r)-imgBmp.getPixel(c-1, r))/2.0, 2.0) + 
				              Math.pow((imgBmp.getPixel(c, r)-imgBmp.getPixel(c, r-1))/2.0, 2.0));
				histog[(int)mag]++;
				if (mag>max) max=mag;
			}
		
		for (int i=0; i<181; i++)
			System.out.print(histog[i]+" ");
		System.out.println();
		System.out.println("max="+max);
		float entropyg = 0.0f;
		for (int i=0; i<181; i++) {
			if (histog[i]!=0) {
				histogNorm[i] = histog[i]/(float)pixels.length;
				entropyg -= histogNorm[i]*Math.log(histogNorm[i]);
			}
		}
		
		System.out.println("Enrtopy="+entropy+" Entropyg="+entropyg);
		setParamValue("entropy", entropy);
		
		return img;
	}
}
