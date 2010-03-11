package javavis.jip2d.functions;

import java.util.ArrayList;

import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

/**
*Makes a reversal of image pixels and geometric data 
*from vertical axis.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Image with the pixels reverses from vertical axis.<BR><BR>
*</ul>
*/
public class FMirror extends JIPFunction {
	private static final long serialVersionUID = 1393197595197881043L;

	public FMirror() {
		super();
		name = "FMirror";
		description = "Mirror an image";
		groupFunc = FunctionGroup.Manipulation;
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		int w = img.getWidth();
		int h = img.getHeight();
		ImageType t = img.getType();

		if (img instanceof JIPImgBitmap) {
			int b = ((JIPImgBitmap)img).getNumBands();
			JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(b, w, h, t);
			for (int nb = 0; nb < b; nb++)
				for (int y = 0; y < h; y++)
					for (int x = 0; x < w; x++) {
						double ip = ((JIPImgBitmap)img).getPixel(nb, x, y);
						res.setPixel(nb, x, y, ((JIPImgBitmap)img).getPixel(nb, w - x - 1, y));
						res.setPixel(nb, w - x - 1, y, ip);
					}
			return res;
		} else {
			JIPImgGeometric res = null;
			res = (JIPImgGeometric)JIPImage.newImage(w, h, t);
			if (img.getType() == ImageType.POINT || img.getType() == ImageType.SEGMENT) {
				ArrayList<Integer> geodata = new ArrayList<Integer>();
				ArrayList<Integer> vaux = (ArrayList<Integer>)((JIPImgGeometric)img).getData();
				for (int i = 0; i < vaux.size(); i += 2) { 
					geodata.add(w - 1 - vaux.get(i));
					geodata.add(vaux.get(i+1));
				}
				res.setData(geodata);
				
			} else {
				if (img.getType() == ImageType.POLY || img.getType() == ImageType.EDGES) {         
					ArrayList<ArrayList<Integer>> geodata = new ArrayList<ArrayList<Integer>>();
					for (ArrayList<Integer> vaux : (ArrayList<ArrayList<Integer>>)((JIPImgGeometric)img).getData()) {
						ArrayList<Integer> vauxN = new ArrayList<Integer>();
						for (int i = 0; i < vaux.size(); i += 2) {
							vauxN.add(w - 1 - vaux.get(i));
							vauxN.add(vaux.get(i+1));
						}
						geodata.add(vauxN);
					}
					res.setData(geodata);
				}
				else 
					throw new JIPException("Mirror can not be applied to this image format");
			}
			return res;
		}
	}
}
