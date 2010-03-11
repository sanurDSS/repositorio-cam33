package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*Applies a smoothing to the image. Two different algorithms can be applied.<BR>
*Applicable to: BYTE, SHORT, COLOR and FLOAT<BR>
*Uses: FConvolveImage.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>radius: Radius of the window<BR>
*<li>alg: algorithm to smooth: Median, Average<BR>
*<li>method: Method to deal with the borders. ZERO->border pixels are marked as 0.
*PAD->The first row is duplicated so that the -1 row is the same. The same for last row and first and last columns. 
*WRAP->The -1 row is the last row and the n+1 row is the first. The same for columns.<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Processed image with the same type than input image.<BR><BR>
*</ul>
*/
public class FSmooth extends JIPFunction {
	private static final long serialVersionUID = 7850747295611493227L;

	public FSmooth() {
		super();
		name = "FSmoothAverage";
		description = "Image smoothing";
		groupFunc = FunctionGroup.Adjustment;

		JIPParamInt p1 = new JIPParamInt("radius", false, true);
		p1.setDefault(2);
		p1.setDescription("Radius");
		JIPParamList p2 = new JIPParamList("alg", false, true);
		String []paux = new String[2];
		paux[0]="Average";
		paux[1]="Median";
		p2.setDefault(paux);
		p2.setDescription("Method to smooth");
		JIPParamList p3 = new JIPParamList("method", false, true);
		paux = new String[3];
		paux[0]="ZERO";
		paux[1]="WRAP";
		paux[2]="PAD";
		p3.setDefault(paux);
		p3.setDescription("Method to process the border");

		addParam(p1);
		addParam(p2);
		addParam(p3);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {	
		if (img instanceof JIPImgGeometric || img.getType()==ImageType.BIT) 
			throw new JIPException("Smooth can not be applied to this image format");

		int radio =  getParamValueInt("radius");
		String p2 = getParamValueString("method");
		String alg = getParamValueString("alg");

		if (alg.equals("Average")) {
			JIPFunction convolucion = new FConvolveImage();
			int tam = radio * 2 + 1;
			double mat[] = new double[tam * tam];
			for (int a = 0; a < tam * tam; a++)
				mat[a] = 1.0f;
			JIPImgBitmap convo = (JIPImgBitmap)JIPImage.newImage(radio * 2 + 1, radio * 2 + 1, ImageType.FLOAT);
			convo.setAllPixels(mat);
			convolucion.setParamValue("image", convo);
			convolucion.setParamValue("div", (float) ((radio * 2 + 1) * (radio * 2 + 1)));
			convolucion.setParamValue("method", p2);
			
			return convolucion.processImg(img);
		}
		else if (alg.equals("Median")) {
			JIPImgBitmap imgBmp = (JIPImgBitmap)img;
			int b = imgBmp.getNumBands(), w = imgBmp.getWidth(), h = imgBmp.getHeight();
			JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(b, w, h, imgBmp.getType());
			double[] bmpf, binf, vectorf;
			
			for (int nb = 0; nb < b; nb++) {
				bmpf = imgBmp.getAllPixels(nb);
				binf = new double[w * h];
				vectorf = new double[(radio + radio + 1) * (radio + radio + 1)];
				for (int i = 0; i < h; i++)
					for (int j = 0; j < w; j++) {
						int inicioX = j - radio, finX = j + radio;
						int inicioY = i - radio, finY = i + radio;
						if (inicioX < 0)
							inicioX = 0;
						if (finX >= w)
							finX = w - 1;
						if (inicioY < 0)
							inicioY = 0;
						if (finY >= h)
							finY = h - 1;
						int cont = 0;
						for (int y = inicioY; y <= finY; y++)
							for (int x = inicioX; x <= finX; cont++, x++)
								vectorf[cont] = bmpf[x + y * w];
						binf[j + i * w] = median(vectorf, cont - 1);
					}
				res.setAllPixels(nb, binf);
			}
			return res;
		}
		else throw new JIPException("Smooth: algorithm to apply not recognized");
	}

	/**
	*Calculates the median in the elements of a type vector. We have reimplemented the
	* Arrays.sort because for short vectors it is more efficient.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>vector: Vector which has the elements<BR>
	*<li>longitud: Length to use<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>Returns the median of the input elements.<BR><BR>
	*</ul>
	*/
	public double median(double vector[], int longitud) {
		int k = longitud / 2;
		int izq = 0;
		int der = longitud;
		double x = 0, aux;
		int i, j;
		while (izq < der) {
			x = vector[k];
			i = izq;
			j = der;
			do {
				while (vector[i] < x)
					i++;
				while (x < vector[j])
					j--;
				if (i <= j) {
					aux = vector[i];
					vector[i] = vector[j];
					vector[j] = aux;
					i++;
					j--;
				}
			}
			while (i <= j);
			if (j < k)
				izq = i;
			if (k < i)
				der = j;
		}
		return (x);
	}
}
