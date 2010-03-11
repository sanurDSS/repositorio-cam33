package javavis.jip2d.functions;

import java.io.*;
import java.util.ArrayList;

import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFile;
import javavis.base.parameter.JIPParamString;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpColor;

/**
 * Learning process in the License Template application. It creates a file with
 * the histograms of the input image
 * @author miguel
 */
public class FLearnLT extends JIPFunction{
	private static final long serialVersionUID = -6920601139042990149L;

	public FLearnLT(){
		super();
		name="FLearnLT";
		description="Learning process in the License Template application";
		groupFunc = FunctionGroup.Applic;

		JIPParamFile p1=new JIPParamFile("fileName", true, true);
		p1.setDescription("File name");
		
		JIPParamString p2=new JIPParamString("string", false, true);
		p2.setDescription("String containing the characters in the image");
		p2.setDefault("ABCDEGHJLMNSXYZ0123456789");
		
		addParam(p1);
		addParam(p2);
	}
	
	public JIPImage processImg(JIPImage img) throws JIPException {
		if (!(img instanceof JIPBmpColor)) 
			throw new JIPException("FLearnLT can not be applied to this image format");
		String filename=getParamValueString("fileName");
		String str=getParamValueString("string");
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
		
		try {
			RandomAccessFile outFile = new RandomAccessFile(filename, "rw");	
			ArrayList<int[]> histograms = (ArrayList<int[]>)func.getParamValueObj("histo"); 
			int[]aux;
			
			if (histograms.size()!=str.length())
				throw new JIPException("FLearnLT: input string and numbers in the image must be the same");
			
			outFile.writeInt(histograms.size());
			for (int b=0; b<histograms.size(); b++) { 							
				outFile.writeChar(str.charAt(b));
				aux = histograms.get(b);			
				outFile.writeInt(aux.length);
				for (int i=0; i<aux.length; i++) {
					outFile.writeInt(aux[i]);		
				}
			}
			
			outFile.close();
		} 
		catch (IOException e) {System.out.println(e);}
		
		return img;
	}
}
		
		
	