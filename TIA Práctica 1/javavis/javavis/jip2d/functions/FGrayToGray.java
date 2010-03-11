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
*Converts a grey image into another grey image. 
*Applicable to: BIT, BYTE, SHORT or REAL<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>gray: Type of the output image (BIT, BYTE, SHORT, REAL)<BR><BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>The same image with different grey type.<BR><BR>
*</ul>
*/

public class FGrayToGray extends JIPFunction {
	private static final long serialVersionUID = -8983716229889971105L;

	public FGrayToGray() {
		super();
        name="FGrayToGray";
        description="Converts a grey image into another type.";
        groupFunc = FunctionGroup.Transform;

        JIPParamList p1 = new JIPParamList("gray", false, true);
        String []paux = new String[4];
        paux[0]="BYTE";
        paux[1]="BIT";
        paux[2]="SHORT";
        paux[3]="FLOAT";
        p1.setDefault(paux);
        p1.setDescription("Type of gray for the result image");

		addParam(p1);
    }
    
    public JIPImage processImg(JIPImage img) throws JIPException {
		if(img.getType()==ImageType.COLOR || img instanceof JIPImgGeometric) 
			throw new JIPException("GrayToGray can not be applied to this image format");
		String p1=getParamValueString("gray");
		ImageType tipo = Enum.valueOf(ImageType.class,p1);
		JIPImgBitmap imgBmp = (JIPImgBitmap)img;
		int nbands=imgBmp.getNumBands();
		JIPImgBitmap res=(JIPImgBitmap)JIPImage.newImage(nbands,img.getWidth(),img.getHeight(),tipo);
		double []bmp, bmpRes;
		double maxInput=0, maxOutput=0;

		switch (img.getType()) {
			case BYTE: maxInput=255; break;
			case FLOAT:
			case BIT: maxInput=1; break;
			case SHORT: maxInput=65535; break;
		}
		switch (tipo) {
			case BYTE: maxOutput=255; break;
			case FLOAT:
			case BIT: maxOutput=1; break;
			case SHORT: maxOutput=65535; break;
		}
		for(int b=0;b<nbands;b++) {
			bmp=imgBmp.getAllPixels(b);
			bmpRes=new double[bmp.length];
			for (int i=0; i<bmp.length; i++) 
				bmpRes[i]=maxOutput*bmp[i]/maxInput;
			res.setAllPixels(b, bmpRes);
		}

		return res;
	}
}
