package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamImage;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.bitmaps.JIPBmpColor;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*Makes a convolutions of an image with another image specified by parameter.<BR>
*It is applicable for: BYTE, WORD, COLOR and REAL types.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>image: Image to use in convolution<BR>
*<li>mult: Multiplier<BR>
*<li>div: Divisor <BR>
*<li>method: Method to treat the borders. ZERO->border pixels are marked as 0.
*PAD->The first row is duplicated so that the -1 row is the same. The same for last row and first and last columns. 
*WRAP->The -1 row is the last row and the n+1 row is the first. The same for columns.<BR><BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Convolutioned image with the input image, with type the same as the input image.<BR><BR>
*</ul>
*/

public class FConvolveImage extends JIPFunction {
	private static final long serialVersionUID = -6863560219268735784L;

	public FConvolveImage() {
		super();
		name = "FConvolveImage";
		description = "Convolution of two images";
		groupFunc = FunctionGroup.Convolution;

		JIPParamImage p1 = new JIPParamImage("image", true, true);
		p1.setDescription("Image for convolution");
		JIPParamFloat p2 = new JIPParamFloat("mult", false, true);
		p2.setDefault(1.0f);
		p2.setDescription("Multiplier");
		JIPParamFloat p3 = new JIPParamFloat("div", false, true);
		p3.setDefault(1.0f);
		p3.setDescription("Divisor");
		JIPParamList p4 = new JIPParamList("method", false, true);
		String []paux = new String[3];
		paux[0]="ZERO";
		paux[1]="WRAP";
		paux[2]="PAD";
		p4.setDefault(paux);
		p4.setDescription("Method to process edges");

		addParam(p1);
		addParam(p2);
		addParam(p3);
		addParam(p4);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgGeometric || img instanceof JIPBmpBit) 
			throw new JIPException("ConvolveImage can not be applied to this image type");
		JIPImage convo = getParamValueImg("image");
		if (convo instanceof JIPImgGeometric) 
			throw new JIPException("ConvolveImage can not be applied with this image type");
		float p2 = getParamValueFloat("mult");
		float p3 = getParamValueFloat("div");
		String p4 = getParamValueString("method");
		JIPImgBitmap imgBmp = (JIPImgBitmap)img;
		double []con;

		ImageType t = imgBmp.getType();
		int w = imgBmp.getWidth();
		int h = imgBmp.getHeight();
		int b = imgBmp.getNumBands();
		int cw = convo.getWidth();
		int ch = convo.getHeight();
		int radiox, radioy, inix, iniy, finx, finy;
		double sum = 0.0f;

		radiox = cw / 2;
		radioy = ch / 2;

		int x, y, cx, cy, cx2 = 0, cy2 = 0, imparX = 0, imparY = 0;
		if (radiox * 2 != cw)
			imparX = 1;
		if (radioy * 2 != ch)
			imparY = 1;
		
		if (convo instanceof JIPBmpColor) {
			JIPFunction ctg = new FColorToGray();
			JIPImage auxImg = ctg.processImg(convo);
			con = ((JIPImgBitmap)auxImg).getAllPixels();
		}
		else
			con = ((JIPImgBitmap)convo).getAllPixels();

		if (p4.equals("ZERO")) {
			inix = radiox;
			iniy = radioy;
			finx = w - radiox;
			finy = h - radioy;
		} else {
			inix = iniy = 0;
			finx = w;
			finy = h;
		}
		double[] bmp, bin = new double[w * h];
		JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(b, w, h, t);
		for (int nb = 0; nb < b; nb++) {
			bmp = ((JIPImgBitmap)img).getAllPixels(nb);

			for (x = inix; x < finx; x++)
				for (y = iniy; y < finy; y++) {
					sum = 0.0f;

					for (cx = x - radiox; cx < x + radiox + imparX; cx++)
						for (cy = y - radioy; cy < y + radioy + imparY; cy++) {
							if (cx < 0 || cy < 0 || cx >= w || cy >= h) {
								if (p4.equals("WRAP")) {
									if (cx < 0)
										cx2 = w + cx;
									if (cx >= w)
										cx2 = cx - w;
									if (cy < 0)
										cy2 = h + cy;
									if (cy >= h)
										cy2 = cy - h;
									sum += bmp[cx2 + cy2 * w] * con[(radiox + (cx - x))
										+ (radioy + (cy - y)) * cw];
								}
								if (p4.equals("PAD")) {
									if (cx < 0)
										cx2 = 0;
									if (cx >= w)
										cx2 = w - 1;
									if (cy < 0)
										cy2 = 0;
									if (cy >= h)
										cy2 = h - 1;
									sum += bmp[cx2 + cy2 * w] * con[(radiox + (cx - x))
										+ (radioy + (cy - y)) * cw];
								}
							} else								
								sum += bmp[cx + cy * w] * con[(radiox + (cx - x))
									+ (radioy + (cy - y)) * cw];
						}
					sum = (sum * p2) / p3;
					bin[x + y * w] = sum;
				}
			res.setAllPixels(nb, bin);
		}
		return res;
	}
}
