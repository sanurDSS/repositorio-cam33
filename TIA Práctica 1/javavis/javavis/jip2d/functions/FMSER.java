package javavis.jip2d.functions;


import javavis.jip2d.base.geometrics.JIPImgGeometric;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPImage;
import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.jip2d.base.bitmaps.*;
import javavis.base.parameter.*;

import java.util.ArrayList;

public class FMSER extends JIPFunction {
	private static final long serialVersionUID = -5543080812213828342L;
	
	private ArrayList<MSER> MSERS = new ArrayList<MSER>();
	
	private double INNER = 1000;
	
	private int minSize;
	private int maxSize;
	private int delta;
	
	public FMSER()
	{
		super();
		name = "FMSER";
		description = "Looks for the Maximally Stable Extremal Regions in an image";
		groupFunc = FunctionGroup.FeatureExtract;
		
		JIPParamInt p1 = new JIPParamInt("mins", true, true);
		p1.setDefault(50);
		p1.setDescription("Minimum size of the MSERs");
		JIPParamInt p2 = new JIPParamInt("maxs", true, true);
		p2.setDefault(500);
		p2.setDescription("Maximum size of the MSERS");
		JIPParamInt p3 = new JIPParamInt("delta", true, true);
		p3.setDescription("Width of the intensity range (delta)");
		p3.setDefault(1);
		
		addParam(p1);
		addParam(p2);
		addParam(p3);
	}
	
	
	public JIPImage processImg(JIPImage img) throws JIPException 
	{
		// Input parameters checking
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("Noise can not be applied to this image format");
		JIPBmpByte imgByte = null;	
		ImageType t=img.getType();
		if (t != ImageType.BYTE) {
			switch(t) {
				case BIT: throw new JIPException("This function requires a BYTE type image"); 
				case FLOAT:
				case SHORT: FGrayToGray fgg = new FGrayToGray();
							fgg.setParamValue("gray", ImageType.BYTE);
							imgByte=(JIPBmpByte)fgg.processImg(img);
							break;
				case COLOR: FColorToGray fcg = new FColorToGray();
							imgByte=(JIPBmpByte)fcg.processImg(img);
							break;
			}
		}
		
		minSize = getParamValueInt("mins");
		maxSize = getParamValueInt("maxs");
		delta = getParamValueInt("delta");
		
		if (minSize < 1 || maxSize < 1 || maxSize<=minSize)
		{
			throw new JIPException("The value of the parameter mins must be greater than 0 and lesser than maxs");
		}
		
		int height = img.getHeight();
		int width = img.getWidth();
		
		// Output generation
		JIPBmpColor result = new JIPBmpColor(width, height);
		result.setAllPixelsByte(0, imgByte.getAllPixelsByte());
		result.setAllPixelsByte(1, imgByte.getAllPixelsByte());
		result.setAllPixelsByte(2, imgByte.getAllPixelsByte());
		// From lowest to highest intensity
		processMSER(imgByte, result, width, height);
		
		// From highest to lowest intensity
		FNegate fn = new FNegate();
		JIPImage negated = fn.processImg(imgByte);
		JIPBmpByte negatedByte = (JIPBmpByte)negated;
		processMSER(negatedByte, result, width, height);
		
		// All MSERS are stored in the MSERS ArrayList, that can be accessed
		// from this method
		
		return result;
	}
	
	public void processMSER(JIPBmpByte imgByte, JIPBmpColor result, int width, int height) throws JIPException 
	{
		DisjointSetNode [][]allPixels = new DisjointSetNode[height][width];
		
		// The data structure for the pixels is initialized
		for (int y = 0; y<height; y++)
		{
			for (int x = 0; x<width; x++)
			{
				allPixels[y][x] = new DisjointSetNode(x,y,(int)imgByte.getPixel(x,y));
			}
		}
		// The pixels are ordered by intensity
		ArrayList<DisjointSetNode>[] orderedPixels = binsort(allPixels, height, width);
		
		// Main loop - looking for MSERs
		for (int i=0;i<256;i++)
		{
			for (int j=0;j<orderedPixels[i].size();j++)
			{
				DisjointSetNode currentPixel = orderedPixels[i].get(j);
				currentPixel.sizes = new ArrayList<Integer>();
				currentPixel.sizes.add(i);
				currentPixel.sizes.add(1); // A region of size 1
				
				int x = currentPixel.x;
				int y = currentPixel.y;
				
				// We check if any connected component must be joined with the new region
				if (x > 0 && allPixels[y][x-1].Find().sizes != null && allPixels[y][x-1].Find() != currentPixel.Find())
				{
					mergeComponents(currentPixel, allPixels[y][x-1], i);
				}
				
				if (x < width-1 && allPixels[y][x+1].Find().sizes != null && allPixels[y][x+1].Find() != currentPixel.Find())
				{
					mergeComponents(currentPixel, allPixels[y][x+1], i);
				}
				
				if (y > 0 && allPixels[y-1][x].Find().sizes != null && allPixels[y-1][x].Find() != currentPixel.Find())
				{
					mergeComponents(currentPixel, allPixels[y-1][x], i);
				}
				
				if (y < height-1 && allPixels[y+1][x].Find().sizes != null && allPixels[y+1][x].Find() != currentPixel.Find())
				{
					mergeComponents(currentPixel, allPixels[y+1][x], i);
				}
				
			}
		}	
	
		for (int i=0;i<MSERS.size();i++)
		{
			ArrayList<Pixel> boundary = new ArrayList<Pixel>();
			// We get all the pixels inside each MSER and its boundary to draw it. These two
			// lines are the most computationally expensive, and are not part of the original
			// algorithm
			getMSER((JIPBmpByte)imgByte.clone(), new JIPBmpFloat(imgByte.getWidth(), imgByte.getHeight()), MSERS.get(i).x, MSERS.get(i).y, MSERS.get(i).threshold, boundary);
			drawMSER(boundary, result);
		}
	}
	
	// Draws the boundary of a MSER
	private void drawMSER(ArrayList <Pixel> mp, JIPBmpColor img)
	{
		try {
			while (mp.size() != 0)
			{
				Pixel p = mp.get(0);
				mp.remove(0);
				img.setPixel(0,p.x,p.y,0);
				img.setPixel(1,p.x,p.y,255);
				img.setPixel(2,p.x,p.y,0);
			}
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	// Gets the boundary and all the pixels inside a MSER. This method is a modification
	// of the flood fill algorithm
	private ArrayList<Pixel> getMSER(JIPBmpByte imgByte, JIPBmpFloat inner, int x, int y, int t, ArrayList<Pixel>boundary)
	{
		ArrayList<Pixel> result = new ArrayList<Pixel>();
		try {
			ArrayList<Pixel> queue = new ArrayList<Pixel>();
			if (imgByte.getPixel(x, y) >= t)
				return result;
			queue.add(new Pixel(x,y));
			
			while (queue.size() != 0)
			{
				Pixel p = queue.get(0);
				int xn = p.x;
				int yn = p.y;
				queue.remove(0);
				
				if (imgByte.getPixel(xn,yn) < t && inner.getPixel(xn, yn) != INNER)
				{
					inner.setPixel(xn,yn,INNER);
					result.add(new Pixel(xn,yn));
					if (xn > 0) {
						if (imgByte.getPixel(xn-1, yn) < t && inner.getPixel(xn-1,yn)!= INNER) {
							queue.add(new Pixel(xn-1,yn));
						} else if (inner.getPixel(xn-1,yn) != INNER){
							boundary.add(new Pixel(xn-1,yn));
						}
					}
					if (xn < imgByte.getWidth() - 1)
					{
						if (imgByte.getPixel(xn+1, yn) < t && inner.getPixel(xn+1,yn) != INNER) {
							queue.add(new Pixel(xn+1,yn));
						} else if (inner.getPixel(xn+1,yn) != INNER){
							boundary.add(new Pixel(xn+1,yn));
						}
					}
					if (yn > 0) {
						if (imgByte.getPixel(xn,yn-1)<t && inner.getPixel(xn,yn-1) != INNER) {
							queue.add(new Pixel(xn,yn-1));
						} else if (inner.getPixel(xn,yn-1) != INNER){
							boundary.add(new Pixel(xn,yn-1));
						}
					}
					if (yn < imgByte.getHeight() - 1) {
						if (imgByte.getPixel(xn,yn+1)<t && inner.getPixel(xn,yn+1) != INNER) {
							queue.add(new Pixel(xn,yn+1));
						} else if (inner.getPixel(xn,yn+1) != INNER){
							boundary.add(new Pixel(xn,yn+1));
						}
					}
					
				}
				else
				{
					result.add(new Pixel(xn,yn));
				}
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return result;
	}
	
	// Checks if two connected components mus be joined; in this case, the smallest one
	// is removed, and we look for MSERS in its size array
	private void mergeComponents (DisjointSetNode cn, DisjointSetNode n, int intensity)
	{
		
		// We join the two connected components and we see which of the two components roots
		// is the root of the new connected component
		DisjointSetNode previouscn = cn.Find(), previousn = n.Find();
		
		DisjointSetNode newRoot = cn.Union(n);
		DisjointSetNode parent, son;
		if (newRoot == previouscn)
		{
			parent = previouscn;
			son = previousn;
		}
		else
		{
			parent = previousn;
			son = previouscn;
		}
		
		// We look for new MSERS in son sizes array (local minima in sizes change rate)
		// First a vector of change rates is built
		int changeRates = son.sizes.get(son.sizes.size()-2) - son.sizes.get(0) -2*delta;
		if (changeRates > 2)
		{
			int []rates = new int[changeRates];
			for (int i=0;i<changeRates;i++)
			{
				rates[i] = son.sizes.get(2*i + 1 + 2*delta*2) - son.sizes.get(2*i+1);
			}
		    // And next we look for local minima in change rate
			int prev = rates[0];
			int i=1;
			while (rates[i] == prev && i <changeRates-1)
					i++;
			while (i<changeRates-1)
			{
				if (rates[i] != rates[i+1])
				{
					if (rates[i] < prev && rates[i] < rates[i+1])
					{
						// Only MSERS in the range of sizes [minSize, maxSize] are stored]
						int mserSize = son.sizes.get(2*(i + delta) + 1);
						if (mserSize >= minSize && mserSize <= maxSize)
						{
							// 	NEW MSER
							MSER newMSER = new MSER(son.x, son.y, son.sizes.get(0) + i + delta+1,mserSize);
							// The intensity threshold is an upper threshold; a MSER is composed by
							// all pixels surrounding the centroid which intensity is less than it
							MSERS.add(newMSER);
						}
					}
					prev = rates[i];
				}
				i++;
			}
		}
		
		// The parent's sizes array is updated, while removing the son's one
		int parentLastIntensity = parent.sizes.get(parent.sizes.size()-2);
		int parentLastSize = parent.sizes.get(parent.sizes.size()-1);
		int sonLastSize = son.sizes.get(son.sizes.size()-1);
		if (parentLastIntensity == intensity)
			parent.sizes.set(parent.sizes.size()-1, parentLastSize + sonLastSize);
		else
		{
			int curInt = parentLastIntensity+1;
			while (curInt != intensity)
			{
				parent.sizes.add(curInt);
				parent.sizes.add(parentLastSize);
				curInt++;
			}
			parent.sizes.add(intensity);
			parent.sizes.add(parentLastSize + sonLastSize);
		}
		son.sizes = null;
	}
	
	// BINSORT or bucket algorithm. Complexity is O(n) since all the possible values are in
	// a limited range [0..255]
	private ArrayList<DisjointSetNode>[] binsort(DisjointSetNode allPixels[][], int h, int w)
	{
		ArrayList<DisjointSetNode>[] ordered = new ArrayList[256];
		int i;
		for (i=0;i<256;i++)
			ordered[i] = new ArrayList<DisjointSetNode>();
		
		for (int y=0;y<h;y++)
			for (int x=0;x<w;x++)
				ordered[allPixels[y][x].index].add(allPixels[y][x]);
		
		return ordered;
	}
	
	/* Disjoint set node private class */
	private class DisjointSetNode 
	{
		DisjointSetNode parent;
		int index;
		int rank;
		int x;
		int y;
		ArrayList<Integer> intensities = null;
		ArrayList<Integer> sizes = null;
		
		
		public DisjointSetNode(int x, int y, int i) 
		{
			parent = this;
			index = i;
			rank = 0;
			this.x = x;
			this.y = y;
		}
		
		public DisjointSetNode Find()
		{
			if (this!=this.parent)
				this.parent = this.parent.Find();
			return this.parent;
		}
		
		public DisjointSetNode Union(DisjointSetNode other)
		{
			DisjointSetNode A = this.Find();
			DisjointSetNode B = other.Find();
			
			if (A != B)
			{
				DisjointSetNode root;
				
				if (A.rank > B.rank)
				{
					B.parent = A;
					root = A;
				}
				else
				{
					A.parent = B;
					root = B;
				}
				if (A.rank == B.rank)
					B.rank++;
				
				return root;
			}
			else
				return A;
		}
	
	}
	
	/* Pixel private class */
	private class Pixel
	{
		public int x;
		public int y;
		
		public Pixel(int newx, int newy)
		{
			x = newx;
			y = newy;
		}
	}
	
	/* MSER private class*/
	private class MSER 
	{
		public int threshold;
		public int x;
		public int y;
		public int size;
		
		public MSER(int newX, int newY, int newT, int newSize)
		{
			x = newX;
			y = newY;
			threshold = newT;
			size = newSize;
		}
	}
	
}
