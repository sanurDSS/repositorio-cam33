package javavis.jip2d.functions;

import java.util.ArrayList;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.geometrics.JIPGeomPoint;
import javavis.jip2d.base.geometrics.JIPGeomSegment;


/**
*Calculates the intersection points from a segment image.
*The image must be of Segment type.<BR>
*Applicable to: SEGMENT<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Point of image which represents intersection points.<BR><BR>
*</ul>
*/
public class FInterSegment extends JIPFunction {
	private static final long serialVersionUID = 5471936099222093701L;
	
	public FInterSegment() {
		super();
		name = "FInterSegment";
		description = "Calculates segment intersection";
		groupFunc = FunctionGroup.Geometry;
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		int ax0, ay0, ax1, ay1, sx0, sy0, sx1, sy1;

		if (img.getType() != ImageType.SEGMENT) 
			throw new JIPException("InterSegment can not be applied to this image format");
		
		JIPGeomSegment imgSeg = (JIPGeomSegment)img;

		ArrayList<Integer> segments = imgSeg.getData();
		ArrayList<Integer> points = new ArrayList<Integer>();
		int tam = segments.size();
		int[] dev = new int[2];
		for (int i = 0; i < tam / 4; i++) {
			ax0 = segments.get(4 * i);
			ay0 = segments.get(4 * i + 1);
			ax1 = segments.get(4 * i + 2);
			ay1 = segments.get(4 * i + 3);
			for (int j = i + 1; j < tam / 4; j++) {
				sx0 = segments.get(4 * j);
				sy0 = segments.get(4 * j + 1);
				sx1 = segments.get(4 * j + 2);
				sy1 = segments.get(4 * j + 3);
				if (intersect(ax0, ay0, ax1, ay1, sx0, sy0, sx1, sy1)) {
					cutPoint(ax0, ay0, ax1, ay1, sx0, sy0, sx1, sy1, dev);
					points.add(dev[0]);
					points.add(dev[1]);
				}
			}
		}
		JIPGeomPoint res = new JIPGeomPoint(img.getWidth(), img.getHeight());
		res.setData(points);
		return res;
	}

	/**
	*<P><FONT COLOR="RED">
	*<B>Description</B><BR>
	*<P><FONT COLOR="BLUE">
	*Method to calculate the overlap point between two segments. When we can use
	*this method, we must check that the overlap point exist before.<BR>
	*<ul><B>Input parametes:</B><BR>
		*<li>ax1: X coordinate of initial point of first segment.<BR>
		*<li>ay1: Y coordinate of initial point of first segment.<BR>
		*<li>bx1: X coordinate of final point of the first segment.<BR>
		*<li>by1: Y coordinate of final point of the first segment.<BR>
		*<li>cx1: X coordinate of initial point of second segment.<BR>
		*<li>cy1: Y coordinate of initial point of second segment.<BR>
		*<li>dx1: X coordinate of final point of the second segment.<BR>
		*<li>dy1: Y coordinate of final point of the second segment.<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>devx: Put in the class variable the x coordinate of the point which belongs to
	*the intersecion<BR>
	*<li>devy: Put in the class variable the y coordinate of the point which belongs to
	*the intersecion<BR><BR>
	*</ul>
	*/
	void cutPoint(int ax, int ay, int bx, int by, int cx, int cy,
		int dx, int dy, int[] dev) {
		double m1, n1, m2, n2, xx, yy;

		if ((ax - bx) == 0) {
			xx = ax;
			m2 = (cy - dy) / (double)(cx - dx);
			n2 = cy - ((cy - dy) / (double)(cx - dx)) * cx;
			yy = m2 * ax + n2;
		} else if ((cx - dx) == 0) {
			xx = cx;
			m1 = (ay - by) / (double)(ax - bx);
			n1 = ay - ((ay - by) / (double)(ax - bx)) * ax;
			yy = m1 * dx + n1;
		} else {
			m1 = (ay - by) / (double)(ax - bx);
			n1 = ay - ((ay - by) / (double)(ax - bx)) * ax;
			m2 = (cy - dy) / (double)(cx - dx);
			n2 = cy - ((cy - dy) / (double)(cx - dx)) * cx;

			xx = (n2 - n1) / (double)(m1 - m2);
			yy = m1 * xx + n1;
		}
		dev[0] = (int) xx;
		dev[1] = (int) yy;
	}

	/**
	*<P><FONT COLOR="RED">
	*<B>Description</B><BR>
	*<P><FONT COLOR="BLUE">	
	*Method which shows us if a point is on the left of two points.<BR>
	*<ul><B>Input parameters:</B><BR>
		*<li>ax: X coordinate of point number 1<BR>
		*<li>ay: Y coordinate of point number 1<BR>
		*<li>bx: X coordinate of point number 2<BR>
		*<li>by: Y coordinate of point number 2<BR>
		*<li>cx: X coordinate of point number 3<BR>
		*<li>cy: Y coordinate of point number 3<BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>return: Returns true if point is on the left<BR><BR>
	*</ul>
	*/
	private boolean left(int ax, int ay, int bx, int by, int cx, int cy) {
		return area2(ax, ay, bx, by, cx, cy) > 0;
	}

	/**
	*<P><FONT COLOR="RED">
	*<B>Description</B><BR>
	*<P><FONT COLOR="BLUE">	
	*Method to calculate if two segments intersects<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>ax0: X coordinate of the initial point in the first segment<BR>
	*<li>ay0: Y coordinate of the initial point in the first segment<BR>
		*<li>ax1: X coordinate of final point in the first segment<BR>
		*<li>ay1: Y coordinate of final point in the first segment<BR>
		*<li>sx0: X coordinate of initial point in the second segment<BR>
		*<li>sy0: Y coordinate of initial point in the second segment<BR>
		*<li>sx1: X coordinate of final point in the second segment<BR>
		*<li>sy1: Y coordinate of final point in the second segment<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>@return True if there is same intersection.<BR><BR>
	*</ul>
	*/
	private boolean intersect(int ax0, int ay0, int ax1, int ay1,
		int sx0, int sy0, int sx1, int sy1) {

		if (colinear(ax0, ay0, ax1, ay1, sx0, sy0) || colinear(ax0, ay0, ax1, ay1, sx1, sy1)
			|| colinear(sx0, sy0, sx1, sy1, ax0, ay0) || colinear(sx0, sy0, sx1, sy1, ax1, ay1))
			return false;

		return xor(left(ax0, ay0, ax1, ay1, sx0, sy0), left(ax0, ay0, ax1, ay1, sx1, sy1))
			&& xor(left(sx0, sy0, sx1, sy1, ax0, ay0), left(sx0, sy0, sx1, sy1, ax1, ay1));
	}

	/**
	*<P><FONT COLOR="RED">
	*<B>Description</B><BR>
	*<P><FONT COLOR="BLUE">	
	*Method which make a XOR function between two booleans<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>a: Boolean number 1<BR>
	*<li>b: Boolean number 2<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>@return True or false depending on the result of XOR function<BR><BR>
	*</ul>
	*/
	private boolean xor(boolean a, boolean b) {
		return (!a && b) || (a && !b);
	}

	/**
	*<P><FONT COLOR="RED">
	*<B>Description</B><BR>
	*<P><FONT COLOR="BLUE">	
	*Returns if three points are collinear<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>p1x: X coordinate of point number 1<BR>
	*<li>p1y: Y coordinate of point number 1<BR>
		*<li>p2x: X coordinate of the point number 2<BR>
		*<li>p2y: Y coordinate of the point number 2<BR>
		*<li>p3x: X coordinate of the point number 3<BR>
		*<li>p3y: Y coordinate of the point number 3<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>@return Returns true if the points are collinears<BR><BR>
	*</ul>
	*/
	private boolean colinear(int p1x, int p1y, int p2x, int p2y, int p3x, int p3y) {
		return area2(p1x, p1y, p2x, p2y, p3x, p3y) == 0;
	}

	/**
	*<P><FONT COLOR="RED">
	*<B>Description</B><BR>
	*<P><FONT COLOR="BLUE">    
	*Returns the area (with sign) of the triangle formed by by three points.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>ax: X coordinate of point number 1<BR>
	*<li>ay: Y coordinate of point number 1<BR>
	*<li>bx: X coordinate of point number 2<BR>
	*<li>by: Y coordinate of point number 2<BR>
	*<li>cx: X coordinate of point number 3<BR>
	*<li>cy: Y coordinate of point number 3<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>@return Return the area of the triangle<BR><BR>
	*</ul>
	*/
	private int area2(int ax, int ay, int bx, int by, int cx, int cy) {
		return ax * by - ay * bx + ay * cx - ax * cy + bx * cy - cx * by;
	}

}
