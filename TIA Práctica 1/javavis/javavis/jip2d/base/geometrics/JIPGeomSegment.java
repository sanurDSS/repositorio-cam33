package javavis.jip2d.base.geometrics;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import javavis.base.ImageType;
import javavis.base.JIPException;

/**
* Abstract class to define a SEGMENT geometric object. It contains geometric data
*/
public class JIPGeomSegment extends JIPImgGeometric implements Serializable {
	private static final long serialVersionUID = -7755470955703916794L;

	/**
	 * Data
	 */
	ArrayList<Integer> data;
	
	/**
	 * Constructor
	 */
	public JIPGeomSegment (JIPGeomSegment img) throws JIPException {
		super(img);
		data=new ArrayList<Integer>((ArrayList<Integer>)img.getData());
	}

	/**
	 * Constructor
	 */
	public JIPGeomSegment (int w, int h) throws JIPException {
		super(w,h);
		data=new ArrayList<Integer>();
	}

	/**
	 * Constructor
	 */
	public JIPGeomSegment (int w, int h, ArrayList<Color> colors) throws JIPException {
		super(w,h, colors);
		data=new ArrayList<Integer>();
	}
	
	/**
	 * Adds a segment into the list of segment
	 */
	public void addSegment (int x0, int y0, int x1, int y1) throws JIPException {
		addSegment(x0, y0, x1, y1, null);
	}
	
	/**
	 * Adds a segment into the list of segment, with a associated color
	 */
	public void addSegment (int x0, int y0, int x1, int y1, Color col) throws JIPException {
		if (x0 < 0 || y0 < 0 || x0 > width - 1 || y0 > height - 1 ||
				x1 < 0 || y1 < 0 || x1 > width - 1 || y1 > height - 1)
			throw new JIPException("JIPGeomSegment.addSegment: segment coordinates exceeds the image dimensions");
		data.add(x0);
		data.add(y0);
		data.add(x1);
		data.add(y1);
		vecColors.add(col);
	}
	
	/**
	 * Gets a segment as a int array
	 * @param index Indicates the index (from 0 to n-1)
	 * @return int array: first element, X coordinate of the first point, second, Y coordinate of the first, 
	 * third element, X coordinate of the second point, fourth, Y coordinate of the second.
	 * @throws JIPException When index is out of bounds
	 */
	public int[] getSegment (int index) throws JIPException {
		if (index <0 || index > data.size()/4-1) {
			throw new JIPException ("JIPGeomSegment.getSegment: index of out bounds");
		}
		int[] res = new int[4];
		res[0] = data.get(4*index);
		res[1] = data.get(4*index+1);
		res[2] = data.get(4*index+2);
		res[3] = data.get(4*index+3);
		
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
		return ImageType.SEGMENT;
	}

}
