package javavis.jip2d.util.sift;

import javavis.base.JIPException;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpFloat;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;

/**
 * Stores the images of which the Gaussian pyramid consists.
 * The pixel values can contain negative values.
 */
public class ImageDoG {
	public double[][] pixels;
	public int ancho;
	public int alto;
	//se supone que esta DoG es el producto de la resta de 2 imagenes suavizadas.
	//Supongamos que fueron suavizadas con sigma1 y sigma2 resp. de modo que esta DoG
	//se ha calculado como sigma1 - sigma2. Pues el siguiente sigma representa a sigma2
	public double sigma;
	
	public ImageDoG(double[][] pixels, int ancho, int alto) {
		this.pixels = pixels;
		this.ancho = ancho;
		this.alto = alto;
	}

	public ImageDoG(double[][] pixels) {
		this.pixels = pixels;
		this.ancho = pixels[0].length;
		this.alto = pixels.length;
	}
	
	
	public ImageDoG(int ancho, int alto) {
		this.ancho = ancho;
		this.alto = alto;
		pixels = new double[ancho][alto];
	}
	
	/**
	 * Resta dos imagenes de JavaVis devolviendo una ImagenDoG
	 * @param imgA imagen uno
	 * @param imgB imagen que se resta
	 * @return imagen resultado
	 * @throws JIPException 
	 */
	public static ImageDoG substract(JIPImgBitmap imgA, JIPImgBitmap imgB) throws JIPException {
		ImageDoG res;
		int ancho, alto;
		
		ancho = imgA.getWidth();
		alto = imgA.getHeight();
		res = new ImageDoG(ancho, alto);		
		for(int x=0; x<ancho; x++) 
			for(int y=0; y<alto; y++) 
				res.pixels[x][y] = imgA.getPixel(x,y) - imgB.getPixel(x,y);		
		
		return res;
	}
	
	/**
	 * Devuelve el nivel de gris más bajo de la imagen.
	 * @return
	 */
	public double getMin() {
		double min;
		
		min = Double.MAX_VALUE;
		for(int x=0; x<ancho; x++)
			for(int y=0; y<alto; y++)
				if (pixels[x][y]<min)
					min = pixels[x][y];
		return min;		
	}

	/**
	 * Devuelve el nivel de gris más alto de la imagen.
	 * @return
	 */
	public double getMax() {
		double max;
		
		max = Double.MIN_VALUE;
		for(int x=0; x<ancho; x++)
			for(int y=0; y<alto; y++)
				if (pixels[x][y]>max)
					max = pixels[x][y];
		return max;		
	}

	
	/**
	 * Escala los valores de gris de una imagen escalados en el rango especificado
	 * @param min valor de gris que se escalará a 0
	 * @param max valor de gris que se escalará a 1
	 * @return los valores de gris escalados
	 */
	public void scalePixels(double min, double max) {
		double rango;
		
		rango = max - min;
		for(int x=0; x<ancho; x++)
			for(int y=0; y<alto; y++)
				pixels[x][y] = (pixels[x][y]-min)/rango;
		
	}
	
	/**
	 * Devuelve una JIPImage de tipo REAL construida a partir de esta imagen
	 * @return
	 * @throws JIPException 
	 */
	public JIPImage getJIPImage() throws JIPException {
		JIPImgBitmap img;
		int x,y;
		
		img = new JIPBmpFloat(ancho, alto);
		for(x=0; x<ancho; x++)
			for(y=0; y<alto; y++)
				img.setPixel(x, y, (float)pixels[x][y]);
		return img;
	}
	
	
}