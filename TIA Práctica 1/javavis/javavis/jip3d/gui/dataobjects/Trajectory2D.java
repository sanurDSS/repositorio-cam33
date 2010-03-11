package javavis.jip3d.gui.dataobjects;


import java.io.FileReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;

import javavis.jip3d.geom.MyTransform;
import javavis.jip3d.geom.MyTransform2D;
import javavis.jip3d.geom.Point3D;
import javavis.jip3d.gui.ScreenOptions;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Point3f;


/**
 * A trajectory2D represents a set of transformations in 2D space. Normally with a translation in X, Z axis
 * and a rotation around Y axle.
 * @author dviejo
 *
 */
public class Trajectory2D extends Trajectory {
	private static final long serialVersionUID = -7665129261248658695L;
	double height;

	public Trajectory2D(ScreenOptions opt, double h)
	{
		super(opt);
		files = new ArrayList<String>();
		transforms = new ArrayList<MyTransform>();
		height = h;
		this.setType(ScreenOptions.tTRAJ2D);
		scr_opt.alpha = 0.5f;
	}


	protected TransformGroup paint() {

		TransformGroup tgRet = new TransformGroup();
		TransformGroup tgLineas = new TransformGroup();
		LineStripArray lineas;
		int num_poses = transforms.size();
		Point3f punto;
		int []strips = {num_poses};
		TransformGroup []camaras = new TransformGroup[num_poses];
		Transform3D transform;
		Point3D reference;
		int cont;

		MyTransform2D tr_global = new MyTransform2D();
		MyTransform tr_actual;

		//Trajectory 2D transparency
		TransparencyAttributes trans_att = new TransparencyAttributes(TransparencyAttributes.BLENDED, scr_opt.alpha);
		object_app.setTransparencyAttributes(trans_att);

		lineas = new LineStripArray(num_poses, LineStripArray.COORDINATES, strips);

		for(cont=0;cont<num_poses;cont++)
		{
			reference = new Point3D();
			tr_actual = transforms.get(cont);
			tr_global.applyTransform(tr_actual); //esto puede que haya que aplicarlo por el 'otro lado'

			transform = new Transform3D(tr_global.getMatrix4d());
			camaras[cont] = new TransformGroup(transform);
			camaras[cont].addChild(createAxis());
			tgRet.addChild(camaras[cont]);

			reference.applyTransform(tr_global);
			punto = new Point3f(reference.getCoordsf());
			lineas.setCoordinate(cont, punto);
		}

		Shape3D forma = new Shape3D(lineas, object_app);
		forma.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		forma.setCapability(Shape3D.ALLOW_PICKABLE_READ);
		forma.getGeometry().setCapability(GeometryArray.ALLOW_COORDINATE_READ);
		forma.getGeometry().setCapability(GeometryArray.ALLOW_COUNT_READ);
		forma.getGeometry().setCapability(GeometryArray.ALLOW_FORMAT_READ);
		tgLineas.addChild(forma);
		tgRet.addChild(tgLineas);

		return tgRet;
	}

	@Override
	public int readData(String file_name, String path) {
		Reader fr;
		StreamTokenizer st;
		int num_poses;
		String file;

		name = file_name;
		this.path = path;

		try
		{
			fr=new FileReader(path+file_name);
			st=new StreamTokenizer(fr);
			st.wordChars('/'-1, '/'+1);
			//leemos el numero de lineas del fichero = poses
			st.nextToken();
			num_poses = (int)st.nval;
			for(int i=0;i<num_poses;i++)
			{
				st.nextToken();
				file = st.sval;
				files.add(file);
				transforms.add(new MyTransform2D(st));
			}
			scr_opt.num_points = files.size();
		}catch(Exception e)
		{
			System.err.println("Error Reconstruccion: "+e);
		}

		return 0;
	}


	@Override
	public void applyTransform(MyTransform trans) {
		this.transforms.get(0).applyTransform(trans);
	}

}
