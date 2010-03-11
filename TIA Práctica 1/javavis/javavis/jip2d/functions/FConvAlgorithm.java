package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*Defines and applies several well known algorithms for edge detection.<BR>
*Applicable to: BYTE, SHORT, COLOR and FLOAT<BR>
*Uses: FConvolveImage.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>method: Method to apply. Prewitt, Sobel and Laplacian.<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Processed image with the same type than input image.<BR><BR>
*</ul>
*/
public class FConvAlgorithm extends JIPFunction {
	private static final long serialVersionUID = 7850747295611493227L;

	public FConvAlgorithm() {
		super();
		name = "FConvAlgorithm";
		description = "Edge detection algorithms";
		groupFunc = FunctionGroup.Edges;

		
		JIPParamList p = new JIPParamList("method", false, true);
		String[] paux = new String[3];
		paux[0]="Prewitt";
		paux[1]="Sobel";
		paux[2]="Laplacian";
		p.setDefault(paux);
		p.setDescription("Method to apply");

		addParam(p);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {	
		if (img instanceof JIPImgGeometric || img.getType()==ImageType.BIT) 
			throw new JIPException("ConvAlgorithm can not be applied to this image format");

		String method = getParamValueString("method");
		JIPFunction convolucion = new FConvolveImage();
		double mat[] = new double[9]; // The three algorithms defines a 3x3 convolve matrix
		JIPImgBitmap convo = (JIPImgBitmap)JIPImage.newImage(3, 3, ImageType.FLOAT);
		int numBands = ((JIPImgBitmap)img).getNumBands();

		if (method.equals("Prewitt")) {
			JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(2*numBands, img.getWidth(), img.getHeight(), img.getType());
			JIPImgBitmap aux, aux2;
			mat[0] = 1.0; mat[1] = 0.0; mat[2] = -1.0;
			mat[3] = 1.0; mat[4] = 0.0; mat[5] = -1.0;
			mat[6] = 1.0; mat[7] = 0.0; mat[8] = -1.0;
			convo.setAllPixels(mat);
			convolucion.setParamValue("image", convo);
			aux = (JIPImgBitmap)convolucion.processImg(img);
			mat[0] = 1.0; mat[1] = 1.0; mat[2] = 1.0;
			mat[3] = 0.0; mat[4] = 0.0; mat[5] = 0.0;
			mat[6] = -1.0; mat[7] = -1.0; mat[8] = -1.0;
			convo.setAllPixels(mat);
			convolucion.setParamValue("image", convo);
			aux2 = (JIPImgBitmap)convolucion.processImg(img);
			for (int b=0; b<numBands; b++) {
				res.setAllPixels(b*2, aux.getAllPixels(b));
				res.setAllPixels(b*2+1, aux2.getAllPixels(b));
			}
			return res;
		}
		else if (method.equals("Sobel")) {
			JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(2*numBands, img.getWidth(), img.getHeight(), img.getType());
			JIPImgBitmap aux, aux2;
			mat[0] = 1.0; mat[1] = 0.0; mat[2] = -1.0;
			mat[3] = 2.0; mat[4] = 0.0; mat[5] = -2.0;
			mat[6] = 1.0; mat[7] = 0.0; mat[8] = -1.0;
			convo.setAllPixels(mat);
			convolucion.setParamValue("image", convo);
			aux = (JIPImgBitmap)convolucion.processImg(img);
			mat[0] = 1.0; mat[1] = 2.0; mat[2] = 1.0;
			mat[3] = 0.0; mat[4] = 0.0; mat[5] = 0.0;
			mat[6] = -1.0; mat[7] = -2.0; mat[8] = -1.0;
			convo.setAllPixels(mat);
			convolucion.setParamValue("image", convo);
			aux2 = (JIPImgBitmap)convolucion.processImg(img);
			for (int b=0; b<numBands; b++) {
				res.setAllPixels(b*2, aux.getAllPixels(b));
				res.setAllPixels(b*2+1, aux2.getAllPixels(b));
			}
			return res;
		}
		else if (method.equals("Laplacian")) {
			mat[0] = 0.0; mat[1] = -1.0; mat[2] = 0.0;
			mat[3] = -1.0; mat[4] = 4.0; mat[5] = -1.0;
			mat[6] = 0.0; mat[7] = -1.0; mat[8] = 0.0;
			convo.setAllPixels(mat);
			convolucion.setParamValue("image", convo);
			return convolucion.processImg(img);
		}
		else throw new JIPException("ConvAlgorithm: algorithm to apply not recognized");
	}
}
