
package javavis.jip2d.functions;

import java.util.*;

import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamBool;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamObject;
import javavis.jip2d.util.sift.HistoOrient;
import javavis.jip2d.util.sift.ImageDoG;
import javavis.jip2d.util.sift.MatrixSimple;
import javavis.jip2d.util.sift.GaussianPyramid;
import javavis.jip2d.util.sift.SiftPoint;
import javavis.jip2d.base.*;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPGeomPoly;
import javavis.jip2d.base.geometrics.JIPImgGeometric;


/**
* 
*Finds Sift interest points and calculates their Sift descriptors, using
*David Lowe's detector and descriptor. <BR>
*Applicable to float bitmap images<BR>
*<ul><B>Input parameters:</B><BR>
*<li>res: If true returns the Sift points. If false, returns the difference of Gaussians<BR>
*<li>minPyramidWidth: minimum width for constructing the pyramid<BR>
*<li>noEdges: Remove Sift points which are placed on an edge.<BR>
*<li>thresholdExtreme: Threshold for considering that an extreme of the DoG is a SIFT point.<BR>
*<li>subpixel: Perform subpixel interpolation.<BR>
*<li><BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>points: the coordinates of the interest points or the DoG<BR><BR>
*</ul>
*/
public class FSift extends JIPFunction {
	private static final long serialVersionUID = -123228275505182119L;
	
	//Decide whether a point is over an edge
	//Ratio between eigenvalues used for deciding whether a point is over an edge 
	private static final double RATIO_EDGE = 10;
	//radius of the vecinity
	private static final int RADIO_NITZBERG = 3;
	
	//Orientation assignment
	//num. de intervalos en el histograma de orientaciones (para asignar orientacion a los puntos)
	private static final int BINS_H_ORIENT = 36;
	//factor utilizado en la sigma de la mascara gaussiana para asignar orientacion
	private static final float FACT_GAUSSMASK = 1.5f;
	//porcentaje con respecto al maximo absoluto para considerar que un valor 
	//del histograma de gradientes merece formar un nuevo punto SIFT
	private static final float PORC_HIST_GRAD = 90.0f;
	
	//Descriptor
	/**
	 * The descriptor consists of NUM_HIST_DESC x NUM_HIST_DESC histograms
	 */ 
	private static final int NUM_HIST_DESC = 4;
	/**
	 * The histogram of orientations of the gradient is calculated in a window
	 * of VENT_HIST_DESC x VENT_HIST_DESC pixels
	 */
	private static final int VENT_HIST_DESC = 4;
	/**
	 * Number of intervals of the orientations histogram
	 */	
	private static final int BINS_HIST_DESC = 8;
	/**
	 * In the normalized vector, if a value is higher than this threshold, it is considered too high.
	 */
	private static final double UMBRAL_GRAD = 0.2;

	

	public FSift() {
		super();
		name = "FSift";
		description = "Interest points detection using the David Lowe's SIFT saliency detector.";
		groupFunc = FunctionGroup.FeatureExtract;
		
		JIPParamBool p1 = new JIPParamBool("returnSiftPoints", false, true);
		p1.setDefault(true);
		p1.setDescription("If true returns the SIFT points. If false, returns the difference of Gaussians");
		JIPParamInt p2 = new JIPParamInt("numOctaves", false, true);
		p2.setDefault(4);
		p2.setDescription("Number of octaves.");
		JIPParamBool p3 = new JIPParamBool("noEdges", false, true);
		p3.setDescription("Remove SIFT points which are placed on an edge.");
		p3.setDefault(true);
		JIPParamFloat p4 = new JIPParamFloat("thresholdExtreme", false, true);
		p4.setDescription("Threshold for considering that an extreme of the DoG is a SIFT point.");
		p4.setDefault(GaussianPyramid.MIN_EXTREME);		
		JIPParamBool p7 = new JIPParamBool("subpixel", false, true);
		p7.setDescription("Perform subpixel interpolation.");
		p7.setDefault(false);
		JIPParamObject p8 = new JIPParamObject("points",false,false);
		p8.setDescription("The coordinates of the interest points or the DoG.");
		p8.setDefault(null);
		
		addParam(p1);
		addParam(p2);
		addParam(p3);
		addParam(p4);
		addParam(p7);
		addParam(p8);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if(img instanceof JIPImgGeometric || img.getType()==ImageType.BIT)
			throw new JIPException("FSift can not be applied to this image type.");
		JIPImgBitmap imgBmp;
		
		if (img.getType() != ImageType.FLOAT) {
			if (img.getType() == ImageType.COLOR) {
				FColorToGray fctg = new FColorToGray();
				fctg.setParamValue("gray", "FLOAT");
				imgBmp=(JIPImgBitmap)fctg.processImg(img);
			}
			else {
				FGrayToGray fctg = new FGrayToGray();
				fctg.setParamValue("gray", "FLOAT");
				imgBmp=(JIPImgBitmap)fctg.processImg(img);
			}
		}
		else {
			imgBmp=(JIPImgBitmap)img;
		}
		
		GaussianPyramid pyramid = null;
		int numOctaves; 
		ArrayList<SiftPoint> points = null;
	
		//get parameters
		numOctaves = getParamValueInt("numOctaves");

		//construct gaussian pyramid
		pyramid = new GaussianPyramid(imgBmp, numOctaves);
		
		//return the difference of gaussians, if so required
		if (!getParamValueBool("returnSiftPoints"))
			return pyramid.getImagenDoGs(false);
		
		//get the SIFT points
		points = pyramid.getPuntosSIFT(getParamValueFloat("thresholdExtreme"));
		//delete those SIFT points which are on edges
		if (getParamValueBool("noEdges"))
			points = deleteEdges(points, RATIO_EDGE, RADIO_NITZBERG, pyramid);
		
		//interpolate points
		if (getParamValueBool("subpixel"))
			points = interpolatePoints(points, pyramid);
		
		//Assign an orientation to the points
		points = setOrientation(points, pyramid);
		
		//calculate descriptors and save them in a vector of results
		points = calcDescriptor(points, pyramid);	
		setParamValue("points", points);
	
		//return the image showing SIFT points
		return getCircImg(points, img.getWidth(), img.getHeight(), pyramid);
	}


	/**
	 * Returns a geometric image in JIP format with a list of SIFT points represented
	 * on it. The points are represented as a circumference with a radius which is
	 * proportional to the sigma used in the DoG.
	 * @param list list of points
	 * @param aVectorVectornchoInic image width
	 * @param altoInic image height
	 * @throws JIPException 
	 */
	private JIPImage getCircImg(ArrayList<SiftPoint> list, int anchoInic, int altoInic, GaussianPyramid pir) throws JIPException {
		ArrayList<ArrayList<Integer>> vecPun = new ArrayList<ArrayList<Integer>>();
		SiftPoint punto;

		Iterator<SiftPoint> it = list.listIterator();
		while (it.hasNext()) {
			punto = (SiftPoint) it.next();
			vecPun.add(genCircunferenciaSift(punto, pir));
			vecPun.add(genOrientacionSift(punto, pir));
		}
		
		JIPGeomPoly resul = new JIPGeomPoly(anchoInic, altoInic);
		resul.setData(vecPun);
		return resul;
	}


	/**
	 * Draws a line (JIP polygon with 2 vertices) with the orientation assigned to a SIFT point
	 * @param punto SIFT point
	 * @param pir Gaussian pyramid from the area under the point
	 * @return line
	 */
	private ArrayList<Integer> genOrientacionSift(SiftPoint punto, GaussianPyramid pir) {
		ArrayList<Integer> lin;
		int radio;
		
		lin = new ArrayList<Integer>();
		lin.add((int)punto.x);
		lin.add((int)punto.y);		
		radio = (int)(3.0 * pir.getDoG(punto.numDoG).sigma);
		lin.add((int)(punto.x + Math.cos(punto.orientation)*radio));
		lin.add((int)(punto.y + Math.sin(punto.orientation)*radio));		
		return lin;
	}



	/**
	 * Generates a JIP polygon consisting of a circumference around a SIFT point
	 * @param punto SIFT point
	 * @return
	 */
	
	private ArrayList<Integer> genCircunferenciaSift(SiftPoint punto, GaussianPyramid pir) {
		ArrayList<Integer> circ;
		final float INC_ANG = 10f;
		int radio;
		
		circ = new ArrayList<Integer>();
		radio = (int)(3.0 * pir.getDoG(punto.numDoG).sigma);
		for(float ang=0f; ang<360; ang+=INC_ANG) {
			circ.add((int)(punto.x + Math.cos(Math.toRadians(ang))*radio));
			circ.add((int)(punto.y + Math.sin(Math.toRadians(ang))*radio));
		}
		
		return circ;
	}
	
	/**
	 * Calculates a descriptor for each one of the SIFT points. 
	 * This way we can compare points found in different images and know whether
	 * they are the same.
	 * @param puntos list of SIFT point in the image
	 * @param piramide Gaussian pyramid used for finding the SIFT points
	 * @return lits of SIFT points with their descriptors
	 * @throws JIPException 
	 */
	private ArrayList<SiftPoint> calcDescriptor(ArrayList<SiftPoint> puntos, GaussianPyramid piramide) throws JIPException {
		Iterator<SiftPoint> it;
		SiftPoint p;
		JIPImgBitmap img;
		//descriptor con los histogramas
		double[] desc;
		//centros de las ventanas donde se calculan los histogramas
		double[] xGrid, yGrid;
		//coord de cada una de las muestras por donde vamos calculando el gradiente
		double xSample, ySample;
		//gradiente en un punto: magnitud en x, en y, magnitud total, orientacion
		double gradX, gradY, magGrad, gradOrient;
		//peso del gradiente en un histograma (dependera de la dist del sample al centro de la ventana)
		double pesoX, pesoY;
		//peso del gradiente segun orientacion
		double[] pesoOrient = new double[BINS_HIST_DESC];
		//parametros de una gaussiana con radio el de la ventana y centro en el punto
		double gSigma, gMult, gFactor;
		//ponderacion del sample segun la gaussiana
		double pesoG;
		
		//precalcular los centros de las ventanas donde se calculan los histogramas
		xGrid = new double[NUM_HIST_DESC*NUM_HIST_DESC];
		yGrid = new double[NUM_HIST_DESC*NUM_HIST_DESC];
		double gridLim = VENT_HIST_DESC*(NUM_HIST_DESC/2 - 0.5);
		int i=0;
		for(double x=-gridLim; x<=gridLim; x+=VENT_HIST_DESC)
			for(double y=-gridLim;y<=gridLim; y+=VENT_HIST_DESC) {
				xGrid[i] = x;
				yGrid[i] = y;
				i++;
			}
		it = puntos.iterator();
		while (it.hasNext()) {
			//reservar memoria para los histogramas
			desc = new double[NUM_HIST_DESC*NUM_HIST_DESC*BINS_HIST_DESC];
			p = it.next();
			img = (JIPImgBitmap) piramide.getImgSuav(p.numDoG);
			//precalcular seno y coseno de la orientacion del punto
			double cos = Math.cos(p.orientation);
			double sin = Math.sin(p.orientation);
			//recorrer la rejilla de samples, girada y centrada en el punto SIFT
			double sampleLimit = VENT_HIST_DESC*NUM_HIST_DESC/2 - 0.5; 
			i=0;
			//precalcular parametros de la gaussiana, para asignar peso a los gradientes
			//la gaussiana tiene una sigma = radio_ventana/3, vamos que el radio es 3*sigma
			gSigma = NUM_HIST_DESC/2*VENT_HIST_DESC/3.0;
			gMult = (1.0 / (gSigma * (Math.sqrt(2.0 * Math.PI))));
			gFactor = (-1.0 / (2.0 * gSigma * gSigma));
			for(double xs=-sampleLimit; xs<=sampleLimit; xs++)
				for(double ys=-sampleLimit; ys<=sampleLimit; ys++) {
					//coord. de la muestra, rotada y centrada en el punto
					xSample = p.xDog + xs*cos - ys*sin;
					ySample = p.yDog + xs*sin + ys*cos;
					//calcular el gradiente en la muestra
					try{
					gradX = img.getPixel((int)Math.round(xSample)+1,(int)Math.round(ySample))
							- img.getPixel((int)Math.round(xSample)-1,(int)Math.round(ySample));
					}catch(javavis.base.JIPException e){
						gradX = 0;
					}
					try{
					gradY = img.getPixel((int)Math.round(xSample),(int)Math.round(ySample)+1)
							- img.getPixel((int)Math.round(xSample),(int)Math.round(ySample)-1);
					}catch(javavis.base.JIPException e){
						gradY = 0;
					}
					//orientacion del gradiente
					gradOrient = Math.atan2(gradY, gradX) +  Math.PI;
					//la orientacion debe ser relativa a la del punto
					gradOrient = gradOrient - p.orientation;
					if (gradOrient<0)
						gradOrient = Math.PI*2.0 + gradOrient;
					//magnitud del gradiente
					magGrad = Math.sqrt(gradX*gradX + gradY*gradY);
					//calcular peso del sample segun la gaussiana
					double xg = xSample - p.xDog;
					double yg = ySample - p.yDog;
					pesoG = (gMult * Math.exp(gFactor *( xg * xg + yg * yg)));
					//pesos para repartir la influencia entre varios bins de un histograma, dependiendo
					//de la dist del angulo al angulo del centro del bin
					double angBin = Math.PI*2.0/BINS_HIST_DESC;
					for(int bin=0; bin<BINS_HIST_DESC; bin++) {
						//la dist se mide en bins. Si está a un bin o más el peso será 0
						//Como el angulo está en el bin gradOrient/angBin... 
						pesoOrient[bin] = Math.max(1-Math.abs(gradOrient/angBin-bin), 0);
					}
					//recorrer todos los histogramas añadiendo el nuevo gradiente
					for(int hist=0; hist<NUM_HIST_DESC*NUM_HIST_DESC; hist++) {
						//peso para añadir a cada histograma, dependiendo de la dist del sample
						//al centro de la ventana. La dist. se mide en celdas. 
						//Si esta a una celda o mas el peso sera 0.
						pesoX = Math.max(1-Math.abs((xs-xGrid[hist])/VENT_HIST_DESC), 0); 
						pesoY = Math.max(1-Math.abs((ys-yGrid[hist])/VENT_HIST_DESC), 0);
						//añadir gradiente ponderado segun dists, gaussiana y angulo
						if ((pesoX>0)&&(pesoY>0)) {
							for(int bin=0; bin<BINS_HIST_DESC; bin++) {
								desc[hist*BINS_HIST_DESC + bin] += pesoX*pesoY*pesoG*pesoOrient[bin]*magGrad;
							}
						}
					}//del "for" de histogramas
				}//del "for" de samples
			//normalizar el vector descriptor
			normalizar(desc);
			//umbralizar el descriptor para evitar gradientes muy grandes
			umbralizar(desc);
			//poner el descriptor en el punto SIFT
			p.descriptor = desc;
		}//del "while"		
		return puntos;
	}
	/**
	* Normalizes a descriptor vector (modulus 1).
	*/
	private void normalizar(double[] descriptor) {
		double modulo = 0.0;
		
		
		for(int i=0; i<descriptor.length; i++) 
			modulo+=(descriptor[i]*descriptor[i]);
		modulo = Math.sqrt(modulo);
		for(int i=0; i<descriptor.length; i++) {
			descriptor[i]/=modulo;			
		}
	}
	
	/**
	 * Removes all the components of the descriptor which have a value higher than UMBRAL_GRAD 
	 */
	private void umbralizar(double[] descriptor) {
		
		for(int i=0; i<descriptor.length; i++)
			if (descriptor[i]>UMBRAL_GRAD)
				descriptor[i]=UMBRAL_GRAD;
		//renormalize descriptor
		normalizar(descriptor);
		
	}
	
	//TODO: make orientation assignment simpler
	/**
	 * Assigns an orientation to each SIFT point. This orientation corresponds to
	 * the main direction of the gradient in the point's vicinity.
	 * The 0 angle corresponds to the horizontal left-to-right direction.
	 * @param puntos
	 * @param piramide
	 * @return
	 * @throws JIPException 
	 */
	private ArrayList<SiftPoint> setOrientation(ArrayList<SiftPoint> puntos, GaussianPyramid piramide) throws JIPException {
		Iterator<SiftPoint> it;
		SiftPoint p, p2;
		HistoOrient hist;
		float[][] gaussiana;
		int radioVent;
		int x,y;
		int ancho, alto;
		double gradX, gradY;
		JIPImgBitmap imgSuav;
		double orient;
		ArrayList<SiftPoint> puntosNew;
		//maximos en histograma de gradiente acumulado por angulos
		ArrayList<Float> max;
		Float ori;
		
		puntosNew = new ArrayList<SiftPoint>();
		it = puntos.iterator();
		while (it.hasNext()) {
			p = it.next();
			imgSuav = (JIPImgBitmap) piramide.getImgSuav(p.numDoG);
			ancho = imgSuav.getWidth();
			alto = imgSuav.getHeight();
			//crea histograma vacío
			hist = new HistoOrient(BINS_H_ORIENT);
			//crea máscara gaussiana
			gaussiana = calcGaussian2D(FACT_GAUSSMASK*piramide.getSigmaNominal(p.numDoG));
			//recorrer los puntos de la imagen debajo de la mascara
			radioVent = gaussiana.length / 2;			
			for(int i=-radioVent, iVent=0; i<=radioVent; i++, iVent++)
				for(int j=-radioVent, jVent=0; j<=radioVent; j++,jVent++) {
					//coords. de la imagen
					x = (int)Math.round(p.xDog) + i;
					y = (int)Math.round(p.yDog) + j;
					//comprobar si nos vamos a salir
					if ((x<1)||(x>=ancho-1)||(y<1)||(y>=alto-1))
						break;
					//calcular magnitud del gradiente
					gradX = 0.5*(imgSuav.getPixel(x+1,y)-imgSuav.getPixel(x-1,y));
					gradY = 0.5*(imgSuav.getPixel(x,y+1)-imgSuav.getPixel(x,y-1)); 
					//calcular orientación del gradiente
					orient = Math.atan2(gradY, gradX) +  Math.PI; 
					//meter el gradiente ponderado el el histograma
					hist.addBin(orient, gaussiana[iVent][jVent]*Math.sqrt(gradX*gradX + gradY*gradY));
				}
			hist.smooth();
			//encontrar el/los maximo/s del gradiente acumulado
			max = hist.getMax(PORC_HIST_GRAD);
			//si solo hay 1 máximo, asignar esta orientación al punto SIFT
			if (max.size()==1) {
				ori = max.get(0);
				p.orientation = ori.doubleValue();
				puntosNew.add(p);
			}
			//si hay más, crear un nuevo punto SIFT por cada máximo
			else
				for(int i=1; i<max.size(); i++) {
					p2 = new SiftPoint(p);
					ori = max.get(i);
					p2.orientation = ori.doubleValue();
					puntosNew.add(p2);				
				}				
		}
		return puntosNew;
	}
	
	/**
	 * Removes those SIFT points which are on an edge.
	 * The edgeness constraint is calculated from the Hessian matrix (of second derivates).
	 * If the ratio between the autovalues of the Hessian is higher than
	 * a threshold, then there is an edge and the point has to be removed.
	 * @param puntos
	 * @param ratio ratio between eigenvalues. Below this ratio an edge is considered to exist.
	 * @param radio vicinity radius used for calculating the derivates of the H matrix 
	 * @return
	 */
	private ArrayList<SiftPoint> deleteEdges(ArrayList<SiftPoint> puntos, double ratio, int radio, GaussianPyramid pir) {
		SiftPoint point;
		//(ratio + 1)^2 /ratio
		double thresh;		
		//Hessian mask
		double[][] hxx = {{.1, -.2, .1},{.3, -.6, .3},{.1, -.2, .1}}; //dx2
		double[][] hyy = {{.1, .3, .1},{-.2, -.6, -.2},{.1, .3, .1}}; //dy2
		double[][] hxy = {{.125, 0, -.125},{0, 0, 0},{-.125, 0, .125}}; //dxy
		//la hessiana es de 2x2 aunque el (1,2) y el (2,1) son el mismo
		double dxx, dxy, dyy;
		//traza y determinante de la hessiana
		double trH, detH;

		Iterator<SiftPoint> it = puntos.iterator();
		thresh = (ratio+1)*(ratio+1)/ratio;		
		while(it.hasNext()) {
			point = it.next();
			//Hessian
			dxx = applyMask3x3(hxx, pir.getDoG(point.numDoG), (int)Math.round(point.xDog), (int)Math.round(point.yDog));
			dxy = applyMask3x3(hxy, pir.getDoG(point.numDoG), (int)Math.round(point.xDog), (int)Math.round(point.yDog));
			dyy = applyMask3x3(hyy, pir.getDoG(point.numDoG), (int)Math.round(point.xDog), (int)Math.round(point.yDog));
			
			//trace
			trH = dxx + dyy;
			//determinant
			detH = dxx*dyy - dxy*dxy;
			
			//if Tr(H)^2/Det(H) >= (ratio + 1)^2 /ratio or the determinant is below 0, is an edge
			if ((trH*trH/detH)>=thresh || detH<=0.0f) {
				it.remove();
			}
		}
		return puntos;

	}
	
	private double applyMask3x3(double[][] mak, ImageDoG im, int x, int y) {
		double res = 0.0;
		double[][] pixIm;
		
		pixIm = im.pixels;
		res=mak[0][0]*pixIm[x-1][y-1];
		res+=mak[0][1]*pixIm[x-1][y];
		res+=mak[0][2]*pixIm[x-1][y+1];
		res+=mak[1][0]*pixIm[x][y-1];
		res+=mak[1][1]*pixIm[x][y];
		res+=mak[1][2]*pixIm[x][y+1];
		res+=mak[2][0]*pixIm[x+1][y-1];
		res+=mak[2][1]*pixIm[x+1][y];
		res+=mak[2][2]*pixIm[x+1][y+1];
		return res;			
	}
	


	
	/**
	 * Calculates a 2D Gaussian mask
	 * @param sigma sigma parameter of the Gaussian
	 * @return the mask
	 */
	private float[][] calcGaussian2D(float sigma) {
		float factor, sum;
		int radio, diametro;
		float[][] vector;

		radio = (int) (sigma * 3.0 + 0.5);
		diametro = radio * 2 + 1;
		vector = new float[diametro][diametro];
		factor = (float) (-0.5 * sigma * sigma);
		sum = 0.0f;
		for (int i = 0, r = -radio; r <= radio; r++, i++) 
			for (int j = 0, r2 = -radio; r2<=radio; r2++, j++) {
				vector[i][j] = (float) Math.exp(factor *( r * r + r2 * r2));
				sum += vector[i][j];
			}
		for (int i = 0; i < diametro; i++)
			for (int j = 0; j < diametro; j++)
				vector[i][j] /= sum;
		
		return vector;
		
	}

	
	private ArrayList<SiftPoint> interpolatePoints(ArrayList<SiftPoint> puntos, GaussianPyramid pir) {
		SiftPoint p;
		double[] interp;
		
		Iterator<SiftPoint> it = puntos.iterator();
		while(it.hasNext()) {
			p = it.next();
			System.out.println("p="+p.numDoG);
			interp = interpolarPunto(p, pir);
			if (Math.abs(interp[2])>.5||Math.abs(interp[1])>.5) {
				p.xDog = p.xDog + interp[2];
				p.yDog = p.yDog + interp[1];
				p.x = p.xDog*p.level;
				p.y = p.yDog*p.level;			   
			    interp = interpolarPunto(p,pir);
				p.xDog = p.xDog + interp[2];
				p.yDog = p.yDog + interp[1];
				p.x = p.xDog*p.level;
				p.y = p.yDog*p.level;			   
				p.sigma += interp[0];						
			}
				
			
		}
		return puntos;
	}
	
	private double[] interpolarPunto(SiftPoint p, GaussianPyramid pir) {
		double[][] abajo, arriba, act;
		double[][] valH;
		MatrixSimple hessiana;
		double[] b;
		int x,y;
		double escalaSup, escalaInf;

		
		act = pir.getDoG(p.numDoG).pixels;
		abajo = pir.getDoG(p.numDoG+1).pixels;
		arriba = pir.getDoG(p.numDoG-1).pixels;
		x = (int)p.xDog;
		y = (int)p.yDog;
		//NOTA: para lo de arriba y abajo, imaginarse la piramide invertida (base mas estrecha que la cuspide)
		//o sea, el nivel superior es mas ancho que el inferior. Esto es el orden de generacion mas que orden piramidal
		//si el nivel sup/inf son del mismo tamaño que el actual, el punto
		//de arriba/abajo de uno dado tendrá las mismas coord. En caso
		//contrario dependerá de la dif. de escala. Calcular esta escala
		if (pir.getDoG(p.numDoG-1).ancho > pir.getDoG(p.numDoG).ancho) 
			escalaSup = GaussianPyramid.SCALE;
		else
			escalaSup = 1.0;
		if (pir.getDoG(p.numDoG+1).ancho < pir.getDoG(p.numDoG).ancho) 
			escalaInf = 1.0/GaussianPyramid.SCALE;
		else
			escalaInf = 1.0;
		int xSup = (int) (x*escalaSup);
		int ySup = (int) (y*escalaSup);
		int xInf = (int) (x*escalaInf);
		int yInf = (int) (y*escalaInf);
		
		//calcular hessiana
		valH = new double[3][3];
		valH[2][2] = act[x-1][y] - 2 * act[x][y] + act[x + 1][y]; 
		valH[1][1] = act[x][y - 1] - 2 * act[x][y] + act[x][y + 1];
		valH[0][0] = arriba[xSup][ySup] - 2 * act[x][y] + abajo[xInf][yInf];
		valH[0][1] = valH[1][0] = 0.25 * (abajo[xInf+1][yInf] - abajo[xInf-1][yInf] -
				(arriba[xSup+1][ySup] - arriba[xSup-1][ySup]));
		valH[0][2] = valH[2][0] = 0.25 * (abajo[xInf][yInf+1] - abajo[xInf][yInf-1] -
				(arriba[xInf][yInf+1] - arriba[xInf][yInf-1]));
		valH[1][2] = valH[2][0] = 0.25 * (act[x+1][y+1] - act[x-1][y+1] -
				(act[x+1][y-1] - act[x-1][y-1]));
		hessiana = new MatrixSimple(valH);
		//vector b del sistema Ax=b a resolver.
		b = new double[3];
		b[2] = - 0.5 * (act[x + 1][y] - act[x - 1][y]);
		b[1] = - 0.5 * (act[x][y + 1] - act[x][y - 1]);
		b[0] = - 0.5 * (abajo[xInf][yInf] - arriba[xSup][ySup]);
		//resolver el sistema
		hessiana.resolverSistLineal(b);
		//cambiar los valores actuales del punto por los interpolados
		//comprobar que el punto interpolado no se sale de la imagen
		double nuevaX = p.xDog + b[2];
		double nuevaY = p.yDog + b[1];
		if (nuevaX>=0 && nuevaX<=act.length-1 && nuevaY>=0 && nuevaY<act[0].length) {
			p.xDog = nuevaX;
			p.yDog = nuevaY;
			p.x = p.xDog * p.level;
			p.y = p.yDog * p.level;
			p.sigma += b[0];	
		}
		return b;
	}






	
}
