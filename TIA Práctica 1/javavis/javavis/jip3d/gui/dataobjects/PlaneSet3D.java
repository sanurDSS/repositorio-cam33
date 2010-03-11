package javavis.jip3d.gui.dataobjects;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;

import javavis.jip3d.geom.MyTransform;
import javavis.jip3d.geom.MyTransform3D;
import javavis.jip3d.geom.Plane3D;
import javavis.jip3d.geom.Point3D;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;


public class PlaneSet3D extends ScreenData {
	private static final long serialVersionUID = 7860690628252146121L;

	public int divisions = 8;

	public PlaneSet3D(ScreenOptions opt) {
		super(opt);
		opt.color = new Color3f(0,0,1);
		this.setType(ScreenOptions.tPLANARSET3D);
	}

	@Override
	protected TransformGroup paint() {
		TransformGroup tgRet=new TransformGroup();
		Shape3D forma;
		int []strip;
		int tam;
		LineStripArray limites;
		Point3f []puntos = new Point3f[1];
		ArrayList <Point3f>apuntos = new ArrayList<Point3f>();
		double inc_angle = 2*Math.PI/divisions;
		int cont, i;
		Object []elements = data.elements();
		Plane3D plano;
		Point3D p3d, p3d1;
		strip = new int[elements.length];
		double []normal;
		double rad, a, aux;
		double []V = new double[3];
		double angle = 0;
		MyTransform t3d;
		double [][]matriz;

		for(cont=0;cont<elements.length;cont++)
		{
			plano = (Plane3D)elements[cont];
			if(plano.bounded)
			{
				tam = plano.bounds.size();
				strip[cont] = tam + 1;
				for(i=0;i<tam;i++)
				{
					p3d = plano.bounds.get(i);
					apuntos.add( new Point3f(p3d.getCoordsf()) );
				}
				p3d = plano.bounds.get(0);
				apuntos.add(new Point3f(p3d.getCoordsf()));
			}
			else
			{
				tam = divisions;
				strip[cont] = tam + 1;
				normal = plano.vector.getCoords();
				rad = plano.radius;

				//generate bounding points
				if(normal[2]!=0)
				{
					if(normal[2]!=1)
					{
						a = -(normal[0]*normal[1]*2)/normal[2];
						V[0] = normal[1];
						V[1] = normal[0];
						V[2] = a;
					}
					else
					{
						V[0] = 1;
						V[1] = 0;
						V[2] = 0;
					}
				}
				else
				{
					V[0] = -normal[1];
					V[1] = normal[0];
					V[2] = 0;
				}
				aux = Math.sqrt(V[0]*V[0] + V[1]*V[1] + V[2]*V[2]);
				V[0] /= aux;
				V[1] /= aux;
				V[2] /= aux;
				p3d1 = new Point3D(plano.origin.getX() + V[0]*rad, plano.origin.getY() + V[1]*rad,
						plano.origin.getZ() + V[2]*rad);
				apuntos.add(new Point3f(p3d1.getCoordsf()));

				angle = inc_angle;
				p3d = new Point3D(p3d1);
				t3d = new MyTransform3D();
				matriz = plano.generalRotationMatrix(angle);
				t3d.assign(matriz);
				for(i=0;i<divisions-1;i++)
				{
					p3d.applyTransform(t3d);
					apuntos.add(new Point3f(p3d.getCoordsf()));
				}

				apuntos.add(new Point3f(p3d1.getCoordsf()));
			}

		}

		try
		{
			puntos = apuntos.toArray(puntos);
		} catch (Exception e)
		{
			System.out.println("PlaneSet3D::paint Error: Can not convert from ArrayList to Point3f[]");
			return tgRet;
		}

		tam = puntos.length;
		limites = new LineStripArray(tam,LineStripArray.COORDINATES|LineStripArray.COLOR_3, strip);
		for(cont=0;cont<tam;cont++)
		{
			limites.setCoordinate(cont, puntos[cont]);
			limites.setColor(cont, scr_opt.color);
		}
		forma = new Shape3D(limites,this.object_app);
		forma.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		forma.setCapability(Shape3D.ALLOW_PICKABLE_READ);
		forma.getGeometry().setCapability(GeometryArray.ALLOW_COORDINATE_READ);
		forma.getGeometry().setCapability(GeometryArray.ALLOW_COUNT_READ);
		forma.getGeometry().setCapability(GeometryArray.ALLOW_FORMAT_READ);

		tgRet.addChild(forma);

		return tgRet;
	}

	@Override
	public int readData(String file_name, String path) {
		Reader fr;
		StreamTokenizer st;
		int cont;
		int num_puntos;
		Plane3D plane;
		Point3D origen;

		name = file_name;

		try
		{
			fr=new FileReader(path+file_name);
			st=new StreamTokenizer(fr);
			String comentario="#";
			byte []bcom=comentario.getBytes();
			int icom=(int)bcom[0];
			st.commentChar(icom);

			//leemos el numero de puntos que tiene el fichero
			st.nextToken();
			num_puntos=(int)st.nval;
			for(cont=0;cont<num_puntos;cont++)
			{
				plane = new Plane3D(st);
				origen = plane.origin;
				data.insert(origen.getCoords(), plane);
			}
			scr_opt.num_points = data.size();

		} catch (IOException e)
		{
			System.out.println("PlaneSet3D::readData Error: can not read data from "+path+file_name);
			return -1;
		}

		return data.size();
	}

	@Override
	public void writeData(String name, String path) {
		int cont;
		int num_puntos = data.size();
		Object []elements = data.elements();

		if(path.charAt(path.length()-1)!='/')
			path += '/';

		FileWriter fw;
		try
		{
			fw = new FileWriter(path+name);
			fw.write(num_puntos+" ");
			fw.write("\n");

			for(cont=0;cont<num_puntos;cont++)
			{
				fw.write(((Plane3D)elements[cont]).toString());
				fw.write("\n");
			}
			System.out.println("Puntos escritos: "+cont);
			fw.close();
		} catch(IOException e)
		{
			System.err.println("PlaneSet3D::writeData Error: can not write data to: "+path+name);
		}

	}

	@Override
	public void applyTransform(MyTransform trans) {
		int tam, cont;
		Object []elements;
		Plane3D element;
		elements = elements();
		tam = elements.length;

		for(cont=0;cont<tam;cont++)
		{
			element = (Plane3D)elements[cont];
			element.applyTransform(trans);
		}

	}

}
