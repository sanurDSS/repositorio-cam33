package javavis.jip3d.functions;

import java.util.ArrayList;

import javavis.base.Function3DGroup;
import javavis.base.ParamType;
import javavis.base.JIPException;
import javavis.jip3d.geom.MyTransform;
import javavis.jip3d.geom.MyTransform3D;
import javavis.jip3d.geom.Octree;
import javavis.jip3d.geom.Point3D;
import javavis.jip3d.gui.Function3D;
import javavis.jip3d.gui.FunctionParam;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;
import javavis.jip3d.gui.dataobjects.PointSet3D;
import javavis.jip3d.gui.dataobjects.Trajectory;

/**
 * This class is used for representing together the points obtained from consecutive 3D images along a
 * trajectory. The input data of this function can be either a 2D or a 3D trajectory. Resulting points
 * can be represented in 3D or projected on a horizontal plane. Also a point density reduction is applied
 * for reducing the amount of data in the result.
 * @author dviejo
 *
 */
public class FPointTrajectory extends Function3D {

	public FPointTrajectory()
	{
		super();

		this.allowed_input = ScreenOptions.tTRAJ2D | ScreenOptions.tTRAJ3D;
		this.group = Function3DGroup.Mapping;



		FunctionParam p1 = new FunctionParam("Resolution", ParamType.FLOAT);
		p1.setValue(0.10);
		FunctionParam p2 = new FunctionParam("Bidimensional", ParamType.BOOL);
		p2.setValue(true);

		this.addParam(p1);
		this.addParam(p2);

	}
	@Override
	public void proccessData(ScreenData scr_data) throws JIPException {
		result_list = new ArrayList<ScreenData>();
		Octree total_data;
		Point3D bound_sup;
		Point3D bound_inf;
		float resolution = (float)this.paramValueReal("Resolution");
		boolean bidim = this.paramValueBool("Bidimensional");
		PointSet3D points;
		PointSet3D ret;
		int file_number;
		int cont, cont_points;
		String file_name;
		Trajectory traj;
		Object []elements;
		Point3D element;
		ScreenOptions opt_aux = new ScreenOptions();
		ArrayList<Point3D> complete_list;
		String path;
		MyTransform transform, total_transform;

		bound_sup = new Point3D(200, 200, 200);
		bound_inf = new Point3D(-200, -200, -200);
		total_data = new Octree(bound_inf, bound_sup, resolution);

		traj = (Trajectory)scr_data;
		file_number = traj.files.size();
		path = traj.path;

		double prog_inc = 50.0/file_number;

		total_transform = new MyTransform3D();
		for(cont=0;cont<file_number;cont++)
		{
			file_name = traj.files.get(cont);
			transform = new MyTransform3D(traj.transforms.get(cont));
			points = new PointSet3D(opt_aux);
			points.readData(file_name, path);
			total_transform.applyTransform(transform);

			elements = points.elements();
			for(cont_points=0;cont_points<elements.length;cont_points++)
			{
				element = (Point3D)elements[cont_points];
				element.applyTransform(total_transform);
				if(bidim)
					element.setY(0.0);
				total_data.insert(element);
			}
			progress += prog_inc;
		}

		complete_list = total_data.getAll();
		prog_inc = 50.0 / complete_list.size();
		ret = new PointSet3D(new ScreenOptions());
		ret.name = "CompletePointSet";
		for(cont=0;cont<complete_list.size();cont++)
		{
			element = complete_list.get(cont);
			ret.insert(element);
			progress += prog_inc;
		}
		result_list.add(ret);
	}

}
