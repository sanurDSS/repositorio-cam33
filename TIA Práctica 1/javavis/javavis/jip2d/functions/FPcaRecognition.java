/*
 * Created on 06-feb-2006
 *
 * Autor: Carlos Rico Avendao
 * Mail: cra9@alu.ua.es
 */

package javavis.jip2d.functions;

import cern.colt.matrix.impl.*;
import cern.colt.matrix.*;
import cern.colt.matrix.linalg.*;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFile;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;

/**
 * Recognition step of PCA. It shows the image number with a lowest distance
 * to the input image
 * @author Miguel
 */
public class FPcaRecognition extends JIPFunction{
	private static final long serialVersionUID = 5342769621646467355L;

	public FPcaRecognition() {
		name = "FPcaRecognition";
		description = "Applies the Pca Recognition step";
		groupFunc = FunctionGroup.Pca;
		
		JIPParamFile p1 = new JIPParamFile("BD", false, true);
		p1.setDescription("File which has the eigenSpace");
		p1.setDefault("eigenInformation.pca");
		addParam(p1);
	}
	
	public JIPImage processImg(JIPImage img) throws JIPException {
		ImageType t = img.getType();
		if (t != ImageType.BYTE && t != ImageType.FLOAT && t != ImageType.SHORT) 
			throw new JIPException("Pca can not be applied to this image format");
		
		String fichBD = getParamValueString("BD");
		
		//Get image dimensions (all views with equal dimensions)
		int w = img.getWidth(); // width
		int h = img.getHeight(); // height
		int N = w * h; // Image dimension: N
		
		/************** Get the vectorized samples set *****************/
		// Each vectorized sample is a column in the samples matrix X
		DenseDoubleMatrix1D I = new DenseDoubleMatrix1D(N);

		int n = 0;
		// For each pixel in the image
		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++)  
				I.set(n++, ((JIPImgBitmap)img).getPixel(0, x, y));
		
		/************** Read the data base *****************/
		try{
			DataInputStream inFichero = new DataInputStream (new FileInputStream(fichBD));
			
			// Read sizes
			int M = inFichero.readInt();
			int k = inFichero.readInt();
			
			// Read the mean
			DenseDoubleMatrix1D media = new DenseDoubleMatrix1D(N);
			for (n = 0; n < N; n++)
				media.set(n, inFichero.readDouble());
			
			// Read eigenVectors
			DenseDoubleMatrix2D eVectores = new DenseDoubleMatrix2D(N, k);
			for (int i = 0; i < k; i++) 
				for (n = 0; n < N; n++) 
					eVectores.set(n, i, inFichero.readDouble());
			
			// Read eigenvalues
			DenseDoubleMatrix1D eValores = new DenseDoubleMatrix1D(k);
			for(int i=0; i<k; i++)
				eValores.set(i, inFichero.readDouble());
			
			// Read new coordinates
			DenseDoubleMatrix2D imgCoord = new DenseDoubleMatrix2D(k, M);
			for (int m = 0; m < M; m++)
				for (int i = 0; i < k; i++)
					imgCoord.set(i, m, inFichero.readDouble());			
			
			inFichero.close();
			
			/********** Transform the original image into the eigenspace ********/
			Algebra alg = new Algebra();
			DoubleMatrix2D eVt;
			DoubleMatrix1D gNewCoordinates;
			
			// Substract the mean
			for (n = 0; n < N; n++)
				I.set(n, I.get(n)-media.get(n));
			
			// Multiply by the eigenVectors
			// Transpose eVectores
			eVt = eVectores.viewDice();
			gNewCoordinates = alg.mult(eVt, I);
			
			/******** Calculate the closest original image*******/
			// We use the euclidean distance
			double distMin = distance(gNewCoordinates, imgCoord, 0);
			double distAux;
			int imgChoose = 0;
			for (int i=1; i< M ; i++) {
				distAux = distance(gNewCoordinates, imgCoord, i);
				if(distAux < distMin) {
					distMin = distAux;
					imgChoose = i;
				}
			}
			System.out.println("The image with closest distance: "+imgChoose+" Distance: "+distMin);	
		}
		catch(IOException ex){
			throw new JIPException("Some error with the input file: "+ex);
		}
		return img;
	}

	/**
	 * Euclidean distance from the input image to a given DB image
	 *  
	 * @param Ie, input image
	 * @param Ibd, BD images
	 * @param numImg, index of the image in the DB
	 * @return double Euclidean distance
	 */
	public double distance(DoubleMatrix1D Ie, DenseDoubleMatrix2D Ibd, int numImg){
		double dist=0.0;
		
		for(int i=0; i<Ie.size(); i++)
			dist+=Math.pow(Ie.get(i)-Ibd.get(i,numImg), 2.0);

		return Math.sqrt(dist);
	}
}
