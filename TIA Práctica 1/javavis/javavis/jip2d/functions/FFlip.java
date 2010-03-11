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
*from horizontal axis.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Image with the pixels reverse over horizontal axis.<BR><BR>
*</ul>
*/
public class FFlip extends JIPFunction {
	private static final long serialVersionUID = 2452843773543656719L;

	public FFlip() {
		super();
		name = "FFlip";
		description = "Flip an image";
		groupFunc = FunctionGroup.Manipulation;
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		int w = img.getWidth();
		int h = img.getHeight();
		ImageType t = img.getType();

		if (img instanceof JIPImgBitmap) {
			JIPImgBitmap imgBmp = (JIPImgBitmap)img;
			int b = imgBmp.getNumBands();
			JIPImgBitmap res = (JIPImgBitmap)JIPImage.newImage(b, w, h, t);
			double ip;
			for (int nb = 0; nb < b; nb++)
				for (int y = 0; y < h; y++)
					for (int x = 0; x < w; x++) {
						ip = imgBmp.getPixel(nb, x, y);
						res.setPixel(nb, x, y, imgBmp.getPixel(nb, x, h - y - 1));
						res.setPixel(nb, x, h - y - 1, ip);
					}
			return res;
		} else {
			JIPImgGeometric res = null;
			JIPImgGeometric imgGeom = (JIPImgGeometric)img;
			if (img.getType() == ImageType.POINT || img.getType() == ImageType.SEGMENT) {
				ArrayList<Integer> geodata = new ArrayList<Integer>(imgGeom.getData());
				for (int i = 1; i < geodata.size(); i += 2) // only process the Y coordinates
					geodata.set(h - 1 - geodata.get(i), i);
				res = (JIPImgGeometric)JIPImage.newImage(w, h, t);
				res.setData(geodata);
			} else {
				if (img.getType() == ImageType.POLY || img.getType() == ImageType.EDGES) {
					ArrayList<ArrayList<Integer>> geodata = 
						new ArrayList<ArrayList<Integer>>(imgGeom.getData());
					for (ArrayList<Integer> auxVec : geodata) {
						int tamV = auxVec.size();
						for (int i = 1; i < tamV; i += 2) 
							auxVec.set(h-1-auxVec.get(i), i);
					}
					res = (JIPImgGeometric)JIPImage.newImage(w, h, t);
					res.setData(geodata);
				}
			}
			return res;
		}
	}
}
