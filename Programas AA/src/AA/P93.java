package AA;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.HashMap;

public class P93
{
	/**
	 * Vector con los pesos de los objetos.
	 */
	private int[] v;

	/**
	 * Cantidad de objetos que hay en el vector.
	 */
	private int N;

	/**
	 * Valor del peso que ya está en la balanza.
	 */
	private int p;

	/**
	 * Tablas que almacenan las soluciones parciales para las posibles combinaciones de <suma,k>
	 * en la entrada del método int best(int suma, int k).
	 */
	private HashMap<Integer, HashMap<Integer, Integer>> tabla;
	private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> tabla2;

	/**
	 * Resuelve el problema de la mochila.
	 * @param data data[0]: pesos de los objetos separados por un espacio, data[1]: valor de los objetos, data[2]: capacidad de la mochila
	 * @return el máximo valor que podrían alcanzar los objetos de la mochila
	 */
	public ArrayList<Integer> bestSolution(String data)
	{
		// Extrae y divide las cadenas de entrada en múltiples cadenas.
		String[] aux = data.split("\\p{Space}+");

		// Valores iniciales de entrada de la función.
		N = aux.length-1;
		p = new Integer(aux[0]);
		
		// Los elementos del montículo se ordenarán según la diferencia absoluta con el peso p original.
		Comparator<Integer> comparador = new Comparator<Integer>()
		{
			/*public int compare(Integer x, Integer y)
			{
				if (x == y)
				{
					return 0;
				}
				else
				{
					if (x < y)
					{
						return 1;
					}
					else
					{
						return -1;
					}
				}
			}*/

			public int compare(Integer x, Integer y)
			{
				if (x == y)
					return 0;
				else
					if (Math.abs(p-x) < Math.abs(p-y))
						return 1;
					else
						return -1;
			}
		};

		// Convierte de String a Integer y lo introduce en un montículo mínimo.
		PriorityQueue<Integer> cola = new PriorityQueue<Integer>(N, comparador);
		for (int i = 0; i < N; i++)
		{
			cola.add(new Integer(aux[i+1]));
		}

		// Extrae los elementos del montículo y los inserta en el vector.
		v = new int[N];
		for (int i = N-1; i >= 0; i--)
		{
			v[i] = cola.poll();
		}

		//System.out.print (p);
		//for (int i = 0; i < N; i++)
		//	System.out.print(" " + v[i]);
		//System.out.println();

		// Tablas que almacenan las posibles combinaciones de la entrada del problema.
		tabla = new HashMap<Integer, HashMap<Integer, Integer>>();
		tabla2 = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();

		ArrayList<Integer> resultado = new ArrayList<Integer>();
		best(0, 0, resultado);
		
		return resultado;
	}

	/**
	 * Resuelve el problema recursivamente utilizando programación dinámica.
	 * @param suma Resultado de sumar o restar las pesas utilizadas.
	 * @param k Índice de la casilla del vector que tiene que examinarse a continuación.
	 * @return Devuelve el mínimo número de pesas utilizadas.
	 */
	private int best(int suma, int k, ArrayList<Integer> resultado)
	{
		if (suma == p)
		{
			return 0;
		}
		else
		{
			// Comprobamos que no hayamos alcanzado el límite de elementos.
			if (k < N)
			{
				// Extraemos la tabla que se corresopnda con la primera componente.
				HashMap<Integer, Integer> tablaAux = null;
				HashMap<Integer, ArrayList<Integer>> tabla2Aux = null;
				tablaAux = tabla.get(k + 1);
				tabla2Aux = tabla2.get(k + 1);
				if (tablaAux == null)
				{
					tablaAux = new HashMap<Integer, Integer>();
					tabla.put(k + 1, tablaAux);
					tabla2Aux = new HashMap<Integer, ArrayList<Integer>>();
					tabla2.put(k + 1, tabla2Aux);
				}

				// Extraemos el valor de la tabla para el resultado "sumando". Si no está, se calcula.
				Integer sumar = tablaAux.get(suma + v[k]);
				ArrayList<Integer> resultadoSumar = tabla2Aux.get(suma + v[k]);
				if (sumar == null)
				{
					resultadoSumar = new ArrayList<Integer>();
					sumar = best(suma + v[k], k + 1, resultadoSumar);
					tablaAux.put(suma + v[k], sumar);
					tabla2Aux.put(suma + v[k], resultadoSumar);
				}
				if (sumar != Integer.MAX_VALUE)
					sumar++;

				// Extraemos el valor de la tabla para el resultado "restando". Si no está, se calcula.
				Integer restar = tablaAux.get(suma - v[k]);
				ArrayList<Integer> resultadoRestar = tabla2Aux.get(suma - v[k]);
				if (restar == null)
				{
					resultadoRestar = new ArrayList<Integer>();
					restar = best(suma - v[k], k + 1, resultadoRestar);
					tablaAux.put(suma - v[k], restar);
					tabla2Aux.put(suma - v[k], resultadoRestar);
				}
				if (restar != Integer.MAX_VALUE)
					restar++;

				// Extraemos el valor de la tabla para el resultado "no hacer nada". Si no está, se calcula.
				Integer nada = tablaAux.get(suma);
				ArrayList<Integer> resultadoNada = tabla2Aux.get(suma);
				if (nada == null)
				{
					resultadoNada = new ArrayList<Integer>();
					nada = best(suma, k + 1, resultadoNada);
					tablaAux.put(suma, nada);
					tabla2Aux.put(suma, resultadoNada);
				}

				if (sumar < restar && sumar < nada)
				{
					resultado.add(v[k]);
					resultado.addAll(resultadoSumar);
				}
				else
				{
					if (restar < nada && restar < sumar)
					{
						resultado.add(-v[k]);
						resultado.addAll(resultadoRestar);
					}
					else
					{
						resultado.addAll(resultadoNada);
					}
				}
				
				return Math.min(sumar, Math.min(restar, nada));
			}
			else
			{
				return Integer.MAX_VALUE;
			}
		}
	}
}
