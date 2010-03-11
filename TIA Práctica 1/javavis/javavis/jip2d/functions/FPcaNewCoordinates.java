/*
 * Created on 04-feb-2006
 *
 * Autor: Carlos Rico Avendao
 * Mail: cra9@alu.ua.es
 */

package javavis.jip2d.functions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFile;
import javavis.base.parameter.JIPParamFloat;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.JIPSequence;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;


import cern.colt.matrix.impl.*;
import cern.colt.matrix.*;
import cern.colt.matrix.linalg.*;


public class FPcaNewCoordinates extends JIPFunction {
	private static final long serialVersionUID = -3877706238719331222L;

	public FPcaNewCoordinates() {
		name = "FPcaNewCoordinates";
		description = "Calculating the new coordinates with PCA";
		groupFunc = FunctionGroup.Pca;
		
		JIPParamFile p1 = new JIPParamFile("BD", false, true);
		p1.setDescription("File which has the eigenSpace");
		p1.setDefault("eigenSpace.pca");
		addParam(p1);
		JIPParamFloat p2 = new JIPParamFloat("perc", false, true);
		p2.setDescription("Percentage of the accumulative total (0..1)");
		p2.setDefault(0.9f);
		addParam(p2);
	}
	
	public JIPImage processImg(JIPImage img) throws JIPException {
		throw new JIPException("Please, select 'Apply to complete sequence'");
	}
	
	public JIPSequence processSeq(JIPSequence seq) throws JIPException {
		for (int i=0; i<seq.getNumFrames(); i++) {
			ImageType t = seq.getFrame(i).getType();
			if (t != ImageType.BYTE && t != ImageType.FLOAT && t != ImageType.SHORT) 
				throw new JIPException("Pca can not be applied to this image format: some of the image in the sequence is one of an invalid type");
		}
		
		JIPSequence result = seq;
		
		//Get image dimensions (all views with equal dimensions)
		int w = seq.getFrame(0).getWidth(); // width
		int h = seq.getFrame(0).getHeight(); // height
		int N = w * h; // Image dimension: N
		
		//Almacenamos el valor del porcentaje total acumulado
		double porcentaje = getParamValueFloat("perc");
		
		//Leer datos del archivo: tamao, media, eigenvectores, eigenvalores
		String fichBD = "eigenInformation.pca";
		String fichBD2 = getParamValueString("BD");

		try{
			// Creamos el stream del fichero
			DataInputStream inFichero = new DataInputStream (new FileInputStream(fichBD));
			// First, get the number of input views (samples): M
			int M = inFichero.readInt();
			
			/************** Get the vectorized samples set *****************/
			// Each vectorized sample is a column in the samples matrix X
			DenseDoubleMatrix2D X = new DenseDoubleMatrix2D(N, M);
			
	        // Each row in X
			int n = 0;
			// For each column in X
			for (int m = 0; m < M; m++) {
				// For each pixel in the sample
				for (int y = 0; y < h; y++)
					for (int x = 0; x < w; x++)  
						// Build its associated column in X
						X.set(n++, m, ((JIPImgBitmap)seq.getFrame(m)).getPixel(0, x, y));
				n = 0;
			}			
			
			/************** Leemos los datos del fichero *****************/
			//Leemos la media
			DenseDoubleMatrix1D media = new DenseDoubleMatrix1D(N);
			for (n = 0; n < N; n++)
				media.set(n, inFichero.readDouble());
			
			//Leemos los eigenvectores
			DenseDoubleMatrix2D eVectores = new DenseDoubleMatrix2D(N, M);
			for (int m = 0; m < M; m++) 
				for (n = 0; n < N; n++) 
					eVectores.set(n, m, inFichero.readDouble());
			
			//Leemos los eigenvalores
			DenseDoubleMatrix1D eValores = new DenseDoubleMatrix1D(M);
			for(int m=0; m<M; m++)
				eValores.set(m, inFichero.readDouble());
			
			// Cerramos el stream del fichero
			inFichero.close();
		
			/************** Calculamos "k" *****************/
			//Eleccion de "k" en base al porcentaje especificado
			double acumulado = 0.0;
			double sumaNValores = 0.0;
			double sumaKValores = 0.0;
			int k = 0;
			
			//Sumamos los N eigenvalores
			for(int m=0; m<M; m++)
				sumaNValores += eValores.get(m);
			
			//Vamos incrementando "k" hasta que suepere o iguale el porcentaje
			for(int i=0; i < M && acumulado < porcentaje; i++){
				sumaKValores += eValores.get(i);
				acumulado = sumaKValores / sumaNValores;
				k = i+1;
			}
			
			/************** Calculamos las nuevas coordenadas de las imagenes *****************/
			//Matriz donde guardamos todas las nuevas coordenadas
			DenseDoubleMatrix2D GNC = new DenseDoubleMatrix2D(k, M);
			
			//Calcular las nuevas coordenadas de las imagenes en el eigenspace
			for(int m=0; m<M; m++){
				
				//Usamos una matriz (G) donde guardar las nuevas coordenadas de la imagen 
				DenseDoubleMatrix1D G = new DenseDoubleMatrix1D(N);
				
				//Copiamos los antiguos valores
				G.assign(X.viewColumn(m));
			
				//Restamos la media al vector original
				for (n = 0; n < N; n++)
					G.set(n, G.get(n)-media.get(n));
				
				//Multiplicamos el resultado anterior por la matriz eigenvectores
				Algebra alg = new Algebra();
				// Transpose eVectores
				DoubleMatrix2D eVt = eVectores.viewDice(); // k x N
				DoubleMatrix1D gNewCoordinates = alg.mult(eVt, G); // k x 1
				
				for (int i = 0; i < k; i++)
					GNC.set(i, m, gNewCoordinates.get(i));	// 1 columna de k x M			
			}
			
			System.out.println("Calcular nuevas coordenadas de las imagenes");
			
			/************** Guardamos en un fichero todos los datos calculados *****************/
			//Almacenamos en un fichero: tamao, media, eigenvectores(k), eigenvalores(k),
			//y coordenadas de las imagenes en el nuevo eigenespacio

			//Creamos el stream del fichero
			DataOutputStream outFichero = new DataOutputStream (new FileOutputStream(fichBD2));
			//FileWriter out3D = new FileWriter("3DEigenSpace.txt");
			
			//Almacenamos el tamaño (M)(k) y la media
			outFichero.writeInt(M);
			outFichero.writeInt(k);
			for (n = 0; n < N; n++)
				outFichero.writeDouble(media.get(n));

			//Almacenamos los eigenvectores en el fichero
			for (int i = 0; i < k; i++)
				for (n = 0; n < N; n++)
					outFichero.writeDouble(eVectores.get(n,i));

			
			//Almacenamos los eigenvalores en el fichero
			for(int i=0; i<k; i++){
				outFichero.writeDouble(eValores.get(i));
			}
			
			//Almacenamos las nuevas coordenadas de las imagenes
			for (int m = 0; m < M; m++)
				for (int i = 0; i < k; i++)
					outFichero.writeDouble(GNC.get(i,m));
			
			
			//Cerramos el stream del fichero
			outFichero.close();
			
		}catch(IOException ex){
			throw new JIPException("FPcaNewCoordinates: "+ex);
		}
		
		return result;
	}
}
