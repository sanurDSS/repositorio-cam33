package javavis.jip3d.functions;

import java.util.ArrayList;

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
import javavis.jip3d.gui.Function3D;
import javavis.jip3d.gui.FunctionParam;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;
import javavis.jip3d.gui.dataobjects.FeatureSet2D;

/**
 * Implementación del método ICP propuesto por Besl y McKay en 1992 (A Method for Registration
 * of 3-D Shapes, PAMI'92). Dados dos conjuntos de puntos 3D que representan la superficie de un mismo objeto
 * o de una misma escena desde dos puntos de vista distintos, este método encuentra la transformación que
 * deberá aplicarse a uno de los conjuntos para alinearlo con el otro. The best rotation is computed by
 * a quaternions based method. La implementación realizada en FICPBasic
 * se correponde con el método básico (sin aceleración en la estimación de la transformación) propuesto
 * inicialmente en el mencionado artículo.
 * @author Miguel Cazorla
 *
 */
public class FICPQuatFeatures extends Function3D {
	private double error;
	private static int NUMBERNEIGHBOURS = 10;
	
	public FICPQuatFeatures() {
		super();
		this.allowed_input = ScreenOptions.tFEATURESET2D;
		this.group = Function3DGroup.Egomotion;

		FunctionParam p1 = new FunctionParam("Next Object", ParamType.SCRDATA);
		FunctionParam p2 = new FunctionParam("Iterations", ParamType.INT);
		p2.setValue(40);
		FunctionParam p3 = new FunctionParam("Using Descriptor", ParamType.BOOL);
		p3.setValue(true);

		this.addParam(p1);
		this.addParam(p2);
		this.addParam(p3);
	}

	@Override
	public void proccessData(ScreenData scr_data) throws JIPException {
		result_list = new ArrayList<ScreenData>();
		int iterations = this.paramValueInt("Iterations");
		ScreenData scenedata = this.paramValueScrData("Next Object");
		boolean usingDesc = this.paramValueBool("Using Descriptor");

		MyTransform3D tr3d;
		MyTransform3D auxT;
		Feature2D []sceneTr;
    	Object []scene, model;
    	ArrayList<Pair> closest;
    	int cont=0;
    	double[][] weights;

    	double prog_inc = 100.0/iterations;

    	scene = scenedata.elements();
    	model = new Feature2D[scr_data.elements().length];
    	
    	for(cont=0;cont<model.length;cont++)
    		model[cont] = (Feature2D)scr_data.elements()[cont];

    	tr3d = new MyTransform3D();
		cont = 0;
		error = Double.MAX_VALUE;
		do {
			cont++;
			// Aplicamos la transformacion al conjunto de datos
			sceneTr = applyTrans (scene, tr3d);
			//buscamos los mas cercanos
			if (!usingDesc) {
				closest = findClosest(sceneTr, sceneTr, scr_data.getData());
				auxT = calcTrans (closest);
			}
			else {
				weights = weightPairs(sceneTr, sceneTr, scr_data.elements());
				auxT = calcTransDesc (weights, sceneTr, scr_data.getData())	;
			}
			
			//update total transformation
			tr3d.applyTransform(auxT);

			progress += prog_inc;
			System.out.println("Iterations: "+cont+"\n"+tr3d.toString());
		} while (cont<iterations);
		System.out.println("Iterations: "+cont+"\n"+tr3d.toString());
		sceneTr = applyTrans (scene, tr3d);

		FeatureSet2D resulttras = new FeatureSet2D(new ScreenOptions());
		resulttras.name = "ICPBasic";
		resulttras.scr_opt.width = 1;
		resulttras.scr_opt.global_color = true;
		resulttras.scr_opt.color = new Color3f(0,1,0);
		for(Feature2D point: sceneTr)
			resulttras.insert(point);
		result_list.add(resulttras);

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

		meanx = meany = meanz = 0;
		mean2x = mean2y = mean2z = 0;
		sigmapx[0][0] = sigmapx[0][1] = sigmapx[0][2] = 0;
		sigmapx[1][0] = sigmapx[1][1] = sigmapx[1][2] = 0;
		sigmapx[2][0] = sigmapx[2][1] = sigmapx[2][2] = 0;

		for(Pair p: pairs)
		{
			modelP = p.p_model;
			sceneP = p.p_scene;
			meanx += sceneP.getX();
			meany += sceneP.getY();
			meanz += sceneP.getZ();
			mean2x += modelP.getX();
			mean2y += modelP.getY();
			mean2z += modelP.getZ();
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

		meanx /= tam;
		meany /= tam;
		meanz /= tam;
		mean2x /= tam;
		mean2y /= tam;
		mean2z /= tam;
		mean[0][0] = meanx * mean2x; mean[0][1] = meanx * mean2y; mean[0][2] = meanx * mean2z;
		mean[1][0] = meany * mean2x; mean[1][1] = meany * mean2y; mean[1][2] = meany * mean2z;
		mean[2][0] = meanz * mean2x; mean[2][1] = meanz * mean2y; mean[2][2] = meanz * mean2z;

		for(int i=0;i<3;i++)
			for(int j=0;j<3;j++)
				sigmapx[i][j] = (sigmapx[i][j] - mean[i][j]) / tam;
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

	public MyTransform3D calcTransDesc(double[][] weights, Object []scene, Object []model)
	{
		MyTransform3D ret;
		Quaternion quat;
		double [][]sigmapx = new double[3][3];
		double [][]mean = new double[3][3];
		double []delta = new double[3];
		double [][]Q = new double[4][4];
		double meanx, meany, meanz;
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

		meanx = meany = meanz = 0;
		mean2x = mean2y = mean2z = 0;
		sigmapx[0][0] = sigmapx[0][1] = sigmapx[0][2] = 0;
		sigmapx[1][0] = sigmapx[1][1] = sigmapx[1][2] = 0;
		sigmapx[2][0] = sigmapx[2][1] = sigmapx[2][2] = 0;

		for (int s=0; s<scene.length; s++) {
			sceneP = (Feature2D)scene[s];
			meanx += sceneP.getX();
			meany += sceneP.getY();
			meanz += sceneP.getZ();
			for (int m=0; m<model.length; m++) {
				modelP = (Feature2D)model[m];
				sigmapx[0][0]+=weights[s][m]*sceneP.getX()*modelP.getX();
				sigmapx[0][1]+=weights[s][m]*sceneP.getX()*modelP.getY();
				sigmapx[0][2]+=weights[s][m]*sceneP.getX()*modelP.getZ();
				sigmapx[1][0]+=weights[s][m]*sceneP.getY()*modelP.getX();
				sigmapx[1][1]+=weights[s][m]*sceneP.getY()*modelP.getY();
				sigmapx[1][2]+=weights[s][m]*sceneP.getY()*modelP.getZ();
				sigmapx[2][0]+=weights[s][m]*sceneP.getZ()*modelP.getX();
				sigmapx[2][1]+=weights[s][m]*sceneP.getZ()*modelP.getY();
				sigmapx[2][2]+=weights[s][m]*sceneP.getZ()*modelP.getZ();
			}
		}
		for (int m=0; m<model.length; m++) {
			modelP = (Feature2D)model[m];
			mean2x += modelP.getX();
			mean2y += modelP.getY();
			mean2z += modelP.getZ();
		}

		meanx /= scene.length;
		meany /= scene.length;
		meanz /= scene.length;
		mean2x /= model.length;
		mean2y /= model.length;
		mean2z /= model.length;
		mean[0][0] = meanx * mean2x; mean[0][1] = meanx * mean2y; mean[0][2] = meanx * mean2z;
		mean[1][0] = meany * mean2x; mean[1][1] = meany * mean2y; mean[1][2] = meany * mean2z;
		mean[2][0] = meanz * mean2x; mean[2][1] = meanz * mean2y; mean[2][2] = meanz * mean2z;

		for(int i=0;i<3;i++)
			for(int j=0;j<3;j++)
				sigmapx[i][j] = (sigmapx[i][j] - mean[i][j]) / scene.length;
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
	
	public double[][] weightPairs(Object[] scenetr, Object []scene, MyKDTree model) throws JIPException
    {
    	Feature2D p_scene;
    	Feature2D[] neighbours;
    	double[][] weights=new double[scene.length][model.length];
    	double norm, var=10;
    	
    	for(int s=0;s<scene.length;s++) {
    		try {
        		p_scene = (Feature2D)scenetr[s];
    			norm=0.0;
    			neighbours=(Feature2D[])p_scene.nearest(p_scene.getCoords(), NUMBERNEIGHBOURS);
        		for(int m=0;m<model.length;m++) {
        			weights[s][m]=Math.exp(-p_scene.getDistance((Feature2D)model[m])/var);
        			norm += weights[s][m];
        		}
    			if (norm<0.000001) norm=0.000001;
    			for (int m=0; m<model.length; m++) {
    				weights[s][m] /= norm;
    			}
    		} catch(Exception e) {
    			throw new JIPException(e.getMessage());
    		}
    	}
    	return weights;
    }
	
	
    public ArrayList<Pair> findClosest(Object[] scenetr, Object []scene, MyKDTree model) throws JIPException
    {
    	Feature2D p_scene;
    	Feature2D closest;
    	ArrayList<Pair> ret = new ArrayList<Pair>();
    	error = 0.0;
    	
    	for(int cont=0;cont<scene.length;cont++) {
    		try {
        		p_scene = (Feature2D)scenetr[cont];
    			closest = (Feature2D)model.nearest(p_scene.getCoords());
    			error += p_scene.getDistance(closest);
    			ret.add(new Pair(closest, (Feature2D)scene[cont]));
    		} catch(Exception e) {
    			throw new JIPException(e.getMessage());
    		}
    	}
    	error /= scene.length;
    	return ret;
    }

	private Feature2D[] applyTrans(Object[] source, MyTransform3D tr3d) {
		Feature2D[] ret = new Feature2D[source.length];

		for(int cont=0;cont<source.length;cont++) {
			ret[cont] = new Feature2D((Feature2D)source[cont]);
			ret[cont].applyTransform(tr3d);
		}
		return ret;
	}

	private class Pair {
		public Feature2D p_model;
		public Feature2D p_scene;

		public Pair(Feature2D m, Feature2D s) {
			p_model = m;
			p_scene = s;
		}
	}

}
