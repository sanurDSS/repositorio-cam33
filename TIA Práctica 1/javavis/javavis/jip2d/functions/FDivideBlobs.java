package javavis.jip2d.functions;

import java.util.ArrayList;
import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.util.Blob;

public class FDivideBlobs extends JIPFunction{
	private static final long serialVersionUID = 534245315835245598L;


	public FDivideBlobs(){
		super();
		name="FDivideBlobs";
		description="Find the blobs in the (binary) image and returns the blobs " +
				"separated in bands";
		groupFunc = FunctionGroup.Others;
		
		JIPParamInt p1 = new JIPParamInt("radius", false, true);
		p1.setDefault(50);
		p1.setDescription("Radius to scale");
		
		JIPParamInt p2 = new JIPParamInt("minp", false, true);
		p2.setDefault(200);
		p2.setDescription("Minimum number of points necessary to be a blob");
		
		addParam(p1);
		addParam(p2);
	}
				
	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img.getType()!=ImageType.BIT) 
			throw new JIPException ("DivideBlobs only applied to BIT images");
		
		int radio = getParamValueInt("radius");
		int minp = getParamValueInt("minp");

		// First, detects blobs in the image
		JIPFunction func = new FBlobs();
		func.processImg(img);
		
		ArrayList<Blob> blobsList = (ArrayList<Blob>)func.getParamValueObj("blobs");
		ArrayList<Integer> xcoordList = new ArrayList<Integer>();
		ArrayList<Blob> blobListAux = new ArrayList<Blob>(); 
		for (Blob b : blobsList) {
			// Check if the number of points is enough
			if (b.length() > minp) {
				b.calcEverything();
				boolean found=false;
				// Sort the blobs from right to left
				for (int i=0; i<blobListAux.size(); i++) {
					if (xcoordList.get(i) > b.centro_x) {
						blobListAux.add(i, b);
						xcoordList.add(i, b.centro_x);
						found=true;
						break;
					}
				}
				if (!found) {
					blobListAux.add(b);
					xcoordList.add(b.centro_x);
				}
			}
		}

		// It creates a new image with a number of bands equal to the number of blobs 
		JIPBmpBit res = new JIPBmpBit(blobListAux.size(), 2*radio, 2*radio);
		JIPImgBitmap aux;
		Blob b;
		double sfX, sfY;
		int r, c, cx, cy;
		// Apply a scale to get all the images with the same size
		for (int i=0; i<blobListAux.size(); i++) {
			b=blobListAux.get(i);
			aux=b.getImage();
			cx=aux.getWidth()/2;
			cy=aux.getHeight()/2;
			sfX = b.xsize/(2.0*radio);
			sfY = b.ysize/(2.0*radio);
			for (int x=0; x<2*radio; x++)
				for (int y=0; y<2*radio; y++) {
					c = (int)(sfX * (x-radio)) + cx;
					r = (int)(sfY * (y-radio)) + cy;
					res.setPixel(i, x, y, aux.getPixel(c, r));
				}
		}
		return res;
	}
}	