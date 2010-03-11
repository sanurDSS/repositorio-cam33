package javavis.jip2d.base.geometrics;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import javavis.base.ImageType;
import javavis.base.JIPException;

/**
* Abstract class to define a EDGES geometric object. It contains geometric data
*/
public class JIPGeomEdges extends JIPImgGeometric implements Serializable {
	private static final long serialVersionUID = -7755470955703916794L;

	/**
	 * Data
	 */
	ArrayList<ArrayList<Integer>> data;
	
	/**
	 * Constructor
	 */
	public JIPGeomEdges (JIPGeomEdges img) throws JIPException {
		super(img);
		data=new ArrayList<ArrayList<Integer>>((ArrayList<ArrayList<Integer>>)img.getData());
	}

	/**
	 * Constructor
	 */
	public JIPGeomEdges (int w, int h) throws JIPException {
		super(w,h);
		data=new ArrayList<ArrayList<Integer>>();
	}

	/**
	 * Constructor
	 */
	public JIPGeomEdges (int w, int h, ArrayList<Color> colors) throws JIPException {
		super(w,h, colors);
		data=new ArrayList<ArrayList<Integer>>();
	}
	
	/**
	 * Adds an edge at the end of the list
	 * @param d A list of integers defining an edge
	 * @throws JIPException
	 */
	public void addEdge (ArrayList<Integer> d) throws JIPException {
		if (d==null || d.size()==0) {
			throw new JIPException ("JIPGeomEdges.addEdge: data null or empty");
		}
		data.add(d);
	}
	
	/**
	 * Gets an edge
	 * @param index Index of the edge to return
	 * @return A list of integers, representing an edge
	 * @throws JIPException
	 */
	public ArrayList<Integer> getEdge (int index) throws JIPException {
		if (index < 0 || index > data.size()) {
			throw new JIPException ("JIPGeomEdge.getEdge: index out of bounds");
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
		return ImageType.EDGES;
	}

}
