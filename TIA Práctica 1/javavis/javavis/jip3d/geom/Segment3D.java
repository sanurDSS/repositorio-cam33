package javavis.jip3d.geom;

import java.io.Serializable;
import java.io.StreamTokenizer;

public class Segment3D implements Serializable {


	private static final long serialVersionUID = 5109956589849799681L;

	public Point3D begin;
	public Point3D end;

	public Segment3D()
	{
		begin = new Point3D();
		end = new Point3D();
	}

	public Segment3D(Segment3D source)
	{
		begin = new Point3D(source.begin);
		end = new Point3D(source.end);
	}

	public Segment3D(Point3D b, Point3D e)
	{
		begin = b;
		end = e;
	}

	public Segment3D(Feature2D b, Feature2D e)
	{
		begin = new Point3D(b.getCoords());
		end = new Point3D(e.getCoords());
	}

	public Segment3D(StreamTokenizer st)
	{
		begin = new Point3D(st);
		end = new Point3D(st);
	}

	public void applyTransform(MyTransform tr)
	{
		begin.applyTransform(tr);
		end.applyTransform(tr);
	}

	public String toString()
	{
		String ret = begin.toString();
		ret += "\n";
		ret += end.toString();
		return ret;
	}

}
