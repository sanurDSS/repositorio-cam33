package javavis.jip2d.functions;

import java.util.ArrayList;

import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamObject;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpBit;

/**
 * Calculates a circular histogram of a binary image. Used in the RecogLT function
 **/
public class FHistoCirc extends JIPFunction {
	private static final long serialVersionUID = -7262973524107183332L;

	public FHistoCirc() {
		super();
		name = "FHistoCirc";
		description = "Gets a circular histogram from a binay image";
		groupFunc = FunctionGroup.Applic;
		
		JIPParamObject p1 = new JIPParamObject("histo", false, false);
		addParam(p1);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img.getType()!=ImageType.BIT) 
			throw new JIPException("FHistoCirc only defined for BIT type images");
		
		JIPBmpBit imgBmp = (JIPBmpBit)img;
		ArrayList<int[]> histoList = new ArrayList<int[]>();
		int w = img.getWidth();
		int h = img.getHeight();
		int maxDist = (int)Math.sqrt(Math.pow(w/2, 2.0) + Math.pow(h/2, 2.0)) + 1;
		int[] histo;
		int dist;
		// For each band in the image
		for (int b=0; b<imgBmp.getNumBands(); b++) {
			histo = new int[maxDist];
			for (int r=0; r<h; r++) 
				for (int c=0; c<w; c++) 
					if (imgBmp.getPixelBool(b,c,r)) {
						dist = (int)Math.sqrt(Math.pow(r-h/2, 2.0) + Math.pow(c-w/2, 2.0));
						histo[dist]++;
					}
			for (int i=1; i<histo.length; i++)
				histo[i] += histo[i-1];
			histoList.add(histo);
		}
		
		setParamValue("histo", histoList);

		return img;
	}
}
