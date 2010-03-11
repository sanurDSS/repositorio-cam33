package javavis.jip2d.util;

/**
* Class used to calculate the histogram of the REAL images. This type of images
* have a pixel intensity between [0..1], In a infinite intensity set is required to
* calculate this class, that give us an intensity with its instances number per value.
* The internal data are:
* float intensidad: The intensity, its values are between [0..1].
* int nOcurrencias: number of instances.    
*/

public class RealDataHistogram {

	/** Intensity in pixel */
	private float intensidad;
	/** Number of instances */
	private int nOcurrencias;

	/**
	* Constructor of the class. It starts the intensity and the number of instances to -1.
	*/
	public RealDataHistogram() {
		this.nOcurrencias = -1;
		this.intensidad = -1;
	}

	/**
	* Method to get the intensity value.
	* @return Intensity value.
	*/
	public float getIValue() {
		return intensidad;
	}

	/**
	* Method to get the instances value.
	* @return Number of instances.
	*/
	public int getOValue() {
		return nOcurrencias;
	}

	/**
	* Method to assign same value to the intensity.
	* @param Intensity value.
	*/
	public void setIValue(float in) {
		this.intensidad = in;
	}

	/**
	* Method to assign value to the instances.
	* @param Number of instances.
	*/
	public void setOValue(int nOcu) {
		this.nOcurrencias = nOcu;
	}

}
