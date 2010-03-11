package javavis.jip3d.gui.dataobjects;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

import javavis.jip3d.geom.MyTransform;
import javavis.jip3d.geom.Normal3D;
import javavis.jip3d.geom.Point3D;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.PointArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;


public class NormalSet3D extends ScreenData {
	private static final long serialVersionUID = -7888161621635341841L;

	public NormalSet3D(ScreenOptions opt) {
		super(opt);
		opt.length = 0.1;
		opt.color = new Color3f(1,0,0);
		this.setType(ScreenOptions.tNORMALSET3D);
	}

	@Override
	protected TransformGroup paint() {
		TransformGroup tgRet=new TransformGroup();
		LineArray geometria;
		PointArray geom_puntos;
		Shape3D forma;
		Object []elements = data.elements();
		int tam = elements.length;
		Point3f[] puntos = new Point3f[2];
		Normal3D normal;
		Point3D punto;
		int cont;


		geometria = new LineArray(2*tam, LineArray.COORDINATES|LineArray.COLOR_3);
		geom_puntos = new PointArray(tam, PointArray.COORDINATES);

		for(cont=0;cont<tam;cont++)
		{
			normal = (Normal3D)elements[cont];
			punto = normal.origin;
			puntos[0] = new Point3f(punto.getCoordsf());
			puntos[1] = new Point3f(punto.getCoordsf());
			puntos[1].x += normal.vector.getX() * scr_opt.length;
			puntos[1].y += normal.vector.getY() * scr_opt.length;
			puntos[1].z += normal.vector.getZ() * scr_opt.length;
			geometria.setCoordinates(cont*2, puntos);
			geometria.setColor(cont*2, scr_opt.color);
			geometria.setColor(cont*2 + 1, scr_opt.color);

			geom_puntos.setCoordinate(cont, puntos[0]);
		}

		forma = new Shape3D(geometria,this.object_app);
		forma.getGeometry().setCapability(GeometryArray.ALLOW_COORDINATE_READ);
		forma.getGeometry().setCapability(GeometryArray.ALLOW_COUNT_READ);
		forma.getGeometry().setCapability(GeometryArray.ALLOW_FORMAT_READ);
		tgRet.addChild(forma);

		forma = new Shape3D(geom_puntos,this.object_app);
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
		Normal3D normal;
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
				normal = new Normal3D(st);
				origen = normal.origin;
				data.insert(origen.getCoords(), normal);
			}
			scr_opt.num_points = data.size();

		} catch (IOException e)
		{
			System.out.println("NormalSet3D::readData Error: can not read data from "+path+file_name);
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
			fw.write(num_puntos);
			fw.write("\n");

			for(cont=0;cont<num_puntos;cont++)
			{
				fw.write(((Normal3D)elements[cont]).toString());
				fw.write("\n");
			}
			fw.close();

		} catch(IOException e)
		{
			System.err.println("NormalSet3D::writeData Error: can not write data to: "+path+name);
		}
	}

	@Override
	public void applyTransform(MyTransform trans) {
		int tam, cont;
		Object []elements;
		Normal3D element;
		elements = elements();
		tam = elements.length;

		for(cont=0;cont<tam;cont++)
		{
			element = (Normal3D)elements[cont];
			element.applyTransform(trans);
		}

	}

}
