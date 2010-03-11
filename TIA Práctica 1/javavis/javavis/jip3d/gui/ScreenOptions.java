package javavis.jip3d.gui;

import java.io.Serializable;

import javax.vecmath.Color3f;

public class ScreenOptions  implements Serializable{
	private static final long serialVersionUID = 2650475040078570146L;

	public static final int tUNDEFINED = 0;
	public static final int tPOINTSET3D = 1;
	public static final int tTRAJ2D = 2;
	public static final int tTRAJ3D = 4;
	public static final int tPLANARSET3D = 8;
	public static final int tNORMALSET3D = 16;
	public static final int tCYLINDERSET3D = 32;
	public static final int tVECTORSET3D = 64;
	public static final int tSEGMENTSET3D = 128;
	public static final int tFEATURESET2D = 256;

	public static final int tALLTYPES = 1023;


	public Color3f color;
	public boolean global_color; //for PointSet3D
	public boolean improved;	//for PointSet3D
	public float shine;
	public double width;
	public int type;
	public float alpha;		//for trajectory
	public double length; 	//for normal vectors
	public boolean is_visible;
	public int num_points; //for statistics

	public ScreenOptions()
	{
		color = new Color3f();
		global_color = true;  //for PointSet3D
		improved = false;		//for PointSet3D
		shine = 10f;
		width = 1;
		type = tALLTYPES;
		alpha = 0;
		is_visible = false;
		length = 0.1;			//for NormalSet3D
		num_points = 0;
	}

	public ScreenOptions(Color3f c, float s)
	{
		color = new Color3f(c);
		global_color = true;  //for PointSet3D
		shine = s;
		width = 2;
		type = tALLTYPES;
		alpha = 0;
		is_visible = false;
		length = 0.1;
		num_points = 0;
	}

}
