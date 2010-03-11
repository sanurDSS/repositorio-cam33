package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamBool;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPGeomPoint;
import javavis.jip2d.base.geometrics.JIPImgGeometric;
import javavis.jip2d.util.Mascara;

/**
*FSusan: Implements the Susan method to detect corner points, and edges detection.
*S.M. Smith and J.M. Brady. SUSAN - a new approach to low level image processing. 
*International Journal of Computer Vision, 23(1):45-78, May 1997. <BR>
*A circular mask is applied for each image point which return the USAN area,<br>
*then, according this values, it recognizes if that point is a corner point.<br>
*After it, it deletes not maximum values (that is, those points which do not comply with input threshold)<br>
*and it returns an image where only those maximal values are pixels mark with 1 value.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img1: Input image<BR>
* <li>thres_t: diference of grey levels that is considered as equal (It belongs to SUSAN area)<BR>
* <li>thres_g: maximum USAN area that is considered to detect corners or edges. <BR>
* <li>distance: distance (in pixels) between centroid and the mask kernel<BR>
*where it considers that the point is a valid corner point.<BR>
* <li>maxim: margin to delete non maximums <BR>
* <li>radius: radius of the circular mask used <BR>
* <li>corner: returns corners or edges <BR><BR> 
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Result image with type ImageType.BYTE.<BR><BR>
*</ul>
* The function returns a ImageType.POINT image if corners is activated and 
* ImageType.BYTE if not. <br><br>
*/
public class FSusan extends JIPFunction {
	private static final long serialVersionUID = -6325253526988864760L;
	/* Exponente para el calculo del area USAN */
	protected static final double iDEF_EXP = 6.0;
	
	public FSusan() {
		super();
		name = "Susan";
		description = "Applies Susan method to detect corners or edges";
		groupFunc = FunctionGroup.Edges;

		JIPParamInt umbral_t = new JIPParamInt("thres_t", false, true);
		umbral_t.setDefault(15);
		umbral_t.setDescription("Threshold to differentiate the color levels");

		JIPParamInt umbral_g = new JIPParamInt("thres_g", false, true);
		umbral_g.setDefault(-1);
		umbral_g.setDescription("Threshold to calculate the USAN area (if -1 default area is asigned)");

		JIPParamInt umbral_dist = new JIPParamInt("distance", false, true);
		umbral_dist.setDefault(1);
		umbral_dist.setDescription("Threshold to delete falses positives");

		JIPParamInt umbral_maximo = new JIPParamInt("maxim", false, true);
		umbral_maximo.setDefault(10);
		umbral_maximo.setDescription("Threshold to delete non maximums (number of pixels of area)");

		JIPParamInt radio = new JIPParamInt("radius", false, true);
		radio.setDefault(3);
		radio.setDescription("Radius to circular mask");

		JIPParamBool esquinas = new JIPParamBool("corners", false, true);
		esquinas.setDefault(true);
		esquinas.setDescription("Detection of corners or edges");

		addParam(umbral_t);
		addParam(umbral_g);
		addParam(umbral_dist);
		addParam(umbral_maximo);
		addParam(radio);
		addParam(esquinas);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img.getType() == ImageType.BIT || img instanceof JIPImgGeometric) 
			throw new JIPException("Susan can not be applied to this image format");
		
		int w = img.getWidth();
		int h = img.getHeight();
		boolean corners = getParamValueBool("corners");
		int radio = getParamValueInt("radius");
		int thres_g = getParamValueInt("thres_g");
		int thres_t = getParamValueInt("thres_t");
		int distfp = getParamValueInt("distance");
		
		Mascara m = new Mascara(radio);

		if (thres_g == -1)
			if (corners) thres_g = m.maxArea / 2;
			else thres_g = 3 * m.maxArea / 4;

		JIPImgBitmap srcbn;
		if (img.getType() == ImageType.COLOR) {
			JIPFunction fctg = new FColorToGray();
			fctg.setParamValue("gray", "BYTE");
			srcbn = (JIPImgBitmap)fctg.processImg(img);
		}
		else srcbn = (JIPImgBitmap)img;
		
		JIPImgBitmap result =  (JIPImgBitmap)JIPImage.newImage(w, h, ImageType.BYTE);

		for (int i = radio; i < w-radio; i++)
			for (int j = radio; j < h-radio; j++) {
				int area = calculateUSANArea(srcbn, m, j, i, corners, radio, 
						thres_t, distfp);
				if (area >= 0 && area < thres_g) result.setPixel(i, j, thres_g - area);
				else result.setPixel(i, j, 0);
			}

		if (!corners)
			return result;
		else {
			JIPGeomPoint resultpoints = (JIPGeomPoint)JIPImage.newImage(w, h, ImageType.POINT);
			JIPBmpBit resultBN = nonMaximumSupression(result, radio);
			for (int i = radio; i < w-radio; i++) 
				for (int j = radio; j < h-radio; j++) 
					if (resultBN.getPixelBool(i, j))
						resultpoints.addPoint(i, j);
			return resultpoints;
		}
	}

	/**
	* Eliminates the points which are not maximum.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>img: Image to scan<BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>result: binary image which pixels with 
	* value equal to 1 are matched with the maximum of the original image<br><BR>
	*</ul>
	*/
	protected JIPBmpBit nonMaximumSupression(JIPImgBitmap img, int radio) throws JIPException {
		int w = img.getWidth();
		int h = img.getHeight();
		JIPBmpBit result = (JIPBmpBit)JIPImage.newImage(w, h, ImageType.BIT);

		for (int col = radio; col < w-radio; col++) 
			for (int row = radio; row < h-radio; row++) 
				result.setPixelBool(col, row, is_maximum(img, col, row));

		return result;
	}

	/**
	* Calculates the USAN area, it uses a mask in the point (fil, col). Returns the negative area<br>
	* if point is not a valid corner point.<BR> 
	*<ul><B>Input parameters:</B><BR>
	*<li>img: Image to scan<BR>
	*<li>fil: Pixel row<BR>
	*<li>col: Pixel column<BR>	<BR>
	*</ul>	
	*<ul><B>Output parameters:</B><BR>
	*<li>result: Calculated area<BR><BR>
	*</ul>
	*/
	protected int calculateUSANArea(JIPImgBitmap img, Mascara m, int fil, int col, 
			boolean corners, int radio, int thres_t, int distfp) throws JIPException {
		double result = 0.0, pendiente;
		int filMascara, colMascara = 0, centroideX = 0, centroideY = 0,
			total = 0, x, y;
		int w = img.getWidth();
		int h = img.getHeight();
		
		for (int i = col - radio; i < col + radio; i++) {
			filMascara = 0;
			for (int j = fil - radio; j < fil + radio; j++) {
				if (i >= 0 && j >= 0 && i < w && j < h && i != col && j != fil
					&& m.mascara[filMascara][colMascara] 
					&& Math.abs(img.getPixel(i, j) - img.getPixel(col, fil)) <= thres_t) {
						result += Math.exp(-Math.pow((img.getPixel(i, j)
								- img.getPixel(col, fil)) / thres_t, iDEF_EXP));
						centroideX += i;
						centroideY += j;
						total++;
				}
				filMascara++;
			}
			colMascara++;
		}

		if (total > 0) {
			centroideX /= total;
			centroideY /= total;
		} else {
			centroideX = col;
			centroideY = fil;
		}

		if (!corners)
			return (int) result;

		/* Eliminamos los que tengan el centroide demasiado cerca
		   del nucleo, o los que no cumplan el principio de contiguidad */
		double dist = Math.sqrt(Math.pow(centroideX - col, 2.0)
					+ Math.pow(centroideY - fil, 2.0));

		if (dist > radio)
			return (int) -result;

		if (dist > distfp) {
			if (centroideX != col) {
				pendiente = (centroideY - fil) / (double) (centroideX - col);
				x = col;
				y = fil;
				while (x != centroideX) {
					x += (centroideX > col ? 1 : -1);
					y += pendiente;
					if (Math.abs(img.getPixel(x, y) - img.getPixel(col, fil)) > thres_t)
						return (int) -result;
				}
			} 
			else {
				y = fil;
				while (y != centroideY) {
					y += (centroideY > fil ? 1 : -1);
					if (Math.abs(img.getPixel(col, y) - img.getPixel(col, fil)) > thres_t)
						return (int) -result;
				}
			}
			return (int) result;
		} 
		else return (int) -result; 
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
}