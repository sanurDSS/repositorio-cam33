package javavis.jip3d.functions;

import javavis.base.Function3DGroup;
import javavis.base.ParamType;
import javavis.base.JIPException;
import javavis.jip3d.geom.MyTransform2D;
import javavis.jip3d.geom.Point3D;
import javavis.jip3d.gui.Function3D;
import javavis.jip3d.gui.FunctionParam;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;

public class FPrueba extends Function3D {

	public FPrueba() {
		super();

		//specify the allowed screen data input type
		this.allowed_input = ScreenOptions.tPOINTSET3D | ScreenOptions.tTRAJ2D;
		this.group = Function3DGroup.Others;


		FunctionParam p1 = new FunctionParam("Iterations", ParamType.INT);
		p1.setValue(10);
		FunctionParam p2 = new FunctionParam("dvalue", ParamType.FLOAT);
		p2.setValue(87.342);
		FunctionParam p3 = new FunctionParam("saludo", ParamType.STRING);
		p3.setValue("Cambia el saludo");
		FunctionParam p4 = new FunctionParam("Fichero", ParamType.DIR);
		FunctionParam p5 = new FunctionParam("Check", ParamType.BOOL);
		p5.setValue(false);

		this.addParam(p1);
		this.addParam(p2);
		this.addParam(p3);
		this.addParam(p4);
		this.addParam(p5);

	}

	@Override
	public void proccessData(ScreenData scr_data) throws JIPException {
		int max = 4999;
		int cont1, cont2, cont3;
		double value = 1.0;
		double progress_inc;
		double inc = this.paramValueReal("dvalue");
		int tam = this.paramValueInt("Iterations");
		String salutation = this.paramValueString("saludo");
		String fichero = this.paramValueString("Fichero");
		boolean guardar = this.paramValueBool("Check");

		progress_inc = 1.0/(tam+1) * 100;

		System.out.println(salutation);
		if(!guardar) System.out.print("No ");
		System.out.println("Save the file: ");
		System.out.println(fichero+"\n");

		for(cont1=0;cont1<tam;cont1++)
		{
			for(cont2=0;cont2<max;cont2++)
				for(cont3=0;cont3<max;cont3++)
				{
					inc *= inc/cont2;
					value += inc * (value+cont3) / cont1;
				}

			///Next Code is an example for enabling function interruption ability
			Thread thisThread = Thread.currentThread();
			if(blinker != thisThread) return;
			progress += progress_inc;

			System.out.println("Remain running: "+cont1);
		}
		System.out.println("Data name: "+scr_data.name);

		double angle = Math.PI/4.0;
		Point3D punto1 = new Point3D(6,0,1);
		Point3D punto2 = new Point3D(0,0,6);
		System.out.println(punto1.toString());
		System.out.println(punto2.toString());

		MyTransform2D tr2d = new MyTransform2D();
		tr2d.setRotation(angle);
		System.out.println("Trans:");
		System.out.println(tr2d.mat.toString());
		punto1.applyTransform(tr2d);
		punto2.applyTransform(tr2d);
		System.out.println(punto1.toString());
		System.out.println(punto2.toString());
	}

}
