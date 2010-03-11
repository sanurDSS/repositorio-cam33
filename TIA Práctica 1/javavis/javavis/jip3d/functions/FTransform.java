package javavis.jip3d.functions;

import javavis.base.Function3DGroup;
import javavis.base.ParamType;
import javavis.base.JIPException;
import javavis.jip3d.geom.MyTransform;
import javavis.jip3d.geom.MyTransform3D;
import javavis.jip3d.gui.Function3D;
import javavis.jip3d.gui.FunctionParam;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;

public class FTransform extends Function3D {

	public FTransform() {
		super();
		this.allowed_input = ScreenOptions.tALLTYPES;
		this.group = Function3DGroup.Transform;

		FunctionParam p1 = new FunctionParam("angle X", ParamType.FLOAT);
		p1.setValue(0.0);
		FunctionParam p2 = new FunctionParam("angle Y", ParamType.FLOAT);
		p2.setValue(0.0);
		FunctionParam p3 = new FunctionParam("angle Z", ParamType.FLOAT);
		p3.setValue(0.0);
		FunctionParam p4 = new FunctionParam("traslation X", ParamType.FLOAT);
		p4.setValue(0.0);
		FunctionParam p5 = new FunctionParam("traslation Y", ParamType.FLOAT);
		p5.setValue(0.0);
		FunctionParam p6 = new FunctionParam("traslation Z", ParamType.FLOAT);
		p6.setValue(0.0);

		this.addParam(p1);
		this.addParam(p2);
		this.addParam(p3);
		this.addParam(p4);
		this.addParam(p5);
		this.addParam(p6);
	}

	@Override
	public void proccessData(ScreenData scr_data) throws JIPException {
		double angx = this.paramValueReal("angle X");
		double angy = this.paramValueReal("angle Y");
		double angz = this.paramValueReal("angle Z");
		double tx = this.paramValueReal("traslation X");
		double ty = this.paramValueReal("traslation Y");
		double tz = this.paramValueReal("traslation Z");

		MyTransform tr = new MyTransform3D(tx, ty, tz, angx, angy, angz);
		scr_data.applyTransform(tr);
	}

}
