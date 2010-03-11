package javavis.jip3d.functions;


import java.util.ArrayList;

import javavis.base.Function3DGroup;
import javavis.base.ParamType;
import javavis.base.JIPException;
import javavis.jip3d.geom.Normal3D;
import javavis.jip3d.geom.Point3D;
import javavis.jip3d.geom.Vector3D;
import javavis.jip3d.gui.Function3D;
import javavis.jip3d.gui.FunctionParam;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;
import javavis.jip3d.gui.dataobjects.NormalSet3D;

import javax.vecmath.Color3f;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.SingularValueDecomposition;

public class FNormalSVD extends Function3D {

	private double minimum = 9;
	private double pi2 = Math.PI/2.0;
	double relacion = 2/Math.sqrt(3);

	public FNormalSVD()
	{
		super();
		this.allowed_input = ScreenOptions.tPOINTSET3D;
		this.group = Function3DGroup.Normals;

		FunctionParam p1 = new FunctionParam("Window size", ParamType.FLOAT);
		p1.setValue(0.025);
		FunctionParam p2 = new FunctionParam("Minimum size", ParamType.FLOAT);
		p2.setValue(0.0);
		FunctionParam p3 = new FunctionParam("PlaneThick", ParamType.FLOAT);
		p3.setValue(0.005);

		this.addParam(p1);
		this.addParam(p2);
		this.addParam(p3);
	}

	//@Override
	public void proccessData(ScreenData scr_data) throws JIPException {
		result_list = new ArrayList<ScreenData>();
		double win_size = this.paramValueReal("Window size");
		double min_size = this.paramValueReal("Minimum size");
		double thickness = this.paramValueReal("PlaneThick");

		Object []elements;
		Object []vecinos;
		Point3D elemento;
		int cont, tam;
		double rad;
		double []normal;
		Normal3D normalVector;
		Vector3D vec_aux;
		double angle;
		Point3D paux;

		//Returning screenData
		NormalSet3D normals = new NormalSet3D(new ScreenOptions());
		normals.name = "NormalSet"+scr_data.name.substring(scr_data.name.length()-3);
		NormalSet3D saliencies = new NormalSet3D(new ScreenOptions());
		saliencies.scr_opt.color = new Color3f(0, 0, 1);
		saliencies.name = "SalienceSet"+scr_data.name.substring(scr_data.name.length()-3);

		elements = scr_data.elements();
		tam = elements.length;

		double prog_inc = 100.0/tam;

		for(cont=0;cont<tam;cont++)
		{
			progress += prog_inc;
			elemento = (Point3D)elements[cont];
			rad = elemento.getOriginDistance() * win_size;
			if(rad<min_size) rad = min_size;

			try
			{
				vecinos = scr_data.range(elemento, rad);
				if(vecinos.length>minimum)
				{
					paux = calcCentroid(vecinos);
					normal = aplicarSVD(paux, vecinos, thickness);
					if(normal!=null && normal[3]!=2)
					{
						vec_aux = new Vector3D(normal[0], normal[1], normal[2]);
						normalVector = new Normal3D(elemento, vec_aux, thickness, rad);
						vec_aux = new Vector3D(elemento);
						if(normal[3]==1)
						{
							//la normal ha de estar 'mirando' hacia la cámara
							angle = vec_aux.getAngle(normalVector.vector);

							if(angle<pi2)
							{
								normal[0] = -normal[0];
								normal[1] = -normal[1];
								normal[2] = -normal[2];
								vec_aux = new Vector3D(normal[0], normal[1], normal[2]);
								normalVector = new Normal3D(elemento, vec_aux, thickness, rad);
							}
							normals.insert(normalVector);
						}
						else if(normal[3]==0)
						{
							saliencies.insert(normalVector);
						}
					}
				}
			}catch(Exception e)
			{
				System.err.println(e.getMessage());
				result_list = null;
				return;
			}
		}
		result_list.add(normals);
		result_list.add(saliencies);
	}

	static public Object[] restarMedia(Object []conjunto, Point3D media)
	{
		int size = conjunto.length;
		Object []ret = new Object[size];
		int cont;
		Point3D elemento;


		for(cont=0;cont<size;cont++)
		{
			elemento = ((Point3D)conjunto[cont]).subPoint(media);
			ret[cont] = elemento;
		}

		return ret;
	}

	static public Point3D calcCentroid(Object []conjunto)
	{
		Point3D ret;
		double x,y,z;
		int cont;
		x = y = z = 0;
		for(cont=0;cont<conjunto.length;cont++)
		{
			x += ((Point3D)conjunto[cont]).getX();
			y += ((Point3D)conjunto[cont]).getY();
			z += ((Point3D)conjunto[cont]).getZ();
		}
		x /= conjunto.length;
		y /= conjunto.length;
		z /= conjunto.length;
		ret = new Point3D(x, y, z);
		return ret;
	}

	/**
	 *
	 * @param source
	 * @param datos
	 * @param umbral
	 * @return vector con la normal y el valor sigmaT
	 */
	public double[] aplicarSVD(Point3D source, Object []pset, double umbral)
	{
		double []ret = new double[5];
		double [][]matriz = new double [3][3];
		double []singular_values;
		DenseDoubleMatrix2D matrix;
		DoubleMatrix2D U;
		SingularValueDecomposition SVD;
		int i, menor;
		double sigmaN, sigmaT;
		double thickness;
		int tipo_plano;
		double []datos;
		Object []vecinos = restarMedia(pset, source);

		matriz[0][0] = matriz[0][1] = matriz[0][2] =
			matriz[1][0] = matriz[1][1] = matriz[1][2] =
			matriz[2][0] = matriz[2][1] = matriz[2][2] = 0;
		for(i=0;i<vecinos.length;i++)
		{
			datos = ((Point3D)vecinos[i]).getCoords();
			matriz[0][0] += datos[0] * datos[0];
			matriz[0][1] += datos[0] * datos[1];
			matriz[0][2] += datos[0] * datos[2];
			matriz[1][1] += datos[1] * datos[1];
			matriz[1][2] += datos[1] * datos[2];
			matriz[2][2] += datos[2] * datos[2];
		}
		matriz[1][0] = matriz[0][1];
		matriz[2][0] = matriz[0][2];
		matriz[2][1] = matriz[1][2];

		matrix = new DenseDoubleMatrix2D(matriz);
		SVD = new SingularValueDecomposition(matrix);

		singular_values = SVD.getSingularValues();

		menor = 2;

		sigmaN = singular_values[2];
		sigmaT = Math.sqrt(singular_values[0]*singular_values[1]);

		thickness = Math.atan(relacion * sigmaN/sigmaT);

		//are neighbor points belonging to a line?
		double lineness = (singular_values[0] - singular_values[1]) / (singular_values[0] + singular_values[1] + singular_values[2]);
		if(thickness<umbral && lineness<0.5)
		{
			tipo_plano = 1; //is on a planar surface
		}
		else //review this part, has no sense...
		{
			if(lineness<0.03)
				tipo_plano = 3;
			else if (lineness<0.045) //Para exteriores
				tipo_plano = 0;
			else tipo_plano = 2;
		}

		if(tipo_plano!=2 )
		{
			ret[3] = tipo_plano;
			U = SVD.getU();
			if(tipo_plano==1)
			{
				ret[0] = U.get(0, menor);
				ret[1] = U.get(1, menor);
				ret[2] = U.get(2, menor);
				ret[4] = thickness;
			}
			else
			{
				ret[0] = U.get(0, 0);
				ret[1] = U.get(1, 0);
				ret[2] = U.get(2, 0);
				ret[4] = lineness;
			}
		}
		else
			ret = null;
		return ret;
	}

}
