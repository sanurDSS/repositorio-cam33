package javavis.jip2d.functions;

import java.awt.Image;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.util.ArrayList;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.JIPToolkit;
import javavis.base.parameter.JIPParamBool;
import javavis.base.parameter.JIPParamDir;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.JIPSequence;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.util.HistoDistances;


/**
*Calculates  distances of the input directory with the image BD, it is used different
*distances like L1 norm, L2 norm, Kullback-Leiber divergence and Jeffrey divergence and 
*different values like number of bins to compare the results. Functions like
*FCalcHistoBD and FSearchImage are used to simplify this function. The results are 
*written in some files depending of the bin number (disc) and are arranged to export it
*to a datasheet.
*<ul><B>Input parameters:</B><BR>
**<li>img: Input sequence to process<BR>
*<li>type: Type of the image<BR>
*<li>disc: Discretization (number of bins)<BR>
*<li>algorithm: Distance to use<BR>
*<li>createBD: Creates the histogram DB<BR>
*<li>dir: Directory with the DB images<BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>The image with the result (only one distance is used)
*<li>or a sequence of images (one for each distance).<BR>
*</ul>
*/

public class FGroupImages extends JIPFunction { 
	private static final long serialVersionUID = -3221456052818528739L;

	public FGroupImages(){
		super();
		name = "FGroupImages";
		description = "It generates a matrix with the distances between a image BD";
		groupFunc = FunctionGroup.ImageBD;

		JIPParamList p1 = new JIPParamList("type", false, true);
		String []paux = new String[3];
		paux[0]="RGB";
		paux[1]="YCbCr";
		paux[2]="HSI";
		p1.setDefault(paux);
		p1.setDescription("Type of the image");
		JIPParamInt p2 = new JIPParamInt("disc", false, true);
		p2.setDefault(20);
		p2.setDescription("Discretization (number of bins)");
		JIPParamList p3 = new JIPParamList("algorithm", false, true);
		String []palg = new String[5];
		palg[0]="L1";
		palg[1]="L2";
		palg[2]="Jeffrey-divergence";
		palg[3]="Kullback-Leibler divergence";
		palg[4]="All";
		p3.setDefault(palg);
		p3.setDescription("Type of distance to use");
		JIPParamBool p4 = new JIPParamBool("createBD", false, true);
		p4.setDescription("Histograms BD to create?");
		p4.setDefault(false);	
		JIPParamDir p5 = new JIPParamDir("dir", false, true);
		p5.setDescription("Directory to process");
		
		addParam(p1);
		addParam(p2);
		addParam(p3);
		addParam(p4);
		addParam(p5);
				
	}
	
	public JIPImage processImg(JIPImage img) throws JIPException {
		throw new JIPException("FGroupImage must be applied to the complete sequence");
	}
	
		
	public JIPSequence processSeq(JIPSequence seq) throws JIPException {
		String dir = getParamValueString("dir");
		String fileBD = null;
		int disc = getParamValueInt("disc");
		String tipo = getParamValueString("type");
		ArrayList<String> nomImages = new ArrayList<String>();
		JIPSequence secuencia = new JIPSequence();

		//creates the JIPSquence with the images of the directory
		doProcessing(secuencia,dir,nomImages);
				
		//creates the name of the file where write the results 
		if(tipo.equals("RGB"))
			fileBD = "outMatrizRGB"+disc;
		else
			if(tipo.equals("HSI"))
				fileBD = "outMatrizHSI"+disc;
			else
				if(tipo.equals("YCbCr"))
					fileBD = "outMatrizYCbCr"+disc;

		return createSequence (secuencia,nomImages,dir,fileBD,tipo,disc);
	}
	
	
	/**
	 * doProcessing: creates a JIPSequence of the directory, the images can be directly in the directory
	 * or in the directory could be other folders with the images (only this two cases)
	 * @param secuencia JIPSequence of the images
	 * @param dir Directory where are the images
	 * @param nomImages ArrayList with the name of the images
	 */
	public void doProcessing (JIPSequence secuencia, String dir, 
			ArrayList<String> nomImages) throws JIPException {
		File f = new File(dir);
		//Get the names of files and directories in the current directory
		String []clusters = f.list();
		String []images;
		JIPImage imgAux=null;
		int cont=0;
		
		secuencia.setName("Images processed");
		for (int i=0; i<clusters.length; i++)  {
			String group=dir+File.separator+clusters[i];
			File f2 = new File(group);
			//only processes the directories
			if (f2.isDirectory())  {
				images=f2.list();
				//processes all the images in the directory
				for (int pic=0; pic<images.length; pic++)  {
					String fileImg=group+File.separator+images[pic];
					Image imgAWT = JIPToolkit.getAWTImage(fileImg);
					if (imgAWT != null) {
						imgAux=JIPToolkit.getColorImage(imgAWT);
						imgAux.setName(images[pic]);
						secuencia.addFrame(imgAux);
						nomImages.add(images[pic]);
						cont++;
					}
				}
			}
			else {
				//Processes all the images in the directory
				Image imgAWT = JIPToolkit.getAWTImage(group);
				if (imgAWT != null) {
					imgAux=JIPToolkit.getColorImage(imgAWT);
					imgAux.setName(clusters[i]);
					secuencia.addFrame(imgAux);
					nomImages.add(clusters[i]);
					cont++;
				}
			}
		}
	}

	/**
	 * CrearBD: creates a file with the histograms of the images
	 * @param dir Directory to process
	 * @param disc Number of bins
	 * @param tipo Type of the image
	 * @param fileBD File to store the histograms
	 */
	public void createBD (String dir, int disc, String tipo, String fileBD) throws JIPException {
		FCalcHistoBD chBD = new FCalcHistoBD();
		chBD.setParamValue("dir",dir);
		chBD.setParamValue("disc",disc);
		chBD.setParamValue("fileBD",fileBD);
		chBD.setParamValue("type",tipo);
		chBD.processImg(null);
	}
	
	/**
	 * process: creates a sequence of the result image (only applies one distance)
	 * or result images (applies all the distances) to the directory with the images
	 * @param secuencia JIPSequence of the images from the directory to process
	 * @param nomImages ArrayList with the names of the images
	 * @param dir Directory to process
	 * @param fileBD File with the histograms
	 * @param tipo Type of the image
	 * @param disc Number of bins
	 * @return JIPSequence Sequence of the images with the results
	 */
	public JIPSequence createSequence (JIPSequence secuencia, 
			ArrayList<String> nomImages, String dir, String fileBD, 
			String tipo, int disc) throws JIPException {
		String fileBDPruebas = "ResulMatriz.txt";
		String algoritmo = getParamValueString("algorithm");
		String []palg = new String[4];
		palg[0]="L1";
		palg[1]="L2";
		palg[2]="Jeffrey-divergence";
		palg[3]="Kullback-Leibler divergence";
		float [][][] histograma1;
		boolean createBD = getParamValueBool("createBD");		
		
		//creates the histograms BD if it's necessary
		if(createBD)
			createBD(dir,disc,tipo,fileBD);
		
		//Now, gets the image histogram BD 
		FileInputStream fos=null;
		ObjectInputStream oos=null;
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<float[][][]> histograms = new ArrayList<float[][][]>();
		try {
			fos = new FileInputStream(fileBD);
			oos = new ObjectInputStream(fos);
			while (true) {
				names.add(oos.readUTF());
				histograms.add((float[][][])oos.readObject());
			}
		}
		catch (EOFException e) {} // This is just to reach the end of file
		catch (ClassNotFoundException e) {
			System.out.println(e);
		} 
		catch (FileNotFoundException e) {
			throw new JIPException("GroupImages: file "+getParamValueString("imageBD")+" not found");
		}
		catch (IOException e) {
			throw new JIPException("GroupImages: a Input/Output exception ocurred");
		}
		
		FCalcHistoColor chc = new FCalcHistoColor();
		chc.setParamValue("disc", disc);
		chc.setParamValue("type",tipo);
		

		double [][] distancias = new double[secuencia.getNumFrames()][histograms.size()];
		JIPImage resul = JIPImage.newImage(secuencia.getNumFrames(),histograms.size(),ImageType.FLOAT);
		PrintWriter pw=null;
		int posicion=-1;
		
		if (!algoritmo.equals("All")) {
			try {
				pw = new PrintWriter (new FileWriter(fileBDPruebas));
				pw.println("Type of the image: "+tipo+"\t\tDistance: "+algoritmo);
			} catch (IOException e)  {
				throw new JIPException("GroupImages: a Input/Output exception ocurred");
			}
			
			for(int i=0;i<secuencia.getNumFrames();i++) {
				JIPImage imgMatriz = secuencia.getFrame(i);

				//searchs the histogram of the image in the array
				posicion = searchPosition(imgMatriz,names);
				histograma1 = (float[][][])histograms.get(posicion);

				//writes in the result image the type and the distance used
				resul.setName("Result->  type: "+tipo+" distance: "+algoritmo);

				for(int j=0;j<histograms.size();j++) {
					if(algoritmo.equals("L1"))
						distancias[i][j] = HistoDistances.calcL1(histograma1,(float[][][])histograms.get(j));
					else
						if(algoritmo.equals("L2"))
							distancias[i][j] = HistoDistances.calcL2(histograma1,(float[][][])histograms.get(j));
						else
							if(algoritmo.equals("Jeffrey-divergence"))
								distancias[i][j] = HistoDistances.jeffrey(histograma1,(float[][][])histograms.get(j));
							else
								if(algoritmo.equals("Kullback-Leibler divergence"))
									distancias[i][j] = HistoDistances.kullbackLeibler(histograma1,(float[][][])histograms.get(j));

					//writes in the file the distance calculated				
					pw.print(" "+(float)distancias[i][j]);
				}
				pw.println();
			}
			//closes the file
			pw.close();
			//put the distances calculated in the result image
			introducePixels(resul,secuencia.getNumFrames(),histograms.size());
			
			FOpenWindow fopw = new FOpenWindow();
			//opens a window with the result image
			fopw.processImg(resul);
			return secuencia;
		}
		//applies all the distances
		else  {
			try {
				pw = new PrintWriter (new FileWriter(fileBDPruebas));
			} catch (IOException e)  {
				throw new JIPException("GroupImages: a Input/Output exception ocurred");
			}
			for(int m=0;m<4;m++) { //es 4 pq es el num de algoritmos que tenemos
				pw.println("Type of the image: "+tipo+"\t\tDistance: "+palg[m]);
						
				for(int i=0;i<secuencia.getNumFrames();i++) {	
					JIPImage imgMatriz = secuencia.getFrame(i);

					//searchs the histogram of the image in the array
					posicion=searchPosition(imgMatriz,names);
					histograma1 = (float[][][])histograms.get(posicion);

					for(int j=0;j<histograms.size();j++) {
						if(palg[m].equals("L1"))
							distancias[i][j] = HistoDistances.calcL1(histograma1,(float[][][])histograms.get(j));
						else
							if(palg[m].equals("L2"))
								distancias[i][j] = HistoDistances.calcL2(histograma1,(float[][][])histograms.get(j));
							else
								if(palg[m].equals("Jeffrey-divergence"))
									distancias[i][j] = HistoDistances.jeffrey(histograma1,(float[][][])histograms.get(j));
								else
									if(palg[m].equals("Kullback-Leibler divergence"))
										distancias[i][j] = HistoDistances.kullbackLeibler(histograma1,(float[][][])histograms.get(j));
						//writes in the file the distance calculated								
						pw.print(" "+(float)distancias[i][j]);
					}
					pw.println();
				}
			}
			//closes the file
			pw.close();
			
			ArrayList<JIPImage> imagenes = new ArrayList<JIPImage>();
			//put the distances calculated in the result images
			introduceAllPixels(imagenes,secuencia.getNumFrames(),secuencia.getNumFrames(),tipo);
			
			//creates the sequence of the result images
			JIPSequence secuenciaAlg = new JIPSequence((JIPImage)imagenes.get(0));
			for(int i=1;i<imagenes.size();i++)
				secuenciaAlg.addFrame((JIPImage)imagenes.get(i));
			secuenciaAlg.setName("Results");
			return secuenciaAlg;
		}
	}

	/**
	 * searchPosition: searchs a position of an image in the array of histograms
	 * @param imgMatriz JIPImage
	 * @param names ArrasList with the names of the images
	 * @return int Position in the array
	 */
	public int searchPosition(JIPImage imgMatriz,ArrayList<String> names) {
		int pos=-1;
		String nombreImg = imgMatriz.getName();
		String nomAux;
		
		for(int i=0;i<names.size();i++) {
			nomAux = names.get(i);
			if(nomAux.endsWith(nombreImg)) {
				pos=i;
				i=names.size();
			}
		}
		return pos;
	}
	
	
	/**
	 * MaxMin: gets the maximum and the minimum of the calculated values
	 * of the image to standardize in [0..1]
	 * @param maxmin ArrayList with these values
	 */
	public void maxMin(ArrayList<Double> maxmin) throws JIPException {
		double Max,Min;
		Min=Double.MAX_VALUE;
		Max=Double.MIN_VALUE;
		FileReader fileR = null;
		
		try {
			fileR = new FileReader("ResulMatriz.txt");
			StreamTokenizer st = new StreamTokenizer(fileR);
    						
			while(st.nextToken() != StreamTokenizer.TT_EOF)  {	
				switch(st.ttype) {
					case StreamTokenizer.TT_NUMBER:
		        		if(st.nval>=Max)
		        			Max=st.nval;
		        		if(st.nval<=Min)
		        			Min=st.nval;
						break;
				}
			}
			fileR.close();
		} catch (Exception e)  {
			throw new JIPException("GroupImages: a Input/Output exception ocurred");
		}
		
		maxmin.add(Max);
		maxmin.add(Min);
	}
	
	/**
	 * MaxMinAll: gets the maximum and the minimum of the calculated values
	 * of all the images to standardize in [0..1]
	 * @param maxmin ArrayList with these values
	 */
	public void maxMinAll(ArrayList<Double> maxmin) throws JIPException {
		String []palg = new String[4];
		palg[0]="L1";
		palg[1]="L2";
		palg[2]="Jeffrey-divergence";
		palg[3]="Kullback-Leibler";
		int cont = 0;
		double Max,Min;
		Min=Double.MAX_VALUE;
		Max=Double.MIN_VALUE;
		FileReader fileR = null;
		
		try {
			fileR = new FileReader("ResulMatriz.txt");
			StreamTokenizer st = new StreamTokenizer(fileR);
    						
			while(st.nextToken() != StreamTokenizer.TT_EOF)  {	
				switch(st.ttype) {
					case StreamTokenizer.TT_WORD:
						if((st.sval).equals(palg[cont])) {
							if(cont!=0) {
								maxmin.add(Max);
								maxmin.add(Min);
							}
							if(cont==3)
								st.nextToken();
							cont++;
							Min=Double.MAX_VALUE;
							Max=Double.MIN_VALUE;
						}
						break;							
					case StreamTokenizer.TT_NUMBER:
		        		if(st.nval>=Max)
		        			Max=st.nval;
		        		if(st.nval<=Min)
		        			Min=st.nval;
						break;
				}
			}
			maxmin.add(Max);
			maxmin.add(Min);
			fileR.close();
		} catch (Exception e)  {
			throw new JIPException("GroupImages: a Input/Output exception ocurred");
		}
		
	}
	
	/**
	 * IntroducePixels: put the values of the distances calculated in the result image
	 * @param resul Result image
	 * @param numFil Number of rows
	 * @param numCol Number of columns
	 */
	public void introducePixels(JIPImage resul,int numFil,int numCol) throws JIPException{
		double min,max;
		double xPrima = 0.0;
		int fila,columna;
		fila=columna=0;
		FileReader fr = null;
		ArrayList<Double> maxmin = new ArrayList<Double>();
		
		maxMin(maxmin);
		
		max = maxmin.get(0);
		min = maxmin.get(1);
	
		try {
			fr = new FileReader("ResulMatriz.txt");
			StreamTokenizer st = new StreamTokenizer(fr);
			while(st.nextToken() != StreamTokenizer.TT_EOF)  {	
				switch(st.ttype) {
					case StreamTokenizer.TT_NUMBER:
						//modifies x for x' (standardized in [0..1])
						if(st.nval != 0.0)
							xPrima=(st.nval-min)/(max-min);
						else
							xPrima = 0.0;
						if(columna<(numCol-1)) {
							((JIPImgBitmap)resul).setPixel(fila,columna,xPrima);
							columna++;
						}
						else {
							columna=0;
							fila++;
						}
						break;
				}
			}
			//closes the file
			fr.close();
		} catch (Exception e)  {
			throw new JIPException("GroupImages: a Input/Output exception ocurred");
		}
	}

	/**
	 * IntroduceAllPixels: put the values of the distances calculated in the result images
	 * @param lista_imagenes ArrayList with the result image of each distance
	 * @param numFil Number of rows
	 * @param numCol Number of columns
	 * @param tipo Type of the image
	 */
	public void introduceAllPixels (ArrayList<JIPImage> lista_imagenes,
			int numFil, int numCol, String tipo) throws JIPException {
		String []palg = new String[4];
		palg[0]="L1";
		palg[1]="L2";
		palg[2]="Jeffrey-divergence";
		palg[3]="Kullback-Leibler";
		double min,max;
		double xPrima = 0.0;
		int fila,columna,cont,contImg;
		fila=columna=cont=contImg=0;
		min=max=0.0;
		FileReader fr = null;
		JIPImgBitmap image=null;
		ArrayList<Double> maxmin = new ArrayList<Double>();
		
		maxMinAll(maxmin);
		
		try {
			fr = new FileReader("ResulMatriz.txt");
			StreamTokenizer st = new StreamTokenizer(fr);
			while(st.nextToken() != StreamTokenizer.TT_EOF)  {	
				switch (st.ttype) {
					case StreamTokenizer.TT_WORD:
						if ((st.sval).equals(palg[cont])) {
							if (cont!=0) {
								image.setName(tipo+" "+palg[cont-1]);
								lista_imagenes.add(image);
							} 
							image = (JIPImgBitmap)JIPImage.newImage(numFil,numCol,ImageType.FLOAT);
							
							max = maxmin.get(contImg);
							min = maxmin.get(contImg+1);
							
							if(cont==3)
								st.nextToken();
							contImg=contImg+2;
							cont++;
							columna=fila=0;
						}
						break;						
					case StreamTokenizer.TT_NUMBER:
						//modifies x for x' (standardized in [0..1])
						if(st.nval != 0.0)
							xPrima=(st.nval-min)/(max-min);
						else
							xPrima = 0.0;
						if(columna<(numCol-1)) {
							image.setPixel(fila,columna,xPrima);
							columna++;
						}
						else {
							columna=0;
							fila++;
						}
						break;
				}
			}
			image.setName(tipo+" "+palg[3]+" divergence");
			lista_imagenes.add(image);
			fr.close();
		} catch (Exception e)  {
			throw new JIPException("GroupImages: a Input/Output exception ocurred");
		}
	}
}