package javavis.jip2d.functions;

import java.io.*;
import java.util.ArrayList;
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFile;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamString;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;


/**
* Makes the training of a SOM, applied to the image histograms problem.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>imageBD: file with the histogram BD.<BR>
*<li>disc: Discretization (number of bins)<BR>
*<li>rows: number of rows in the SOM <BR>
*<li>columns: number of columns in the SOM <BR>
*<li>learning: Initial learning index <BR>
*<li>iter: number of iterations <BR> 
*</ul>
*
*/

public class FSOM extends JIPFunction {
	private static final long serialVersionUID = 7243549182678063124L;

	public FSOM() {
		name = "FSOM";
		description = "Makes the training step of a SOM";
		groupFunc = FunctionGroup.ImageBD;
		
		JIPParamFile p1 = new JIPParamFile("imageBD", true, false);
		p1.setDescription("file with the histogram BD");
		addParam(p1);
		
		JIPParamInt p2 = new JIPParamInt("disc", true, false);
		p2.setDefault(20);
		p2.setDescription("Discretization (number of bins)");
		addParam(p2);
		
		JIPParamInt p3 = new JIPParamInt("rows", true, false);
		p3.setDefault(5);
		p3.setDescription("Number of rows of the SOM");
		addParam(p3);
	
		JIPParamInt p4 = new JIPParamInt("columns", true, false);
		p4.setDefault(5);
		p4.setDescription("Number of columns of the SOM");
		addParam(p4);
		
		JIPParamFloat p5 = new JIPParamFloat("learning", true, false);
		p5.setDefault(0.01f);
		p5.setDescription("Initial learning index");
		addParam(p5);
		
		JIPParamInt p6 = new JIPParamInt("iter", true, false);
		p6.setDefault(200);
		p6.setDescription("Number of iterations");
		addParam(p6);
		
		/* Number of iterations in the learning phase of the SOM */ 
		JIPParamString p7 = new JIPParamString("outputFile", true, false);
		p7.setDefault("file.html");
		p7.setDescription("File to save the results");
		addParam(p7);
	}

	public JIPImage processImg (JIPImage img) throws JIPException {
		double radio, tasaApr, errorCuant;
		Neurona nGanadora;
		ArrayList<ImageAttributes>[][] clasificacion = null;
		
		int disc = getParamValueInt("disc");
		String fileBD = getParamValueString("imageBD");
		int filasSOM = getParamValueInt("rows");
		int colsSOM = getParamValueInt("columns");
		double TAI = getParamValueFloat("learning");
		int numIter = getParamValueInt("iter");
		
		SOM som = new SOM (filasSOM, colsSOM, disc);
		
		ArrayList<ImageAttributes> histogramas = readHistograms (fileBD, disc);
		
		if (histogramas.isEmpty()) 
			throw new JIPException("DB is empty or corrupted");
		
		double radioMapa = Math.max(filasSOM, colsSOM) / 2.0;
		double cteTiempo = numIter / Math.log(radioMapa);
		
		for (int i=0; i<numIter; i++) {	
			radio = radioMapa * Math.exp(-i/cteTiempo);
			tasaApr = TAI * Math.exp(-i/(double)numIter); 
			
			errorCuant = 0.0;
			for (int ej=0; ej<histogramas.size(); ej++) {
				nGanadora = som.searchWinner(histogramas.get(ej).getHistograma());
				if (nGanadora == null) 
					throw new JIPException("There is no winner neuron");
				errorCuant += som.getDistAGanadora();
				som.refreshWeights(histogramas.get(ej).getHistograma(), radio, nGanadora, tasaApr);
			}
		}

		clasificacion = new ArrayList[filasSOM][colsSOM];
		for (int i=0; i<filasSOM; i++)
			for (int j=0; j<colsSOM; j++)
				clasificacion[i][j] = new ArrayList<ImageAttributes>();
		
		for (int ej=0; ej<histogramas.size(); ej++) {
			nGanadora = som.searchWinner(histogramas.get(ej).getHistograma());
			clasificacion[nGanadora.getRow()][nGanadora.getColumn()].add(histogramas.get(ej));
		}
		
		PrintWriter p;
		try {
			p = new PrintWriter (new FileOutputStream (getParamValueString("outputFile")));
			showClassification (clasificacion, filasSOM, colsSOM, p);
				
			showRepresentativeImage (clasificacion, filasSOM, colsSOM, disc, som, p);
			p.close();
		}
		catch (Exception e) {
			throw new JIPException("Error creating the HTML file");
		}
			
		return img;
	}
	
	private ArrayList<ImageAttributes> readHistograms (String imageBD, int disc) throws JIPException {
		FileInputStream fis;
		ObjectInputStream ois;
		ImageAttributes atrib;
		
		ArrayList<ImageAttributes> lista = new ArrayList<ImageAttributes>();
		
		try {
			fis = new FileInputStream (imageBD);
			ois = new ObjectInputStream (fis);
			
			while (ois.available()>0) {
				atrib = new ImageAttributes (ois.readUTF(), (float[][][])ois.readObject());
				lista.add(atrib);
			}	
			
			fis.close();
			ois.close();
		}
		catch (Exception e) {
			throw new JIPException("Error reading "+imageBD);
		}
		
		return lista;
	}
	
	/**
	 * Shows the images for each neuron
	 */
	private void showClassification (ArrayList[][] clasificacion, int filasSOM, int colsSOM, PrintWriter p) {
		String rutaImagen;

		p.println("<html><br>");
		p.println("<head>");
		p.println("<title>SOM applied to histogram images</title>");
		p.println("</head>");
		p.println("<body>");
		for (int i=0; i<filasSOM; i++)
			for (int j=0; j<colsSOM; j++) 
				if (!clasificacion[i][j].isEmpty()) {
					p.println ("<hr>");
					p.println ("<center><h1>NEURON ("+i+","+j+")</h1></center><br>");
					for (int k=0; k<clasificacion[i][j].size(); k++) {
						rutaImagen = "file:///"+((ImageAttributes)clasificacion[i][j].get(k)).getRuta();
						p.println ("<a href=\""+rutaImagen+"\">"+"<img src=\""+rutaImagen+"\" width=\"132\" height=\"92\">&nbsp;"+"</a>");
					}
				}			
	}
	
	/**
	 * Shows a web page with the representative images 
	 */
	private void showRepresentativeImage (ArrayList[][] clasificacion, int filasSOM, int colsSOM, int disc, SOM som, PrintWriter p) {
		float[][][] histoMedio = new float[disc][disc][disc];
		float[][][] histoTemp;
		double distancia, distMin;
		String imagenRepr = "";
		
		p.println ("<hr>");
		p.println ("<hr>");
		p.println ("<br><br><center><h1>Representative images</h1></center><br>");
		p.println ("<center><table border=\"1\" cellpadding=\"2\">");
		
		for (int i=0; i<filasSOM; i++) {
			p.println ("<tr heigth=\"92\">");
			for (int j=0; j<colsSOM; j++) {
				p.println ("<td width=\"132\">");
				if (!clasificacion[i][j].isEmpty()) {
					initializeMeanHisto (histoMedio, disc);
					for (int k=0; k<clasificacion[i][j].size(); k++) {	
						histoTemp = ((ImageAttributes)clasificacion[i][j].get(k)).getHistograma();
						histoMedio = sumHistos (histoTemp, histoMedio, disc);
					}
							
					for (int a=0; a<disc; a++)
						for (int b=0; b<disc; b++)
							for (int c=0; c<disc; c++)
								histoMedio[a][b][c] /= clasificacion[i][j].size();
					
					distMin = Double.MAX_VALUE;
					for (int k=0; k<clasificacion[i][j].size(); k++) {
						distancia = som.distanceL1(((ImageAttributes)clasificacion[i][j].get(k)).getHistograma(), histoMedio);
						if (distancia<distMin) {
							imagenRepr =  ((ImageAttributes)clasificacion[i][j].get(k)).getRuta();
							distMin = distancia;
						}
					}			
					imagenRepr = "file:///"+imagenRepr;
					p.println ("<a href=\""+imagenRepr+"\">"+"<img src=\""+imagenRepr+"\" width=\"132\" height=\"92\">"+"</a>");
				}
				p.println ("</td>");
			}
			p.println ("</tr>");
		}	
					
		p.println ("</table></center>");
		p.println("</body>");
		p.println("</html>");		
	}
	
	private float[][][] sumHistos (float[][][] h1, float[][][] h2, int disc) {
		float[][][] ret = new float[disc][disc][disc];
		for (int i=0; i<disc; i++)
			for (int j=0; j<disc; j++)
				for (int k=0; k<disc; k++)
					ret[i][j][k] = h1[i][j][k] + h2[i][j][k];		
		return ret;
	}
	
	private void initializeMeanHisto (float[][][] histoMedio, int disc) {
		for (int i=0; i<disc; i++)
			for (int j=0; j<disc; j++)
				for (int k=0; k<disc; k++) 
					histoMedio[i][j][k] = 0;
	}
	
	/**
	 * Auxiliar class to manage the histograms
	 */
	private class ImageAttributes {
		/**
		 * @uml.property  name="ruta"
		 */
		String ruta;
		/**
		 * @uml.property  name="histograma"
		 */
		float[][][] histograma;
		
		public ImageAttributes (String _ruta, float[][][] histo) {
			ruta = _ruta;
			histograma = histo;
		}
		
		/**
		 * @return  the ruta
		 * @uml.property  name="ruta"
		 */
		public String getRuta () { return ruta;	}		
		/**
		 * @return  the histograma
		 * @uml.property  name="histograma"
		 */
		public float[][][] getHistograma () { return histograma; }
	}

	/**
	 * This class implements a neuron, part of a SOM, applied to image histograms
	 */
	class Neurona {
		int fila;
		int columna;
		float [][][] pesos;
		int disc;		
		
		public Neurona (int f, int c, int _disc) {
			fila = f;
			columna = c;
			disc = _disc;
			pesos = new float [disc][disc][disc];
			setWeights ();
		}
		
		/**
		 * Set the initial values of the neuron in a random way, so that the sum of 
		 * all of these values is 1
		 */
		private void setWeights () {
			double sum = 0.0;
			for (int i=0; i<disc; i++)
				for (int j=0; j<disc; j++)
					for (int k=0; k<disc; k++) {
						pesos[i][j][k] = (float)Math.random();
						sum += pesos[i][j][k];
					}
			for (int i=0; i<disc; i++)
				for (int j=0; j<disc; j++)
					for (int k=0; k<disc; k++) {
						pesos[i][j][k] /= sum;
					}
		}
	
		public void refreshWeights (float[][][] ejemplo, double radio, double tasaApr, double dist) {
			for (int i=0; i<disc; i++)
				for (int j=0; j<disc; j++)
					for (int k=0; k<disc; k++)
						pesos[i][j][k] += (ejemplo[i][j][k] - pesos[i][j][k]) 
								* Math.exp(-Math.pow(dist,2)/(2*Math.pow(radio,2))) * tasaApr; 
		}
		
		public int getRow () { return fila; }
		public int getColumn () { return columna; }
		public float[][][] getWeights () { return pesos; }
	}
	
	
	/**
	 * This class represents a SOM, applied to image histograms
	 */
	class SOM {
		/**
		 * @uml.property  name="numFilas"
		 */
		int numFilas;
		/**
		 * @uml.property  name="numColumnas"
		 */
		int numColumnas;
		/**
		 * @uml.property  name="neuronas"
		 * @uml.associationEnd  multiplicity="(0 -1)"
		 */
		Neurona [][] neuronas;
		double distanciaAganadora;	
		
		public SOM (int fs, int cs, int disc) {
			numFilas = fs;
			numColumnas = cs;
			neuronas = new Neurona [numFilas][numColumnas];
			
			for (int i=0; i<numFilas; i++)
				for (int j=0; j<numColumnas; j++)
					neuronas[i][j] = new Neurona (i, j, disc);
		}
		
		/**
		 * Gets the closest neuron (distance L1) to the example
		 */
		public Neurona searchWinner (float[][][] histo) {
			Neurona n = null;
			double distancia, distMin = Double.MAX_VALUE;
					
			for (int i=0; i<numFilas; i++)
				for (int j=0; j<numColumnas; j++) {
					distancia = distanceL1(histo, neuronas[i][j].getWeights());
					if (distancia<distMin) {
						n = neuronas[i][j];
						distMin = distancia;
					}
				}
			distanciaAganadora = distMin;
			return n;
		}
		
		/**
		 * Refresh the weights of all the neurons inside a certain radius from
		 * the winner neuron and
		 */
		public void refreshWeights (float[][][] ejemplo, double radio, Neurona nGanadora, double tasaApr) {
			double distEuc;
			for (int i=0; i<numFilas; i++) {
				for (int j=0; j<numColumnas; j++) {
					distEuc =Math.sqrt(Math.pow(i-nGanadora.getRow(),2) + Math.pow (j-nGanadora.getColumn(),2));
					if (distEuc<=radio)
						neuronas[i][j].refreshWeights (ejemplo, radio, tasaApr, euclideanDistance(neuronas[i][j].getWeights(), nGanadora.getWeights()));
				}				 
			}		
		}
			
		/** 
		 * Euclidean Distance
		 */
		public double euclideanDistance (float[][][] h1, float[][][] h2) {
			double distancia;
			int disc;
			
			// Inicializaciones
			disc = h1.length;
			distancia = 0.0;
					
			for (int i=0; i<disc; i++)
				for (int j=0; j<disc; j++)
					for (int k=0; k<disc; k++) {
						distancia += Math.pow(h1[i][j][k] - h2[i][j][k], 2); 					
					}
			return Math.sqrt(distancia);
		}
		
		/**
		 * Returns the L1 norm.
		 */
		public double distanceL1 (float[][][] h1, float[][][] h2) {
			// Inicializaciones
			int disc = h1.length;
			double distancia = 0.0;
					
			for (int i=0; i<disc; i++)
				for (int j=0; j<disc; j++)
					for (int k=0; k<disc; k++)
						distancia += Math.abs(h1[i][j][k] - h2[i][j][k]); 	
			return distancia;
		}
		
		/**
		 * @return  the numFilas
		 * @uml.property  name="numFilas"
		 */
		public int getNumFilas () { return numFilas; }
		/**
		 * @return  the numColumnas
		 * @uml.property  name="numColumnas"
		 */
		public int getNumColumnas () { return numColumnas; }
		public double getDistAGanadora () {return distanciaAganadora; }
	}

}
	
