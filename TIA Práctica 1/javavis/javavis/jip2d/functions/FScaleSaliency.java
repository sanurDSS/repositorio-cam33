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
import javavis.jip2d.base.geometrics.*;
import javavis.jip2d.base.*;

import java.util.Comparator;
import java.util.PriorityQueue;


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

public class FScaleSaliency extends JIPFunction {
	
	private static final long serialVersionUID = -5543080812213811142L;
	
	// clustering constants
	private int K = 3;
	private int Vth = 70;

	// Calculates the pixels to consider at maximum scale to calculate saliency
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
	
	// Used during mask creation. Returns true if a pixel on a mask at scale s is also present
	// at scale s-1
	private boolean contains(ArrayList<ArrayList<Coord>> a, int s, Coord c)
	{
		boolean cont = false;
		
		int i = 0;
		int j = 0;
		
		while (!cont &&  j < s)
		{
			if (a.get(j).get(i).equals(c)) cont = true;
			else i++;
			
			if (i>=a.get(j).size()) {j++;i=0;}
		}
		
		return cont;
	}
	
	// Calculates the pixels to consider at each scale during saliency calculation
	private ArrayList<ArrayList<Coord>> createCumulativeMasks(int s0, int sf)
	{
		ArrayList<ArrayList<Coord>> masks = new ArrayList<ArrayList<Coord>>();
		
		masks.add(createMask(s0));
		
		for (int scale=s0+1;scale<=sf;scale++)
		{
			masks.add(new ArrayList<Coord>());
		
			int maxDist = (scale-1)*(scale-1);
			for (int y=-(scale-1);y<=scale-1;y++)
				for (double x=-(scale-1);x<=scale-1;x++)
				{
					double newX = 0;
					if (x != 0) newX = Math.abs(x) - 0.5;
					Coord c = new Coord((int)x,y);
					if ((y*y + newX*newX) <= maxDist && !contains(masks,scale-s0,c))
						masks.get(scale-s0).add(c);
				}
		}

		return masks;
	}
	
	// Creates a look up table to avoid calculating logarithms during scale saliency algorithm
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
	
	// Constructor
	public FScaleSaliency() {
		super();
		name = "FScaleSaliency";
		description = "Kadir and Brady feature extraction algorithm based on entropy";
		groupFunc = FunctionGroup.FeatureExtract;

		JIPParamInt p1 = new JIPParamInt("s0", true, true);
		p1.setDefault(5);
		p1.setDescription("Initial scale (>4)");
		JIPParamInt p2 = new JIPParamInt("sf", true, true);
		p2.setDefault(20);
		p2.setDescription("Final scale (>(s0+1))");
		JIPParamInt p3 = new JIPParamInt("%feat",true,true);
		p3.setDefault(5);
		p3.setDescription("% of most salient features to show");

		addParam(p1);
		addParam(p2);
		addParam(p3);
	}

	// This funcion is only valid for sequences containing an only frame, so this method
	// is empty
	public JIPImage processImg(JIPImage img) throws JIPException {
		throw new JIPException("This algorithm must be used with a sequence having an only frame");
	}
	
	// Creates polygons to approximate circles
	private ArrayList<Integer> genCircunferencia(int x, int y, int radio) {
		ArrayList<Integer> circ;
		final float INC_ANG = 10f;
		
		circ = new ArrayList<Integer>();
		for(float ang=0f; ang<360; ang+=INC_ANG) {
			circ.add((int)(x + Math.cos(Math.toRadians(ang))*radio));
			circ.add((int)(y + Math.sin(Math.toRadians(ang))*radio));
		}
		
		return circ;
	}
	
	// Method to cluster close regions corresponding to the most salient features
	// on the image
	private ArrayList<Feature> clustering(PriorityQueue <Feature> feat, int p3)
	{
		int i, j;
		int totalFeatures = feat.size();
		ArrayList<Feature> selectedFeat = new ArrayList<Feature>();
		int featuresToShow = totalFeatures*p3/100;
		for (i=0;i<featuresToShow;i++)
			selectedFeat.add(feat.poll());
		ArrayList<Feature> clustFeat = new ArrayList<Feature>();
		// 	Clustering
		for (i=0;i<featuresToShow;i++)		
		{
			Feature f = selectedFeat.get(i);
			// 	Search of the k nearest neighbours
			PriorityQueue<Feature> knn = new PriorityQueue<Feature>(1,new FeatureDistanceComparator());
			for (j=0;j<featuresToShow;j++)
				if (j!=i)
				{
					Feature f2 = selectedFeat.get(j);
					double distance = Math.sqrt(Math.pow(f2.x - f.x,2) + Math.pow(f2.y - f.y,2));
					f2.distance = distance;
					knn.add(f2);
				}
			// 	Find distance to regions already clustered
			double D = Double.MAX_VALUE;
			for (j=0;j<clustFeat.size();j++)
			{
				Feature f2 = clustFeat.get(j);
				double distance = Math.sqrt(Math.pow(f2.x - f.x,2) + Math.pow(f2.y - f.y,2));
				if (distance < D)
					D = distance;
			}
			// 	Mean scale and center variance
			double meanScale = f.scale;
			double meanX = f.x;
			double meanY = f.y;
			ArrayList <Coord> centers = new ArrayList<Coord>();
			for (j=0;j<K;j++)
			{
				Feature f2 = knn.poll();
				centers.add(new Coord(f2.x,f2.y));
				meanX += f2.x;
				meanY += f2.y;
				meanScale += f2.scale;
			}
			meanX = meanX/(double)(K+1);
			meanY = meanY/(double)(K+1);
			meanScale = meanScale / (double)(K+1);
			
			double variance = Math.pow(meanX - f.x, 2) + Math.pow(meanY - f.y, 2);
			for (j=0;j<K;j++)
			{
				Coord c = centers.get(j);
				variance += Math.pow(meanX - c.x, 2) + Math.pow(meanY - c.y, 2);
			}
			// 	If the cluster is not redundant, it is added
			if (D > meanScale && variance < Vth)
			{
				Feature f2 = new Feature((int)meanX, (int)meanY, (int)meanScale, 0.0);
				clustFeat.add(f2);
			}
			
		}
		
		return clustFeat;
	}
	
	// Main method
	public JIPSequence processSeq(JIPSequence seq) throws JIPException 
	{
		if (seq.getNumFrames() > 1)
			throw new JIPException("This algorithm must be used with a sequence having an only frame");
		
		JIPImage img = (seq.getFrames()).get(0);
		
		// Parameter checking
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("This algorithm can not be applied to this image format");
		int p1 = getParamValueInt("s0");
		if (p1 < 5)
			throw new JIPException("S0 value must be greater than 4");
		int p2 = getParamValueInt("sf");
		if (p2 <= p1 + 1)
			throw new JIPException("Sf must be greater than s0 + 1");
		int p3 = getParamValueInt("%feat");
		if (p3 <= 0 || p3 > 100)
			throw new JIPException("The % of most salient features to show must be in the range ]0,100]");
		
		ImageType t = img.getType();
		int bins = 1000;
		int w = img.getWidth();
		int h = img.getHeight();
		

		JIPBmpFloat imgByte = null;
		
		if (t != ImageType.FLOAT) {
			switch(t) {
				case BIT: throw new JIPException("This function requires a BYTE type image"); 
				case BYTE:
				case SHORT: FGrayToGray fgg = new FGrayToGray();
							fgg.setParamValue("gray", "FLOAT");
							imgByte=(JIPBmpFloat)fgg.processImg(img);
							break;
				case COLOR: FColorToGray fcg = new FColorToGray();
							fcg.setParamValue("gray", "FLOAT");
							imgByte=(JIPBmpFloat)fcg.processImg(img);
							break;
			}
		}
		
		ArrayList<ArrayList<Coord>> masks = createCumulativeMasks(p1,p2);
		
		// Initialization
		int i;
		JIPSequence resSeq = new JIPSequence();
		JIPBmpFloat result = new JIPBmpFloat(p2-p1+2,w,h);
		JIPGeomPoly features = new JIPGeomPoly(w,h);
		resSeq.addFrame(features);
		float []lookup = createLookUp(bins);
		int totalSizes[] = new int[p2-p1+1];
		totalSizes[0] = masks.get(0).size();
		float []maxEntropies = new float[p2-p1+1];
		PriorityQueue<Feature> feat = new PriorityQueue<Feature>(1,new FeatureComparator());
		for (i=1;i<p2-p1+1;i++)
		{
			totalSizes[i] = totalSizes[i-1] + masks.get(i).size();
			maxEntropies[i] = 0;
		}
		
		// Scale saliency computation
		for (int y=p2-1;y<h-p2+1;y++)
			for (int x=p2-1;x<w-p2+1;x++)
			{
				double [][]hist = new double[p2-p1+1][128];
				double []entropies = new double[p2-p1+1];
				for (i=0;i<128;i++)
					hist[0][i] = 0;
				// Saliency computation
				for (int s=p1;s<=p2;s++)
				{
					int currentScale = s-p1;
					
					if (s>p1) for (i=0;i<128;i++)
						hist[currentScale][i] = hist[currentScale-1][i];
					for (i=0;i<masks.get(currentScale).size();i++)
						hist[currentScale][(int)(imgByte.getPixel(x+((Coord)masks.get(currentScale).get(i)).x, y+((Coord)masks.get(currentScale).get(i)).y)*255)/2]++;
					float entropy = 0;
					for (i=0;i<128;i++)
						if (hist[currentScale][i] != 0)
						{
							entropy -= lookup[(int)Math.floor(hist[currentScale][i]/(float)totalSizes[currentScale]*bins)];
						}
					result.setPixelFloat(currentScale+1, x, y,entropy);
					if (entropy > maxEntropies[currentScale])
						maxEntropies[currentScale] = entropy;
					entropies[currentScale] = entropy;
				}
				
				// Entropy peaks search and entropy weighting
				for (int s=p1+1;s<p2;s++)
				{
					int currentScale = s - p1;
					if (entropies[currentScale]>entropies[currentScale+1] && entropies[currentScale]>entropies[currentScale-1])
					{
						double weight = 0;
						for (i=0;i<128;i++)
							weight += Math.abs(hist[currentScale][i]/(float)totalSizes[currentScale] - hist[currentScale-1][i]/(float)totalSizes[currentScale-1]);
						weight *= s*s/(2*s-1);
						feat.add(new Feature(x,y,s,weight*entropies[currentScale]));
					}
				}
			}
		
		// Clustering
		ArrayList<Feature> clustFeat = clustering(feat, p3);
		
		
		
		// Results ares showed
		ArrayList<ArrayList<Integer>> vecPun = new ArrayList<ArrayList<Integer>>();
		for (i=0;i<clustFeat.size();i++)
		{
			Feature f = clustFeat.get(i);
			vecPun.add(genCircunferencia(f.x,f.y,f.scale));
		}
		features.setData(vecPun);
		
		result.setAllPixelsFloat(0, imgByte.getAllPixelsFloat());
		for (i=p1;i<=p2;i++)
			for (int y=p2-1;y<h-p2+1;y++)
				for (int x=p2-1;x<w-p2+1;x++)
					result.setPixelFloat(i-p1+1, x, y,result.getPixelFloat(i-p1+1, x, y)/maxEntropies[i-p1]);
				
		resSeq.addFrame(result);
		
		return resSeq;
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
	
	private class Feature {
		public double entropy;
		public int x, y, scale;
		public double distance;
		
		public Feature(int newX, int newY, int newScale, double newEntropy)
		{
			x = newX;
			y = newY;
			scale = newScale;
			entropy = newEntropy;
		}
		
		
	}
	
	private class FeatureComparator  implements Comparator<Feature>
	{
		public FeatureComparator()
		{
			super();
		}
		
		public int compare(Feature o1, Feature o2)
		{
			if (o1.entropy > o2.entropy)
				return -1;
			else if (o1.entropy < o2.entropy)
				return 1;
			else return 0;
		}
	}
	
	private class FeatureDistanceComparator implements Comparator<Feature>
	{
		public FeatureDistanceComparator()
		{
			super();
		}
		
		public int compare(Feature o1, Feature o2)
		{
			if (o1.distance < o2.distance)
				return -1;
			else if (o1.distance > o2.distance)
				return 1;
			else return 0;
		}
	}

}
