package javavis.jip2d.functions;

import java.util.ArrayList;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamObject;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.geometrics.JIPGeomSegment;
import javavis.jip2d.util.Line;



/**
*It detects all the possible lines present in the image. Every pixel votes for the line it
* belongs to, then we use the lines which number of votes are greater or equal than the
* input threshold.
* Lines are shown in the SEGMENT image. A results output parameter containing 
* a vector of lines represented by theta and rho is returned.<br>
* Author: José Mariano Bastida Peñalver
<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image of BIT type.<BR>
*<li>thres: Minimal number of votes to accept the line.<BR>
<BR> 
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>tSEGMENT image with detected lines.<BR>
*<li>OBJECT type vector containing the lines found.<BR><BR> 
*</ul>
*/
public class FHoughLine extends JIPFunction {
	private static final long serialVersionUID = 3684910424267717380L;
	
	public FHoughLine() {
		super();
		name = "FHoughLine";
		description = "Detects lines in an input image";
		groupFunc = FunctionGroup.Geometry;
		JIPParamInt p1 = new JIPParamInt("thres", false, true);
		p1.setDefault(30);
		p1.setDescription("Minimum number of votes");
		addParam(p1);
		
		// Return parameters
		JIPParamObject r1 = new JIPParamObject("lines", false, false);
		r1.setDescription("Lines found");
		addParam(r1);
	}

	
	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img.getType() != ImageType.BIT) 
			throw new JIPException("HoughLine can only be applied to binarized images");
		
		final int DISC_THETA = 180;  // Theta divisions (discretizations)
		final int DISC_RHO = 1000;  // Rho divisions (discretizations)

		JIPBmpBit imgBit = (JIPBmpBit)img;
		int w = img.getWidth();
		int h = img.getHeight();
		int thres = getParamValueInt("thres");

		/* It's not necessary to use all the interval for angles (2*PI). Only half of this interval (PI) is enough to detect
		 * all the lines in the image. This interval will cover from -PI/2 to PI/2
		 */ 
		double maxTheta = Math.PI / 2;
		double maxRho = Math.sqrt(w*w+h*h);  // image diagonal
		double incTheta = Math.PI / DISC_THETA;
		double incRho = 2*maxRho / DISC_RHO;
			
		// Accumulator space: 2D table that stores the number of votes (java initializes it)
		int[][] votes = new int[DISC_THETA][DISC_RHO];
		
		double dTheta, dRho;
		// Votation loop
		for (int x = 0; x < w; x++)
			for (int y = 0; y < h; y++)
				if (imgBit.getPixelBool(x,y))
					for (int theta = 0; theta < DISC_THETA; theta++) {
						dTheta = -maxTheta + theta * incTheta;   // theta range is [-maxTheta, +maxTheta]
						dRho = x*Math.cos(dTheta) + y*Math.sin(dTheta) + maxRho;  // rho in range [-maxRho, +maxRho]
						votes[theta][(int) (dRho/incRho)]++;
					}

		ArrayList<Integer> linePoints = new ArrayList<Integer>();  // cut points of lines to be drawn
		ArrayList<Line> lineParms = new ArrayList<Line>();  // (theta, rho) pairs (lines) stored for extern calls

		// loop to draw all the lines that match or exceed the threshold
		for (int theta = 0; theta < DISC_THETA; theta++)
			for (int rho = 0; rho < DISC_RHO; rho++)
				if (votes[theta][rho] >= thres) {
					dTheta = theta*incTheta - maxTheta; // retrieve "original" theta value 
					dRho = rho*incRho - maxRho;  // retrieve "original" rho value
					Line l = new Line(dTheta, dRho);
           			if (!lineParms.contains(l)) { // prevent from potential line duplicity					
           				lineParms.add(l);
           				int[] cutPoints = getCutPoints(dTheta, dRho, w, h);
           				for (int i=0; i<4; i++)
           					linePoints.add(cutPoints[i]);
           			}
				}

		setParamValue("lines", lineParms);
		JIPGeomSegment res = new JIPGeomSegment(w, h);
		res.setData(linePoints);
		return res;
	}

	/**
	 *<B>Description</B><BR>
	 *Function that returns the two points where the line intersects the edges of the image to draw it later.<br>   
	 *@param theta Angle between abscise axis and perpendicular of the line.
	 *@param rho Distance from the origin to the line.
	 *@return Returns an integer array with the two cut points.
	 */
	int[] getCutPoints(double theta, double rho, int w, int h) {
		int w1 = w-1;  // image last x-pixel
		int h1 = h-1;  // image last y-pixel
		int[] defHoriz = {0, (int)rho, w1, (int)rho};	// horizontal lines
		int[] defVert = {(int)rho, 0, (int)rho, h1};	// vertical lines
		if (Math.cos(theta) == 0)
			return defHoriz;
		else
			if (Math.sin(theta) == 0)
				return defVert;
			else {
				int[] pcp = new int[8];  // potential cut points
				int l = (int)(rho/Math.sin(theta)); // cut y-pixel on left edge of the image
				int r = (int)((rho-w1*Math.cos(theta))/Math.sin(theta)); // y-pixel right
				int u = (int)(rho/Math.cos(theta));  // x-pixel up
				int d = (int)((rho-h1*Math.sin(theta))/Math.cos(theta));  // x-pixel down
				int i=0;  // counts how many pixel coordinates are inside image width & height 
				if (l >= 0 && l <= h1)  {pcp[i++] = 0;  pcp[i++] = l;}
				if (r >= 0 && r <= h1)  {pcp[i++] = w1;  pcp[i++] = r;}			
				if (u > 0 && u < w1)  {pcp[i++] = u;  pcp[i++] = 0;}
				if (d > 0 && d < w1)  {pcp[i++] = d;  pcp[i++] = h1;}
				
			/* It corrects the particular case of the diagonals, in which the cut points are the corners. Due to discretization and
			 * later cast from double to int type, there can be an exception if 0, 1, 3, or 4 cut points (i= 0, 2, 6 or 8
			 * coordinates) are obtained; i=4 means correct (2 cut points), i=8 (4 cut points) is not correct but we can discard the
			 * two last cut points. Otherwise, the non-valid cut point is manually substituted by the correct one of the diagonal. 
			 */	if (i != 4 && i != 8) {
					if (!(l >= 0 && l <= h1))
						l = (Math.min(Math.abs(l-0),Math.abs(l-h1)) == Math.abs(l-0)) ? 0 : h1;
					if (!(r >= 0 && r <= h1))
						r = (Math.min(Math.abs(r-0),Math.abs(r-h1)) == Math.abs(r-0)) ? 0 : h1;
					pcp[0] = 0;  pcp[1] = l;  // one corner
					pcp[2] = w1;  pcp[3] = r; // the other corner		
				}						
				int[] cutPoints = new int[4];
				for (int j=0; j<4; j++)
					cutPoints[j] = pcp[j];
				return cutPoints;
			}
	}

}