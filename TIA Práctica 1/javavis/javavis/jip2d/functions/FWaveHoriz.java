package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*Applies a wave effect to the input image.<BR>
*Applicable to Bitmap images<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>numWaves: Number of waves<BR>
*<li>perc: Distortion Percentage<BR>
*<li>desp: Wave desplacement<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Processed image with the same type as input image but with the applied wave distortion.<BR><BR>
*</ul>
*/
public class FWaveHoriz extends JIPFunction {
	private static final long serialVersionUID = 4636009705663652904L;

	public FWaveHoriz() {
		super();
		name = "FWaveHoriz";
		description = "Applies a wave effect to the input image (horizontal distortion)";
		groupFunc = FunctionGroup.Manipulation;

		JIPParamInt p1 = new JIPParamInt("numWaves", false, true);
		p1.setDefault(5);
		p1.setDescription("Number of waves");
		JIPParamInt p2 = new JIPParamInt("perc", false, true);
		p2.setDefault(5);
		p2.setDescription("Distortion percentage");
		JIPParamInt p3 = new JIPParamInt("desp", false, true);
		p3.setDefault(0);
		p3.setDescription("Scrolling");

		addParam(p1);
		addParam(p2);
		addParam(p3);
	}
	
	public JIPImage processImg(JIPImage img) throws JIPException {
		ImageType t = img.getType();
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("WaveHoriz can not be applied to this image format");
		JIPImgBitmap imgBmp = (JIPImgBitmap)img;
		int nOndas = getParamValueInt("numWaves");
		int radio1 = getParamValueInt("perc");
		int desplazamiento = getParamValueInt ("desp");
		int w = img.getWidth();
		int h = img.getHeight();
		int b = imgBmp.getNumBands();
		double frecuencia = (nOndas * Math.PI * 2.0) / h;
		double desp = (desplazamiento * nOndas * Math.PI * 2.0) / 100.0;
		double radio = (w * radio1) / 100.0;
		JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(b, w, h, t);

		for (int nb = 0; nb < b; nb++) {
			int cont = 0;
			double[] bmp = imgBmp.getAllPixels(nb);
			double[] bin = new double[w * h];
			for (int y = 0; y < h; y++) {
				int xDesp = (int) (Math.sin(y * frecuencia + desp) * radio);
				for (int x = 0; x < w; x++) {
					if ((xDesp >= 0) && (xDesp < w))
						bin[cont++] = bmp[xDesp + y * w];
					else
						bin[cont++] = 0;
					xDesp++;
				}
			}
			res.setAllPixels(nb, bin);
		}
		return res;
	}
}
