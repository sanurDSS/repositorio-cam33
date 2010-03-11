package javavis.jip2d.base.geometrics;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import javavis.base.ImageType;
import javavis.base.JIPException;

/**
* Abstract class to define a POLY geometric object. It contains geometric data
*/
public class JIPGeomPoly extends JIPImgGeometric implements Serializable {
	private static final long serialVersionUID = -7755470955703916794L;

	/**
	 * Data
	 */
	ArrayList<ArrayList<Integer>> data;
	
	/**
	 * Constructor
	 */
	public JIPGeomPoly (JIPGeomPoly img) throws JIPException {
		super(img);
		data=new ArrayList<ArrayList<Integer>>((ArrayList<ArrayList<Integer>>)img.getData());
	}

	/**
	 * Constructor
	 */
	public JIPGeomPoly (int w, int h) throws JIPException {
		super(w,h);
		data=new ArrayList<ArrayList<Integer>>();
	}

	/**
	 * Constructor
	 */
	public JIPGeomPoly (int w, int h, ArrayList<Color> colors) throws JIPException {
		super(w,h, colors);
		data=new ArrayList<ArrayList<Integer>>();
	}
	
	/**
	 * Adds an poly at the end of the list
	 * @param d A list of integers defining a polygon
	 * @throws JIPException
	 */
	public void addPoly (ArrayList<Integer> d) throws JIPException {
		if (d==null || d.size()==0) {
			throw new JIPException ("JIPGeomPoly.addPoly: data null or empty");
		}
		data.add(d);
	}
	
	/**
	 * Gets a polygon
	 * @param index Index of the polygon to return
	 * @return A list of integers, representing a polygon
	 * @throws JIPException
	 */
	public ArrayList<Integer> getPoly (int index) throws JIPException {
		if (index < 0 || index > data.size()) {
			throw new JIPException ("JIPGeomPoly.getPoly: index out of bounds");
		}
		return data.get(index);
	}
	

	public void setData (ArrayList data) {
		this.data=(ArrayList<ArrayList<Integer>>)data;
	}
	
	public ArrayList getData () {
		return data;
	}
	
	public int getLength() {
		return data.size();
	}
	
	public ImageType getType() {
		return ImageType.POLY;
	}

}
