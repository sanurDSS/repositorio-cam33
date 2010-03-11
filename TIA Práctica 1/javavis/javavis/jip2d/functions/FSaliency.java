package javavis.jip2d.functions;

import java.util.ArrayList;
import java.lang.Math;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.*;
import javavis.jip2d.base.geometrics.JIPImgGeometric;


/**
* 
*Estimates the saliency of each pixel of the image at an only scle using Kadir and Brady method.<BR>
*Applicable to BYTE images<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>scale: Scale<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>An image representing the saliency map.<BR><BR>
*</ul>
*/

public class FSaliency extends JIPFunction {
	
	private static final long serialVersionUID = -5543080812213811142L;

	private ArrayList<Coord> createMask(int scale)
	{
		int maxDist = (scale-1)*(scale-1);
		ArrayList<Coord> mask = new ArrayList<Coord>();
		
		for (int y=-(scale-1);y<=scale-1;y++)
			for (double x=-(scale-1);x<=scale-1;x++)
			{
				double newX = 0;
				if (x != 0) newX = Math.abs(x) - 0.5;
				if ((y*y + newX*newX) <= maxDist)
				{
					Coord c = new Coord((int)x,y);
					mask.add(c);
				}
			}

		return mask;
	}
	
	private float[] createLookUp(int bins)
	{
		float value;
		
		float []lookup = new float[bins+1];
		lookup[0] = 0;
		for (int i=1;i<=bins;i++)
		{
			value = i/(float)bins;
			lookup[i] = value*(float)Math.log(value);
		}
			
		return lookup;
	}
	
	public FSaliency() {
		super();
		name = "FSaliency";
		description = "Estimates a saliency map from the image";
		groupFunc = FunctionGroup.FeatureExtract;

		JIPParamInt p1 = new JIPParamInt("scale", true, true);
		p1.setDefault(5);
		p1.setDescription("Scale (>4)");

		addParam(p1);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("This algorithm can not be applied to this image format");
		int p1 = getParamValueInt("scale");
		if (p1 < 5)
			throw new JIPException("Scale value must be greater than 4");
		
		JIPBmpByte imgByte=null;
		
		ImageType t = img.getType();
		int bins = 1000;
		int w = img.getWidth();
		int h = img.getHeight();
		
		if (t != ImageType.BYTE) {
			switch(t) {
				case BIT: throw new JIPException("This function requires a BYTE type image"); 
				case FLOAT:
				case SHORT: FGrayToGray fgg = new FGrayToGray();
							fgg.setParamValue("gray", "BYTE");
							imgByte=(JIPBmpByte)fgg.processImg(img);
							break;
				case COLOR: FColorToGray fcg = new FColorToGray();
							imgByte=(JIPBmpByte)fcg.processImg(img);
							break;
			}
		}
			
		
		ArrayList<Coord> mask = createMask(p1);
		
		JIPBmpFloat result = new JIPBmpFloat(w,h);
		float []lookup = createLookUp(bins);
		
		int i;
		float maxEntropy = 0;
		for (int y=p1-1;y<h-p1+1;y++)
			for (int x=p1-1;x<w-p1+1;x++)
			{
				double []hist = new double[128];
				for (i=0;i<128;i++)
					hist[i] = 0;
				for (i=0;i<mask.size();i++)
					hist[(int)imgByte.getPixel(x+((Coord)mask.get(i)).x, y+((Coord)mask.get(i)).y)/2]++;
				float entropy = 0;
				for (i=0;i<128;i++)
					if (hist[i] != 0)
					{
						hist[i] = hist[i]/(float)mask.size();
						entropy -= lookup[(int)Math.floor(hist[i]*bins)];
					}
				result.setPixelFloat(x, y, entropy);
				if (entropy > maxEntropy)
					maxEntropy = entropy;
			}
			
		for (int y=p1-1;y<h-p1+1;y++)
			for (int x=p1-1;x<w-p1+1;x++)
				result.setPixelFloat(x, y, result.getPixelFloat(x,y)/maxEntropy);
		
		return result;
	}
	
	private class Coord {
		public int x;
		public int y;
		
		public Coord(int xnew, int ynew)
		{
			x = xnew; y = ynew;
		}
		
		boolean equals(Coord c)
		{
			return (c.x == this.x && c.y == this.y);
		}
	}
}
