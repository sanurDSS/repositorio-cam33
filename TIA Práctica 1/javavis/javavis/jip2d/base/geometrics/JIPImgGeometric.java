package javavis.jip2d.base.geometrics;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import javavis.base.JIPException;
import javavis.jip2d.base.JIPImage;

/**
* Abstract class to define a geometric object. It contains geometric data, implemented by an array of Integer
*/
public abstract class JIPImgGeometric extends JIPImage implements Serializable {
	private static final long serialVersionUID = -7755470955703916794L;
	
	/**
	 * Array of colors. Used to allow to give color to the data
	 */
	ArrayList<Color> vecColors;

	/**
	 * Constructor
	 */
	public JIPImgGeometric (JIPImgGeometric img) throws JIPException {
		super(img);
		vecColors=img.getColors();
	}

	/**
	 * Constructor
	 */
	public JIPImgGeometric (int w, int h) throws JIPException {
		super(w,h);
		vecColors=new ArrayList<Color>();
	}

	/**
	 * Constructor
	 */
	public JIPImgGeometric (int w, int h, ArrayList<Color> colors) throws JIPException {
		super(w,h);
		vecColors=colors;
	}

	/**
	 * Sets the array of colors into the geometric data
	 * @param Array of Colors
	 */
	public void setColors(ArrayList<Color> colors) {
		vecColors=colors;
	}

	/**
	 * Gets the array of colors from the geometric data
	 * @return Array of Colors
	 */
	public ArrayList<Color> getColors() {
		return vecColors;
	}
	

	/**
	 * Sets the data for the geometric type
	 * @param data Data to set
	 */
	public abstract void setData (ArrayList data);
	
	/**
	 * Gets the data from the geometric type
	 * @return Geometric data
	 */
	public abstract ArrayList getData ();
	
	/**
	 * Gets the length of the geometric data
	 * @return Length of the geometric data
	 */
	public abstract int getLength();
	
}
