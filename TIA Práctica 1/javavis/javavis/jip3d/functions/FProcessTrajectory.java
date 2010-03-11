package javavis.jip3d.functions;

import javavis.base.Function3DGroup;
import javavis.base.ParamType;
import javavis.base.JIPException;
import javavis.jip3d.geom.MyTransform2D;
import javavis.jip3d.gui.Function3D;
import javavis.jip3d.gui.FunctionParam;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;
import javavis.jip3d.gui.dataobjects.PlaneSet3D;
import javavis.jip3d.gui.dataobjects.PointSet3D;
import javavis.jip3d.gui.dataobjects.Trajectory;
import javavis.jip3d.gui.dataobjects.Trajectory2D;

public class FProcessTrajectory extends Function3D {

	/**
	 * Esta clase esta pensada para procesar una serie de ficheros de puntos3D a partir de un objeto trayectoria.
	 * No se hace resconstrucción final, solo se abren los ficheros, se calculan los planos y se almacenan
	 * datos estadísticos de tiempos y puntos analizados. Es decir, la clase es una especie de batch para
	 * la extracción de planos de una secuencia de imágenes 3D.
	 */
	public FProcessTrajectory()
	{
		super();

		this.allowed_input = ScreenOptions.tTRAJ2D | ScreenOptions.tTRAJ3D;
		this.group = Function3DGroup.Model3D;


		FunctionParam p1 = new FunctionParam("Recorte", ParamType.FLOAT);
		p1.setValue(0.0);
		FunctionParam p2 = new FunctionParam("Directorio Planos", ParamType.DIR);
		FunctionParam p3 = new FunctionParam("Directorio Puntos", ParamType.DIR); //esto debera desaparecer

		this.addParam(p1);
		this.addParam(p2);
		this.addParam(p3);
	}

	@Override
	public void proccessData(ScreenData scr_data) throws JIPException {
		String ruta_planos = this.paramValueString("Directorio Planos");
		String ruta_puntos = this.paramValueString("Directorio Puntos");
		double recorte = this.paramValueReal("Recorte");
		String file_name, path;
		int file_number, cont;
		Trajectory traj;

		long total_time, t1, t2;
		int total_points, total_planes;
		PointSet3D points;
		PointSet3D cutted_points;
		PlaneSet3D planes;

		FDivideHor splitter = new FDivideHor();
		splitter.setParamValue("Height", recorte);
		splitter.setParamValue("Manual", false);

		FPlanePatch seg_planos = new FPlanePatch();

		Trajectory2D traj2D = new Trajectory2D(new ScreenOptions(), 0);
		traj2D.name = "trajectory";
		traj2D.path = ruta_planos;

		total_time = 0;
		total_points = 0;
		total_planes = 0;
		traj = (Trajectory)scr_data;
		file_number = traj.files.size();
		double prog_inc = 100.0 / file_number;
		path = traj.path;
System.out.println("Ruta puntos: "+ruta_puntos);
System.out.println("Ruta planos: "+ruta_planos);
		for(cont=0;cont<file_number;cont++)
		{
System.out.println(cont);
			file_name = traj.files.get(cont);
			points = new PointSet3D(new ScreenOptions());
			points.readData(file_name, path);

			//recortamos el conjunto
			splitter.proccessData(points);
			cutted_points = (PointSet3D)splitter.result_list.remove(0);
			total_points += cutted_points.scr_opt.num_points;
			t1 = System.currentTimeMillis();
			seg_planos.proccessData(cutted_points);
			t2 = System.currentTimeMillis();
			total_time += t2 -t1;
			planes = (PlaneSet3D)seg_planos.result_list.remove(0);
			total_planes += planes.scr_opt.num_points;
			cutted_points.writeData(file_name, ruta_puntos);
			planes.writeData(file_name, ruta_planos);
			traj2D.files.add(file_name);
			traj2D.transforms.add(new MyTransform2D());
			progress += prog_inc;
		}
		traj2D.writeData("trajectory.txt", ruta_planos);

		System.out.println("Statistics:");
		System.out.println("Points per image (mean): "+total_points/file_number);
		System.out.println("Planes per image (mean):"+total_planes/file_number);
		System.out.println("Time for extracting planes per image (mean): "+total_time/file_number);
	}

}
