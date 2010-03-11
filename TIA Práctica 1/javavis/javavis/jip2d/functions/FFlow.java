package javavis.jip2d.functions;

import java.util.ArrayList;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.JIPSequence;
import javavis.jip2d.base.bitmaps.JIPBmpByte;
import javavis.jip2d.base.geometrics.JIPGeomSegment;

/**
*Implements the optical flow algorithm which is defined by diferencial method of 
*B. K. P. Horn and B. G. Schunck, “Determining optical flow,” 
*AI, vol. 17, pp. 185-204, 1986<BR>
* It gets the spatial X & Y gradients and the temporal gradient (T) of two images of the
* input sequence. An iterative HORN & SCHUCK algorithm are used and finally the flows of
* the image are represented.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>seq: Sequence with two images.<BR>
*<li>iter: Number of iterates which the algorithm updates the U & V of Horn&Schunck. Defalut 12.0<BR>
*<li>lambda: Noise reduction factor which appears in the denominator of Horn & Schucnck equations. Default 1.0.<BR>
*<li>tamBloq: How many pixels, horizontal and vertical, are represented in the result image. Default .<BR>
*<li>factor: modifies the vector length of the flow.<BR>
*<li>elim: eliminates flow of length < elim*factor.<BR><BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Secuence with a image where the origin of flow is painted in GREEN and the remainder of it in RED.
*The points where the motion is very small o it is not are painted in BLUE. 
*<BR><BR>
*</ul>
*/

public class FFlow extends JIPFunction {
	private static final long serialVersionUID = -4783214630724479756L;

	public FFlow() {
		super();
		name = "Flows";
		description = "Calculates Ix, Iy & It gradients.";
		groupFunc = FunctionGroup.Others;

		JIPParamInt p1 = new JIPParamInt("iter", false, true);
		p1.setDescription("Number of iterations for the algorithm.");
		p1.setDefault(12);
		JIPParamFloat p2 = new JIPParamFloat("lambda", false, true);
		p2.setDescription("Noise reduction factor");
		p2.setDefault(2.0f);
		JIPParamInt p3 = new JIPParamInt("tamBloq", false, true);
		p3.setDescription("Separation between represented flow");
		p3.setDefault(5);
		JIPParamFloat p4 = new JIPParamFloat("factor", false, true);
		p4.setDescription("Allows to enlarge the represented flow");
		p4.setDefault(1.0f);
		JIPParamInt p5 = new JIPParamInt("elim", false, true);
		p5.setDescription("Eliminates flow of length < elim*factor");
		p5.setDefault(0);

		addParam(p1);
		addParam(p2);
		addParam(p3);
		addParam(p4);
		addParam(p5);
	}
	
	public JIPImage processImg(JIPImage img) throws JIPException {			
		throw new JIPException("Please, select Complete Sequence when applying this function");
	}

	public JIPSequence processSeq(JIPSequence secuencia) throws JIPException {
		if (secuencia.getFrame(0).getType() != ImageType.BYTE) 
			throw new JIPException("Flow can only be applied to byte format");

		int numFrames = secuencia.getNumFrames(); 
		if (numFrames < 2) 
			throw new JIPException("You need at least 2 frames");
		
		int numIter = getParamValueInt("iter");
		double lambda = getParamValueFloat("lambda");
		int w = secuencia.getFrame(0).getWidth();
		int h = secuencia.getFrame(0).getHeight();
		int numPixels = w * h;
		JIPBmpByte imagen2 = (JIPBmpByte)secuencia.getFrame(0);
		JIPBmpByte imagen1;

		for (int nf=0; nf<numFrames-1; nf++) {
			imagen1 = imagen2;
			imagen2 = (JIPBmpByte)secuencia.getFrame(nf+1);
			if (imagen2.getType() != ImageType.BYTE) 
				throw new JIPException("Flow can only be applied to byte format");
	
			double[] gradienteX = new double[numPixels];
			double[] gradienteY = new double[numPixels];
			double[] gradienteT = new double[numPixels];
			for (int i = 0; i < numPixels; i++) {
				gradienteX[i] = 0;
				gradienteY[i] = 0;
				gradienteT[i] = 0;
			}
	
			// X gradient
			for (int j = 0; j < h - 1; j++) 
				for (int i = 0; i < w - 1; i++) { 
					double m1 = imagen1.getPixel(0, i + 1, j) - imagen1.getPixel(0, i, j);
					double m2 = imagen1.getPixel(0, i + 1, j + 1) - imagen1.getPixel(0, i, j + 1);
					double m3 = imagen2.getPixel(0, i + 1, j) - imagen2.getPixel(0, i, j);
					double m4 = imagen2.getPixel(0, i + 1, j + 1) - imagen2.getPixel(0, i, j + 1);
					gradienteX[j * w + i] = ((m1 + m2 + m3 + m4) / 4);
				}
			// Y gradient
			for (int j = 0; j < h - 1; j++) 
				for (int i = 0; i < w - 1; i++) { 
					double m1 = imagen1.getPixel(0, i, j + 1) - imagen1.getPixel(0, i, j);
					double m2 = imagen1.getPixel(0, i + 1, j + 1) - imagen1.getPixel(0, i + 1, j);
					double m3 = imagen2.getPixel(0, i, j + 1) - imagen2.getPixel(0, i, j);
					double m4 = imagen2.getPixel(0, i + 1, j + 1) - imagen2.getPixel(0, i + 1, j);
					gradienteY[j * w + i] = (m1 + m2 + m3 + m4) / 4;
				}
			// T gradient
			for (int j = 0; j < h - 1; j++)
				for (int i = 0; i < w - 1; i++) { 
					double m1 = imagen2.getPixel(0, i, j) - imagen1.getPixel(0, i, j);
					double m2 = imagen2.getPixel(0, i, j + 1) - imagen1.getPixel(0, i, j + 1);
					double m3 = imagen2.getPixel(0, i + 1, j) - imagen1.getPixel(0, i + 1, j);
					double m4 = imagen2.getPixel(0, i + 1, j + 1) - imagen1.getPixel(0, i + 1, j + 1);
					gradienteT[j * w + i] = (m1 + m2 + m3 + m4) / 4;
				}
	
			double[] inicializacionU = new double[numPixels];
			double[] inicializacionV = new double[numPixels];
			for (int i = 0; i < numPixels; i++) {
				inicializacionU[i] = 0.0;
				inicializacionV[i] = 0.0;
			}
			double anguNuevo=0.0;
			for (int j = 1; j < h - 1; j++) 
				for (int i = 1; i < w - 1; i++) { 
					double Ix = gradienteX[j * w + i];
					double Iy = gradienteY[j * w + i];
					if (!(Ix == 0.0 && Iy == 0.0)) {
						double norma = Math.sqrt(Ix * Ix + Iy * Iy);
						double mod = -gradienteT[j * w + i] / norma;
						double angu = Math.atan(Iy / Ix);
						if (Ix >= 0.0 && Iy >= 0.0) { 
							anguNuevo = angu; 
						} else if (Ix < 0.0 && Iy > 0.0) {
							anguNuevo = Math.PI + angu; 
						} else if (Ix < 0.0 && Iy <= 0.0) { 
							anguNuevo = Math.PI + angu; 
						} else if (Ix >= 0.0 && Iy < 0.0) { 
							anguNuevo = 2 * Math.PI + angu; 
						}
						inicializacionU[j * w + i] = mod * Math.cos(anguNuevo);
						inicializacionV[j * w + i] = mod * Math.sin(anguNuevo);
					}
				}
			
			double[] Uactual;
			double[] Unuevo = new double[numPixels];
			double[] Vactual; 
			double[] Vnuevo = new double[numPixels];
			for (int i = 0; i < numPixels; i++) {
				Vnuevo[i] = 0.0;
				Unuevo[i] = 0.0;
			}
	
			Uactual = promediar(inicializacionU, w, h);
			Vactual = promediar(inicializacionV, w, h);
			for (int it = 0; it < numIter; it++) {
				for (int j = 1; j < h - 1; j++) { 
					for (int i = 1; i < w - 1; i++) {
						double numerador = gradienteX[j * w + i] * Uactual[j * w + i] + gradienteY[j * w + i] * 
								Vactual[j * w + i] + gradienteT[j * w + i];
						double denominador = lambda + gradienteX[j * w + i] * 
								gradienteX[j * w + i] + gradienteY[j * w + i] * 
								gradienteY[j * w + i];
						Unuevo[j * w + i] = Uactual[j * w + i]
								- (gradienteX[j * w + i] * numerador / denominador);
						Vnuevo[j * w + i] = Vactual[j * w + i]
								- (gradienteY[j * w + i] * numerador / denominador);
					}
				}
				Uactual = promediar(Unuevo, w, h);
				Vactual = promediar(Vnuevo, w, h);
			}
			JIPImage temp = pintaFlujos(w, h, Unuevo, Vnuevo, 
					getParamValueInt("tamBloq"), getParamValueFloat("factor"), 
					getParamValueInt("elim"));
			temp.setName("Final result");
			secuencia.addFrame(temp);
		}
		return secuencia;
	}


	/**
	*Function which does the average of a image with a 3x3 operator (mask), with values from left-
	*right and top-down: 1/12, 1/6, 1/12, 1/6, 0 1/6, 1/12, 1/6, 1/12.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>Gradient: An array with values of the image pixels which are concatenated by rows.<BR>
	*</ul>
	*<ul><B>Output paramters:</B><BR>
	*<li>Array with average pixels of the image.<BR><BR>
	*</ul>
	*/
	double[] promediar(double[] gradiente, int w, int h) {
		double[] promediado = new double[w * h];
		for (int i = 0; i < w * h; i++)
			promediado[i] = 0.0;

		for (int j = 1; j < h - 1; j++) 
			for (int i = 1; i < w - 1; i++) { 
				double media = (1.0 / 12.0) * (gradiente[(j - 1) * w + (i - 1)] 
						+ gradiente[(j - 1) * w + (i + 1)] 
						+ gradiente[(j + 1) * w + (i - 1)] 
						+ gradiente[(j + 1) * w + (i + 1)]);
				media += (1.0 / 6.0) * (gradiente[(j - 1) * w + (i)]
						+ gradiente[j * w + (i - 1)]
						+ gradiente[j * w + (i + 1)]
						+ gradiente[(j + 1) * w + i]);
				promediado[j * w + i] = media;
			}
		return promediado;
	}

	/**
	  *Function which draws the detected flow in the result image<BR>
	  *<ul><B>Input parameters:</B><BR>
	  *<li>Result: JIPImagen with the input image where the flows are painted.<BR>
	  *<li>U: Results of the HORN & SCHUNCK algorithm for a U component.<BR>
	  *<li>V: Results of the HORN & SCHUNCK algorithm for a V component.<BR>
	  *</ul>
	  *<ul><B>Output parameters:</B><BR>
	  *<li>Result image with the painted flows.<BR><BR>
	  *</ul>
	  */
	JIPImage pintaFlujos(int w, int h, double[] U, double[] V, int tamBloq, 
			float factor, int elim) throws JIPException {
		ArrayList<Integer> vec = new ArrayList<Integer>();
		for (int i = 1; i < h - 1; i += tamBloq)
			for (int j = 1; j < w - 1; j += tamBloq) {
				int i2 = Math.round((float) V[i * w + j] * factor);
				int j2 = Math.round((float) U[i * w + j] * factor);
				if (Math.sqrt(i2*i2 + j2*j2) > factor*elim) {
					vec.add(j);
					vec.add(i);
					vec.add(j + j2);
					vec.add(i + i2);
				}
			}
		JIPGeomSegment res = new JIPGeomSegment (w, h);
		res.setData(vec);
		return res;
	} 
}
