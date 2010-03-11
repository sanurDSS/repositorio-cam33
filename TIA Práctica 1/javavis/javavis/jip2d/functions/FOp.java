package javavis.jip2d.functions;

import java.math.BigInteger;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamImage;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*Makes arithmetical operations (add, divide, and, etc.) between images and/or a value.<BR>
*Applicable to bitmap images<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>op: Operator to use.<BR>
*<li>value: Constant, in case of use a fixed value which second operator.<BR>
*<li>imgOp: Image which is used as second operator if it is not a fixed value.<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Processed image depending on selected operator.<BR><BR>
*</ul>
*/
public class FOp extends JIPFunction {
	private static final long serialVersionUID = 8402536396742702064L;

	public FOp() {
		super();
		name = "FOp";
		description =
			"Make arithmetical operations between input pixels and a value or another image.";
		groupFunc = FunctionGroup.Others;

		JIPParamList p1 = new JIPParamList("op", false, true);
		String []paux = new String[14];
		paux[0]=OperationType.EXP.toString();
		paux[1]=OperationType.LOG.toString();
		paux[2]=OperationType.SQRT.toString();
		paux[3]=OperationType.SQUARE.toString();
		paux[4]=OperationType.ADD.toString();
		paux[5]=OperationType.SUBST.toString();
		paux[6]=OperationType.MULT.toString();
		paux[7]=OperationType.DIV.toString();
		paux[8]=OperationType.DIST.toString();
		paux[9]=OperationType.MAX.toString();
		paux[10]=OperationType.MIN.toString();
		paux[11]=OperationType.AND.toString();
		paux[12]=OperationType.OR.toString();
		paux[13]=OperationType.XOR.toString();
		p1.setDefault(paux);
		p1.setDescription("Operation to apply");
		JIPParamFloat p2 = new JIPParamFloat("value", false, true);
		p2.setDescription("Operator. If you choose this operator, the image operator is ignored.");
		p2.setDefault(0.0f);
		JIPParamImage p3 = new JIPParamImage("imgOp", false, true);
		p3.setDescription("Image operator. It has to be a JIP file.");

		addParam(p1);
		addParam(p2);
		addParam(p3);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("Op can not be applied to this image format");
		JIPImgBitmap res = null;
		String aux = getParamValueString("op");
		OperationType p1=Enum.valueOf(OperationType.class, aux);
		float p2 = getParamValueFloat("value");
		JIPImgBitmap p3 = (JIPImgBitmap)getParamValueImg("imgOp");

		int w = img.getWidth();
		int h = img.getHeight();
		JIPImgBitmap imgBmp = (JIPImgBitmap)img;
		int b = imgBmp.getNumBands();
		ImageType t = img.getType();

		if (p3 != null) {
			if (t != p3.getType()) 
				throw new JIPException("Image format must be identical");
			if (w != p3.getWidth() || h != p3.getHeight()) 
				throw new JIPException("Image sizes must be identical");
			if (b != p3.getNumBands()) 
				throw new JIPException("Number of bands must be identical");
		}
		if ((p1 == OperationType.AND || p1 == OperationType.OR || p1 == OperationType.XOR) && t == ImageType.FLOAT) 
			throw new JIPException("Logical operators can not be applied to this image format");

		res = (JIPImgBitmap)JIPImage.newImage(b, w, h, t);
		// For each band
		for (int nb = 0; nb < b; nb++) {
				double valAux = p2;
				double[] bmp = imgBmp.getAllPixels(nb);
				double[] bin = new double[w * h];
				for (int y = 0; y < h; y++)
					for (int x = 0; x < w; x++) {
						if (p3 != null) 
							valAux = p3.getPixel(nb, x, y);
						bin[x + y * w] = calcValue(bmp[x + y * w], p1, valAux);
					}
				res.setAllPixels(nb, bin);
		}
		return res;
	}

	private double calcValue(double val, OperationType p, double valAux) {
		double valRes = 0;
		BigInteger val1, val2;
		switch (p) {
			case EXP :
				valRes = Math.exp(val);
				break;
			case LOG :
				valRes = Math.log(val);
				break;
			case SQRT :
				valRes = Math.sqrt(val);
				break;
			case SQUARE :
				valRes = Math.pow(val, 2.0);
				break;
			case ADD :
				valRes = val + valAux;
				break;
			case SUBST :
				valRes = val - valAux;
				break;
			case MULT :
				valRes = val * valAux;
				break;
			case DIV :
				if (valAux == 0)
					valRes = 1000000;
				else
					valRes = val / valAux;
				break;
			case DIST :
				valRes = Math.pow(val - valAux, 2.0);
				break;
			case MAX :
				valRes = Math.max(val, valAux);
				break;
			case MIN :
				valRes = Math.min(val, valAux);
				break;
			case AND :
				val1 = new BigInteger("" + val);
				val2 = new BigInteger("" + valAux);
				valRes = (val1.and(val2)).intValue();
				break;
			case OR :
				val1 = new BigInteger("" + val);
				val2 = new BigInteger("" + valAux);
				valRes = (val1.or(val2)).intValue();
				break;
			case XOR :
				val1 = new BigInteger("" + val);
				val2 = new BigInteger("" + valAux);
				valRes = (val1.xor(val2)).intValue();
				break;
		}
		return valRes;
	}
	
	enum OperationType {EXP, LOG, SQRT, SQUARE, ADD, SUBST, MULT, DIV, DIST, MAX, MIN, AND, OR, XOR;}
}
