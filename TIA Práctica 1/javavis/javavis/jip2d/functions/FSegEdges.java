package javavis.jip2d.functions;

import java.util.ArrayList;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.geometrics.JIPGeomEdges;
import javavis.jip2d.base.geometrics.JIPGeomSegment;


/**
*From an edge type image, it obtains a segment image.</B>
*Applicable to: EDGES<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>accuracy: Lowest desviation from the pixel value.<BR>
*<li>granularity: Lowest length of segment during recursive subdivision.<BR>
*<li>magnitude: Lowest magnitude of actual segment.<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Segment type image.<BR><BR>
*</ul>
*/
public class FSegEdges extends JIPFunction {
	private static final long serialVersionUID = -7418005545553156429L;
	/** Number of breakpoints
	 */
	private int nbreakpoints;
	
	public FSegEdges() {
		super();
		name = "FSegEdges";
		description = "Obtains segments from edges.";
		groupFunc = FunctionGroup.Edges;
		
		JIPParamFloat p1 = new JIPParamFloat("accuracy", false, true);
		p1.setDefault(1.0f);
		p1.setDescription("Lowest deviation from the pixel measure");
		JIPParamInt p2 = new JIPParamInt("granularity", false, true);
		p2.setDefault(4);
		p2.setDescription("Lowest length of segment during recursive subdivision.");
		JIPParamInt p3 = new JIPParamInt("magnitude", false, true);
		p3.setDefault(2);
		p3.setDescription("Lowest magnitude of actual segment.");

		addParam(p1);
		addParam(p2);
		addParam(p3);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img.getType() == ImageType.EDGES) {
			ArrayList<Integer> segments = new ArrayList<Integer>();
			ArrayList<ArrayList<Integer>> edges = (ArrayList<ArrayList<Integer>>)((JIPGeomEdges)img).getData();
			ArrayList<Integer> seg;
			float accuracy = getParamValueFloat("accuracy");
			int granularity = getParamValueInt("granularity");
			int magnitude = getParamValueInt("magnitude");
			for (int r = 0; r < edges.size(); r++) {
				seg = new ArrayList<Integer>();
				segEdgesIntoLines(edges.get(r), accuracy, magnitude, granularity, seg);
				segments.addAll(seg);
			}
			JIPGeomSegment res = (JIPGeomSegment)JIPImage.newImage(img.getWidth(), img.getHeight(), ImageType.SEGMENT);
			res.setData(segments);
			return res;
		} 
		else 
			throw new JIPException("SegEdges can not be applied to this image format");
	}

	/**
	*Method which process a particular Edge.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>aux: Edge vector to process<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>Loads the result into class variables.<BR><BR>
	*</ul>
	*/
	void segEdgesIntoLines(ArrayList<Integer> edge, float accuracy, int magnitude, 
			int granularity, ArrayList<Integer> segment) {
		if (edge.size() <= 2) return;
		int []breakpoints = new int[edge.size() / 2];
		breakpoints[0] = 0;
		nbreakpoints=1;
		splitSegment(0, (edge.size() / 2)-1, accuracy, granularity, edge, 
				breakpoints);
		breakpoints[nbreakpoints] = (edge.size() / 2) - 1;

		for (int i = 0; i < nbreakpoints; i++) {
			if (magnitude > 0 && (edge.size() / 2) > 2) {
				float t = 0.0f;
				for (int j = breakpoints[i]; j <= breakpoints[i + 1]; j++)
					t += Math.abs(edge.get(j));
				if (t / (breakpoints[i + 1] - breakpoints[i]) < magnitude)
					continue;
			}
			segment.add(edge.get(breakpoints[i] * 2));
			segment.add(edge.get(breakpoints[i] * 2 + 1));
			segment.add(edge.get(breakpoints[i + 1] * 2));
			segment.add(edge.get(breakpoints[i + 1] * 2 + 1));
		}
	}

	/**
	*Split an edge depending on the best ratio of deviation/length from the segment.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>first: First point of the Edge to use (Initial segment point)<BR>
	*<li>last:  Last point of the Edge to use (Final segment point)<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>Returns the highs length/ratio of the segment deviation.<BR><BR>
	*</ul>
	*/
	float splitSegment(int first, int last, float accuracy, int granularity,
			ArrayList<Integer> edge, int []breakpoints) {
		int save_nbreakpoints = nbreakpoints;
		float sig1, sig2, maxsig;
		float []sig = new float[1];

		if (last - first <= granularity)
			return 0.0f;
		int maxp = maxPoint(first, last, sig, accuracy, edge);
		sig1 = splitSegment(first, maxp, accuracy, granularity, edge, 
				breakpoints);
		breakpoints[nbreakpoints++] = maxp;
		sig2 = splitSegment(maxp, last, accuracy, granularity, edge, 
				breakpoints);
		maxsig = sig1 > sig2 ? sig1 : sig2;

		if (maxsig > sig[0]) 
			return maxsig;
		else {
			nbreakpoints=save_nbreakpoints;
			return sig[0];
		}
	}

	/**
	*Returns the position of the most remote point in relation to the line which
	*link to the first point and the last edge and it is contented in the edge.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>first: First point of the Edge to work (initial segment point)<BR>
	*<li>last:  Last ppoint of the Edge to work (final segment point)<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>Returns the position of the remote point.
	*<li>Returns the square of the length using "sig" class variable.<BR><BR>
	*</ul>
	*/
	int maxPoint(int first, int last, float[] sig, float accuracy, ArrayList<Integer> edge) {
		int maxp; 
		float maxdev=0.0f;
		int x0 = edge.get(first * 2);
		int y0 = edge.get(first * 2 + 1);
		int dx = edge.get(last * 2) - x0;
		int dy = edge.get(last * 2 + 1) - y0;

		for (int i = maxp = first + 1; i < last; i++) {
			float px = edge.get(i * 2) - x0;
			float py = edge.get(i * 2 + 1) - y0;

			double t = (dx * px + dy * py) / (double) (dx * dx + dy * dy);
			px -= dx * t;
			py -= dy * t;
			float dev = px * px + py * py;

			if (dev > maxdev) {
				maxdev = dev;
				maxp = i;
			}
		}

		if (maxdev < accuracy)
			maxdev = accuracy;

		sig[0]=(dx * dx + dy * dy) / maxdev;
		return maxp;
	}
}
