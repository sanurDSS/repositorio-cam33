package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamBool;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*Makes a convolution of the image with a matrix which enhances the input 
*image. Applicable to: BYTE, WORD, COLOR and REAL<BR>
*Uses: FConvolveImage<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>level: From 1 (-0.1 in its neighborhood) to 100 (-10.0 in its neighborhood)<BR>
*<li>strong: Indicates if the sharpen is strong or not<BR>
*<li>method: Method to treat the borders. ZERO->border pixels are marked as 0.
*PAD->The first row is duplicated so that the -1 row is the same. The same for last row and first and last columns. 
*WRAP->The -1 row is the last row and the n+1 row is the first. The same for columns.<BR><BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Image with the same type than input image and enhanced.<BR><BR>
*</ul>
*/
public class FSharpen extends JIPFunction {
	private static final long serialVersionUID = 1147520040846581270L;

	public FSharpen() {
		super();
		name = "FSharpen";
		description = "Sharpen an image";
		groupFunc = FunctionGroup.Convolution;

		JIPParamInt p1 = new JIPParamInt("level", false, true);
		p1.setDescription("Level of brightness (1..100)");
		p1.setDefault(5);
		JIPParamList p2 = new JIPParamList("method", false, true);
		String []paux = new String[3];
		paux[0]="ZERO";
		paux[1]="WRAP";
		paux[2]="PAD";
		p2.setDefault(paux);
		p2.setDescription("Method to process the border");
		JIPParamBool p3 = new JIPParamBool ("strong", false, true);
		p3.setDescription("Indicates if the sharpen is strong");
		p3.setDefault(false);

		addParam(p1);
		addParam(p2);
		addParam(p3);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img.getType() == ImageType.BIT || img instanceof JIPImgGeometric) 
			throw new JIPException("Sharpen can not be applied to this image format");
		String p2 = getParamValueString("method");
		int nivel = getParamValueInt("level"), aux;
		boolean strong = getParamValueBool("strong");

		if (nivel < 1 || nivel > 100) 
			throw new JIPException("Level parameter must be in range [1,100]");
		
		double[] mat = new double[9];
		double valor = -nivel / 10.0;
		mat[1] = mat[3] = mat[5] = mat[7] = valor;
		if (strong) {
			mat[0] = mat[2] = mat[6] = mat[8] = valor;
			aux = -8;
		}
		else {
			aux = -4;
		}
		mat[4] = valor * aux + 1;		
		
		JIPFunction convolucion = new FConvolveImage();
		JIPImgBitmap convo = (JIPImgBitmap)JIPImage.newImage(3, 3, ImageType.FLOAT);
		convo.setAllPixels(mat);
		convolucion.setParamValue("image", convo);
		convolucion.setParamValue("div", 1.0f);
		convolucion.setParamValue("mult", 1.0f);
		convolucion.setParamValue("method", p2);
		return convolucion.processImg(img);
	}
}
