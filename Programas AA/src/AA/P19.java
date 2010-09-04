package AA;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Comparator;



public class P19
{
	/**
	 * Vector con los pesos de los objetos.
	 */
	private int[] v;
	private int N;
	private int p;
	private HashMap<Integer, HashMap<Integer, Integer>> tabla;


	/**
	 * Resuelve el problema de la mochila.
	 * @param data data[0]: pesos de los objetos separados por un espacio, data[1]: valor de los objetos, data[2]: capacidad de la mochila
	 * @return el máximo valor que podrían alcanzar los objetos de la mochila
	 */
	public int best(String data)
	{
		// Extrae y divide las cadenas de entrada en múltiples cadenas.
		String[] aux = data.split("\\p{Space}+");

		// Valores iniciales de entrada de la función.
		N = aux.length-1;
		p = new Integer(aux[0]);

		// Los elementos del montículo se ordenarán según la diferencia absoluta con el peso p original.
		Comparator<Integer> comparador = new Comparator<Integer>()
		{
			public int compare(Integer x, Integer y)
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

		tabla = new HashMap<Integer, HashMap<Integer, Integer>>();

		int resultado = best(0, 0);

		/*System.out.println("Tamaño tabla componente k: " + tabla.size());

		for (int i = 1; i <= N; i++)
		{
			System.out.print(i + ": ");
			System.out.println(tabla.get(i).size());
		}
		System.out.println();*/

		return resultado;
	}

	public int best(int suma, int k)
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
				tablaAux = tabla.get(k + 1);
				if (tablaAux == null)
				{
					tablaAux = new HashMap<Integer, Integer>();
					tabla.put(k + 1, tablaAux);
				}

				// Extraemos el valor de la tabla para el resultado "sumando". Si no está, se calcula.
				Integer sumar = tablaAux.get(suma + v[k]);
				if (sumar == null)
				{
					sumar = best(suma + v[k], k + 1);
					tablaAux.put(suma + v[k], sumar);
				}
				if (sumar != Integer.MAX_VALUE)
					sumar++;

				// Extraemos el valor de la tabla para el resultado "restando". Si no está, se calcula.
				Integer restar = tablaAux.get(suma - v[k]);
				if (restar == null)
				{
					restar = best(suma - v[k], k + 1);
					tablaAux.put(suma - v[k], restar);
				}
				if (restar != Integer.MAX_VALUE)
					restar++;

				// Extraemos el valor de la tabla para el resultado "no hacer nada". Si no está, se calcula.
				Integer nada = tablaAux.get(suma);
				if (nada == null)
				{
					nada = best(suma, k + 1);
					tablaAux.put(suma, nada);
				}
				
				return Math.min(sumar, Math.min(restar, nada));
			}
			else
			{
				return Integer.MAX_VALUE;
			}
		}
	}

	static class P94
	{
		/**
		 * Vector con los pesos de los objetos.
		 */
		private static int[] v;
		private static int N;
		private static int p;
		private static int nMin;
		private static HashMap<Nodo, Integer> tabla;
		public static long tiempoActual = 0;;
		public static long tiempoPD = 0;;
		public static long tiempoRecursivo = 0;
		public static long tiempoIterativo = 0;
		public static long tiempoVoraz = 0;;


		/**
		 * Resuelve el problema de la mochila.
		 * @param data data[0]: pesos de los objetos separados por un espacio, data[1]: valor de los objetos, data[2]: capacidad de la mochila
		 * @return el máximo valor que podrían alcanzar los objetos de la mochila
		 */
		public static int best(String data)
		{
			// Extrae y divide las cadenas de entrada en múltiples cadenas.
			String[] aux;
			aux = data.split("\\p{Space}+");

			// Valores iniciales de entrada de la función.
			N = aux.length-1;
			p = new Integer(aux[0]);
			tabla = new HashMap<Nodo, Integer>(N);

			// Los elementos del montículo se ordenarán según la diferencia absoluta con el peso p original.
			Comparator<Integer> comparador1 = new Comparator<Integer>()
			{
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
			PriorityQueue<Integer> cola = new PriorityQueue<Integer>(N, comparador1);
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

			//nMin = voraz();
			//return best2(0, 0, 0);

			// Mostramos el vector original y tal como se va a procesar.
			/*System.out.println(data);
			System.out.print(p);
			for (int i = 0; i < N; i++)
				System.out.print(" " + v[i]);
			System.out.println();*/

			// Finalmente ejecutamos los algoritmos.
			tiempoActual = System.currentTimeMillis();
			int voraz = voraz();
			tiempoVoraz += System.currentTimeMillis() - tiempoActual;
			tiempoActual = System.currentTimeMillis();
			int iterativo = best(new Nodo(0, voraz, 0));
			tiempoIterativo += System.currentTimeMillis() - tiempoActual;
			nMin = voraz;
			tiempoActual = System.currentTimeMillis();
			int recursivoPD = best(0, 0, 0);
			tiempoPD += System.currentTimeMillis() - tiempoActual;
			nMin = voraz;
			tiempoActual = System.currentTimeMillis();
			int recursivo = best2(0, 0, 0);
			tiempoRecursivo += System.currentTimeMillis() - tiempoActual;

			System.out.println("Voraz    : " + voraz);
			System.out.println("Iterativo: " + iterativo);
			System.out.println("PD       : " + recursivoPD);
			System.out.println("Recursivo: " + recursivo);

			if (iterativo != recursivo || iterativo != recursivoPD)
			{
				int hola = 0;
				hola = 6 / hola;
			}

			return recursivoPD;
		}

		private static int voraz()
		{
			// Declaración de variables.
			int n = 0;
			int suma = 0;

			// Recorremos los elementos.
			for (int i = 0; i < N; i++)
			{
				// Calculamos el resultado hipotético de introducir la pesa (sumando o restando) y no introducirla.
				int difSumar = Math.abs(p - suma - v[i]);
				int difRestar = Math.abs(p - suma + v[i]);
				int difNada = Math.abs(p - suma);

				// Si sumándola obtenemos una mejor aproximación, incrementamos el valor de pesas elegidas.
				if (difSumar < difRestar && difSumar < difNada)
				{
					suma += v[i];
					n++;
				}
				else
				{
					// Si restándola obtenemos una mejor aproximación, incrementamos el valor de pesas elegidas.
					if (difRestar < difSumar)
					{
						suma -= v[i];
						n++;
					}
				}

				// Si la suma coincide con el peso inicial, devolvemos la cantidad de pesas seleccionadas.
				if (suma == p)
				{
					return n;
				}
			}

			// Si no encontramos una solución, devolvemos la máxima cantidad posible.
			return Integer.MAX_VALUE;
		}

		/**
		 * Resuelve el problema de los pesos y la balanza de forma recursiva.
		 * @param suma Suma actual.
		 * @param n Cantidad de pesos usados actualmente.
		 * @param k Cantidad de pesos interrogados.
		 * @return Devuelve la cantidad mínima de pesos que son necesarios para equilibrar la balanza.
		 */
		private static int best2(int suma, int n, int k)
		{
			if (n<nMin)
			{
				if (suma == p)
				{
					nMin = n;
					return n;
				}
				else
				{
					if (k < N)
					{
						int nada = best2(suma, n, k+1);
						int sumar = best2(suma+v[k], n+1, k+1);
						int restar = best2(suma-v[k], n+1, k+1);
						return Math.min(Math.min(nada, sumar), restar);
					}
					else
						return Integer.MAX_VALUE;
				}
			}
			else
			{
				return nMin;
			}
		}

		/**
		 * Resuelve el problema de los pesos y la balanza de forma recursiva.
		 * @param suma Suma actual.
		 * @param n Cantidad de pesos usados actualmente.
		 * @param k Cantidad de pesos interrogados.
		 * @return Devuelve la cantidad mínima de pesos que son necesarios para equilibrar la balanza.
		 */
		private static int best(int suma, int n, int k)
		{
			if (n<nMin)
			{
				if (suma == p)
				{
					nMin = n;
					return n;
				}
				else
				{
					if (k < N)
					{
						// Calculamos el resultado de no hacer nada.
						Nodo nodo = new Nodo(suma, n, k+1);
						Integer nada = tabla.get(nodo);
						if (nada == null)
						{
							nada = best(suma, n, k+1);
							tabla.put(nodo, nada);
						}

						// Sólo calculamos "sumar" y "restar" si es posible que mejore a "nada".
						if (nada > n)
						{
							// Calculamos el resultado de sumar la pesa.
							nodo = new Nodo(suma+v[k], n+1, k+1);
							Integer sumar = tabla.get(nodo);
							if (sumar == null)
							{
								sumar = best(suma+v[k], n+1, k+1);
								tabla.put(nodo, sumar);
							}

							// Sólo calculamos "restar" si es posible que mejore a "sumar".
							if (sumar > n+1)
							{
								// Calculamos el resultado de restar la pesa.
								nodo = new Nodo(suma-v[k], n+1, k+1);
								Integer restar = tabla.get(nodo);
								if (restar == null)
								{
									restar = best(suma-v[k], n+1, k+1);
									tabla.put(nodo, restar);
								}

								return Math.min(Math.min(nada, sumar), restar);
							}

							return Math.min(nada, sumar);
						}

						return nada;
					}
					else
					{
						return Integer.MAX_VALUE;
					}
				}
			}
			else
			{
				return nMin;
			}
		}

		/**
		 * Resuelve el problema de los pesos y la balanza de forma iterativa.
		 * @param inicial Nodo de partida del algoritmo. Puede resolverse con un algoritmo voraz.
		 * @return Devuelve la cantidad mínima de pesos que son necesarios para equilibrar la balanza.
		 */
		private static int best(Nodo inicial)
		{
			PriorityQueue<Nodo> colaPrioridad = new PriorityQueue<Nodo>(P94.N);
			Nodo mejorSolucion = inicial;
			colaPrioridad.add(new Nodo(0, 0, 0));

			while (!colaPrioridad.isEmpty() && colaPrioridad.peek().n < mejorSolucion.n)
			{
				Nodo actual = colaPrioridad.poll();
				if (actual.suma == p)
				{
					mejorSolucion = actual;
				}
				else
				{
					if (actual.k < N)
					{
						colaPrioridad.add(new Nodo(actual.suma, actual.n, actual.k+1));
						if (actual.n+1 < mejorSolucion.n)
						{
							colaPrioridad.add(new Nodo(actual.suma + P94.v[actual.k], actual.n+1, actual.k+1));
							colaPrioridad.add(new Nodo(actual.suma - P94.v[actual.k], actual.n+1, actual.k+1));
						}
					}
				}
			}

			return mejorSolucion.n;
		}

		private static class Nodo implements Comparable<Nodo>
		{
			public final int suma;
			public final int n;
			public final int k;
			private int hashCode;

			public Nodo(int suma, int n, int k)
			{
				this.suma = suma;
				this.n = n;
				this.k = k;

				hashCode = (suma + n + k)%P94.N;
			}

			@Override
			public boolean equals(Object object)
			{
				if (object instanceof Nodo)
				{
					Nodo nodo = (Nodo) object;
					if (suma == nodo.suma && n == nodo.n && k == nodo.k)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				return false;
			}

			@Override
			public int hashCode()
			{
				return hashCode;
			}

			@Override
			public int compareTo(Nodo nodo)
			{
				if (n < nodo.n)
				{
					return -1;
				}
				else
				{
					if (n > nodo.n)
					{
						return 1;
					}
					else
					{
						return 0;
					}
				}
			}
		}
	}

}