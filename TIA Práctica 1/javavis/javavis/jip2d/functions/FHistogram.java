package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;
import javavis.jip2d.util.HistogramWindow;

import javax.swing.JFrame;

/**
*Calculates the histogram of an input image.
*It does not manage geometric frames, and it do not modify the input numFrame.<BR><BR>
*Applicable to: BYTE, SHORT, COLOR and FLOAT<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR><BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Same input image.<BR><BR>
*</ul>
*Additional notes: This function just create javavis.jip2d.base.HistogramWindow 
*and there is where the histogram is calculated.<BR>
*/
public class FHistogram extends JIPFunction {
	private static final long serialVersionUID = 4868691655276493208L;

	public FHistogram() {
		super();
		name = "FHistogram";
		description = "Show the histogram of an image.";
		groupFunc = FunctionGroup.Others;
	}

	public JIPImage processImg(JIPImage img) throws JIPException {        
		if (!(img instanceof JIPImgGeometric) && img.getType() != ImageType.BIT) {
			JFrame frame = new HistogramWindow((JIPImgBitmap)img);
			frame.setSize(290, 405);
			frame.setVisible(true);
		}
		else throw new JIPException("Histogram can not be applied to this image format");
		return img;
	}
}
