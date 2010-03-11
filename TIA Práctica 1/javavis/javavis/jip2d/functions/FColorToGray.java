package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpColor;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;

/**
*Converts a COLOR image, to a BIT, BYTE, SHORT or FLOAT image.<BR>
*It is only applicable for COLOR type.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>gray: Type of the destination image. (BIT, BYTE, SHORT, FLOAT)<BR><BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Image equivalent to the COLOR type but in grey scale.<BR><BR>
*</ul>
*/
public class FColorToGray extends JIPFunction {
	private static final long serialVersionUID = -3289373731488957402L;

	public FColorToGray() {
		super();
		name = "FColorToGray";
		description = "Converts a COLOR image into grey scale.";
		groupFunc = FunctionGroup.Transform;

		JIPParamList p1 = new JIPParamList("gray", false, true);
		String []paux = new String[4];
		paux[0]="BYTE";
		paux[1]="BIT";
		paux[2]="SHORT";
		paux[3]="FLOAT";
		p1.setDefault(paux);
		p1.setDescription("Type of result image");

		addParam(p1);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		String p1 = getParamValueString("gray");
		ImageType tipo = ImageType.BYTE;
		if (p1.equals("BIT"))
			tipo = ImageType.BIT;
		else if (p1.equals("BYTE"))
			tipo = ImageType.BYTE;
		else if (p1.equals("SHORT"))
			tipo = ImageType.SHORT;
		else if (p1.equals("FLOAT"))
			tipo = ImageType.FLOAT;

		JIPImgBitmap res = null;
		if (img.getType() == ImageType.COLOR) {
			int totalPix = img.getWidth()*img.getHeight();
			JIPBmpColor imgCol = (JIPBmpColor)img;
			byte[] red = imgCol.getAllPixelsByteRed();
			byte[] green = imgCol.getAllPixelsByteGreen();
			byte[] blue = imgCol.getAllPixelsByteBlue();
			double[] gray = new double[totalPix];
			double max=0;

			switch (tipo) {
				case BYTE: max=255; break;
				case FLOAT:
				case BIT: max=1; break;
				case SHORT: max=65535; break;
			}
			for (int i = 0; i < totalPix; i++)
			  gray[i] = max*Math.round(0.299 * (red[i]&0xFF) + 0.587 * (green[i]&0xFF) + 0.114 * 
					  (blue[i]&0xFF))/255.0;

			res = (JIPImgBitmap)JIPImage.newImage(img.getWidth(), img.getHeight(), tipo);
			res.setAllPixels(gray);
		}
		else
			throw new JIPException("Function ColorToGray only applied to COLOR images");
		
		return res;
	}
}
