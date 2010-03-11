package javavis.jip3d.geom;
/**
 * @author Diego Viejo Hernando
 *
 * Extiende la funcionalidad de la clase KDTree
 *
 * Fecha de creacion:
 * Ultima modificación: 19 Julio 2004
 */


import java.io.Serializable;
import java.util.ArrayList;

import edu.wlu.cs.levy.CG.*;

public class MyKDTree extends KDTree implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public int num_elementos;	//numero de elementos
	public int K;				//dimensionalidad del arbol
	public double []maxRango;  	//rango maximo que alcanzan los elementos del arbol
	public double []minRango;	//rango mínimo que alcanzan los elementos del arbol
	public double radio;

	public MyKDTree()
	{
		super(3);
		num_elementos = 0;
		radio = 0.0;
		K = 3;

	}

	/**
	 * Constructor de la clase
	 * @param int k: dimensionalidad del arbol
	 */
	public MyKDTree(int k)
	{
		super(k);
		K=k;
		num_elementos = 0;
		maxRango = new double[k];
		minRango = new double[k];
		radio = 0.001;
		for(int i=0;i<k;i++)
		{
			maxRango[i] = -Double.MAX_VALUE;
			minRango[i] = Double.MAX_VALUE;
		}
	}

	/**
	 * Funcion insert. Inserta un elemento en en el arbol
	 * @param double []key: clave por la que se ordenara el elemento insertado dentro del arbol
	 * @param Object value: elemento a insertar
	 */
	public void insert(double []key, Object value)
	{
		try {
			super.insert(key, value);
			num_elementos++;
			for(int i=0;i<K;i++)
			{
				if(key[i]>maxRango[i]) maxRango[i]=key[i];
				if(key[i]<minRango[i]) minRango[i]=key[i];
			}
		} catch(Exception e){}
	}

	/**
	 * Funcion elements. Devuelve todos los elementos del arbol
	 * @return Array de objetos con todos los elementos del arbol.
	 */
	public Object[] elements()
	{
		Object []ret = null;
		try {
			if(num_elementos>0)
				ret = super.range(minRango, maxRango);
		} catch(Exception e)
		{
			System.err.println("ArbolKD (elements): "+e);
		}
		return ret;
	}

	/**
	 * Function range. Returns all elements that are within rad distance from point key
	 * @param key Range center
	 * @param rad Range radius
	 * @return Array with the selected elements
	 * @throws Exception
	 */
	public Object[] range(Point3D key, double rad) throws Exception
	{
		double []min;
		double []max;
		Object []elements;
		ArrayList <Object>result = new ArrayList<Object>();
		int cont;
		Point3D point;
		Plane3D plane;

		min = key.getCoords();
		max = key.getCoords();
		min[0] -= rad;
		min[1] -= rad;
		min[2] -= rad;
		max[0] += rad;
		max[1] += rad;
		max[2] += rad;

		elements = this.range(min, max);
		for(cont=0;cont<elements.length;cont++)
		{
			if(elements[cont] instanceof Point3D)
			{
				point = (Point3D) elements[cont];
				if(point.getDistance(key)<rad) result.add(elements[cont]);
			}
			else if(elements[cont] instanceof Plane3D)
			{
				plane = (Plane3D) elements[cont];
				if(plane.origin.getDistance(key)<rad) result.add(elements[cont]);
			}
			else result.add(elements[cont]);
		}
		return result.toArray();
	}


	/**
	 * Funcion size. Devuelve el numero de elementos del arbol
	 * @return Numero de elementos en el arbol
	 */
	public int size()
	{
		return num_elementos;
	}


	/**
	 * Funcion search. Busca un elemento dentro del arbol
	 * @param double []key: clave de busqueda del elemento
	 * @return El objeto cuya clave coincide con el argumento. O null si no hay ningun elemento en el arbol que coincida co la clave de busqueda
	 */
	public Object search(double []key)
	{
		Object ret = null;
		try {
			ret = super.search(key);
		}catch(Exception e)
		{
			System.err.println(e);
		}
		return ret;

	}

	/**
	 * Funcion neighbor. Devuelve el vecino mas cercano
	 * @param double[] key: clave de busqueda
	 * @param MyKDTree visitados: elementos del arbol
	 * @return el elemento del arbol mas cercano a la clave de busqueda y que no se encuentre en el arbol visitados
	 */
	public Object neighbor(double []key, MyKDTree visitados)
	{
		double incremento = 0.001;
		boolean salir = false;
		Object []elementos;
		Point3D elemento;
		Object mejorVecino = null;
		double mejorDistancia;
		double difX, difY, difZ, distancia;
		double []rangoMax = new double[K];
		double []rangoMin = new double[K];
		rangoMax[0] = key[0]+radio;
		rangoMax[1] = key[1]+radio;
		rangoMax[2] = key[2]+radio;
		rangoMin[0] = key[0]-radio;
		rangoMin[1] = key[1]-radio;
		rangoMin[2] = key[2]-radio;

		try {
			if (visitados.size()==0)
			{
				return super.nearest(key);
			}
			else if(visitados.size()>=this.num_elementos)
				return null;
			else while(!salir&&radio<0.3)
			{
				mejorDistancia = radio;
				elementos = super.range(rangoMin, rangoMax);
				for(int i=0;i<elementos.length;i++)
				{
					elemento = (Point3D)elementos[i];
					if(visitados.search(elemento.getCoords())==null)
					{
						difX = Math.abs(key[0] - elemento.getX());
						if(difX<mejorDistancia)
						{
							difY = Math.abs(key[1]-elemento.getY());
							if(difY<mejorDistancia)
							{
								difZ = Math.abs(key[2]-elemento.getZ());
								if(difZ<mejorDistancia)
								{
									distancia = Math.sqrt(difX*difX + difY*difY + difZ*difZ);
									if(distancia<mejorDistancia)
									{

										salir=true;
										mejorDistancia = distancia;
										mejorVecino = elementos[i];
										radio = distancia+incremento;
									} //fin if #5
								} //fin if #4
							} //fin if #3
						} //fin if #2

					} //fin if #1
				} //fin for
				//Para la siguiente busqueda incrementamos el radio
				if(!salir)
				{
					radio+=incremento;
					rangoMax[0]+=incremento;
					rangoMax[1]+=incremento;
					rangoMax[2]+=incremento;
					rangoMin[0]-=incremento;
					rangoMin[1]-=incremento;
					rangoMin[2]-=incremento;
				}
			} //fin while
		} catch(Exception e)
		{
			System.err.println(e);
		}
		if(mejorVecino!=null)
		{
			elemento = (Point3D)mejorVecino;
			visitados.insert(elemento.getCoords(), elemento);
		}
		return mejorVecino;
	}

	/**
	 * Funcion resetRadio. Inicializa el radio de busqueda para la funcion neighbor
	 */
	public void resetRadio()
	{
		radio = 0.001;
	}

	/**
	 * Funcion setRadio. Fija un valor para el radio de busqueda de la funcion neighbor
	 * @param double valor: el nuevo radio de busqueda
	 */
	public void setRadio(double valor)
	{
		radio = valor;
	}

	/**
	 * Funcion getRadio. Obtiene el radio de busqueda que se esta utilizando actualmente
	 * @return Radio de busqueda actual
	 */
	public double getRadio()
	{
		return radio;
	}
}
