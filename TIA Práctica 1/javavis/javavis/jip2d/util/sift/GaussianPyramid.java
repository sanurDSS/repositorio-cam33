package javavis.jip2d.util.sift;

import java.util.*;

import javavis.base.JIPException;
import javavis.jip2d.base.*;
import javavis.jip2d.base.bitmaps.JIPBmpFloat;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.functions.*;

/**
 * Gaussian pyramid which samples the space of scales with Gaussians.
 * It is calculated with the method of "fast computation of characteristic scale using a
 * half octave pyramid" - Crowley, Riff, Piater.
 */
public class GaussianPyramid {
		/**
		 * Sigma of the Gaussian used for smoothing
		 */
		public final static float SIGMA = 1.6f;
		/**
		 * Scaling between pyramid's levels
		 */
		public final static float SCALE = 2f;
		/**
		 * Minimum absolute value value which has to achieve an extreme point in order to be considered
		 * as a SIFT point.  
		 */
		public final static float MIN_EXTREME = 0.03f;
		
		/**
		 * Set of DoGs
		 */
		private ArrayList<ImageDoG> dogs;
		
		/**
		 * Set of smoothed images
		 */
		private ArrayList<JIPImage> smooths;
		
		/**
		 * Width and height of the first level of the pyramid.
		 */
		private int inicWidth, inicHeigh;
		/**
		 * Number of levels of the pyramid.
		 */
		private int levels;
		
		/**
		 * @param img image from which the pyramid is calculated
		 * @param numOctaves Number of octaves to generate
		 * @throws JIPException 
		 */
		public GaussianPyramid(JIPImgBitmap img, int numOctaves) throws JIPException {
			JIPImgBitmap imgGaussAct, imgGaussPrev;
			FSmoothGaussian gauss;
			FInterpBi interp;
			ImageDoG dog;
			
			inicWidth = img.getWidth();
			inicHeigh = img.getHeight();
			
			dogs = new ArrayList<ImageDoG>();
			smooths = new ArrayList<JIPImage>();
			gauss = new FSmoothGaussian();
			gauss.setParamValue("sigma", SIGMA-1.0f);
			interp = new FInterpBi();
			interp.setParamValue("step", 0.5f);
			img=(JIPBmpFloat)interp.processImg(img);
			interp.setParamValue("step", SCALE);
			gauss.setParamValue("sigma", SIGMA);
			imgGaussPrev = (JIPImgBitmap) gauss.processImg(img);
			// Generates the levels of the pyramid. Each level consists of 3 Gaussians and 2 DoGs
			levels = 0;
			for (int i=0; i<numOctaves; i++) {
				smooths.add(imgGaussPrev); //p0 se guarda
				//generar imagen p1 (sigma = 2^nivel*sqrt(2))
				imgGaussAct = (JIPImgBitmap) gauss.processImg(imgGaussPrev);
				dog = ImageDoG.substract(imgGaussAct, imgGaussPrev);
				dog.sigma = Math.pow(2.0, levels);
				dogs.add(dog);
				//generar imagen p2 (sigma = 2^(nivel+1))
				imgGaussPrev = imgGaussAct; 
				smooths.add(imgGaussPrev); //p1 se guarda
				imgGaussAct = (JIPImgBitmap) gauss.processImg(gauss.processImg(imgGaussAct));
				dog = ImageDoG.substract(imgGaussAct, imgGaussPrev);
				dog.sigma = Math.pow(2.0, levels)*Math.sqrt(2.0);
				dogs.add(dog);
				//remuestreo
				imgGaussPrev = (JIPImgBitmap) interp.processImg(imgGaussAct);
				levels++;
			}
		}
		
		/**
		 * Get SIFT points detected with the pyramid.
		 * @return
		 */
		public ArrayList<SiftPoint> getPuntosSIFT(float umbralExtremo) {
			ArrayList<SiftPoint> puntos;
			ImageDoG dogAct, dogSup, dogInf;
			boolean esExtremo;
			double escalaSup, escalaInf;
			double pix;
			int numDoGs;

			puntos = new ArrayList<SiftPoint>();
			//si hay de (JIPImgBitmap) interp.processImg(imgGaussAct)1 a N DoGs hay que recorrer de la 2 a la N-1 buscando extremos
			numDoGs = dogs.size();
			dogAct = (ImageDoG) dogs.get(0);
			dogInf = (ImageDoG) dogs.get(1);
			for(int i=2; i<=numDoGs-1; i++) {
				dogSup = dogAct;
				dogAct = dogInf;
				dogInf = (ImageDoG) dogs.get(i);
				//si el nivel sup/inf son del mismo tamaño que el actual, el punto
				//de arriba/abajo de uno dado tendrá las mismas coord. En caso
				//contrario dependerá de la dif. de escala. Calcular esta escala
				if (dogSup.ancho > dogAct.ancho) 
					escalaSup = SCALE;
				else
					escalaSup = 1.0;
				if (dogInf.ancho < dogAct.ancho) 
					escalaInf = 1.0/SCALE;
				else
					escalaInf = 1.0;
				//recorrer todos los pixeles buscando extremos locales
				//Para no salirnos de la imagen pasamos de los bordes.
				for(int x=2; x<dogAct.ancho-2; x++)
					for(int y=2; y<dogAct.alto-2; y++) {
						esExtremo = false;
						pix = dogAct.pixels[x][y];
						//¿es un máximo local?
						if (isMaximum(pix, x, y, dogAct.pixels) && 
						    isMaximum(pix, (int)(x*escalaSup), (int)(y*escalaSup), dogSup.pixels) &&
							isMaximum(pix, (int)(x*escalaInf), (int)(y*escalaInf), dogInf.pixels))
									esExtremo = true;
						//¿es un mínimo local?
						if (!esExtremo)
							if (isMinimum(pix, x, y, dogAct.pixels) && 
								isMinimum(pix, (int)(x*escalaSup), (int)(y*escalaSup), dogSup.pixels) &&
								isMinimum(pix, (int)(x*escalaInf), (int)(y*escalaInf), dogInf.pixels))
									esExtremo = true;						
						//guardarlo si es un extremo y su valor absoluto supera el umbral
						if ((esExtremo)&& Math.abs(pix)>umbralExtremo) {
							float nivel = (float)inicWidth/(float)dogAct.ancho;
							puntos.add(new SiftPoint(x*nivel, y*nivel, x, y, i-1, dogAct.sigma, nivel));
						}
					}
			}
				
			return puntos;	
		}
		
		private boolean isMaximum(double pix, int x, int y, double[][] nivel) {
			if ((pix>nivel[x-1][y-1])&&(pix>nivel[x][y-1])&&(pix>nivel[x+1][y-1])
					&&(pix>nivel[x-1][y])&&(pix>=nivel[x][y])&&(pix>nivel[x+1][y])
					&&(pix>nivel[x-1][y+1])&&(pix>nivel[x][y+1])&&(pix>nivel[x+1][y+1]))	
					return true;
				else
					return false;
		}

		private boolean isMinimum(double pix, int x, int y, double[][] nivel) {
			if ((pix<nivel[x-1][y-1])&&(pix<nivel[x][y-1])&&(pix<nivel[x+1][y-1])
					&&(pix<nivel[x-1][y])&&(pix<=nivel[x][y])&&(pix<nivel[x+1][y])
					&&(pix<nivel[x-1][y+1])&&(pix<nivel[x][y+1])&&(pix<nivel[x+1][y+1]))	
					return true;
				else
					return false;		}

		/**
		 * Returns a JIPImage with the DoGs of the pyramid. Each band of the
		 * image is a DoG, so there will be 2 x levels number of bands.
		 * @param escalar determines whether an image should be scaled so that
		 * all the levels of the pyramid have the same size, or on the contrary 
		 * they should maintain their actual size. In this latter case the rest of
		 * the image is filled in with zeros (as in JavaVis all the bands have to
		 * be of the same size).
		 * @throws JIPException 
		 */
		public JIPImage getImagenDoGs(boolean escalar) throws JIPException {

			JIPBmpFloat res;
			ImageDoG dog;
			double max, min, maxImag, minImag;
			FInterpBi interp = null;
			JIPBmpFloat imgEscalada = null;
			
			if (escalar) {
				interp = new FInterpBi();
			}
			
			res = new JIPBmpFloat(2*levels, inicWidth, inicHeigh);
			//recorrer la lista de DoGs buscando min y max valor de gris
			min = Double.MAX_VALUE;
			max = Double.MIN_VALUE;
			Iterator<ImageDoG> it = dogs.iterator();
			while(it.hasNext()) {
				dog = (ImageDoG) it.next();
				//busca minimo global
				minImag = dog.getMin();
				if (minImag<min)
					min = minImag;
				//busca maximo global
				maxImag = dog.getMax();
				if (maxImag>max)
					max = maxImag;				
			}
			//componer las bandas de la imagen
			it = dogs.iterator();
			for(int banda=0; banda<2*levels; banda++) {
				dog = (ImageDoG) it.next();
				//dog.escalaPixels(min, max);
				//si se pide así, escalar el tamaño de las imágenes
				if (escalar) {
					interp.setParamValue("step", (float)dog.ancho/(float)inicWidth);
					imgEscalada = (JIPBmpFloat) interp.processImg(dog.getJIPImage()); 
					for(int x=0; x<inicWidth; x++)
						for(int y=0; y<inicHeigh; y++) 
							res.setPixel(banda, x, y, imgEscalada.getPixel(x, y));							
				}
				else
					for(int x=0; x<dog.ancho; x++)
						for(int y=0; y<dog.alto; y++) 
							res.setPixel(banda, x, y, (float)dog.pixels[x][y]);
			}
			return res;
		}
		
		public ImageDoG getDoG(int n) {
			return (ImageDoG) dogs.get(n);
		}
		
		public JIPImage getImgSuav(int n) {
			return (JIPImage) smooths.get(n);
		}
		
		/**
		 * Get the standard deviation used for generating the smoothed image.
		 * @param numImag order of the image in the pyramid (starting from 0).
		 * @return
		 */
		public float getSigma(int numImag) {
			int nivel, pos;
			
			nivel = numImag / 2;
			pos = numImag % 2;
			if (pos==0)
				return (float) Math.pow(2, nivel);
			else
				return (float) (Math.pow(2,nivel)*Math.sqrt(2.0));
		}
		
		/**
		 * Returns the "nominal" standard deviation used for generating 
		 * a smoothed image. The actual sigma is much larger due to the
		 * scales of the images. 
		 * @param numImag
		 * @return
		 */
		public float getSigmaNominal(int numImag) {
			int pos;
			
			pos = numImag % 2;
			if (pos==0)
				return 1.0f;
			else
				return (float)Math.sqrt(2.0);
		}
		
		public int getAnchoInic() {
			return inicWidth;
		}

}