package javavis.jip2d.functions;

import java.io.*;
import java.awt.Image;

import javavis.base.JIPException;
import javavis.base.JIPToolkit;
import javavis.base.parameter.JIPParamBool;
import javavis.base.parameter.JIPParamDir;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.JIPSequence;


/**
*Calculates the distances of the input directory with the image BD, Different distances are used 
*like L1 norm, L2 norm, Kullback-Leibler divergence and Jeffrey divergence and different values 
*like number of bins to compare the results. It is used functions like FCalcHistoBD and FSearchImage 
*to help in this calculation. The results are written in some files depending on the number of 
*bins (disc) and are arranged to export to a datasheet like Excel (for example).
*<ul><B>Input parameters:</B><BR>
*<li>img: Input sequence to process<BR>
*<li>dir: Directory with the DB images<BR>
*<li>image: Directory with the images to prove<BR>
*<li>type: Type of the image<BR>
*<li>createBD: Creates the histogram DB<BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*</ul>
*/

public class FCompareDistances extends JIPFunction{
	private static final long serialVersionUID = -4173628485139333543L;

	public FCompareDistances() {
		super();
		name = "FCompareDistances";
		description = "Search the most similar image with all the distances";
		groupFunc = FunctionGroup.ImageBD;

		JIPParamDir p1 = new JIPParamDir("dir", false, true);
		p1.setDescription("Directory to process");
		JIPParamDir p2 = new JIPParamDir("image", false, true);
		p2.setDescription("Directory with the images to prove");
		JIPParamList p3 = new JIPParamList("type", false, true);
		String []paux = new String[3];
		paux[0]="RGB";
		paux[1]="YCbCr";
		paux[2]="HSI";
		p3.setDefault(paux);
		p3.setDescription("Type of the image");
		JIPParamBool p4 = new JIPParamBool("createBD", false, true);
		p4.setDescription("Histograms BD to create?");
		p4.setDefault(false);			
		
		addParam(p1);
		addParam(p2);
		addParam(p3);
		addParam(p4);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		
		String imageParameter = getParamValueString("image");
		String tipo = getParamValueString("type");
		boolean createBD = getParamValueBool("createBD");
		String dir = getParamValueString("dir");
		int disc=5;
		String fileBD = "out"+tipo+disc;
		
		//creates the histograms BD if it's necessary
		if(createBD)
			while(disc<=30) {
				//creates a file for each value of disc [5..30]
				createBD(img,fileBD,disc,dir,tipo);
				disc += 5;
				fileBD="out"+tipo+disc;
			}
		
		//creates an array with the images to analyze
		File f = new File(imageParameter);
		String []images = f.list();
		int nimages,contimgs;
		
		nimages=contimgs=0;
		
		//counts the number of images to prove
		for(int i=0;i<images.length;i++)
			if(images[i].endsWith(".jpg")||images[i].endsWith(".JPG"))
				nimages++;
		
		String[] imagesNames = new String[nimages];
		
		for(int j=0;j<images.length;j++)
			if(images[j].endsWith(".jpg")||images[j].endsWith(".JPG")) {
				imagesNames[contimgs]=""+images[j];
				contimgs++;
			}
		
		//calls a function which generates the test
		prove(imagesNames,tipo);
		return img;
	}
	
	/**
	 * CreateBD: creates a file with the histograms of the images
	 * @param imgAux Image to create the histogram
	 * @param fileBDAux File to store the histograms
	 * @param discAux Number of bins
	 * @param dirAux Directory to process
	 * @param tipoAux Type of the image
	 */
	private void createBD(JIPImage imgAux,String fileBDAux,int discAux,String dirAux,String tipoAux) throws JIPException {
		FCalcHistoBD chbd = new FCalcHistoBD();
		//puts the parameters to create the histogram
		chbd.setParamValue("dir",dirAux);
		chbd.setParamValue("disc",discAux);
		chbd.setParamValue("fileBD",fileBDAux);
		chbd.setParamValue("type",tipoAux);
		//calls the function FCalcHistoBD
		chbd.processImg(imgAux);
	}
	
	/**
	 * NameImgFile: writes in the file the name of the image with it's calculating the distances
	 * in function of the parameters
	 * @param imgnames Array with the names of the images
	 * @param cont The position of the current image in the array
	 * @param pw File where write the name of the image
	 */
	private void nameImgFile(String[] imgnames,int cont,PrintWriter pw) {
		pw.println();
		pw.println("Nombre imagen-> "+imgnames[cont]);
	}
	
	/**
	 * WriteFile: writes in the file the results to apply FSearchImage
	 * @param imgAux A JIPImage
	 * @param fileBD1 File with the histogram BD 
	 * @param disc1 Number of bins
	 * @param el_tipo The type of the image
	 * @param algoritmo The distance 
	 * @param pw File where write the name of the image
	 */
	private void writeFile(JIPImage imgAux,String fileBD1,int disc1,String el_tipo,String algoritmo,PrintWriter pw) throws JIPException
	{
		JIPSequence imgAux2; 
		FSearchImage siAux = new FSearchImage();
		
		//creates JIPSequence
		imgAux2 = new JIPSequence(imgAux);
		//puts the values of the parameters of FSearchImage
		siAux.setParamValue("imageBD",fileBD1);
		siAux.setParamValue("disc", disc1);
		siAux.setParamValue("perc", 0.1f); 
		siAux.setParamValue("type",el_tipo);
		siAux.setParamValue("algorithm",algoritmo);
		siAux.processSeq(imgAux2);

		//writes in the file
		if(algoritmo.equals("L1"))
			pw.print(""+disc1+"     "+siAux.getParamValueFloat("distance"));
		else {
			if(algoritmo.equals("Kullback-Leibler divergence"))
				pw.println("     "+siAux.getParamValueFloat("distance"));
			else
				pw.print("     "+siAux.getParamValueFloat("distance"));
		}
	}

	/**
	 * ReadFile: puts in order data of the file to export to Excel (for example)
	 * @param fileResultados File with the results
	 * @param tipo2 Type of the image
	 */
	public void readFile(String fileResultados, String tipo2) throws JIPException {
		String file=fileResultados;
		String fileSal;
		int bins=5;
		String eso;
		char[] esoAux;
		
		while(bins<=30) {
			try {
				StreamTokenizer st = new StreamTokenizer(new FileReader(file));
    			fileSal="Resuls"+tipo2+bins+".txt";
    			PrintWriter pwFichero = new PrintWriter (new FileWriter(fileSal));
    			pwFichero.println("Number de bins -> "+bins);
				
				while(st.nextToken() != StreamTokenizer.TT_EOF)  {	
					switch(st.ttype) {
						case StreamTokenizer.TT_WORD:
			            	break;
						case StreamTokenizer.TT_NUMBER:
			        		if(st.nval==bins) {
			        			for(int i=0;i<4;i++) {
			        				st.nextToken();
			        				eso=""+st.nval;
			        				esoAux=eso.toCharArray();		
			        				
			        				for(int j=0;j<esoAux.length;j++) {
			        					if(esoAux[j]=='.')
			        						esoAux[j]=',';
			        				}
			        				pwFichero.print(esoAux);
			        				pwFichero.print(";");
			        			}
			        			pwFichero.println();
			        		}
			        		break;
					}
				}
				pwFichero.close();
				bins= bins + 5;
			} catch (IOException e)  {
				throw new JIPException("CompareDistances: an Input/Output exception ocurred while puts in order data");
			}
		}
	}
	
	/**
	 * Prove: is the main function, it calls the other functions to calculate and put in order
	 * the results of the comparison of the images
	 * @param imgNames Array with the name of the images
	 * @param tipo2 The type of the image
	 */
	private void prove(String[] imgNames, String tipo2) throws JIPException {
		String imageParameter = getParamValueString("image"); 
		JIPImage imgAux=null;
		String fileBDPruebas = "BDPruebas"+tipo2+".txt";
		PrintWriter pw=null;
		int disc=5;
		String fileBD="out"+tipo2+disc;
		String []algoritmo = new String[4];
		algoritmo[0]="L1";
		algoritmo[1]="L2";
		algoritmo[2]="Jeffrey-divergence";
		algoritmo[3]="Kullback-Leibler divergence";
		
		//open the file
		try{
			pw = new PrintWriter (new FileWriter(fileBDPruebas));
			pw.println(tipo2);
		}catch (Exception e) {
			throw new JIPException("CompareDistances: an Input/Output exception ocurred while writes in a file");
		}
		
		for(int i=0;i<imgNames.length;i++) {
			Image imgAWT = JIPToolkit.getAWTImage(imageParameter+File.separator+imgNames[i]);
			if (imgAWT != null) {
				imgAux=JIPToolkit.getColorImage(imgAWT);
							
				//writes in the file the name of the current image
				nameImgFile(imgNames,i,pw);  
				
				//the value of disc [5..30]
				while(disc<=30) {
					//writes in the file the results of apply the four distances
					for(int num=0;num<4;num++) 
						writeFile(imgAux,fileBD,disc,tipo2,algoritmo[num],pw);
					
					//updates values of the parameters
					disc +=5;
					fileBD="out"+tipo2+disc;
				}

				//updates values of the next image
				if(disc==35) {
					disc=5;
					fileBD="out"+tipo2+disc;
				}
			  }
			} 
			//closes the file
			pw.close();

			//organizes the results in the file in order of the number of bins and the type of the image
			readFile(fileBDPruebas,tipo2);
	}
}

