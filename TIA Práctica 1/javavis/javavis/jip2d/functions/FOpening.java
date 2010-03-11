package javavis.jip2d.functions;

import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFile;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;
import javavis.jip2d.util.MatMorph;

/**
*Implements the opening morphological operation to binary or grey scale images.
*It reads a file which contains the description of the structurant element (ee), 
*that is, it contains the dimensions of the mask (width and height),<br>
*and a matrix of 0s & 1s which defines the ee. 
*It is only applicable for bitmap images.<BR>  
*<ul><B>Input parameters:</B><BR>
*<li>img1: input image<BR>
*<li>ee: File with the description of the structurant element.<br><BR><BR> 
*</ul>

*<ul><B>Output parameters:</B><BR>
*<li>Opened image.<BR><BR>
*</ul>
*/
public class FOpening extends JIPFunction {
	private static final long serialVersionUID = 221852059049077418L;

	public FOpening() {
		super();
		name = "FOpening";
		description = "Apply the opening operation.";
		groupFunc = FunctionGroup.Math_morph;

		JIPParamFile p1 = new JIPParamFile("ee", true, true);
		p1.setDescription("Estructurant Element");

		addParam(p1);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("Opening can not be applied to this image type");

		MatMorph mm = new MatMorph (getParamValueString("ee"));
		if (!mm.isCorrect()) 
			throw new JIPException("Errors reading the structurant element");

		JIPImage auxImg = mm.erode((JIPImgBitmap)img);
		return mm.dilate((JIPImgBitmap)auxImg);	
	}
}
