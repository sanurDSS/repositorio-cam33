package javavis.jip3d.gui.dataobjects;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

import javavis.jip3d.geom.MyTransform;
import javavis.jip3d.geom.Point3D;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;

import javax.media.j3d.PointArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;
/**
 * PointSet3D Class
 * @author dviejo
 *
 */
public class PointSet3D extends ScreenData {
	private static final long serialVersionUID = -2897087012180220064L;

	public PointSet3D(ScreenOptions opt) {
		super(opt);
		opt.global_color = false;
		opt.improved = false;
		this.setType(ScreenOptions.tPOINTSET3D);
	}

	@Override
	protected TransformGroup paint() {
		TransformGroup tgRet=new TransformGroup();
		int tam, cont;
		Object []elements = data.elements();
		Point3f []points;
		tam = elements.length;
		points = new Point3f[tam];
		PointArray geometria;

		if(scr_opt.global_color)
		{
			geometria = new PointArray(tam,PointArray.COORDINATES);
		}
		else
		{
			geometria = new PointArray(tam,PointArray.COORDINATES|PointArray.COLOR_3);
		}

		for(cont=0;cont<tam;cont++)
		{
			points[cont] = new Point3f(((Point3D)elements[cont]).getCoordsf());
		}

		geometria.setCoordinates(0, points);
		if(!scr_opt.global_color)
		{
			for(cont=0;cont<tam;cont++)
				geometria.setColor(cont, ((Point3D)elements[cont]).color);
		}
		Shape3D forma = new Shape3D(geometria,object_app);
		forma.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		tgRet.addChild(forma);

		return tgRet;
	}

	@Override
	public int readData(String file_name, String path) {
		Reader fr;
		StreamTokenizer st;
		int cont;
		int num_puntos;
		boolean isColor, yInverted;
		Point3D point;

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
			st.nextToken();
			isColor=((int)st.nval==1);
			st.nextToken();
			yInverted=((int)st.nval==1);

			for(cont=0;cont<num_puntos;cont++)
			{
				point = new Point3D(st, isColor, yInverted);
				data.insert(point.getCoords(), point);
			}
			scr_opt.num_points = data.size();

		} catch (IOException e)
		{
			System.out.println("PointSet3D::readData Error: can not read data from "+path+file_name);
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
				fw.write(((Point3D)elements[cont]).toString());
				fw.write("\n");
			}
			fw.close();

		} catch(IOException e)
		{
			System.err.println("Error:" + e.getMessage());
		}

	}

	@Override
	public void applyTransform(MyTransform trans) {
		int tam, cont;
		Object []elements;
		Point3D element;
		elements = elements();
		tam = elements.length;

		for(cont=0;cont<tam;cont++)
		{
			element = (Point3D)elements[cont];
			element.applyTransform(trans);
		}
	}

}
