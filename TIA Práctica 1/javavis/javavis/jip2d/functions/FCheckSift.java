package javavis.jip2d.functions;


import java.util.ArrayList;

import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFloat;
import javavis.jip2d.base.*;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;
import javavis.jip2d.util.sift.SiftPoint;


public class FCheckSift extends JIPFunction {
	private static final long serialVersionUID = -123228275505182119L;

	public FCheckSift() {
		super();
		name = "FCheckSift";
		description = "Checks if two images have the same descriptors. Images have to be in the same sequence. Only the" +
				" first two images are processed";
		groupFunc = FunctionGroup.FeatureExtract;
		
		JIPParamFloat p1 = new JIPParamFloat("threshold", false, true);
		p1.setDescription("Threshold for rejecting a matching.");
		p1.setDefault(0.5f);	
		
		addParam(p1);
		
	}
	
	public JIPImage processImg (JIPImage img) throws JIPException{
		throw new JIPException("Select Process Sequence");
	}

	public JIPSequence processSeq(JIPSequence seq) throws JIPException {
		JIPImage img1, img2, aux;
		JIPImgBitmap imgBmp1, imgBmp2;
		ArrayList<SiftPoint> points1, points2;
		double dist, distMin;
		SiftPoint spMin=null;
		float threshold;
		
		if (seq.getNumFrames()<2) 
			throw new JIPException("FCheckSift: sequence must have at least two images");
		img1=seq.getFrame(0);
		img2=seq.getFrame(1);
		if(img1 instanceof JIPImgGeometric || img1.getType()==ImageType.BIT || img1.getType()==ImageType.BYTE)
			throw new JIPException("First frame must be byte, float, short.");
		if(img2 instanceof JIPImgGeometric || img2.getType()==ImageType.BIT || img2.getType()==ImageType.BYTE)
			throw new JIPException("Second frame must be byte, float, short.");

		threshold=getParamValueFloat("threshold");
		FGrayToGray fctg = new FGrayToGray();
		fctg.setParamValue("gray", "FLOAT");
		if (img1.getType() != ImageType.FLOAT) {
			imgBmp1=(JIPImgBitmap)fctg.processImg(img1);
		}
		else {
			imgBmp1=(JIPImgBitmap)img1;
		}
		if (img2.getType() != ImageType.FLOAT) {
			imgBmp2=(JIPImgBitmap)fctg.processImg(img2);
		}
		else {
			imgBmp2=(JIPImgBitmap)img2;
		}
		
		FSift sift = new FSift();
		aux=sift.processImg(imgBmp1);
		seq.insertFrame(aux, 1);
		points1=(ArrayList<SiftPoint>)sift.getParamValueObj("points");
		aux=sift.processImg(imgBmp2);
		seq.addFrame(aux);
		points2=(ArrayList<SiftPoint>)sift.getParamValueObj("points");
		int cont=0;
		
		for (SiftPoint sp1 : points1) {
			distMin=Double.MAX_VALUE;
			for (SiftPoint sp2 : points2) {
				dist=sp1.calcDif(sp2);
				if (dist<distMin) {
					distMin=dist;
					spMin=sp2;
				}
			}
			if (distMin<threshold) {
				System.out.println("Sp1=("+sp1.x+","+sp1.y+","+sp1.numDoG+") Sp2=("+spMin.x+","+spMin.y+","+spMin.numDoG+") Dist="+distMin);
				cont++;
			}
		}
		System.out.println("Number of matches="+cont);
		return seq;
	}
}
