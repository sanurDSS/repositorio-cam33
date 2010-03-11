package javavis.jip3d.gui.dataobjects;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;


import javavis.jip3d.geom.MyTransform;
import javavis.jip3d.geom.Point3D;
import javavis.jip3d.geom.Segment3D;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.PointArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;


public class SegmentSet3D extends ScreenData {
	private static final long serialVersionUID = 8833454068500402734L;

	public SegmentSet3D(ScreenOptions opt) {
		super(opt);
		this.setType(ScreenOptions.tSEGMENTSET3D);
	}

	@Override
	public void applyTransform(MyTransform trans) {
		int tam, cont;
		Object []elements;
		Segment3D element;
		elements = elements();
		tam = elements.length;

		for(cont=0;cont<tam;cont++)
		{
			element = (Segment3D)elements[cont];
			element.applyTransform(trans);
		}
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
		Segment3D segment;
		int cont;


		geometria = new LineArray(2*tam, LineArray.COORDINATES|LineArray.COLOR_3);
		geom_puntos = new PointArray(tam, PointArray.COORDINATES);

		for(cont=0;cont<tam;cont++)
		{
			segment = (Segment3D)elements[cont];
			puntos[0] = new Point3f(segment.begin.getCoordsf());
			puntos[1] = new Point3f(segment.end.getCoordsf());
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
		Segment3D segment;
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
				segment = new Segment3D(st);
				origen = segment.begin;
				data.insert(origen.getCoords(), segment);
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
				fw.write(((Segment3D)elements[cont]).toString());
				fw.write("\n");
			}
			fw.close();

		} catch(IOException e)
		{
			System.err.println("NormalSet3D::writeData Error: can not write data to: "+path+name);
		}
	}
}
