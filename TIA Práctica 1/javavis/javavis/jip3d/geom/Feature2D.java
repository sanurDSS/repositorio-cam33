package javavis.jip3d.geom;


import java.io.IOException;
import java.io.Serializable;
import java.io.StreamTokenizer;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

public class Feature2D implements Serializable {
	private static final long serialVersionUID = -4449486822872926311L;

	DenseDoubleMatrix1D data;
	
	public int posx, posy;
	public double orientation, scale;
	public int descriptor[];
	
	public int visited;

	/**
	 * Default constructor. Creates a void 2D feature.
	 *
	 */
	public Feature2D()
	{
		data = new DenseDoubleMatrix1D(4);
		data.assign(0);
		data.set(3, 1);
		posx = posy = 0;
		visited = 0;
		orientation = 0.0;
		scale = 0.0;
		descriptor = new int[1];
	}

	public Feature2D(Feature2D source)
	{
		data = new DenseDoubleMatrix1D(4);
		data.assign(source.data);
		posx = source.posx;
		posy = source.posy;
		visited = source.visited;
		orientation = source.orientation;
		scale = source.scale;
		descriptor = (int[])source.descriptor.clone();
	}
	
	public Feature2D(StreamTokenizer st, int lengthDesc) {
		visited = 0;

		try {
			st.nextToken();
			posy = (int)Math.round(st.nval);
			st.nextToken();
			posx = (int)Math.round(st.nval);
			
			st.nextToken();
			scale = st.nval;
			st.nextToken();
			orientation = st.nval;
			
			descriptor = new int[lengthDesc];
			for (int i=0; i<lengthDesc; i++) {
				st.nextToken();
				descriptor[i] = (int)st.nval;
			}
			
			data = new DenseDoubleMatrix1D(4);
			data.set(0, 0);
			data.set(1, 0);
			data.set(2, 0);
			data.set(3, 1);
		} catch (IOException e) {
			System.out.println("Error reading Point3D from file");
		}
	}

	/**
	 * Get point coordinates into a vector
	 * @return a double vector with point coordinates inside
	 */
	public double[] getCoords()
	{
		double []array =  data.toArray();
		double []ret = new double[3];
		ret[0] = array[0];
		ret[1] = array[1];
		ret[2] = array[2];

		return ret;
	}

	public double getX()
	{
		return data.get(0);
	}
	public double getY()
	{
		return data.get(1);
	}
	public double getZ()
	{
		return data.get(2);
	}

	public void setX(double val)
	{
		data.set(0, val);
	}
	public void setY(double val)
	{
		data.set(1, val);
	}
	public void setZ(double val)
	{
		data.set(2, val);
	}



	/**
	 * Get point coordinates into a vector
	 * @return a float vector with point coordinates inside
	 */
	public float[] getCoordsf()
	{
		double X = data.get(0);
		double Y = data.get(1);
		double Z = data.get(2);
		return new float[] {(float)X, (float)Y, (float)Z};
	}

	/**
	 * Returns a String that contains the values of the point
	 * @return a String with the values
	 */
	public String toString()
	{
		String ret;
		float EPS = 0.001f;
		float x,y,z;
		double X = data.get(0);
		double Y = data.get(1);
		double Z = data.get(2);

		if(X>-EPS && X<EPS)
			x=0;
		else x=(float)X;
		if(Y>-EPS && Y<EPS)
			y=0;
		else y=(float)Y;
		if(Z>-EPS && Z<EPS)
			z=0;
		else z=(float)Z;
		ret=x+" "+y+" "+z+" "+posy+" "+posx;
		return ret;
	}

	public Feature2D addPoint(Feature2D p)
	{
		Feature2D ret = new Feature2D(this);
		ret.data.assign(p.data, cern.jet.math.Functions.plus);
		return ret;
	}

	public Feature2D subPoint(Feature2D p)
	{
		Feature2D ret = new Feature2D(this);
		ret.data.assign(p.data, cern.jet.math.Functions.minus);
		return ret;
	}

	/**
	 * Returns euclidean distance from this point to the coordinates origin.
	 * @return The distance computed
	 */
	public double getOriginDistance()
	{
		double X = data.get(0);
		double Y = data.get(1);
		double Z = data.get(2);
		return Math.sqrt(X*X + Y*Y + Z*Z);
	}

	/**
	 * Returns euclid distance form this point to the point received as parameter
	 * @param x X coordinate of the second point
	 * @param y Y coordinate of the second point
	 * @param z Z coordinate of the second point
	 * @return The distance computed
	 */
	public double getDistance(double x, double y, double z)
	{
		return Math.sqrt(getDistance2(x, y, z));
	}

	/**
	 * Returns squared euclid distance form this point to a second point received as parameter
	 * @param x X coordinate of the second point
	 * @param y Y coordinate of the second point
	 * @param z Z coordinate of the second point
	 * @return The distance computed
	 */
	public double getDistance2(double x, double y, double z)
	{
		double ret;
		double a, b, c;
		double X = data.get(0);
		double Y = data.get(1);
		double Z = data.get(2);
		a = X - x;
		b = Y - y;
		c = Z- z;
		ret = a*a + b*b + c*c;

		return ret;
	}

	public double getDistance(Feature2D p)
	{
		return getDistance(p.getX(), p.getY(), p.getZ());
	}

	public double getDistance2(Feature2D p)
	{
		return getDistance2(p.getX(), p.getY(), p.getZ());
	}

	public void applyTransform(MyTransform t)
	{
		DoubleMatrix1D result;
		result = t.mat.zMult(data, null);
		data.assign(result);
	}

	public int isVisited() {
		return visited;
	}

	public void setVisited(int visited) {
		this.visited = visited;
	}

}

