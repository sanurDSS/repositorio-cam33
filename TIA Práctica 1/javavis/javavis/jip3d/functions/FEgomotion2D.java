package javavis.jip3d.functions;

import java.util.ArrayList;

import javax.vecmath.Color3f;

import javavis.base.Function3DGroup;
import javavis.base.JIPException;
import javavis.base.ParamType;
import javavis.jip3d.geom.MyKDTree;
import javavis.jip3d.geom.MyTransform2D;
import javavis.jip3d.geom.Plane3D;
import javavis.jip3d.geom.Point3D;
import javavis.jip3d.gui.Function3D;
import javavis.jip3d.gui.FunctionParam;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;
import javavis.jip3d.gui.dataobjects.PlaneSet3D;

/**
 * This class is used for computing Egomotion between two sets of 3D planar patches. It is assumed that 
 * the transformation that aligns those sets has 3 degrees of freedom.
 * @author dviejo
 *
 */
public class FEgomotion2D extends Function3D {
//TODO Es necesaria una revisión del metodo para hacerlo funcionar con la misma filosofia que los Egomotion3D
	private double alpha;
	private double beta;

	private MyTransform2D transform;
	private double[]error;

	public FEgomotion2D() {
		super();
		this.allowed_input = ScreenOptions.tPLANARSET3D;
		this.group = Function3DGroup.Egomotion;


		transform = null;
		error = null;
		alpha = 0.025;
		beta = 0.025;

		FunctionParam p1 = new FunctionParam("Next Object", ParamType.SCRDATA);
		FunctionParam p2 = new FunctionParam("Alpha", ParamType.FLOAT);
		p2.setValue(0.1025);
		FunctionParam p3 = new FunctionParam("Beta", ParamType.FLOAT); //normal fitting weight
		p3.setValue(0.015); //0.005
		FunctionParam p4 = new FunctionParam("Initial Variance", ParamType.FLOAT); //normal fitting weight
		p4.setValue(0.3); //0.85
		FunctionParam p5 = new FunctionParam("Final Variance", ParamType.FLOAT); //normal fitting weight
		p5.setValue(0.0035); //0.15
		FunctionParam p6 = new FunctionParam("Iterations", ParamType.INT);
		p6.setValue(75);

		this.addParam(p1);
		this.addParam(p2);
		this.addParam(p3);
		this.addParam(p4);
		this.addParam(p5);
		this.addParam(p6);
	}

	@Override
	public void proccessData(ScreenData scr_data) throws JIPException {
		result_list = new ArrayList<ScreenData>();
		ScreenData model = this.paramValueScrData("Next Object");
		alpha = this.paramValueReal("Alpha");
		beta = this.paramValueReal("Beta");
		double init_var = this.paramValueReal("Initial Variance");
		double fin_var = this.paramValueReal("Final Variance");
		int iterations = this.paramValueInt("Iterations");
		int cont;
		MyTransform2D tr2d = new MyTransform2D();
		double var = init_var;
		double incvar = (var-fin_var)/15;
		Object []elements = model.elements();
		Plane3D [] elements_model = new Plane3D[elements.length];
		Plane3D [] elements_modeltr;
		Plane3D [] elements_scene;

		double prog_inc = 50.0/iterations;

		double angle, prev_angle;

		double []traslation;

		for(cont=0;cont<elements.length;cont++)
			elements_model[cont] = (Plane3D)elements[cont];

		elements = scr_data.elements();
		elements_scene = new Plane3D[elements.length];
		for(cont=0;cont<elements.length;cont++)
			elements_scene[cont] = (Plane3D)elements[cont];

		if(model.getType() != ScreenOptions.tPLANARSET3D)
		{
			this.dialog.error("Error: input data type", "FEgomotion2D");
			result_list = null;
			return;
		}
		int []closests = new int[model.scr_opt.num_points];

		cont = 0;
		angle = 0;
		do
		{
			prev_angle = angle;
			elements_modeltr = this.applyTransformation(elements_model, tr2d);

			tr2d = findClosestRotation(elements_scene, elements_model, elements_modeltr, closests, var);
			angle = tr2d.getAngle();
			cont++;
			if(var>fin_var+incvar)
				var -= incvar;
			else var = fin_var;

			progress += prog_inc;
//System.out.println(tr2d.toString());
		} while(cont<iterations && (Math.abs(angle - prev_angle) > 0.0001));
System.out.print("Numero de iteraciones: "+cont);

//		System.out.println(tr2d.toString());
		elements_modeltr = this.applyTransformation(elements_model, tr2d);


//		pairs = new SegmentSet3D(new ScreenOptions());
//		pairs.name = "closest";
//		pairs.scr_opt.width = 1;
//		pairs.scr_opt.color = new Color3f(1,0,0);
//		for(cont=0;cont<closests.length;cont++)
//		{
//			pairs.insert(new Segment3D(elements_model[cont].origin, elements_scene[closests[cont]].origin));
//		}
//		result_list.add(pairs);

//		directions = findMainDirections(elements_modeltr);
//		traslation = findClosestTranslation2(scr_data.getData(), elements_model, elements_modeltr, closests, var, directions[0], directions[1]);
//		tr2d.setTraslation(traslation[0], traslation[1]);
//System.out.println("Direcciones principales");
//System.out.println(directions[0].toString());
//System.out.println(directions[1].toString());

		cont = 0;
		var = init_var;
		double []prev_trasl = new double[2];
		prev_trasl[0] = prev_trasl[1] = 0;
		do
		{
			elements_modeltr = this.applyTransformation(elements_model, tr2d);

			//sacamos cada paso intermedio

			progress = 50;

			traslation = findClosestTranslation(elements_scene, elements_modeltr, elements_modeltr, closests, var);
			prev_trasl[0] += traslation[0];
			prev_trasl[1] += traslation[1];
			tr2d.setTraslation(prev_trasl[0], prev_trasl[1]);
			cont++;
			if(var>fin_var+incvar)
				var -= incvar;
			else var = fin_var;

			progress += prog_inc;
		} while(cont<iterations && (Math.abs(traslation[0]) > 0.0005 || Math.abs(traslation[1]) > 0.0005));

		elements_modeltr = this.applyTransformation(elements_model, tr2d);
		transform = tr2d;
		error = calcError(scr_data.getData(), elements_modeltr, closests);
		System.out.println(tr2d.toString());

		PlaneSet3D resulttras = new PlaneSet3D(new ScreenOptions());
		resulttras.name = "egomotion2D";
		resulttras.scr_opt.width = 1;
		resulttras.scr_opt.color = new Color3f(0,1,0);
		resulttras.scr_opt.global_color = true;
		for(Plane3D plane: elements_modeltr)
			resulttras.insert(plane);
		result_list.add(resulttras);

	}

	private MyTransform2D findClosestRotation(Plane3D [] elements_scene, Plane3D[] model, Plane3D[] modeltr, int []closests, double variance)
	{
		MyTransform2D ret = new MyTransform2D();
		double angle_res = 0;
		double total = 0;
		double []distances;
		double []angles;

		Plane3D element_scene;
		Plane3D element_model;
		Plane3D element_modeltr;
		double angle;
		double dist;
		double total_dist;
		double less_total;
		double less_angle = 100;
		double less_dist = 100;
		int tam, tam2;
		int cont, cont2;
		double dist_aux;

		less_total = Double.MAX_VALUE;
		tam = elements_scene.length;
		tam2 = model.length;
		distances = new double[tam2];
		angles = new double[tam2];

		for(cont=0;cont<tam2;cont++)
		{
			element_modeltr = modeltr[cont];
			less_total = Double.MAX_VALUE;
			for(cont2=0;cont2<tam;cont2++)
			{
				element_scene = elements_scene[cont2];
				angle = element_modeltr.vector.getX()*element_scene.vector.getX() + element_modeltr.vector.getZ()*element_scene.vector.getZ();
				angle /= Math.sqrt(element_modeltr.vector.getX()*element_modeltr.vector.getX()+ element_modeltr.vector.getZ()*element_modeltr.vector.getZ()) * Math.sqrt(element_scene.vector.getX()*element_scene.vector.getX()+element_scene.vector.getZ()*element_scene.vector.getZ());
				angle = Math.acos(angle);

				dist_aux = Math.abs(element_scene.pointDistance(element_modeltr.origin));
				dist = dist_aux + element_scene.origin.getDistance(element_modeltr.origin);

				total_dist = angle + beta * Math.abs(dist);
				if(total_dist<less_total)
				{
					less_total = total_dist;
					less_angle = angle;
					less_dist = dist;
					closests[cont] = cont2;
				}
			}
			distances[cont] = Math.exp(-(less_angle*less_angle)/(variance))+ beta * Math.exp(-(less_dist*less_dist)/variance);
			total += distances[cont];
			element_model = model[cont];
			element_scene = elements_scene[closests[cont]];
			angle = element_model.vector.getX()*element_scene.vector.getX() + element_model.vector.getZ()*element_scene.vector.getZ();
			angle /= Math.sqrt(element_model.vector.getX()*element_model.vector.getX()+ element_model.vector.getZ()*element_model.vector.getZ()) * Math.sqrt(element_scene.vector.getX()*element_scene.vector.getX()+element_scene.vector.getZ()*element_scene.vector.getZ());
			//caso degenerado, por errores de precision, angle puede ser >1
			if(angle>1) angle = 0;
			else
				angle = Math.acos(angle);
			if((element_model.vector.getZ()*element_scene.vector.getX() - element_model.vector.getX()*element_scene.vector.getZ())<0)
				angle = -angle;
			angles[cont] = angle;

		}

		if(total!=0)
			for(cont=0;cont<tam2;cont++)
			{
				distances[cont] /= total;
				angle_res += angles[cont] * distances[cont];
			}

		ret.setRotation(angle_res);
		return ret;
	}

	private Plane3D[] applyTransformation(Plane3D[] source, MyTransform2D tr2d)
	{
		Plane3D []ret;
//		Plane3D element;
		int cont, tam;

		tam = source.length;
		ret = new Plane3D[tam];

		for(cont=0;cont<tam;cont++)
		{
			ret[cont] = new Plane3D(source[cont]);
			ret[cont].applyTransform(tr2d);
		}

		return ret;
	}

	public double[]calcError(MyKDTree scene, Plane3D[] modeltr, int[]closests)
	{
		double []ret = new double[2]; //traslation and angle
		Object [] elements_scene;
		Plane3D element_scene;
		int cont, tam = closests.length;
		double angle, dist;

		ret[0] = ret[1] = 0;
		elements_scene = scene.elements();

		for(cont=0;cont<tam;cont++)
		{
			element_scene = (Plane3D)elements_scene[closests[cont]];
			angle = modeltr[cont].anglePlane(element_scene);
			dist = modeltr[cont].pointDistance(element_scene.origin);

			ret[0] += dist;
			ret[1] += angle;
		}
		ret[0] /= tam;
		ret[1] /= tam;
		return ret;
	}

	private double [] findClosestTranslation(Plane3D[] elements_scene, Plane3D[] model, Plane3D[] modeltr, int []closests, double variance)//, double []dir1, double[]dir2)
	{
		double []ret = new double[2];
		double total;
		double []distances;
		double [][]traslations;

		Plane3D element_scene;
		Plane3D element_model;
		Plane3D element_modeltr;
		double angle;
		double dist;
		double total_dist;
		double less_total;
		int tam, tam2;
		int cont, cont2;

		less_total = Double.MAX_VALUE;
		tam = elements_scene.length;
		tam2 = model.length;
		distances = new double[tam2];
		traslations = new double[2][tam2];
		Point3D projection;

		total = 0;
		for(cont=0;cont<tam2;cont++)
		{
			element_modeltr = modeltr[cont];
			less_total = Double.MAX_VALUE;
			for(cont2=0;cont2<tam;cont2++)
			{
				element_scene = elements_scene[cont2];
				angle = element_modeltr.vector.getX()*element_scene.vector.getX() + element_modeltr.vector.getZ()*element_scene.vector.getZ();
				angle /= Math.sqrt(element_modeltr.vector.getX()*element_modeltr.vector.getX()+ element_modeltr.vector.getZ()*element_modeltr.vector.getZ()) * Math.sqrt(element_scene.vector.getX()*element_scene.vector.getX()+element_scene.vector.getZ()*element_scene.vector.getZ());
				angle = Math.acos(angle);

				dist = Math.abs(element_scene.pointDistance(element_modeltr.origin)) +
				Math.abs(element_modeltr.origin.getDistance(element_scene.origin));

				total_dist = angle + alpha * Math.abs(dist);
				if(total_dist<less_total)
				{
					less_total = total_dist;
					closests[cont] = cont2;
				}
			}
			distances[cont] = Math.exp(-(less_total*less_total)/(variance));
			total += distances[cont];
			element_model = model[cont];
			element_scene = elements_scene[closests[cont]];
			projection = element_scene.pointProjection(element_model.origin);
			traslations[0][cont] = projection.getX()-element_model.origin.getX();
			traslations[1][cont] = projection.getZ()-element_model.origin.getZ();
		}

		ret[0] = 0;
		ret[1] = 0;
		if(total!=0)
			for(cont=0;cont<tam2;cont++)
			{
				distances[cont] /= total;
				ret[0] += traslations[0][cont] * distances[cont];
				ret[1] += traslations[1][cont] * distances[cont];
			}

		return ret;
	}


	public MyTransform2D getTransform()
	{
		return transform;
	}

	public double[] getError()
	{
		return error;
	}

}
