package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamBool;
import javavis.base.parameter.JIPParamFloat;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPGeomPoint;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*<B>FNistzberg:</B> Implements the Nitzberg method for corner and edge detector.
*(M. Nitzberg, D. Mumford, y T. Shiota. "Filtering, Segmentation and Depth". Springer-Verlag, 1993)<BR>
*Implements the Nitzberg method for corner and edge detector. <br>
*When a pixel value is greater than the threshold, it is considered as a corner.
*The output can be a point image with the corners (corners=true) or a bitmap with
*the edges detected in the image.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>thres: Value of the threshold.<br>
*<li>corners: boolean value which indicates if we detect edge or corner points.<br><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Point image (corners) or bitmap with the edges<BR><BR>
*</ul>
*/
public class FNitzberg extends JIPFunction {
	private static final long serialVersionUID = -5402145479686371334L;

	public FNitzberg() {
		super();
		name = "FNitzberg";
		description = "Applies the Nitzberg detector";
		groupFunc = FunctionGroup.Edges;

		JIPParamFloat p1 = new JIPParamFloat("thres", false, true);
		p1.setDescription("Threshold");
		p1.setDefault(2.0f);
		JIPParamBool p2 = new JIPParamBool("corners", false, true);
		p2.setDescription("Corners or edge output");
		p2.setDefault(true);

		addParam(p1);
		addParam(p2);
	}

	public JIPImage processImg(JIPImage src) throws JIPException {
		if (src instanceof JIPImgGeometric)
			throw new JIPException("Nitzberg can not be applied to this image format");
		JIPImgBitmap tmp, smooth, src_f=null;
		float thres = getParamValueFloat("thres") * 0.0001f;
		int n_cols = src.getWidth();
		int n_rows = src.getHeight();

		JIPImgBitmap fgx = (JIPImgBitmap)JIPImage.newImage(9, 1, ImageType.FLOAT);
		JIPImgBitmap fgy = (JIPImgBitmap)JIPImage.newImage(1, 9, ImageType.FLOAT);
		JIPImgBitmap ix2 = (JIPImgBitmap)JIPImage.newImage(n_cols, n_rows, ImageType.FLOAT);
		JIPImgBitmap ixy = (JIPImgBitmap)JIPImage.newImage(n_cols, n_rows, ImageType.FLOAT);
		JIPImgBitmap iy2 = (JIPImgBitmap)JIPImage.newImage(n_cols, n_rows, ImageType.FLOAT);
		JIPImgBitmap edgy = (JIPImgBitmap)JIPImage.newImage(n_cols, n_rows, ImageType.FLOAT);
		JIPImgBitmap corn = (JIPImgBitmap)JIPImage.newImage(n_cols, n_rows, ImageType.FLOAT);
		JIPGeomPoint dest_edges = (JIPGeomPoint)JIPImage.newImage(n_cols, n_rows, ImageType.POINT);

		// 1-d gaussian kernels
		fgy.setPixel(0, 0, 0.0003677f);
		fgx.setPixel(0, 0, 0.0003677f);
		fgy.setPixel(0, 1, 0.0077312f);
		fgx.setPixel(1, 0, 0.0077312f);
		fgy.setPixel(0, 2, 0.066709f);
		fgx.setPixel(2, 0, 0.066709f);
		fgy.setPixel(0, 3, 0.2408f);
		fgx.setPixel(3, 0, 0.2408f);
		fgy.setPixel(0, 4, 0.36876f);
		fgx.setPixel(4, 0, 0.36876f);
		fgy.setPixel(0, 5, 0.2408f);
		fgx.setPixel(5, 0, 0.2408f);
		fgy.setPixel(0, 6, 0.066709f);
		fgx.setPixel(6, 0, 0.066709f);
		fgy.setPixel(0, 7, 0.0077312f);
		fgx.setPixel(7, 0, 0.0077312f);
		fgy.setPixel(0, 8, 0.0003677f);
		fgx.setPixel(8, 0, 0.0003677f);

		// Convert color to grayscale and from [0:255] to [0:1]
		if (src.getType()==ImageType.COLOR) {
			FColorToGray colorToGray = new FColorToGray();
			colorToGray.setParamValue("gray", "FLOAT");
			src_f = (JIPImgBitmap)colorToGray.processImg(src);
		}
		else {
			if (src.getType()==ImageType.BYTE || src.getType()==ImageType.BIT 
					|| src.getType()==ImageType.SHORT) {
				FGrayToGray grayToGray = new FGrayToGray();
				grayToGray.setParamValue("gray", "FLOAT");
				src_f = (JIPImgBitmap)grayToGray.processImg(src);
			}
			else if (src.getType()==ImageType.FLOAT) src_f = (JIPImgBitmap)src; 
				 else 
					throw new JIPException("Nitzberg can not be applied to this image format");
		}

		// Smooth
		FConvolveImage convolve = new FConvolveImage();
		convolve.setParamValue("image", fgx);
		tmp = (JIPImgBitmap)convolve.processImg(src_f);
		convolve.setParamValue("image", fgy);
		smooth = (JIPImgBitmap)convolve.processImg(tmp);

		// Eliminate the first and last columns and rows to avoid corner near this lines
		double ix, iy;
		int xl, xr, yl, yr;
		for (int col = 5; col < n_cols - 5; col++) {
			for (int row = 5; row < n_rows - 5; row++) {
				xl = col - 1;
				xr = col + 1;
				yl = row - 1;
				yr = row + 1;
				if (xl < 0)
					xl = 0;
				if (xr > n_cols - 1)
					xr = n_cols - 1;
				if (yl < 0)
					yl = 0;
				if (yr > n_rows - 1)
					yr = n_rows - 1;
				ix = (smooth.getPixel(xr, row) - smooth.getPixel(xl, row)) / 2.0;
				iy = (smooth.getPixel(col, yr) - smooth.getPixel(col, yl)) / 2.0;
				ix2.setPixel(col, row, ix * ix);
				ixy.setPixel(col, row, ix * iy);
				iy2.setPixel(col, row, iy * iy);
			}
		}

		convolve.setParamValue("image", fgx);
		tmp = (JIPImgBitmap)convolve.processImg(ix2);
		convolve.setParamValue("image", fgy);
		ix2 = (JIPImgBitmap)convolve.processImg(tmp);
		convolve.setParamValue("image", fgx);
		tmp = (JIPImgBitmap)convolve.processImg(ixy);
		convolve.setParamValue("image", fgy);
		ixy = (JIPImgBitmap)convolve.processImg(tmp);
		convolve.setParamValue("image", fgx);
		tmp = (JIPImgBitmap)convolve.processImg(iy2);
		convolve.setParamValue("image", fgy);
		iy2 = (JIPImgBitmap)convolve.processImg(tmp);
		
		double []eigen=new double[4];
		for (int col = 5; col < n_cols-5; col++) {
			for (int row = 5; row < n_rows-5; row++) {
				get_eigen(ix2.getPixel(col, row), ixy.getPixel(col, row),
					iy2.getPixel(col, row), eigen);
				edgy.setPixel(col, row, eigen[0]);
				corn.setPixel(col, row, eigen[1]);
			}
		}

		// Binarize    
		for (int col = 5; col < n_cols - 5; col++) 
			for (int row = 5; row < n_rows - 5; row++) 
				// If it is over a threshold and is a local maxima in a 7x7 neigborhood
				if (corn.getPixel(col, row) > thres && is_maximum(corn, col, row))
					dest_edges.addPoint(col, row);

		if (getParamValueBool("corners"))
			return dest_edges;
		else {
			// Increase the brightness to be able to see the edges
			FBrightness fb = new FBrightness ();
			fb.setParamValue("perc", 1000);
			return fb.processImg(edgy);
		}
	}

	private boolean is_maximum(JIPImgBitmap image, int col, int row) throws JIPException {
		double value = image.getPixel(col, row);

		return (value > image.getPixel(col - 3, row - 3)
				&& value > image.getPixel(col - 3, row - 2)
				&& value > image.getPixel(col - 3, row - 1)
				&& value > image.getPixel(col - 3, row)
				&& value > image.getPixel(col - 3, row + 1)
				&& value > image.getPixel(col - 3, row + 2)
				&& value > image.getPixel(col - 3, row + 3)
				&& value > image.getPixel(col - 2, row - 3)
				&& value > image.getPixel(col - 2, row - 2)
				&& value > image.getPixel(col - 2, row - 1)
				&& value > image.getPixel(col - 2, row)
				&& value > image.getPixel(col - 2, row + 1)
				&& value > image.getPixel(col - 2, row + 2)
				&& value > image.getPixel(col - 2, row + 3)
				&& value > image.getPixel(col - 1, row - 3)
				&& value > image.getPixel(col - 1, row - 2)
				&& value > image.getPixel(col - 1, row - 1)
				&& value > image.getPixel(col - 1, row)
				&& value > image.getPixel(col - 1, row + 1)
				&& value > image.getPixel(col - 1, row + 2)
				&& value > image.getPixel(col - 1, row + 3)
				&& value > image.getPixel(col, row - 3)
				&& value > image.getPixel(col, row - 2)
				&& value > image.getPixel(col, row - 1)
				&& value > image.getPixel(col, row + 1)
				&& value > image.getPixel(col, row + 2)
				&& value > image.getPixel(col, row + 3)
				&& value > image.getPixel(col + 1, row - 3)
				&& value > image.getPixel(col + 1, row - 2)
				&& value > image.getPixel(col + 1, row - 1)
				&& value > image.getPixel(col + 1, row)
				&& value > image.getPixel(col + 1, row + 1)
				&& value > image.getPixel(col + 1, row + 2)
				&& value > image.getPixel(col + 1, row + 3)
				&& value > image.getPixel(col + 2, row - 3)
				&& value > image.getPixel(col + 2, row - 2)
				&& value > image.getPixel(col + 2, row - 1)
				&& value > image.getPixel(col + 2, row)
				&& value > image.getPixel(col + 2, row + 1)
				&& value > image.getPixel(col + 2, row + 2)
				&& value > image.getPixel(col + 2, row + 3)
				&& value > image.getPixel(col + 3, row - 3)
				&& value > image.getPixel(col + 3, row - 2)
				&& value > image.getPixel(col + 3, row - 1)
				&& value > image.getPixel(col + 3, row)
				&& value > image.getPixel(col + 3, row + 1)
				&& value > image.getPixel(col + 3, row + 2)
				&& value > image.getPixel(col + 3, row + 3));
	}

	private void get_eigen(double a, double b, double c, double eigen[]) {
		double d = Math.sqrt((a + c) * (a + c) - 4 * (a * c - b * b)), u;
		
		eigen[0] = (a + c + d) / 2;
		eigen[1] = (a + c - d) / 2;
		if ((eigen[0] - a) == 0.0f) {
			eigen[2] = (a > c) ? 1.0f : 0.0f;
			eigen[3] = (a > c) ? 0.0f : 1.0f;
		} else {
			u = b / (eigen[0] - a);
			d = Math.sqrt(u * u + 1.0);
			eigen[2] = u / d;
			eigen[3] = 1.0f / d;
		}
	}
}
