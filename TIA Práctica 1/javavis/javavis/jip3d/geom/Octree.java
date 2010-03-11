package javavis.jip3d.geom;

import java.util.ArrayList;


public class Octree {


	private float resolution = 0.05f;
	final static private int num_hijos = 8;

	private Point3D node;
	private Octree []hijos;
	Point3D ancla; //limite inferior
	Point3D limite; //limite superior
	boolean is_leaf;
	int num_points;


	/**
	 * Constructor de la clase. Recibe como parametro los límites inferior y superior del area delimitada
	 * por este octree. Si no es hoja, el nodo contendra los datos del punto medio
	 * @param l_i limite inferior
	 * @param l_s limite superior
	 */
	public Octree(Point3D l_i, Point3D l_s, float res)
	{
		double difX, difY, difZ;
		hijos = new Octree[8];
		for(int i=0;i<num_hijos;i++) hijos[i] = null;
		ancla = l_i;
		limite = l_s;
		num_points = 0;
		node = new Point3D();
		resolution = res;

		difX = Math.abs(ancla.getX()- limite.getX());
		difY = Math.abs(ancla.getY()- limite.getY());
		difZ = Math.abs(ancla.getZ()- limite.getZ());
		if(resolution>difX || resolution>difY || resolution>difZ)
			is_leaf = true;
		else
		{
			is_leaf = false;
			node.setX( (limite.getX() + ancla.getX()) / 2.0f );
			node.setY( (limite.getY() + ancla.getY()) / 2.0f );
			node.setZ( (limite.getZ() + ancla.getZ()) / 2.0f );
		}
	}

	public Octree(Point3D p, Point3D l_i, Point3D l_s, float res)
	{
		double difX, difY, difZ;
		Point3D sig_i, sig_s;
		hijos = new Octree[8];
		for(int i=0;i<num_hijos;i++) hijos[i] = null;
		ancla = l_i;
		limite = l_s;
		num_points = 0;
		node = new Point3D();
		resolution = res;
		int posicion;

		sig_i = new Point3D();
		sig_s = new Point3D();

		difX = Math.abs(ancla.getX() - limite.getX());
		difY = Math.abs(ancla.getY() - limite.getY());
		difZ = Math.abs(ancla.getZ() - limite.getZ());
		if(resolution>difX || resolution>difY || resolution>difZ)
		{
			is_leaf = true;
			node.addData(p,	num_points);
			num_points++;
		}
		else
		{
			is_leaf = false;
			node.setX( (limite.getX() + ancla.getX()) / 2.0f );
			node.setY( (limite.getY() + ancla.getY()) / 2.0f );
			node.setZ( (limite.getZ() + ancla.getZ()) / 2.0f );
			posicion = this.buscar(p, sig_i, sig_s);
			if(hijos[posicion]!=null)
				hijos[posicion].insert(p);
			else hijos[posicion] = new Octree(p, sig_i, sig_s, resolution);
		}
	}

	/**
	 * Metodo insertarPunto. Intenta insertar el punto que le pasamos por parametro en la
	 * rejilla.
	 * @param p - El punto que queremos insertar
	 */
	public void insert(Point3D p)
	{
		int position;
		Point3D sig_i, sig_s;

		sig_i = new Point3D();
		sig_s = new Point3D();

		if(is_leaf)
		{
			node.addData(p, num_points);
			num_points++;
		}
		else
		{
			position = this.buscar(p, sig_i, sig_s);
			if(hijos[position]!=null)
				hijos[position].insert(p);
			else hijos[position] = new Octree(p, sig_i, sig_s, resolution);
		}
	}

	public ArrayList<Point3D>getAll()
	{
		ArrayList<Point3D> ret = new ArrayList<Point3D>();
		int cont;

		if(this.is_leaf)
			ret.add(node);
		else
		{
			for(cont=0;cont<8;cont++)
			{
				if(hijos[cont]!=null)
				{
					ret.addAll(hijos[cont].getAll());
				}
			}
		}
		return ret;
	}

	private int buscar(Point3D p, Point3D sig_l_i, Point3D sig_l_s)
	{
		int ret;

		if(p.getX() < node.getX())
		{
			if(p.getY() < node.getY())
			{
				if(p.getZ() < node.getZ())
				{
					ret = 0;
					sig_l_i.setX(ancla.getX());
					sig_l_i.setY(ancla.getY());
					sig_l_i.setZ(ancla.getZ());
					sig_l_s.setX(node.getX());
					sig_l_s.setY(node.getY());
					sig_l_s.setZ(node.getZ());
				}
				else
				{
					ret = 1;
					sig_l_i.setX(ancla.getX());
					sig_l_i.setY(ancla.getY());
					sig_l_i.setZ(node.getZ());
					sig_l_s.setX(node.getX());
					sig_l_s.setY(node.getY());
					sig_l_s.setZ(limite.getZ());
				}
			}
			else
			{
				if(p.getZ() < node.getZ())
				{
					ret = 2;
					sig_l_i.setX(ancla.getX());
					sig_l_i.setY(node.getY());
					sig_l_i.setZ(ancla.getZ());
					sig_l_s.setX(node.getX());
					sig_l_s.setY(limite.getY());
					sig_l_s.setZ(node.getZ());
				}
				else
				{
					ret = 3;
					sig_l_i.setX(ancla.getX());
					sig_l_i.setY(node.getY());
					sig_l_i.setZ(node.getZ());
					sig_l_s.setX(node.getX());
					sig_l_s.setY(limite.getY());
					sig_l_s.setZ(limite.getZ());
				}
			}
		}
		else
		{
			if(p.getY() < node.getY())
			{
				if(p.getZ() < node.getZ())
				{
					ret = 4;
					sig_l_i.setX(node.getX());
					sig_l_i.setY(ancla.getY());
					sig_l_i.setZ(ancla.getZ());
					sig_l_s.setX(limite.getX());
					sig_l_s.setY(node.getY());
					sig_l_s.setZ(node.getZ());
				}
				else
				{
					ret = 5;
					sig_l_i.setX(node.getX());
					sig_l_i.setY(ancla.getY());
					sig_l_i.setZ(node.getZ());
					sig_l_s.setX(limite.getX());
					sig_l_s.setY(node.getY());
					sig_l_s.setZ(limite.getZ());
				}
			}
			else
			{
				if(p.getZ() < node.getZ())
				{
					ret = 6;
					sig_l_i.setX(node.getX());
					sig_l_i.setY(node.getY());
					sig_l_i.setZ(ancla.getZ());
					sig_l_s.setX(limite.getX());
					sig_l_s.setY(limite.getY());
					sig_l_s.setZ(node.getZ());
				}
				else
				{
					ret = 7;
					sig_l_i.setX(node.getX());
					sig_l_i.setY(node.getY());
					sig_l_i.setZ(node.getZ());
					sig_l_s.setX(limite.getX());
					sig_l_s.setY(limite.getY());
					sig_l_s.setZ(limite.getZ());
				}
			}
		}

		return ret;
	}
}
