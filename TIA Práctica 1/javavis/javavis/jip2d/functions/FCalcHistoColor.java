package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamList;
import javavis.base.parameter.JIPParamObject;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;

/**
*It calculates the histogram from a color image. The histogram is calculated from the
*RGB image and the discretization parameter indicates the number of bins, 
*It returns a 3 dimensions array (one for each component),
*which is the calculated histogram. <BR>
*Only applicable for COLOR type.<BR>
*Use: FCalcHistoColor<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image.<BR>
*<li>disc: Discretization (number of bins)<BR>
*<li>type: Type of the image<BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Returns the input image, without change it.<BR>
*<li>histo: 3 dimensions float array.<BR><BR>
*</ul>
*/

public class FCalcHistoColor extends JIPFunction {
	private static final long serialVersionUID = 7003490716984734494L;

	public FCalcHistoColor() {
		super();
		name = "FCalcHistoColor";
		description = "Calculates the histogram of a color image";
		groupFunc = FunctionGroup.ImageBD;

		JIPParamInt p1 = new JIPParamInt("disc", false, true);
		p1.setDefault(20);
		p1.setDescription("Discretization (number of bins)");
		
		JIPParamList p2 = new JIPParamList("type", false, true);
		String []paux = new String[4];
		paux[0]="RGB";
		paux[1]="HSB";
		paux[2]="YCbCr";
		paux[3]="HSI";
		p2.setDefault(paux);
		p2.setDescription("Color format");
		
		addParam(p1);
		addParam(p2);
		
		// Output parameter
		JIPParamObject po = new JIPParamObject("histo", false, false);
		po.setDescription("3 dimensions float array");
		addParam(po);
	}
	
	public JIPImage processImg(JIPImage img) throws JIPException {
		// As we have 256 values for each band, we divide this value
		// by the discretization parameter (number of bins) to get 
		// the number of values in each bin
		String tipo = getParamValueString("type");
		int disc = getParamValueInt("disc");
		int size = img.getWidth()*img.getHeight();
		float binSize = 1.01f/disc;
		float [][][]acumF = new float[disc][disc][disc];
		JIPImgBitmap imagen;
		double[] F, S, T;

		// Cheks is the img is a color image
		if (img.getType() == ImageType.COLOR) {
			if (tipo.equals("RGB")) {
				binSize = 256.0f/disc;
		  		F=((JIPImgBitmap)img).getAllPixels(0);
		  		S=((JIPImgBitmap)img).getAllPixels(1);
		  		T=((JIPImgBitmap)img).getAllPixels(2);
			}
			else {
				FRGBToColor frtc = new FRGBToColor();
				frtc.setParamValue("format", tipo);
	
				imagen=(JIPImgBitmap)frtc.processImg(img);
				F=imagen.getAllPixels(0);
				S=imagen.getAllPixels(1);
				T=imagen.getAllPixels(2);
			}
			
	  		// Calculates the histogram
	  		for (int j = 0; j < size; j++) 
	  			acumF[(int)(F[j]/binSize)][(int)(S[j]/binSize)]
  									[(int)(T[j]/binSize)]++;
	  		
	  		// Normalize, so that the sum is 1
	  		for (int i = 0; i < disc; i++) 
	  			for (int j = 0; j < disc; j++)
	  				for (int k = 0; k < disc; k++)
	  					acumF[i][j][k] = acumF[i][j][k] / size;

	  		// The histogram calculated is stored		  		
	  		setParamValue("histo",acumF);
		}
		else
			throw new JIPException("CalcHistoColor only defined for COLOR images");
		
		return img;
	}
}
