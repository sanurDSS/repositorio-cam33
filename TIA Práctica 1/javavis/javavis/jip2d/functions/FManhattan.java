package javavis.jip2d.functions;

import java.util.ArrayList;
import java.util.Arrays;

import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.geometrics.JIPImgGeometric;


/**
*<B>FManhattan:</B> Finds the camera angle efficiently, to make it, it uses bayesian information.
*J. Coughlan and A.L. Yuille. "Manhattan World: Orientation and Outlier Detection by Bayesian Inference". 
*Neural Computation. Vol. 15, No. 5, pp. 1063-88. May 2003<br><br>
*First, we obtain segments from image: FCanny plus Flink plus FSegEdges.
*Afterwards, some edges are removed in order to eliminate short ones. 
*The main algorithm scans every possible orientations. For each orientation,
*it calculates for each segment its probability associated to an orientation. 
*It accumulates the probability of each segment.
*Finally, it returns the orientation with a maximum number of votes.<BR>

*<ul><B>Input parameters:</B><BR>
*<li>img: Input image of the camera to be processed
*<li>focal: Focal length of the camera.<br>
*</ul>

*<ul><B>Output parameters:</B><BR>
*<li>result: Image with found edges of tREAL type.
*<li>angle: Angle obtained.<br><BR>
*</ul>
*/
public class FManhattan extends JIPFunction {
	private static final long serialVersionUID = -7945018801531035140L;

	public FManhattan() {
		super();
		name = "FManhattan";
		description = "Calculates the camera orientation angle.";
		groupFunc = FunctionGroup.Others;
		
		JIPParamInt p1 = new JIPParamInt("focal", false, true);
		p1.setDescription("Focal length");
		p1.setDefault(500);
		addParam(p1);
		
		JIPParamFloat r1 = new JIPParamFloat("angle", false, false);
		addParam(r1);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img.getType() != ImageType.COLOR) 
			throw new JIPException("Manhattan can not be applied to this image format");

		FCanny c = new FCanny();
		c.setParamValue("brightness", 300);
		JIPImage aux = c.processImg(img);
		FLink l = new FLink();
		JIPImage aux2 = l.processImg(aux);
		FSegEdges e = new FSegEdges();
		JIPImgGeometric res = (JIPImgGeometric) e.processImg(aux2);

		ArrayList<Integer> segmentos = (ArrayList<Integer>)res.getData();
		int w = img.getWidth(), h = img.getHeight();
		
		double[] votos = new double[91];
		Arrays.fill(votos, 0.0);

		double f = getParamValueInt("focal");
		double ang_tol = 4.0 * Math.PI / 180.0;
		double inbox = 0.9 / (4 * ang_tol);
		double outbox = 0.1 / (2 * Math.PI - 4 * ang_tol);
		double p_i = 0.2, p_j = 0.2, p_k = 0.2, p_outlier = 0.4;
		double sum = p_i + p_j + p_k + p_outlier;
		p_i /= sum;
		p_j /= sum;
		p_k /= sum;
		p_outlier /= sum;
		double or_k = 0.0;
		double voto_fuera = 1 / (2.0 * Math.PI) * p_outlier;

		for (int angulo = -45; angulo <= 45; angulo++) {
			double phibesttan = Math.tan(Math.toRadians(angulo));
			for (int i = 0; i < segmentos.size(); i += 4) {
				int x1 = segmentos.get(i);
				int y1 = segmentos.get(i + 1);
				int x2 = segmentos.get(i + 2);
				int y2 = segmentos.get(i + 3);
				int v1 = x2 - x1;
				int v2 = y1 - y2;

				double m_x = (x1 + x2) / 2.0;
				double m_y = (y1 + y2) / 2.0;
				double longitud = Math.sqrt(v1 * v1 + v2 * v2);
				double orientacion = Math.atan2(v2, v1);
				orientacion += Math.PI / 2.0;
				int u = (int) m_x - (w / 2);
				int v = (h / 2) - (int) m_y;
				double or_i = Math.atan2((-f * phibesttan) - u, v);
				double or_j = Math.atan2((f / phibesttan) - u, v);

				double voto_i = (consistent(or_i, orientacion, ang_tol) ? inbox : outbox) * p_i;
				double voto_j = (consistent(or_j, orientacion, ang_tol) ? inbox : outbox) * p_j;
				double voto_k = (consistent(or_k, orientacion, ang_tol) ? inbox : outbox) * p_k;

				votos[angulo + 45] += Math.log(mayor_de4(voto_i, voto_j, voto_k, voto_fuera))
						* longitud;
			}
		}

		int pos = -1;
		double auxD = -64000.0;
		for (int i = 0; i < 91; i++) 
			if (votos[i] > auxD) {
				auxD = votos[i];
				pos = i;
			}
			
		setParamValue("angle", (float)(pos - 45));
		info = "Angle obtained "+(pos-45);
		return res;
	}

	double modulo(double x, double m) {
		return (x - m * Math.floor(x / m));
	}

	boolean consistent(double x, double y, double del_ang) {
		return ((modulo(x - y, Math.PI) < del_ang)
				|| (Math.PI - modulo(x - y, Math.PI) < del_ang));
	}

	double mayor_de4(double x1, double x2, double x3, double x4) {
		if (x1 >= x2 && x1 >= x3 && x1 >= x4)
			return x1;
		if (x2 >= x1 && x2 >= x3 && x2 >= x4)
			return x2;
		if (x3 >= x1 && x3 >= x2 && x3 >= x4)
			return x3;
		if (x4 >= x1 && x4 >= x2 && x4 >= x3)
			return x4;
		if (x1 == x2 && x1 == x3 && x1 == x4)
			return 0;
		return x1;
	}
}
