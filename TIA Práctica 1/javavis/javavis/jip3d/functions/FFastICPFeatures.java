package javavis.jip3d.functions;

import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Color3f;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import javavis.base.Function3DGroup;
import javavis.base.JIPException;
import javavis.base.ParamType;
import javavis.jip3d.geom.Feature2D;
import javavis.jip3d.geom.MyKDTree;
import javavis.jip3d.geom.MyTransform3D;
import javavis.jip3d.geom.Point3D;
import javavis.jip3d.geom.Quaternion;
import javavis.jip3d.geom.Segment3D;
import javavis.jip3d.gui.Function3D;
import javavis.jip3d.gui.FunctionParam;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;
import javavis.jip3d.gui.dataobjects.FeatureSet2D;
import javavis.jip3d.gui.dataobjects.SegmentSet3D;

public class FFastICPFeatures extends Function3D {
	double error;

	public FFastICPFeatures() {
		super();
		this.allowed_input = ScreenOptions.tFEATURESET2D;
		this.group = Function3DGroup.Egomotion;


		FunctionParam p1 = new FunctionParam("Next Object", ParamType.SCRDATA);
		FunctionParam p2 = new FunctionParam("Iterations", ParamType.INT);
		p2.setValue(60);
		FunctionParam p3 = new FunctionParam("Intermediate", ParamType.BOOL);
		p3.setValue(false);


		this.addParam(p1);
		this.addParam(p2);
		this.addParam(p3);
	}

	@Override
	public void proccessData(ScreenData scr_data) throws JIPException {
		result_list = new ArrayList<ScreenData>();
		int iterations = this.paramValueInt("Iterations");
		ScreenData scenedata = this.paramValueScrData("Next Object");
		double error_prev;
		boolean intermediate = this.paramValueBool("Intermediate");


		MyTransform3D tr3d;
		MyTransform3D auxT;
    	Feature2D []sceneTr;
    	Object []scene, model;
    	ArrayList<Pair> closest;
    	int cont=0;
    	Object []elements;

    	double prog_inc = 100.0/iterations;

    	elements = scenedata.elements();
    	scene = scenedata.elements();

    	elements = scr_data.elements(); //model
    	model = new Feature2D[elements.length];
    	for(cont=0;cont<elements.length;cont++)
    		model[cont] = (Feature2D)elements[cont];

    	tr3d = new MyTransform3D();
		cont = 0;
		error = Double.MAX_VALUE;
		do {
			cont++;
			error_prev = error;
			// Aplicamos la transformaci�n al conjunto de datos
			sceneTr = applyTrans (scene, tr3d);
			//buscamos los mas cercanos
			closest = findClosest(sceneTr, sceneTr, scr_data.getData(), 2200);

			//guardamos el resultado parcial
			if(intermediate)
				mostrarPares(closest, cont);

			//obtenemos la trasformacionn en funciona de los mas cercanos
			auxT = calcTrans (closest);
			//update total transformation
			tr3d.applyTransform(auxT);

			progress += prog_inc;
		} while (cont<iterations && Math.abs(error-error_prev)>0.0001);
		sceneTr = applyTrans (scene, tr3d);

		FeatureSet2D resulttras = new FeatureSet2D(new ScreenOptions());
		resulttras.name = "ICPBasic";
		resulttras.scr_opt.width = 1;
		resulttras.scr_opt.global_color = true;
		resulttras.scr_opt.color = new Color3f(0,1,0);
		for(Feature2D point: sceneTr)
			resulttras.insert(point);
		result_list.add(resulttras);
		mostrarPares(closest, cont);
	}

	public MyTransform3D calcTrans(ArrayList<Pair> pairs)
	{
		MyTransform3D ret;
		Quaternion quat;
		double [][]sigmapx = new double[3][3];
		double [][]mean = new double[3][3];
		double []delta = new double[3];
		double [][]Q = new double[4][4];
		double meanx, meany, meanz;
		int tam = pairs.size();
		double mean2x, mean2y, mean2z;
		Feature2D modelP, sceneP;
		double trace;
		DenseDoubleMatrix2D A;
		DoubleMatrix2D V;
		DoubleMatrix2D D;
		EigenvalueDecomposition EVD;
		Point3D meanM, meanS;
		int best;
		double value;
		double var_total = 0;

		meanx = meany = meanz = 0;
		mean2x = mean2y = mean2z = 0;
		sigmapx[0][0] = sigmapx[0][1] = sigmapx[0][2] = 0;
		sigmapx[1][0] = sigmapx[1][1] = sigmapx[1][2] = 0;
		sigmapx[2][0] = sigmapx[2][1] = sigmapx[2][2] = 0;

		for(Pair p: pairs)
		{
			modelP = p.p_model;
			sceneP = p.p_scene;
			meanx += sceneP.getX() * p.dist;
			meany += sceneP.getY() * p.dist;
			meanz += sceneP.getZ() * p.dist;
			mean2x += modelP.getX() * p.dist;
			mean2y += modelP.getY() * p.dist;
			mean2z += modelP.getZ() * p.dist;
			var_total += p.dist;
			sigmapx[0][0]+=sceneP.getX()*modelP.getX();
			sigmapx[0][1]+=sceneP.getX()*modelP.getY();
			sigmapx[0][2]+=sceneP.getX()*modelP.getZ();
			sigmapx[1][0]+=sceneP.getY()*modelP.getX();
			sigmapx[1][1]+=sceneP.getY()*modelP.getY();
			sigmapx[1][2]+=sceneP.getY()*modelP.getZ();
			sigmapx[2][0]+=sceneP.getZ()*modelP.getX();
			sigmapx[2][1]+=sceneP.getZ()*modelP.getY();
			sigmapx[2][2]+=sceneP.getZ()*modelP.getZ();
		}

		meanx /= var_total;
		meany /= var_total;
		meanz /= var_total;
		mean2x /= var_total;
		mean2y /= var_total;
		mean2z /= var_total;
		mean[0][0] = meanx * mean2x; mean[0][1] = meanx * mean2y; mean[0][2] = meanx * mean2z;
		mean[1][0] = meany * mean2x; mean[1][1] = meany * mean2y; mean[1][2] = meany * mean2z;
		mean[2][0] = meanz * mean2x; mean[2][1] = meanz * mean2y; mean[2][2] = meanz * mean2z;

		for(int i=0;i<3;i++)
			for(int j=0;j<3;j++)
				sigmapx[i][j] = (sigmapx[i][j]- mean[i][j]) / tam  ;
		delta[0] = sigmapx[1][2] - sigmapx[2][1];
		delta[1] = sigmapx[2][0] - sigmapx[0][2];
		delta[2] = sigmapx[0][1] - sigmapx[1][0];

		trace = sigmapx[0][0] + sigmapx[1][1] + sigmapx[2][2];

		Q[0][0] = trace; Q[0][1] = delta[0]; Q[0][2] = delta[1]; Q[0][3] = delta[2];
		Q[1][0] = delta[0]; Q[1][1] = sigmapx[0][0] + sigmapx[0][0] - trace; Q[1][2] = sigmapx[0][1] + sigmapx[1][0]; Q[1][3] = sigmapx[0][2] + sigmapx[2][0];
		Q[2][0] = delta[1]; Q[2][1] = sigmapx[1][0] + sigmapx[1][0]; Q[2][2] = sigmapx[1][1] + sigmapx[1][1] - trace; Q[2][3] = sigmapx[1][2] + sigmapx[2][1];
		Q[3][0] = delta[2]; Q[3][1] = sigmapx[2][0] + sigmapx[0][2]; Q[3][2] = sigmapx[2][1] + sigmapx[1][2]; Q[3][3] = sigmapx[2][2] + sigmapx[2][2] - trace;
		A = new DenseDoubleMatrix2D(Q);
		EVD = new EigenvalueDecomposition(A);
		D = EVD.getD();
		V = EVD.getV();
		best = 0;
		value = D.getQuick(0, 0);
		//Look for the maximum eigenvalue
		for(int i=1;i<4;i++)
			if(D.getQuick(i, i)>value)
			{
				value = D.getQuick(i, i);
				best = i;
			}
		quat = new Quaternion(V.getQuick(0, best), V.getQuick(1, best), V.getQuick(2, best), V.getQuick(3, best), 0, 0, 0);

		ret =  quat.getTransform();

		meanS = new Point3D(meanx, meany, meanz);
		meanM = new Point3D(mean2x, mean2y, mean2z);
		meanS.applyTransform(ret);

		ret.setTranslation((meanM.subPoint(meanS)).getCoords());

		return ret;
	}

    public ArrayList<Pair> findClosest(Object[] scenetr, Object []scene, MyKDTree model, int max) throws JIPException
    {
    	Feature2D p_scene;
    	Feature2D closest;
    	int cont;
    	ArrayList<Pair> ret = new ArrayList<Pair>();
    	ArrayList<Pair> candidates = new ArrayList<Pair>();
    	error = 0.0;
    	Random generator = new Random(System.currentTimeMillis());
    	int pos, tam;
    	double dist;
    	double mean = 0;
    	double var = 0;
    	double aux, weight;

    	for(cont=0;cont<max;cont++)
    	{
    		try
    		{
    			pos = generator.nextInt(scene.length);
        		p_scene = (Feature2D)scenetr[pos];
    			closest = (Feature2D)model.nearest(p_scene.getCoords());
    			dist = p_scene.getDistance(closest);
    			mean += dist;
    			candidates.add(new Pair(closest, (Feature2D)scene[pos], dist));
    		} catch(Exception e)
    		{
    			throw new JIPException(e.getMessage());
    		}
    	}
    	tam = candidates.size();
    	mean /= tam;

    	for(Pair p:candidates)
    	{
    		aux = p.dist - mean;
    		var += aux * aux;
    	}
    	var /= tam;
    	var = Math.sqrt(var);

    	for(Pair p:candidates)
    	{
    		if(p.dist<var)
    		{
    			dist = p.dist - mean;
    			weight = Math.exp(-(dist*dist)/var);
    			p.dist = weight;
    			ret.add(p);
    			error += p.dist;
    		}
    	}
    	error /= ret.size();
    	return ret;
    }

	private Feature2D[] applyTrans(Object[] source, MyTransform3D tr3d)
	{
		Feature2D []ret;
		int cont, tam;

		tam = source.length;
		ret = new Feature2D[tam];

		for(cont=0;cont<tam;cont++)
		{
			ret[cont] = new Feature2D((Feature2D)source[cont]);
			ret[cont].applyTransform(tr3d);
		}

		return ret;
	}

	private class Pair
	{
		public Feature2D p_model;
		public Feature2D p_scene;
		double dist;

		public Pair(Feature2D m, Feature2D s, double d)
		{
			p_model = m;
			p_scene = s;
			dist = d;
		}
	}

	private void mostrarPares(ArrayList<Pair> pairs, int cont)
	{
		FeatureSet2D points;
		SegmentSet3D segments;
		Feature2D point;

		points = new FeatureSet2D(new ScreenOptions());
		points.name = "Scene"+cont;

		segments = new SegmentSet3D(new ScreenOptions());
		segments.name = "Pairs"+cont;

		for(Pair p: pairs)
		{
			point = new Feature2D(p.p_scene);
			points.insert(point);
			segments.insert(new Segment3D(point, p.p_model));
		}

		result_list.add(points);
		result_list.add(segments);
	}

}
