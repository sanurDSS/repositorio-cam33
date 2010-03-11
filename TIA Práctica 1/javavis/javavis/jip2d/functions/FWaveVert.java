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
*Applicable to: BIT, BYTE, WORD, COLOR and REAL<BR>
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
public class FWaveVert extends JIPFunction {
	private static final long serialVersionUID = -705618644823140479L;

	public FWaveVert() {
		super();
		name = "FWaveVert";
		description = "Applies a wave effect to the input image (vertical distortion)";
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
		if (img instanceof JIPImgGeometric) {
			throw new JIPException("WaveVert can not be applied to this image format");
		}	
		ImageType t = img.getType();
		JIPImgBitmap imgBmp = (JIPImgBitmap)img;
		int nOndas = getParamValueInt("numWaves");
		int radio1 = getParamValueInt("perc");
		int desplazamiento = getParamValueInt("desp");
		int w = img.getWidth();
		int h = img.getHeight();
		int b = imgBmp.getNumBands();
		double frecuencia = (nOndas * Math.PI * 2.0) / h;
		double desp = (desplazamiento * nOndas * Math.PI * 2.0) / 100.0;
		double radio = (w * radio1) / 100.0;
		JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(b, w, h, t);

		for (int nb = 0; nb < b; nb++) {
			double[] bmp = imgBmp.getAllPixels(nb);
			double[] bin = new double[w * h];
			for (int x = 0; x < w; x++) {
				int yDesp = (int) (Math.sin(x * frecuencia + desp) * radio);
				for (int y = 0; y < h; y++) {
					if ((yDesp >= 0) && (yDesp < h))
						bin[x + y * w] = bmp[x + yDesp * w];
					else
						bin[x + y * w] = 0;
					yDesp++;
				}
			}
			res.setAllPixels(nb, bin);
		}
		return res;
	}
}
