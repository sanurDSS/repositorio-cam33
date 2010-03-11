package javavis.jip2d.functions;

import java.util.ArrayList;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPGeomEdges;


/**
*Creates a set of edges which are returned as an Edge image.
*It is used with Canny output images. Only band 0 is processed.<BR>
*Applicable to: BYTE, SHORT, FLOAT<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>low: Lower threshold (In percentage)<BR>
*<li>high: Upper threshold (In percentage)<BR>
*<li>minlengh: Minimum size of the included points.<BR><BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Edge image for the image points.<BR><BR>
*</ul>
*/
public class FLink extends JIPFunction {
	private static final long serialVersionUID = 4366090933966628065L;

	public FLink() {
		super();
		name = "FLink";
		description = "Creates a set of edges from an image. Only band 0 is processed";
		groupFunc = FunctionGroup.Edges;
		
		JIPParamInt p1 = new JIPParamInt("low", false, true);
		p1.setDefault(30);
		p1.setDescription("Lower threshold (In percentage)");
		JIPParamInt p2 = new JIPParamInt("high", false, true);
		p2.setDefault(45);
		p2.setDescription("Upper threshold (In Percentage)");
		JIPParamInt p3 = new JIPParamInt("minlength", false, true);
		p3.setDefault(2);
		p3.setDescription("Minimum size of the included points");

		addParam(p1);
		addParam(p2);
		addParam(p3);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img.getType() == ImageType.BYTE || img.getType() == ImageType.SHORT || img.getType() == ImageType.FLOAT) {
			ArrayList<ArrayList<Integer>> edges = new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> pointData = new ArrayList<Integer>();
			ImageType t = img.getType();
			int w = img.getWidth();
			int h = img.getHeight();
			int lowUmbral = getParamValueInt("low");
			int highUmbral = getParamValueInt("high");
			int minLong = getParamValueInt("minlength");

			switch (t) {
				case SHORT :
					lowUmbral *= 65535 / 100;
					highUmbral *= 65535 / 100;
					break;
				case FLOAT :
					JIPFunction gray = new FGrayToGray();
					gray.setParamValue("gray", "BYTE");
					img = gray.processImg(img);
			}
			double []bmp = ((JIPImgBitmap)img).getAllPixels(0);
			for (int r = 0; r < h; r++)
				for (int c = 0; c < w; c++)
					if (bmp[c + r * w] >= highUmbral) 
						trackEdge(r, c, w, h, lowUmbral, minLong,  pointData, edges, bmp);
			JIPGeomEdges res = new JIPGeomEdges(w,h);
			res.setData(edges);
			return res;
		}
		else 
			throw new JIPException("FLink can not be applied to this image format");
	}

	/**
	*It makes a trace in a Edge from the position which is passed as argument.
	*Each point which is added into Edge, it is set a zero into original image.
	*After it, the array is turned round and it continues in the new direction.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>row: Start row<BR>
	*<li>col: Start column<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>It introduces the Edge in the edges list (Variable of the class)<BR><BR>
	*</ul>
	*/
	public void trackEdge(int row, int col, int w, int h, int LowUmbral, 
			int MinLong, ArrayList<Integer> pointData, ArrayList<ArrayList<Integer>> edges, 
			double[]bmp) {
		int []p = new int[2];
		int []paux = new int[2];
		p[0] = row;
		p[1] = col;
		paux[0] = row;
		paux[1] = col;
		ArrayList<Integer> vecAux;
		
		storePoint(p, w, pointData, bmp);

		while (nextPoint(w, h, LowUmbral, p, bmp))
			storePoint(p, w, pointData, bmp);
		if (nextPoint(w, h, LowUmbral, paux, bmp)) {
			vecAux = new ArrayList<Integer>();
			int tam=pointData.size();
			for (int i=0; i<tam; i+=2) {
				vecAux.add(pointData.get(tam-(i+2)));
				vecAux.add(pointData.get(tam-(i+1)));
			}
			storePoint(paux, w, vecAux, bmp);
			while (nextPoint(w, h, LowUmbral, paux, bmp))
				storePoint(paux, w, vecAux, bmp);
		}
		else 
			vecAux=new ArrayList<Integer>(pointData);
		if (vecAux.size() / 2 >= MinLong)
			edges.add(vecAux);
		pointData.clear();
	}

	/**
	*Sets the point of Edge in the class variable which 
	*has the temporal list of edges.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>r: Row to load<BR>
	*<li>c: Column to load<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>Sets the point in the temporal list of edges.<BR><BR>
	*</ul>
	*/
	public void storePoint(int []p, int w, ArrayList<Integer> pointData, double[]bmp) {
		pointData.add(p[1]);
		pointData.add(p[0]);
		bmp[p[1] + p[0] * w] = 0;
	}

	/**
	 *Calculates the next point to continue with the trace.<BR>
	 *<ul><B>Input parameters:</B><BR>
	 *<li>rp: Start row<BR>
	 *<li>cp: Start column<BR><BR>
	 *</ul>
	 *<ul><B>Output parameters:</B><BR>
	 *<li>Sets the point in the temporal list ot edges<BR><BR>
	 *</ul>
	 *Additional notes: rp and cp are a class variables, they are used
	 *from TraceEdge to pass some reference values.
	 */
	public boolean nextPoint(int w, int h, int LowUmbral, int []p, double[]bmp) {
		int i, r, c;
		int[] roff = new int[8];
		int[] coff = new int[8];
		roff[0] = 1;
		roff[1] = 0;
		roff[2] = -1;
		roff[3] = 0;
		roff[4] = 1;
		roff[5] = 1;
		roff[6] = -1;
		roff[7] = -1;
		coff[0] = 0;
		coff[1] = 1;
		coff[2] = 0;
		coff[3] = -1;
		coff[4] = 1;
		coff[5] = -1;
		coff[6] = -1;
		coff[7] = 1;

		for (i = 0; i < 8; i++) {
			r = p[0] + roff[i];
			c = p[1] + coff[i];
			if (r >= 0 && c >= 0 && r < h && c < w)
				if (bmp[c + r * w] >= LowUmbral) {
					p[0] += roff[i];
					p[1] += coff[i];
					return true;
				}
		}
		return false;
	} 
}
