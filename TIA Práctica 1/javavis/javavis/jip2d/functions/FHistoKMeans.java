package javavis.jip2d.functions;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Random;

import javavis.base.JIPException;
import javavis.base.parameter.JIPParamBool;
import javavis.base.parameter.JIPParamDir;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.util.Distances;
import javavis.jip2d.util.HistoDistances;


/**
*Calculates the histograms of the input image and compares them to the ones in the
*image BD (got from the FCalcHistoBD). Using the L1 norm, the most similar image in
*the BD is returned and, depending of the perc parameter, some additional images
*can be returned. It does not process an image, but the complete sequence (but only
*the first image is processed). The returned sequence is ordered by distance: 
*first, the image with the less distance, then the next one
* and so on until the percentage parameter is reached.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input sequence to process<BR>
*<li>dir: Directory with the images to group<BR>
*<li>histo: Creates histograms when it's necessary<BR>
*<li>numk: Number of groups<BR>
*<li>type: Type of the image<BR>
*<li>algorithm: Distance to use<BR>
*<li>findBest: Applies KMedias until the result is acceptable<BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*</ul>
*/

public class FHistoKMeans extends JIPFunction{
	private static final long serialVersionUID = 7556500542430306880L;

	public FHistoKMeans() {
		super();
		name = "FHistoMeans";
		description = "Groups the image histograms in k groups";
		groupFunc = FunctionGroup.ImageBD;

		JIPParamDir p1 = new JIPParamDir("dir", false, true);
		p1.setDescription("Directory to process");
		JIPParamBool p2 = new JIPParamBool("histo", false, true);
		p2.setDescription("Create histograms");
		p2.setDefault(false);
		JIPParamInt p3 = new JIPParamInt("numk", false, true);
		p3.setDescription("Number of groups");
		p3.setDefault(2);
		JIPParamList p4 = new JIPParamList("type", false, true);
		String []paux = new String[3];
		paux[0]="RGB";
		paux[1]="YCbCr";
		paux[2]="HSI";
		p4.setDefault(paux);
		p4.setDescription("Type of the image");
		JIPParamList p5 = new JIPParamList("algorithm", false, true);
		String []palg = new String[4];
		palg[0]="L1";
		palg[1]="L2";
		palg[2]="Jeffrey-divergence";
		palg[3]="Kullback-Leibler divergence";
		p5.setDefault(palg);
		p5.setDescription("Type of distance to use");
		JIPParamBool p6 = new JIPParamBool("findBest", false, true);
		p6.setDescription("Find the best groups of K");
		p6.setDefault(false); 
		
		addParam(p1);
		addParam(p2);
		addParam(p3);
		addParam(p4);
		addParam(p5);
		addParam(p6);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		String dir = getParamValueString("dir");
		boolean createHis = getParamValueBool("histo");
		String tipo = getParamValueString("type");
		int k = getParamValueInt("numk");
		int disc = 20; //default value
		String fileBD = "outkmedias";
		boolean findBest = getParamValueBool("findBest");
		boolean right = true;
		File f;
		String pathDes, image;
		
		//check it's necessary to create histograms
		if(createHis)
			createDB(dir,disc,tipo,fileBD);
		
		//gets the image histogram BD
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
			throw new JIPException(e.getMessage());
		} 
		catch (FileNotFoundException e) {
			throw new JIPException("FKMedias: file "+getParamValueString("fileBD")+" not found");
		}
		catch (IOException e) {
			throw new JIPException("FKMedias: a Input/Output exception ocurred");
		}

		//creates an array of Distancia
		Distances[] v_distancias = new Distances[names.size()];

		//applies the algorithm until the result is correct
		do {
			right = kmeans(names,histograms,v_distancias);
		} while(findBest && right);
		

		//creates the folders with the new groups
		//(there are any problem if the images and the new folders are
		//in the same hard disk)
		for (int group=1; group<=k; group++) {
			 pathDes = "group"+group;
			 f = new File(pathDes);
			 
			 if (!f.mkdirs()) 
				 throw new JIPException ("FHistoKMeans: can not create a dir");
			 
			 for (int j=0; j<names.size(); j++) {
			 	if (v_distancias[j].getGroup()==group) {
			 		image = names.get(j).substring(names.get(j).lastIndexOf(File.separator));
			 		pathDes = "group"+group+image;
			 		f = new File(pathDes);
			 	}
			 }
		}
		return img;		
	}

	
	/**
	 * Kmedias: is the main function of KMedias
	 * @param names ArrayList with the names of the images
	 * @param histograms ArrayList with te histograms of the images
	 * @param v_distancias Array with the images with their distance, name and group
	 * @return boolean True if the group is acceptable, False when in some group there
	 * is only a image (result not acceptable)
	 */
	public boolean kmeans(ArrayList<String> names, ArrayList<float[][][]> histograms,Distances[] v_distancias) 
			throws JIPException{
		int numImg = names.size();
		int k = getParamValueInt("numk");
		Distances[] v_distanciasAnt = new Distances[numImg];
		boolean converge = false;
		int iteracion = 0;
		int []positions = new int[k];
		ArrayList<float[][][]> averageHisto = new ArrayList<float[][][]>();

		//reserves the memory of the positions of the arrays
		for(int i=0;i<numImg;i++) {
			v_distancias[i]=new Distances();
			v_distanciasAnt[i]=new Distances();
		}
		
		//select the initial histograms by chance (the same number as k)
		selectInitialHistograms(positions,numImg,k);

		//copies the base histograms to start KMedias
		for(int i=0;i<k;i++)
			averageHisto.add(histograms.get(positions[i]));

		//calculates the distances 
		distance(names,histograms,averageHisto,v_distancias,k);
		
		//calculates the averages histograms
		calculateAveragesHisto(names,histograms,averageHisto,v_distancias,k);
		
		do {
			//copies the previous distances
			copyDistances(v_distancias,v_distanciasAnt);

			//calculates the distances
			distance(names,histograms,averageHisto,v_distancias,k);

			//calculates the averages histograms
			calculateAveragesHisto(names,histograms,averageHisto,v_distancias,k);

			//compares the arrays, if are the same it's the end
			//if aren't the same continues
			converge = compareDistances(v_distancias,v_distanciasAnt);
			iteracion++;
		}while(converge==false);
		
		//checks the number of the images in each group		
		if(countImgs(v_distancias,k))
			return true;
		else 
			return false;
	}
	
	/**
	 * CountImgs: counts the images that belongs to a concrete group
	 * in each group must have more than one, because if there are 
	 * only one the groups aren't correct
	 * @param v_distancias Array with the images with their distance, name and group 
	 * @param k Number of groups
	 * @return boolean True if only there is an image, False when there are more than one image
	 */
	public boolean countImgs(Distances[] v_distancias,int k) {
		int[] numImgs = new int[k];
		
		for(int group=0;group<k;group++) {
			for(int j=0;j<v_distancias.length;j++) {
				if(v_distancias[j].getGroup()==(group+1))
					numImgs[group]++;
			}
			if(numImgs[group]==1)
				return true;
		}
		return false;
	}
	
	/**
	 * CreateDB: creates a file with the histograms of the images
	 * @param dir Directory to process
	 * @param disc Number of bins
	 * @param tipo Type of the image
	 * @param fileBD File to store the histograms
	 */
	public void createDB (String dir,int disc, String tipo, String fileBD) throws JIPException {
		FCalcHistoBD chBD = new FCalcHistoBD();
		chBD.setParamValue("dir",dir);
		chBD.setParamValue("disc",disc);
		chBD.setParamValue("fileBD",fileBD);
		chBD.setParamValue("type",tipo);
		chBD.processImg(null);
	}
	
	/**
	 * Minimum: searchs the minimum of an array
	 * @param vector 
	 */
	public int minimum(double[] vector) {
		int position = -1;
		double valor = 0.0;
		
		valor = 100;
		for(int m=0;m<vector.length;m++)
			if(valor>vector[m]) {
				valor = vector[m];
				position = m;
			}

		return position;
	}
	
	/**
	 * CopyDistances: copies an array to another array
	 * @param v_distancias Array with the images with their distance, name and group
	 * @param v_distanciasAnt Previous array with the images with their distance, name and group
	 */
	public void copyDistances(Distances[] v_distancias,Distances[] v_distanciasAnt) {
		for(int m=0;m<v_distancias.length;m++)
				v_distanciasAnt[m].copyDis(v_distancias[m]);
	}
	
	/**
	 * selectInitialHistograms: selects two base histograms with a random function
	 * @param positions Positions of the arrays where are the base histograms
	 * @param numImg Number of images
	 * @param k Number of groups
	 */
	public void selectInitialHistograms(int[] positions, int numImg, int k) {
		ArrayList<Integer> lista = new ArrayList<Integer>();
		int tamanyo=0;
		Integer numAux;
		Random r = new Random();
		
		//one element
		lista.add(r.nextInt(numImg));
		//two elements
		while(lista.size()<2) {
			numAux = r.nextInt(numImg);
			if(numAux.compareTo(lista.get(0))<0)
				lista.add(0,numAux);
			else
				if(!numAux.equals(lista.get(0)))
					lista.add(numAux);
		}
				
		//more than 2 elements
		while(lista.size()<k) {
			numAux = r.nextInt(numImg);
			tamanyo=lista.size();
			for(int i=0;i<tamanyo;i++) {
				if(numAux.equals(lista.get(i)))
					i=tamanyo;
				else {
					if(numAux.compareTo(lista.get(i))<0) {
						if(i==0) { //first position
							lista.add(0,numAux);
							i=tamanyo;
						}
						else { //whatever intermediate position
							lista.add(i,numAux);
							i=tamanyo;
						}					
					}
					//last position
					if(i==(tamanyo-1) && numAux.compareTo(lista.get(i))>0) {
						lista.add(numAux);
						i=tamanyo;
					}
				}
			}
		}
		
		for(int i=0;i<lista.size();i++)
			positions[i]=lista.get(i);
	}
	
	/**
	 * Distance: calculates the distance between two histograms
	 * @param nomImg ArrayList with the name of the images
	 * @param aux_histo ArrayList with the histograms of the images
	 * @param averageHisto ArrayList with the average histograms
	 * @param v_distancias Array with the images with their distance, name and group
	 * @param k Number of groups
	 */
	public void distance(ArrayList<String> nomImg, ArrayList<float[][][]> aux_histo, 
			ArrayList<float[][][]> averageHisto, Distances[] v_distancias,int k) throws JIPException {
		double[] distance = new double[k];
		int position = 0;
		
		// And now, we have to search for images with low distances
		// but we have to know the algorithm to use
		String algo = getParamValueString("algorithm");
		for(int j=0;j<nomImg.size();j++) {
		  	v_distancias[j].setNameImg(nomImg.get(j));
		  	//calculates the distance between two histograms
			for(int i=0;i<k;i++) { 
				if (algo.equals("L1"))
					distance[i] = HistoDistances.calcL1(averageHisto.get(i), aux_histo.get(j));
				else if (algo.equals("L2"))
					distance[i] = HistoDistances.calcL2(averageHisto.get(i), aux_histo.get(j));
				else if (algo.equals("Jeffrey-divergence"))
					distance[i] = HistoDistances.jeffrey(averageHisto.get(i), aux_histo.get(j));
				else if (algo.equals("Kullback-Leibler divergence"))
					distance[i] = HistoDistances.kullbackLeibler(averageHisto.get(i), aux_histo.get(j));
			}
			//searchs the minimun distance
			position = minimum(distance);

			//puts the image in the group with the minimum distance
			v_distancias[j].setDistance(distance[position]);
			v_distancias[j].setGroup(position+1);
		}
	}
	
	/**
	 * CompareDistances: compares both arrays of Distancias, if are the same returns true
	 * @param v_distancias Array with the images with their distance, name and group
	 * @param v_distanciasAnt Previous array with the images with their distance, name and group
	 * @return boolean if the arrays are the same or not
	 */
	public boolean compareDistances(Distances[] v_distancias, Distances[] v_distanciasAnt) {
		int cont = 0;
		
		for(int i=0;i<v_distancias.length;i++)
			if(v_distancias[i].isEquals(v_distanciasAnt[i]))
				cont++;
		
		if(cont==v_distancias.length)
			return true;
		else
			return false;
	}
	
	/**
	 * CalculateAveragesHisto: calculates the new average histograms of each group
	 * @param names ArrayList with the names of the images
	 * @param histograms ArrayList with the histograms of the images
	 * @param averageHisto ArrayList with the average histograms
	 * @param v_distancias Array with the images with their distance, name and group
	 * @param k Number of groups
	 */
	public void calculateAveragesHisto (ArrayList<String> names,
			ArrayList<float[][][]> histograms, ArrayList<float[][][]> averageHisto,
			Distances[] v_distancias,int k) {
		float[][][] auxHisto2 = histograms.get(0);
		float[][][] auxHisto = new float[auxHisto2.length][auxHisto2.length][auxHisto2.length];
		float numImgs = 0.0f;
		
		for(int group=1;group<=k;group++) {
			for (int i=0; i<auxHisto.length; i++) {
				for (int j=0; j<auxHisto.length; j++) {
					for (int l=0; l<auxHisto.length; l++) {
						for(int pos=0;pos<histograms.size();pos++) {
							if((v_distancias[pos].getGroup()==group) && 
											((v_distancias[pos].getNameImg()).equals(names.get(pos)))) {	
								auxHisto2 = histograms.get(pos);
								auxHisto[i][j][l]+= auxHisto2[i][j][l];
								numImgs++;
							}
						}
						auxHisto[i][j][l] = auxHisto[i][j][l] / numImgs;
						//updates values
						numImgs = 0.0f;
					}
				}
			}
			//copies the new average histogram
			averageHisto.add(auxHisto);
			//update values
			auxHisto = new float[auxHisto2.length][auxHisto2.length][auxHisto2.length];
		}
	}	
}
