package javavis.jip3d.functions;

import java.util.ArrayList;

import javavis.base.Function3DGroup;
import javavis.base.JIPException;
import javavis.base.ParamType;
import javavis.jip3d.geom.Octree;
import javavis.jip3d.geom.Point3D;
import javavis.jip3d.gui.Function3D;
import javavis.jip3d.gui.FunctionParam;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;
import javavis.jip3d.gui.dataobjects.PointSet3D;

/**
 * Class FPointFilter. This class is used to reduce the number of 3D points in a 3D image.
 * @author dviejo
 *
 */
public class FPointFilter extends Function3D {

	public FPointFilter() {
		super();
		this.allowed_input = ScreenOptions.tPOINTSET3D;
		this.group = Function3DGroup.Mapping;

		// resolution param. Cube side length for grouping points
		FunctionParam p1 = new FunctionParam("Resolution", ParamType.FLOAT);
		p1.setValue(0.10);

		this.addParam(p1);

	}

	@Override
	public void proccessData(ScreenData scr_data) throws JIPException {
		result_list = new ArrayList<ScreenData>();
		Octree total_data;
		Point3D bound_sup;
		Point3D bound_inf;
		float resolution = (float)this.paramValueReal("Resolution");
		Object []elements;
		Point3D element;
		int cont;
		ArrayList<Point3D> complete_list;
		PointSet3D ret;
		double prog_inc;


		bound_sup = new Point3D(200, 200, 200);
		bound_inf = new Point3D(-200, -200, -200);
		total_data = new Octree(bound_inf, bound_sup, resolution);

		elements = scr_data.elements();
		prog_inc = 50.0/elements.length;

		for(cont=0;cont<elements.length;cont++)
		{
			element = (Point3D) elements[cont];
			total_data.insert(element);
			progress += prog_inc;
		}
		complete_list = total_data.getAll();

		ret = new PointSet3D(new ScreenOptions());
		ret.name = "ReducedPointSet";
		prog_inc = 50.0 / complete_list.size();
		for(cont=0;cont<complete_list.size();cont++)
		{
			element = complete_list.get(cont);
			ret.insert(element);
			progress += prog_inc;
		}
		result_list.add(ret);

	}

}
