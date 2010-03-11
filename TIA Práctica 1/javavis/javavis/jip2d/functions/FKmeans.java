package javavis.jip2d.functions;

import java.util.Random;
import java.util.Arrays;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.bitmaps.JIPBmpColor;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;


/**
*An image segmentation is done.
*This method divides the image into 'k' homogeneous clusters, applying the K-means algorithm
*It can be applied to images of gray as to those of color.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>number: number of clusters. This parameter can not be negative.<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Segmented input image.<BR><BR>
*</ul>
*/

public class FKmeans extends JIPFunction {
	private static final long serialVersionUID = -2321246194138087467L;

	public FKmeans() {
		super();
		name = "FKmeans";
		description = "Applies the k-means algorithm";
		groupFunc = FunctionGroup.Manipulation;

		JIPParamInt p1 = new JIPParamInt("number", false, true);
		p1.setDefault(4);
		p1.setDescription("Number of clusters");
		
		addParam(p1);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		JIPImage aux;
		int cjtos=getParamValueInt("number");
		if (cjtos <= 0) 
			throw new JIPException("Clusters number must be greater than 0");
		if (img instanceof JIPImgGeometric || img instanceof JIPBmpBit)
			throw new JIPException("Kmeans can not be applied to this image type");
		
		if (img.getType() == ImageType.COLOR)
			aux = kmeans((JIPBmpColor)img, cjtos); 
		else 
			aux = kmeans((JIPImgBitmap)img, cjtos);
		
		return aux;
	}
	
	
	public JIPImage kmeans (JIPBmpColor img, int cjtos) throws JIPException {		
		JIPBmpColor aux = (JIPBmpColor)img.clone();
		
		int w = img.getWidth();
		int h = img.getHeight();
		
		double []valoresR = new double [cjtos];	 //contains clusters values of actual iteration (RED)
		double []valoresG = new double [cjtos];	//contains clusters values of actual iteration (GREEN)
		double []valoresB = new double [cjtos];	//contains clusters values of actual iteration (BLUE)
		double []valorAntR = new double [cjtos];	//contains clusters values of previous iteration (RED)
		double []valorAntG = new double [cjtos];	//contains clusters values of previous iteration (GREEN)
		double []valorAntB = new double [cjtos];	//contains clusters values of previous iteration (BLUE)
		double [] valorPixelR;	//the original value of each pixel of image of the RED band.(they are never modified)
		double [] valorPixelG;	//the original value of each pixel of image of the GREEN band. (they are never modified)
		double [] valorPixelB;	//the original value of each pixel of image of the BLUE band. (they are never modified)
		int [] cluster = new int [w*h];		//It contains the class to which each pixel belongs
		double [] valorFinalR = new double [w*h]; //the final value of each pixel of the RED band
		double [] valorFinalG = new double [w*h]; //the final value of each pixel of the GREEN band
		double [] valorFinalB = new double [w*h]; //the final value of each pixel of the BLUE band
		boolean salir = true;
		double diferencia = 0;
		double min_dif = 1000;
		
		//it initializes the values of the classes so that these values are
		//uniformly distributed
		searchValues(cjtos, img, valoresR, valoresG, valoresB);
				
		for (int i=0; i<cjtos; i++) {
			valorAntR[i]=0;
			valorAntG[i]=0;
			valorAntB[i]=0;
		}
		
		valorPixelR = img.getAllPixelsRed();
		valorPixelG = img.getAllPixelsGreen();
		valorPixelB = img.getAllPixelsBlue();
		
		double dif1=0.0;
		double dif2=0.0;
		double dif3=0.0;
		double sumaPotencias=0.0;
		int elems[] = new int[cjtos];
				
		do {
			//it examines each pixel of the image and assigns 
			//it to one of the clusters depending on the minimum distance
			for (int i=0; i<valorPixelR.length; i++){
				min_dif=1000000;
				for (int j=0;j<valoresR.length;j++) {
					dif1=Math.abs(valorPixelR[i] - valoresR[j]);
					dif2=Math.abs(valorPixelG[i] - valoresG[j]);
					dif3=Math.abs(valorPixelB[i] - valoresB[j]);
					sumaPotencias=Math.pow(dif1, 2) + Math.pow(dif2, 2) + Math.pow(dif3, 2);
					diferencia = Math.sqrt(sumaPotencias);
					if (diferencia < min_dif){
						min_dif = diferencia;
						cluster[i] = j;
					}
				}
			}
			

			System.arraycopy(valoresR,0,valorAntR,0,cjtos);
			System.arraycopy(valoresG,0,valorAntG,0,cjtos);
			System.arraycopy(valoresB,0,valorAntB,0,cjtos);
			Arrays.fill(valoresR,0);
			Arrays.fill(valoresG,0);
			Arrays.fill(valoresB,0);
			Arrays.fill(elems,0);
			
			for (int j=0; j<cluster.length; j++) {
				valoresR[cluster[j]] += valorPixelR[j];
				valoresG[cluster[j]] += valorPixelG[j];
				valoresB[cluster[j]] += valorPixelB[j];
				elems[cluster[j]]++;
			}
			
			//the centers of the classes are recalculated
			for (int j=0; j<valoresR.length; j++)
				if (elems[j]==0) {//it avoids that no class has 0 elements
					Random alea = new Random(); //it chooses random coordinates
					int x=alea.nextInt(w) ;
					int y=alea.nextInt(h);
					valoresR[j] = img.getPixelRed(x,y);
					valoresG[j] = img.getPixelGreen(x,y);
					valoresB[j] = img.getPixelBlue(x,y);
					elems[j]++;
				}
				else {
					valoresR[j] /= elems[j];
					valoresG[j] /= elems[j];
					valoresB[j] /= elems[j];
				}
			
			salir = true;
			for (int i=0; i<cjtos; i++) { 
				if (valoresR[i]!=valorAntR[i]){
					salir=false;
					break;
				}
			}
		} while (!salir);
		
		for(int i = 0; i < valorFinalR.length; i++) {
			valorFinalR[i] = valoresR[cluster[i]];
			valorFinalG[i] = valoresG[cluster[i]];
			valorFinalB[i] = valoresB[cluster[i]];
		}
		
		aux.setAllPixelsBlue(valorFinalB);
		aux.setAllPixelsGreen(valorFinalG);
		aux.setAllPixelsRed(valorFinalR);
		
		return aux;
	}
	
	public void	searchValues(int clases, JIPBmpColor img, double[]valoresR, double[]valoresG, 
			double[]valoresB) throws JIPException {
		double [] vectorR = img.getAllPixelsRed();
		double [] vectorG = img.getAllPixelsGreen();
		double [] vectorB = img.getAllPixelsBlue();
		
		//it orders of minor to greater, we only ordered the RED Array
		//the other vectors, are ordered according to the RED vector
		Arrays.sort (vectorR);
		Arrays.sort (vectorG);
		Arrays.sort (vectorB);
				
		int sep = vectorR.length/clases;
		int j=0, i=0;
		while (i < vectorR.length && j<clases) {
			valoresR[j] = vectorR[i]; valoresG[j] = vectorG[i]; valoresB[j] = vectorB[i];
			i = i + sep;
			j++;
		}
	}
	
	
	public JIPImage kmeans(JIPImgBitmap img, int cjtos) throws JIPException {
		JIPImgBitmap aux = (JIPImgBitmap)img.clone();
		int w = img.getWidth();
		int h = img.getHeight();
				
		double []valores;	//contains clusters values of actual iteration 
		double []valorAnt = new double [cjtos];	//contains clusters values of previous iteration 
		double [] valorPixel;	//the original value of each pixel of image.  (they are never modified)
		int [] cluster = new int [w*h];	//It contains the class to which each pixel belongs
		double [] valorFinal = new double [w*h]; //the final value of each pixel
		boolean salir = true;
		double diferencia = 0;
		double min_dif = 1000;
		int min_clase = 0;
				
		//it initializes the values of the classes so that these values are
		//uniformly distributed
		valores = searchValues(cjtos, img);  	
		
		for (int i=0; i<cjtos; i++)
			valorAnt[i]=0;
		valorPixel = img.getAllPixels();
		
		int elems[] = new int[cjtos];
		
		do {
			//it examines each pixel of the image and assigns 
			//it to one of the clusters depending on the minimum distance
			for (int i=0; i<valorPixel.length; i++){
				min_dif=1000;
				for (int j=0;j<valores.length;j++) {
					diferencia = Math.abs(valorPixel[i]-valores[j]);
					if (diferencia < min_dif){
						min_dif = diferencia;
						min_clase = j;
					}
				}
				cluster[i] = min_clase;							
			}
			System.arraycopy(valores,0,valorAnt,0,cjtos);
			Arrays.fill(valores,0);
			Arrays.fill(elems,0);
			
			for (int j=0; j<cjtos; j++) {
				valores[cluster[j]] += valorPixel[j];
				elems[cluster[j]]++;
			}
			
			//the centers of the classes are recalculated
			for (int j=0; j<cjtos; j++)
				if (elems[j]==0) { //it avoids that no class has 0 elements
					Random alea = new Random();
					int x=alea.nextInt(w) ;//it chooses random coordinates
					int y=alea.nextInt(h);
					valores[j] = img.getPixel(x,y);
					elems[j]++;
				}
				else valores[j] /= elems[j];
			
			salir = true;
			for (int i=0; i<cjtos; i++) 
				if (valores[i]!=valorAnt[i]){
					salir=false;
					break;
				}
		} while (!salir);
		
		for(int i = 0; i < valorFinal.length; i++)
			valorFinal[i] = valores[cluster[i]];
		
		aux.setAllPixels(valorFinal);
		
		return aux;
	}
	
	
	public double[] searchValues (int clases, JIPImgBitmap img) throws JIPException {
		double [] vectorClases = new double [clases];
		double [] vector = img.getAllPixels();
		
		//it orders from minor to greater, 
		Arrays.sort (vector);
		
		int sep = vector.length/clases;
		int j=0, i=0;
		
		while (i < vector.length&& j<clases) {
			vectorClases[j] = vector[i];
			i = i + sep;
			j++;
		}
		return vectorClases;
	}

}