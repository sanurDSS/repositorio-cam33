package javavis.jip3d.functions;


import java.util.ArrayList;

import javavis.base.Function3DGroup;
import javavis.base.ParamType;
import javavis.base.JIPException;
import javavis.jip3d.geom.Normal3D;
import javavis.jip3d.geom.Plane3D;
import javavis.jip3d.geom.Point3D;
import javavis.jip3d.geom.Vector3D;
import javavis.jip3d.gui.Function3D;
import javavis.jip3d.gui.FunctionParam;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;
import javavis.jip3d.gui.dataobjects.PlaneSet3D;


public class FPlanePatch extends Function3D {

	private double minimum = 9;
	private double pi2 = Math.PI/2.0;

	public FPlanePatch()
	{
		super();
		this.allowed_input = ScreenOptions.tPOINTSET3D|ScreenOptions.tNORMALSET3D;
		this.group = Function3DGroup.Model3D;

		FunctionParam p1 = new FunctionParam("Window size", ParamType.FLOAT);
		p1.setValue(0.03);
		FunctionParam p2 = new FunctionParam("Minimum size", ParamType.FLOAT);
		p2.setValue(0.150);
		FunctionParam p3 = new FunctionParam("PlaneThick", ParamType.FLOAT);
		p3.setValue(0.005);

		this.addParam(p1);
		this.addParam(p2);
		this.addParam(p3);

	}
	@Override
	public void proccessData(ScreenData scr_data) throws JIPException {
		result_list = new ArrayList<ScreenData>();

		if(scr_data.getType() == ScreenOptions.tPOINTSET3D)
			patchPointSet(scr_data);
		else
			patchNormalSet(scr_data);

	}

	private void patchPointSet(ScreenData scr_data)
	{
		PlaneSet3D new_plane_set = new PlaneSet3D(new ScreenOptions());
		new_plane_set.name = "patches" + scr_data.name.substring(3);
		FNormalSVD FSVD = new FNormalSVD();
		double win_size = this.paramValueReal("Window size");
		double min_size = this.paramValueReal("Minimum size");
		double thickness = this.paramValueReal("PlaneThick");

		Object []neighbors;
		Object []elements;
		Point3D element;
		Plane3D new_plane;
		int cont, cont2;
		int tam;
		double rad;
		double []normal;
		Vector3D vec_aux;
		Normal3D normalVector;
		double angle;

		elements = scr_data.elements();
		tam = elements.length;

		double prog_inc = 100.0 / tam;

		for(cont2=0;cont2<tam;cont2++)
		{
			element = (Point3D)elements[cont2];
			// has been this element visited?
			if(element.visited<1)
			{
				element.visited = 3;
				rad = element.getOriginDistance() * win_size;
				if(rad<min_size) rad = min_size;

				try
				{
					neighbors = scr_data.range(element, rad);
					if(neighbors.length > minimum)
					{
						element = FNormalSVD.calcCentroid(neighbors);
						normal = FSVD.aplicarSVD(element, neighbors, thickness); //saliency
						if(normal!=null)
						{
							if(normal[3]==1)
							{
								vec_aux = new Vector3D(normal[0], normal[1], normal[2]);
								normalVector = new Normal3D(element, vec_aux, normal[4], rad);
								vec_aux = new Vector3D(element);
								angle = vec_aux.getAngle(normalVector.vector);
								if(angle<pi2)
								{
									normal[0] = -normal[0];
									normal[1] = -normal[1];
									normal[2] = -normal[2];
									vec_aux = new Vector3D(normal[0], normal[1], normal[2]);
									normalVector = new Normal3D(element, vec_aux, normal[4], rad);
								}
								//anyadido para evitar solapamientos
								rad *= 1.6;
								neighbors = scr_data.range(element, rad);
								for(cont=0;cont<neighbors.length;cont++)
								{
									((Point3D)neighbors[cont]).visited = 1;

								}
								//create a new plane
								new_plane = new Plane3D(normalVector);
								new_plane_set.insert(new_plane);
							}
						}
						else 
						{
							for(cont=0;cont<neighbors.length;cont++)
							{
								element = (Point3D)neighbors[cont];
								element.visited = 2;
							}
						}
					}
				} catch(Exception e)
				{
					System.err.println(e.getMessage());
					result_list = null;
					return;
				}
			}
			progress += prog_inc;
		}

		for(cont=0;cont<tam;cont++)
		{
			element = (Point3D) elements[cont];
			element.visited = 0;
		}
		result_list.add(new_plane_set);
	}

	//TODO write method for working when normals are already computed (not necessary compute them again)
	private void patchNormalSet(ScreenData scr_data)
	{

	}
}
