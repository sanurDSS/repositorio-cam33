package javavis.jip3d.functions;

import java.util.ArrayList;

import javavis.base.Function3DGroup;
import javavis.base.ParamType;
import javavis.base.JIPException;
import javavis.jip3d.geom.MyTransform;
import javavis.jip3d.geom.MyTransform3D;
import javavis.jip3d.geom.Point3D;
import javavis.jip3d.geom.MyKDTree;
import javavis.jip3d.gui.Function3D;
import javavis.jip3d.gui.FunctionParam;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;
import javavis.jip3d.gui.dataobjects.PointSet3D;

import javax.vecmath.Color3f;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.SingularValueDecomposition;

/**
 * Class FICP. This is a basic implementation of Besl ICP algorithm. A Singular Value Decomposition 
 * based method is used for computing the rotation that best aligns the two set of points
 * @author dviejo
 *
 */
public class FICP extends Function3D {

	double error;

	public FICP() {
		super();
		this.allowed_input = ScreenOptions.tPOINTSET3D;
		this.group = Function3DGroup.Egomotion;


		FunctionParam p1 = new FunctionParam("Next Object", ParamType.SCRDATA);
		FunctionParam p2 = new FunctionParam("Iterations", ParamType.INT);
		p2.setValue(40);
		FunctionParam p3 = new FunctionParam("KDTree", ParamType.BOOL);
		p3.setValue(true);

		this.addParam(p1);
		this.addParam(p2);
//		this.addParam(p3);
	}

	@Override
	public void proccessData(ScreenData scr_data) throws JIPException {
		result_list = new ArrayList<ScreenData>();
		int iterations = this.paramValueInt("Iterations");
		ScreenData scenedata = this.paramValueScrData("Next Object");
		double error_prev;
//		boolean tree = this.paramValueBool("KDTree");

		MyTransform3D auxT;
    	Point3D []sceneTr, sceneSubs;
    	MyKDTree modelSubs;
    	Point3D []scene, model;
    	Point3D modelM, sceneM;
    	Point3D []closest;
    	int cont=0;
    	Object []elements;

    	double prog_inc = 100.0/iterations;

    	elements = scenedata.elements();
    	scene = new Point3D[elements.length];
    	for(cont=0;cont<elements.length;cont++)
    		scene[cont] = (Point3D)elements[cont];

    	elements = scr_data.elements(); //model
    	model = new Point3D[elements.length];
    	for(cont=0;cont<elements.length;cont++)
    		model[cont] = (Point3D)elements[cont];

    	// Calculo la media del modelo y la escena y se la resta al conjunto de puntos para no tener que recalcularla
    	modelM=calcMean(model);
    	modelSubs=subsMeanModel(model,modelM);

    	sceneM=calcMean(scene);
    	sceneSubs=subsMean(scene,sceneM);
		// Con esto hemos conseguido encontrar la traslaci�n entre los dos conjuntos de datos
		// Queda encontrar la transformaci�n. Partimos de un registro inicial
		auxT = new MyTransform3D();
		// El algoritmo va calculando de manera iterativa:
			// Primero los puntos m�s cercanos
		cont = 0;
		error = Double.MAX_VALUE;
		do {
			cont++;
			error_prev = error;
			// Aplicamos la transformaci�n al conjunto de datos
			sceneTr = applyTrans (scene, auxT);

			// Encontramos los puntos m�s cercanos
			closest = findClosest(sceneTr, modelSubs, model);

			// Calculamos la transformaci�n a partir de los puntos m�s cercanos
			auxT = calcTrans (closest, sceneSubs, sceneM, modelM);

			progress += prog_inc;
		} while (cont<iterations && Math.abs(error-error_prev)>0.0001); // Terminamos cuando llegamos a 30 iteraciones o el error ha descendido por debajo de un umbral
		System.out.println("Iterations: "+cont+"\n"+auxT.toString());
		sceneTr = applyTrans (scene, auxT);

		PointSet3D resulttras = new PointSet3D(new ScreenOptions());
		resulttras.name = "ICP";
		resulttras.scr_opt.width = 1;
		resulttras.scr_opt.global_color = true;
		resulttras.scr_opt.color = new Color3f(0,1,0);
		for(Point3D point: sceneTr)
			resulttras.insert(point);
		result_list.add(resulttras);

	}

	private MyTransform3D calcTrans (Point3D []modelR, Point3D []sceneR,
    		Point3D sceneM, Point3D modelM) {
    	MyTransform3D auxT;
		double matriz[][]=new double [3][3];
		Point3D modelP, sceneP;

		matriz[0][0]=0.00d; matriz[0][1]=0.00d; matriz[0][2]=0.00d;
		matriz[1][0]=0.00d; matriz[1][1]=0.00d; matriz[1][2]=0.00d;
		matriz[2][0]=0.00d; matriz[2][1]=0.00d; matriz[2][2]=0.00d;
		for (int i=0; i<sceneR.length; i++) {
			modelP=modelR[i];
			sceneP=sceneR[i];
			matriz[0][0]+=sceneP.getX()*modelP.getX();
			matriz[0][1]+=sceneP.getX()*modelP.getY();
			matriz[0][2]+=sceneP.getX()*modelP.getZ();
			matriz[1][0]+=sceneP.getY()*modelP.getX();
			matriz[1][1]+=sceneP.getY()*modelP.getY();
			matriz[1][2]+=sceneP.getY()*modelP.getZ();
			matriz[2][0]+=sceneP.getZ()*modelP.getX();
			matriz[2][1]+=sceneP.getZ()*modelP.getY();
			matriz[2][2]+=sceneP.getZ()*modelP.getZ();
		}

		DenseDoubleMatrix2D mat= new DenseDoubleMatrix2D(matriz);
		SingularValueDecomposition SVD = new SingularValueDecomposition(mat);

		// Calculo la matriz U y V
		DenseDoubleMatrix2D UT, U, V, X;
		X=new DenseDoubleMatrix2D(3,3);

		U=(DenseDoubleMatrix2D)SVD.getU();
		V=(DenseDoubleMatrix2D)SVD.getV();
		UT=(DenseDoubleMatrix2D)U.viewDice(); // Calcula la transpuesta
		V.zMult(UT,X);

		Algebra algebra = new Algebra();
		if (algebra.det(X)< -0.9) {
			V.setQuick(0,2,-V.getQuick(0,2));
			V.setQuick(1,2,-V.getQuick(1,2));
			V.setQuick(2,2,-V.getQuick(2,2));
			V.zMult(UT,X);
		}

		auxT = new MyTransform3D(X);
		Point3D sceneRaux = new Point3D(sceneM);
		sceneRaux.applyTransform(auxT);
		double tx=modelM.getX()-sceneRaux.getX();
		double ty=modelM.getY()-sceneRaux.getY();
		double tz=modelM.getZ()-sceneRaux.getZ();
		auxT.setTranslation(tx, ty, tz);
    	return auxT;
    }

    // Devuelve un vector con el n�mero del punto en el otro conjunto que es el m�s cercano
    // Aqu� es donde tienes que meter el �rbol KD
    private Point3D[] findClosest (Point3D []scene, MyKDTree modeltree, Point3D []model) {
    	Point3D []closests = new Point3D[scene.length];
		double mindist;
		Point3D closest;
		ArrayList<Point3D> list = new ArrayList<Point3D>();
		for(int cont=0;cont<model.length;cont++)
			list.add(model[cont]);

		error=0.0;
		try{
			for (int p=0; p<scene.length; p++) {
				closest = (Point3D)modeltree.nearest(scene[p].getCoords());
				mindist = scene[p].getDistance(closest);
				closests[p] = closest;
				error+=mindist;
			}
    	} catch(Exception e)
    	{
    		System.out.println("FICP::findClosestTree Error. Wrong key size");
    	}
    	error /= scene.length;
    	return closests;
    }

	private Point3D[] applyTrans(Point3D[] source, MyTransform tr2d)
	{
		Point3D []ret;
		int cont, tam;

		tam = source.length;
		ret = new Point3D[tam];

		for(cont=0;cont<tam;cont++)
		{
			ret[cont] = new Point3D(source[cont]);
			ret[cont].applyTransform(tr2d);
		}

		return ret;
	}


    private Point3D[] subsMean (Point3D []set, Point3D mean) {
    	Point3D []setR=new Point3D[set.length];
    	for (int i=0; i<set.length; i++) {
    		setR[i] = set[i].subPoint(mean);
    	}
    	return setR;
    }

    /**
     * Este metodo lo he creado por la necesidad de relacionar el conjunto de puntos (con la media restada)
     * con el arbol KD de los puntos originales. De esta forma, el arbolKD que se devuelve tiene como claves
     * las posiciones originales de los puntos, pero lo que se guarda en los nodos del arbol es el punto al
     * que se le ha restado la media del conjunto original.
     * @param set
     * @param mean
     * @return
     */
    private MyKDTree subsMeanModel (Point3D []set, Point3D mean) {
    	MyKDTree ret = new MyKDTree(3);
    	Point3D TPoint;
    	for (int i=0; i<set.length; i++) {
    		TPoint = set[i].subPoint(mean);
    		ret.insert(set[i].getCoords(), TPoint);
    	}
    	return ret;
    }


    private Point3D calcMean (Point3D []set) {
    	double x, y, z;

    	x=0.0;
		y=0.0;
		z=0.0;
    	for (int i=0; i<set.length; i++) {
    		x += set[i].getX();
			y += set[i].getY();
			z += set[i].getZ();
    	}
    	x /= set.length;
		y /= set.length;
		z /= set.length;

    	Point3D paux = new Point3D(x, y, z);
    	return paux;
    }

}
