package javavis.jip3d.gui.dataobjects;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javavis.jip3d.geom.MyTransform;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;

import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

public abstract class Trajectory extends ScreenData implements Serializable {
	private static final long serialVersionUID = 4110652216141190374L;

	public ArrayList <String>files;
	public ArrayList <MyTransform>transforms;

	final public float tam = 0.05f;

	public String path;

	public Trajectory(ScreenOptions opt)
	{
		super(opt);
		opt.color = new Color3f(1,0,0);
	}

	protected TransformGroup createAxis()
	{
		TransformGroup TGret = new TransformGroup();
		LineAttributes latt = new LineAttributes(1, LineAttributes.PATTERN_SOLID, true);
		Appearance app = new Appearance();
		LineArray lineas = new LineArray(6, LineArray.COORDINATES|LineArray.COLOR_3);

		app.setLineAttributes(latt);

		lineas.setCoordinate(0, new Point3f());
		lineas.setCoordinate(1, new Point3f(tam, 0, 0));
		lineas.setCoordinate(2, new Point3f());
		lineas.setCoordinate(3, new Point3f(0, tam, 0));
		lineas.setCoordinate(4, new Point3f());
		lineas.setCoordinate(5, new Point3f(0, 0, tam));

		lineas.setColor(0, new Color3f(1, 0, 0));
		lineas.setColor(1, new Color3f(1, 0, 0));
		lineas.setColor(2, new Color3f(0, 1, 0));
		lineas.setColor(3, new Color3f(0, 1, 0));
		lineas.setColor(4, new Color3f(0, 0, 1));
		lineas.setColor(5, new Color3f(0, 0, 1));

		Shape3D forma = new Shape3D(lineas, app);
		forma.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		forma.setCapability(Shape3D.ALLOW_PICKABLE_READ);
		forma.getGeometry().setCapability(GeometryArray.ALLOW_COORDINATE_READ);
		forma.getGeometry().setCapability(GeometryArray.ALLOW_COUNT_READ);
		forma.getGeometry().setCapability(GeometryArray.ALLOW_FORMAT_READ);
		TGret.addChild(forma);


		return TGret;
	}

	@Override
	public void writeData(String name, String path) {
		int tam = files.size();
		int cont;
		FileWriter fw = null;
		if(path.charAt(path.length()-1)!='/')
			path += '/';
		try
		{
			fw = new FileWriter(path+name);
			fw.write(String.valueOf(tam));
			fw.write(String.valueOf("\n"));

			for(cont=0;cont<tam;cont++)
			{
				fw.write(files.get(cont)+ " " + transforms.get(cont).toString());
				fw.write(String.valueOf("\n"));
			}

		} catch(IOException e) 
		{
			System.err.println("Error saving Trajectory to: "+path+name);
		} 
		finally {
			if (fw!=null)
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
