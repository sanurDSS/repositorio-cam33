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
*Applies a convolution with a 3x3 mask.<BR>
*Applicable to: BYTE, WORD, COLOR and REAL<BR>
*Uses: FConvolveImage.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>matrix: Elements of the convolution matrix.<BR>
*<li>mult: Multiplicator<BR>
*<li>div: Divisor<BR>
*<li>method: Method to deal with the borders. ZERO->border pixels are marked as 0.
*PAD->The first row is duplicated so that the -1 row is the same. The same for last row and first and last columns. 
*WRAP->The -1 row is the last row and the n+1 row is the first. The same for columns.<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Processed image with the same type that input image.<BR><BR>
*</ul>
*/
public class FConvol3x3 extends JIPFunction {
	private static final long serialVersionUID = -2215425311082781579L;

	public FConvol3x3() { 
		super();
		name = "FConvol3x3";
		description = "Applies a convolution with a 3x3 mask.";
		groupFunc = FunctionGroup.Convolution;

		JIPParamFloat p1 = new JIPParamFloat("a1", false, true);
		p1.setDefault(0.0f);
		p1.setDescription("");
		JIPParamFloat p2 = new JIPParamFloat("a2", false, true);
		p2.setDefault(0.0f);
		p2.setDescription("");
		JIPParamFloat p3 = new JIPParamFloat("a3", false, true);
		p3.setDefault(0.0f);
		p3.setDescription("");
		JIPParamFloat p4 = new JIPParamFloat("b1", false, true);
		p4.setDefault(0.0f);
		p4.setDescription("");
		JIPParamFloat p5 = new JIPParamFloat("b2", false, true);
		p5.setDefault((float) 1);
		p5.setDescription("");
		JIPParamFloat p6 = new JIPParamFloat("b3", false, true);
		p6.setDefault(0.0f);
		p6.setDescription("");
		JIPParamFloat p7 = new JIPParamFloat("c1", false, true);
		p7.setDefault(0.0f);
		p7.setDescription("");
		JIPParamFloat p8 = new JIPParamFloat("c2", false, true);
		p8.setDefault(0.0f);
		p8.setDescription("");
		JIPParamFloat p9 = new JIPParamFloat("c3", false, true);
		p9.setDefault(0.0f);
		p9.setDescription("");
		JIPParamFloat p10 = new JIPParamFloat("mult", false, true);
		p10.setDefault(1.0f);
		p10.setDescription("Multiplicador ");
		JIPParamFloat p11 = new JIPParamFloat("div", false, true);
		p11.setDefault(1.0f);
		p11.setDescription("Divisor ");
		JIPParamList p12 = new JIPParamList("method", false, true);
		String []paux = new String[3];
		paux[0]="ZERO";
		paux[1]="WRAP";
		paux[2]="PAD";
		p12.setDefault(paux);
		p12.setDescription("How to process the border");

		addParam(p1);
		addParam(p2);
		addParam(p3);
		addParam(p4);
		addParam(p5);
		addParam(p6);
		addParam(p7);
		addParam(p8);
		addParam(p9);
		addParam(p10);
		addParam(p11);
		addParam(p12);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("Convol3x3 can not be applied to this image format");

		double mat[] = new double[9];
		mat[0] = getParamValueFloat("a1");
		mat[1] = getParamValueFloat("a2");
		mat[2] = getParamValueFloat("a3");
		mat[3] = getParamValueFloat("b1");
		mat[4] = getParamValueFloat("b2");
		mat[5] = getParamValueFloat("b3");
		mat[6] = getParamValueFloat("c1");
		mat[7] = getParamValueFloat("c2");
		mat[8] = getParamValueFloat("c3");
		float mult = getParamValueFloat("mult");
		float div = getParamValueFloat("div");
		String metodo = getParamValueString("method");

		JIPFunction convolucion = new FConvolveImage();
		JIPImage convo = JIPImage.newImage(3, 3, ImageType.FLOAT);
		((JIPImgBitmap)convo).setAllPixels(mat);

		convolucion.setParamValue("image", convo);
		convolucion.setParamValue("div", div);
		convolucion.setParamValue("mult", mult);
		convolucion.setParamValue("method", metodo);

		return convolucion.processImg(img);
	}
}
