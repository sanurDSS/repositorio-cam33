package javavis.jip2d.functions;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.io.IOException;

import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFile;
import javavis.base.parameter.JIPParamString;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpColor;
import javavis.jip2d.functions.FEqualize;
import javavis.jip2d.functions.FRGBToColor;
import javavis.jip2d.functions.FSegmentHSB;
import javavis.jip2d.functions.FClousure;

/**
 * 
 **/
public class FRecogLT extends JIPFunction {
	private static final long serialVersionUID = -7262973524107183332L;

	public FRecogLT() {
		super();
		name = "FRecogLT";
		description = "Recognize license plates";
		groupFunc = FunctionGroup.Applic;

		JIPParamFile p1 = new JIPParamFile("file", false, true);
		p1.setDescription("File with the histogram DB");	
		
		JIPParamString p2 = new JIPParamString("lt", false, false);
		p2.setDescription("Result of the recognizing process");

		addParam(p1);
		addParam(p2);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (!(img instanceof JIPBmpColor)) 
			throw new JIPException("FLearnLT can not be applied to this image format");

		String filename=getParamValueString("file");
		int numHistos, histoLength;
		char letra;
		ArrayList<Character> listChar = new ArrayList<Character>();
		ArrayList<int[]> histoOrig = new ArrayList<int[]>();
		int[] histo;

		// Read the histograms stored in a file (from FLearnLT)
		try{
			RandomAccessFile inFile = new RandomAccessFile(filename, "rw");
			numHistos = inFile.readInt();
			for (int h=0; h<numHistos; h++) {
				letra=inFile.readChar();
				listChar.add(letra);
				histoLength=inFile.readInt();
				histo=new int[histoLength];
				for (int i=0; i<histoLength; i++) {
					histo[i]=inFile.readInt();					
				}
				histoOrig.add(histo);
			}
			inFile.close();
		} catch(IOException e){System.out.println(e);}

		JIPImage res;
		JIPFunction func;

		func = new FEqualize();
		res = func.processImg(img);

		func = new FRGBToColor();
		func.setParamValue("format", "HSB");
		img = func.processImg(res);

		func = new FSegmentHSB();
		func.setParamValue("h", 0.45f);
		func.setParamValue("herror", 0.5f);
		func.setParamValue("s", 0.4f);
		func.setParamValue("serror", 0.4f);
		func.setParamValue("b", 0.15f);
		func.setParamValue("berror", 0.28f);
		res = func.processImg(img);

		func = new FClousure();
		func.setParamValue("ee", "Images//ee.txt");
		img = func.processImg(res);

		func = new FDivideBlobs();
		res = func.processImg(img);

		func = new FHistoCirc();
		img = func.processImg(res);
		
		ArrayList<int[]> histoList=(ArrayList<int[]>)func.getParamValueObj("histo");
		
		// Now, a comparison between every histogram of the current image and the histograms
		// from the file are done. 
		String result = "";
		for (int[] h1 : histoList) {
			double best=Double.MAX_VALUE, dist;
			int bestInd=-1;
			for (int i=0; i<histoOrig.size(); i++) {
				dist = compareHistos(h1, histoOrig.get(i));
				if (best > dist) {
					best=dist;
					bestInd=i;
				}
			}
			result += listChar.get(bestInd).toString();
		}
		System.out.println(result);
		setParamValue("lt", result);

		return img;
	}
		
		//Returns the Euclidean distance between two histograms
		private double compareHistos (int[] h1, int[] h2){
			double suma=0;
			for (int i=0; i< h1.length; i++)
				suma += Math.pow(h1[i]-h2[i],2);		
			
			return (Math.sqrt(suma));
		}
}
