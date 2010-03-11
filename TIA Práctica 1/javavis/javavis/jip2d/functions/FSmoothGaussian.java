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
*Applies a gaussian smoothing to an input image.<BR>
*Applicable to: BYTE, WORD, COLOR and REAL<BR>
*Uses: FConvolveImage.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>sigma: Level of gaussian smooth<BR>
*<li>axis: Filter orientation (horizontal, vertical or both) <BR>
*<li>method: Method to treat the borders. ZERO->border pixels are marked as 0.
*PAD->The first row is duplicated so that the -1 row is the same. The same for last row and first and last columns. 
*WRAP->The -1 row is the last row and the n+1 row is the first. The same for columns.<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Processed image with the same type that input image.<BR><BR>
*</ul>
*/
public class FSmoothGaussian extends JIPFunction {
	private static final long serialVersionUID = -1139660566687024448L;

	public FSmoothGaussian() {
		super();
		name = "FSmoothGaussian";
		description = "Gaussian Smoothing";
		groupFunc = FunctionGroup.Adjustment;

		JIPParamFloat p1 = new JIPParamFloat("sigma", false, true);
		p1.setDefault(2.0f);
		p1.setDescription("Smoothing level");
		JIPParamList p2 = new JIPParamList("axis", false, true);
		String []paux2 = new String[3];
		paux2[0]="Both";
		paux2[1]="Horizontal";
		paux2[2]="Vertical";
		p2.setDefault(paux2);
		p2.setDescription("Filter orientation");
		JIPParamList p3 = new JIPParamList("method", false, true);
		String []paux = new String[3];
		paux[0]="ZERO";
		paux[1]="WRAP";
		paux[2]="PAD";
		p3.setDefault(paux);
		p3.setDescription("How to process the border");

		addParam(p1);
		addParam(p2);
		addParam(p3);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgGeometric || img.getType()==ImageType.BIT) 
			throw new JIPException("SmoothGaussian can not be applied to this image format");

		float p1 =  getParamValueFloat("sigma");
		String axis =  getParamValueString("axis");
		String method = getParamValueString("method");
		
		if (p1 == 0.0)
			return img;

		int radio = (int) (p1 * 3.0 + 0.5);
		int diametro = radio * 2 + 1;
		double[] vector = new double[diametro];

		double mult = 1.0 / (p1 * Math.sqrt(2.0 * Math.PI));
		double factor = -1.0 / (2.0 * p1 * p1);

		for (int i = 0, r = -radio; r <= radio; r++, i++) {
			vector[i] = mult * Math.exp(factor * r * r);
		}

		JIPFunction convolucion = new FConvolveImage();

		JIPImage convo = JIPImage.newImage(diametro, 1, ImageType.FLOAT);
		((JIPImgBitmap)convo).setAllPixels(vector);
		JIPImage convo2 = JIPImage.newImage(1, diametro, ImageType.FLOAT);
		((JIPImgBitmap)convo2).setAllPixels(vector);
		convolucion.setParamValue("method", method);

		if (axis.equals("Horizontal")) {
			convolucion.setParamValue("image", convo);
			return convolucion.processImg(img);
		}
		if (axis.equals("Vertical")) {
			convolucion.setParamValue("image", convo2);
			return convolucion.processImg(img);
		}
		if (axis.equals("Both")) {
			convolucion.setParamValue("image", convo);
			JIPImage aux = convolucion.processImg(img);
			convolucion.setParamValue("image", convo2);
			return convolucion.processImg(aux);
		}
		throw new JIPException("Invalid axis");
	}
}
