package javavis.jip3d.gui;


import java.io.Serializable;


import javavis.jip3d.geom.Feature2D;
import javavis.jip3d.geom.MyTransform;
import javavis.jip3d.geom.Normal3D;
import javavis.jip3d.geom.Plane3D;
import javavis.jip3d.geom.Point3D;
import javavis.jip3d.geom.Segment3D;
import javavis.jip3d.geom.Vector3D;
import javavis.jip3d.geom.MyKDTree;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PointAttributes;
import javax.media.j3d.TransformGroup;

/**
 * Esta clase representa objetos que van a ser dibujados en pantalla. Tendra las cabeceras para
 * lectura/escritura en fichero y para visualizacion en pantalla. Cada clase que
 * herede de esta tendra que conocer como hacer todas estas funciones para los datos a representar.
 * @author dviejo
 *
 */

public abstract class ScreenData implements Serializable{
	private static final long serialVersionUID = 2767756987547500603L;

	public String name;

	transient public Appearance object_app;
	transient public Material object_material; //for lighting
	transient public ColoringAttributes object_coloring; //for not lighting;
	transient public LineAttributes latt;

	transient public BranchGroup BGPaint; //transient objects won't be persistent

	public ScreenOptions scr_opt;

	protected MyKDTree data;

	// TODO Hacer que debug se obtenga del JFrame
	protected boolean DEBUG;

	public ScreenData(ScreenOptions opt)
	{
		scr_opt = opt;
		data = new MyKDTree(3);
		DEBUG = false;
	}

	public abstract int readData(String name, String path);

	public abstract void writeData(String name, String path);

	protected abstract TransformGroup paint();

	public BranchGroup draw()
	{
		if(BGPaint!=null&&BGPaint.isLive()) BGPaint.detach();
		if(!this.scr_opt.is_visible)
		{
			BGPaint = null;
		}
		else
		{
			BGPaint = new BranchGroup();
			BGPaint.setCapability(BranchGroup.ALLOW_DETACH);

			object_app = new Appearance();
			object_material = new Material();
			object_coloring = new ColoringAttributes();
			latt = new LineAttributes((float)scr_opt.width, LineAttributes.PATTERN_SOLID, true);

			//material values
			object_material.setDiffuseColor(scr_opt.color);
			object_material.setShininess(scr_opt.shine);

			//coloring attributes values
			object_coloring.setColor(scr_opt.color);

			//point attributes values
			PointAttributes point_att = new PointAttributes((float)scr_opt.width, scr_opt.improved);

			//appearance
			object_app.setColoringAttributes(object_coloring);
			object_app.setMaterial(object_material);
			object_app.setLineAttributes(latt);
			object_app.setPointAttributes(point_att);

			BGPaint.addChild(this.paint());

		}

		return BGPaint;
	}

	public int getType()
	{
		return scr_opt.type;
	}

	public void setType(int t)
	{
		scr_opt.type = t;
	}

	public boolean isVisible()
	{
		return scr_opt.is_visible;
	}

	public void setVisible(boolean v)
	{
		scr_opt.is_visible = v;
	}

	public MyKDTree getData()
	{
		return data;
	}

	public Object []elements()
	{
		return data.elements();
	}

	public Object []range(double []min, double []max)
	{
		Object []ret;
		try
		{
			ret = data.range(min, max);
		} catch(Exception e)
		{
			ret = null;
		}
		return ret;
	}

	public Object []range(Point3D key, double rad)
	{
		Object []ret;
		try
		{
			ret = data.range(key, rad);
		} catch(Exception e)
		{
			ret = null;
		}
		return ret;
	}

	public double [] getMinRango()
	{
		return data.minRango;
	}

	public double [] getMaxRango()
	{
		return data.maxRango;
	}


	public void insert(Object obj)
	{
		if(obj instanceof Point3D)
			data.insert(((Point3D)obj).getCoords(), obj);
		else if(obj instanceof Vector3D)
			data.insert(((Vector3D)obj).getCoords(), obj);
		else if(obj instanceof Normal3D)
			data.insert(((Normal3D)obj).origin.getCoords(), obj);
		else if(obj instanceof Plane3D)
			data.insert(((Plane3D)obj).origin.getCoords(), obj);
		else if(obj instanceof Segment3D)
			data.insert(((Segment3D)obj).begin.getCoords(), obj);
		else if(obj instanceof Feature2D)
			data.insert(((Feature2D)obj).getCoords(), obj);
		scr_opt.num_points++;
	}

	public abstract void applyTransform(MyTransform trans);

	public String toString()
	{
		return name;
	}

}
