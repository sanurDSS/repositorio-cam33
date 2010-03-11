package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*Applies a convolution with a 5x5 mask.<BR>
*Applicable to: BYTE, WORD, COLOR and REAL<BR>
*Uses: FConvolveImage.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>matrix: Elements which mach to the convolution matrix.<BR>
*<li>mult: Multiplicator<BR>
*<li>div: Divisor<BR>
*<li>method: Method to manage the borders. ZERO->border pixels are marked as 0.
*PAD->The first row is duplicated so that the -1 row is the same. The same for last row and first and last columns. 
*WRAP->The -1 row is the last row and the n+1 row is the first. The same for columns.<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Processed image with the same type that input image.<BR><BR>
*</ul>
*/
public class FConvol5x5 extends JIPFunction {
	private static final long serialVersionUID = -85842734760757503L;

	public FConvol5x5() {
		super();
		name = "FConvol5x5";
		description = "Applies a convolution with a 5x5 mask.";
		groupFunc = FunctionGroup.Convolution;

		JIPParamFloat p;
		for (int i = 0; i < 27; i++) {
			p = new JIPParamFloat("p" + i, false, true);
			if (i != 12 && i != 25 && i != 26)
				p.setDefault(0.0f);
			else
				p.setDefault(1.0f);
			p.setDescription("");
			addParam(p);
		}
		JIPParamList metodo = new JIPParamList("method", false, true);
		String []paux = new String[3];
		paux[0]="ZERO";
		paux[1]="WRAP";
		paux[2]="PAD";
		metodo.setDefault(paux);
		metodo.setDescription("Method to work with limits");
		addParam(metodo);
	}

	/**
	*It creates an image with a mask (3x3) and calls
	*FConvolveImage method which is where convolution is made.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>img: Image to process<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>Convoluted image with the user mask.<BR><BR>
	*</ul>
	*/
	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("Convol5x5 can not be applied to this image format");

		double[] mat = new double[25];
		for (int i = 0; i < 25; i++)
			mat[i] = getParamValueFloat("p"+i);
		float mul = getParamValueFloat("p25");
		float div = getParamValueFloat("p26");
		String metodo = getParamValueString("method");

		JIPFunction convolucion = new FConvolveImage();
		JIPImage convo = JIPImage.newImage(5, 5, ImageType.FLOAT);
		((JIPImgBitmap)convo).setAllPixels(mat);

		convolucion.setParamValue("image", convo);
		convolucion.setParamValue("div", div);
		convolucion.setParamValue("mult", mul);
		convolucion.setParamValue("method", metodo);

		return convolucion.processImg(img);
	}
}
