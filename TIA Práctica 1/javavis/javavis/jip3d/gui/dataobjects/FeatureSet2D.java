package javavis.jip3d.gui.dataobjects;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;

import javavis.jip3d.geom.Feature2D;
import javavis.jip3d.geom.MyTransform;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
/**
 * FeatureSet2D Class
 * @author Miguel Cazorla
 *
 */
public class FeatureSet2D extends ScreenData {
	private static final long serialVersionUID = 377620505581949245L;
	
	public int divisions = 16;
	static private float weight = 50.0f;

	public FeatureSet2D(ScreenOptions opt) {
		super(opt);
		opt.color = new Color3f(0,1,0);
		this.setType(ScreenOptions.tFEATURESET2D);
	}

	@Override
	protected TransformGroup paint() {
		TransformGroup tgRet=new TransformGroup();
		Shape3D forma;
		int strip[] = new int[data.elements().length];
		
		LineStripArray limites;
		Point3f []puntos;
		ArrayList <Point3f>apuntos = new ArrayList<Point3f>();
		float inc_angle = (float)(2*Math.PI/divisions);
		
		int cont=0;
		float[] coords = new float[3];
		Feature2D f2d;
		for (Object o: data.elements()) {
			f2d = (Feature2D)o;
			for (int i=0; i<divisions; i++) {
				coords[0]=(float)(f2d.getX()+(f2d.scale/weight)*Math.cos(f2d.orientation+inc_angle*i)); 
				coords[1]=(float)(f2d.getY()+(f2d.scale/weight)*Math.sin(f2d.orientation+inc_angle*i)); 
				coords[2]=(float)f2d.getZ();
				apuntos.add(new Point3f(coords));
			}
			// The first point again
			coords[0]=(float)(f2d.getX()+(f2d.scale/weight)*Math.cos(f2d.orientation)); 
			coords[1]=(float)(f2d.getY()+(f2d.scale/weight)*Math.sin(f2d.orientation)); 
			coords[2]=(float)f2d.getZ();
			apuntos.add(new Point3f(coords));
			// Now, the center point
			coords[0]=(float)(f2d.getX()); 
			coords[1]=(float)(f2d.getY()); 
			coords[2]=(float)f2d.getZ();
			apuntos.add(new Point3f(coords));
			strip[cont++] = divisions + 2;
		}

		puntos = new Point3f[apuntos.size()];
		try
		{
			puntos = apuntos.toArray(puntos);
		} catch (Exception e)
		{
			System.out.println("FeatureSet2D::paint Error: Can not convert from ArrayList to Feature2D[]");
			return tgRet;
		}
		
		limites = new LineStripArray(puntos.length,LineStripArray.COORDINATES|LineStripArray.COLOR_3, strip);
		for(cont=0;cont<puntos.length;cont++) {
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
		int num_puntos;
		int lengthDesc;
		Feature2D f2d;
		ArrayList<double[]> coord3D, coord2D;
		coord3D=new ArrayList<double[]>();
		coord2D=new ArrayList<double[]>();
		name = file_name;

		try
		{
			// It assumes that a point file is in the same directory, with the same name, and ending .pts
			// Reads the points and it uses to positioning the features
			// For optimization, we assume that features file is ordered (X, Y)
			String aux = file_name;
			aux=aux.substring(0, aux.length()-4)+"pts";
			fr=new FileReader(path+aux);
			st=new StreamTokenizer(fr);
			String comentario="#";
			byte []bcom=comentario.getBytes();
			int icom=(int)bcom[0];
			st.commentChar(icom);
			//leemos el numero de puntos que tiene el fichero
			st.nextToken();
			num_puntos=(int)st.nval;
			st.nextToken();
			boolean isColor=((int)st.nval==1);
			st.nextToken();
			boolean yInverted=((int)st.nval==1);
			for (int i=0; i<num_puntos; i++) {
				double[] auxCoords = new double[3];
				st.nextToken();
				auxCoords[0] = st.nval;
				st.nextToken();
				//auxCoords[1] = (yInverted?-st.nval:st.nval);
				auxCoords[1] = st.nval;
				st.nextToken();
				auxCoords[2] = (yInverted?-st.nval:st.nval);
				coord3D.add(auxCoords);
				
				st.nextToken();
				if (isColor) {
					st.nextToken();
					st.nextToken();
				}
	
				auxCoords = new double[2];
				st.nextToken();
				auxCoords[1] = (int) st.nval;
				st.nextToken();
				auxCoords[0] = (int) st.nval;
				coord2D.add(auxCoords);			
			}
			
			fr=new FileReader(path+file_name);
			st=new StreamTokenizer(fr);
			st.commentChar(icom);

			//leemos el numero de puntos que tiene el fichero
			st.nextToken();
			num_puntos=(int)st.nval;
			st.nextToken();
			lengthDesc=((int)st.nval);
			
			boolean delete;
			for(int cont=0; cont<num_puntos; cont++) {
				delete=false;
				f2d = new Feature2D(st, lengthDesc);
				for (int i=0; i<coord2D.size(); i++) {
					if (f2d.posx==coord2D.get(i)[0] && f2d.posy==coord2D.get(i)[1]) {
						f2d.setX(coord3D.get(i)[0]);
						f2d.setY(coord3D.get(i)[1]);
						f2d.setZ(coord3D.get(i)[2]);
						break;
					}
					if (coord2D.get(i)[1]>f2d.posy) {
						delete=true;
						break;
					}
				}
				if (!delete)
					data.insert(f2d.getCoords(), f2d);
			}
			scr_opt.num_points = data.size();

		} catch (IOException e) {
			System.out.println("Feature2D::readData Error: can not read data from "+path+file_name);
			return -1;
		}

		return data.size();
	}

	@Override
	public void writeData(String name, String path) {
		if(path.charAt(path.length()-1)!='/')
			path += '/';

		FileWriter fw;
		try
		{
			fw = new FileWriter(path+name);
			fw.write(data.size()+" ");
			fw.write("\n");

			for (Feature2D f2d : (Feature2D[])data.elements()) 
				fw.write(f2d.toString()+"\n");

			fw.close();

		} catch(IOException e) {
			System.err.println("Error:" + e.getMessage());
		}

	}

	@Override
	public void applyTransform(MyTransform trans) {
		for (Feature2D f2d : (Feature2D[])elements()) {
			f2d.applyTransform(trans);
		}
	}

}
