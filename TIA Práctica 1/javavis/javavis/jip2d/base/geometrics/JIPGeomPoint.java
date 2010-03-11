package javavis.jip2d.base.geometrics;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import javavis.base.ImageType;
import javavis.base.JIPException;

/**
* Abstract class to define a POINT geometric object. It contains geometric data
*/
public class JIPGeomPoint extends JIPImgGeometric implements Serializable {
	private static final long serialVersionUID = -7755470955703916794L;

	/**
	 * Data
	 */
	ArrayList<Integer> data;
	
	/**
	 * Constructor
	 */
	public JIPGeomPoint (JIPGeomPoint img) throws JIPException {
		super(img);
		data=new ArrayList<Integer>((ArrayList<Integer>)img.getData());
	}

	/**
	 * Constructor
	 */
	public JIPGeomPoint (int w, int h) throws JIPException {
		super(w,h);
		data=new ArrayList<Integer>();
	}

	/**
	 * Constructor
	 */
	public JIPGeomPoint (int w, int h, ArrayList<Color> colors) throws JIPException {
		super(w,h, colors);
		data=new ArrayList<Integer>();
	}
	
	/**
	 * Adds a point into the list of points
	 */
	public void addPoint (int x, int y) throws JIPException {
		addPoint(x, y, null);
	}
	
	/**
	 * Adds a point into the list of points, with a associated color
	 */
	public void addPoint (int x, int y, Color col) throws JIPException {
		if (x < 0 || y < 0 || x > width - 1 || y > height - 1)
			throw new JIPException("JIPGeomPoint.addPoint: point coordinates exceeds the image dimensions");
		data.add(x);
		data.add(y);
		vecColors.add(col);
	}
	
	/**
	 * Gets a point as a int array
	 * @param index Indicates the index (from 0 to n-1)
	 * @return int array: first element, X coordinate, second, Y coordinate
	 * @throws JIPException When index is out of bounds
	 */
	public int[] getPoint (int index) throws JIPException {
		if (index <0 || index > data.size()/2-1) {
			throw new JIPException ("JIPGeomPoint.getPoint: index of out bounds");
		}
		int[] res = new int[2];
		res[0] = data.get(2*index);
		res[1] = data.get(2*index+1);
		
		return res;
	}

	public void setData (ArrayList data) {
		this.data=(ArrayList<Integer>)data;
	}
	
	public ArrayList getData () {
		return data;
	}
	
	public int getLength() {
		return data.size();
	}
	
	public ImageType getType() {
		return ImageType.POINT;
	}

}
