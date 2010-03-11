package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFile;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;
import javavis.jip2d.util.MatMorph;

/**
*The code reads a file which contains the
*description of the structurant element (ee), that is, it contains the dimensions of the mask 
*(width and height),
*and matrix of 0s & 1s which defines the ee. It processes the image with the structurant element
*then obtains the eroded image. It is only applicable for: BIT, BYTE, WORD and COLOR types.<BR>  
*<ul><B>Input parameters:</B><BR>
*<li>img1: input image<BR>
*<li>ee: File with the description of the structurant element.<br><BR><BR> 
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Eroded image of grey scale.<BR><BR>
*</ul>
*/
public class FErode extends JIPFunction {
	private static final long serialVersionUID = -3284565371955221012L;

	public FErode() {
		super();
		name = "FErode";
		description = "Apply the erode morphological operator";
		groupFunc = FunctionGroup.Math_morph;

		JIPParamFile p1 = new JIPParamFile("ee", true, true);
		p1.setDescription("Estructurant Element");

		addParam(p1);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgGeometric || img.getType() == ImageType.FLOAT) 
			throw new JIPException("Opening can not be applied to this image type");

		MatMorph mm = new MatMorph (getParamValueString("ee"));
		if (!mm.isCorrect()) 
			throw new JIPException("Errors reading the structurant element");

		return mm.erode((JIPImgBitmap)img);	
	}
}
