package AA;

import java.util.PriorityQueue;

public class P503
{
	/**
	 * Almacena los valores que el primer miembro de la pareja asigna a cada objeto.
	 */
	private int[] v;

	/**
	 * Almacena los valores que el segundo miembro de la pareja asigna a cada objeto.
	 */
	private int[] w;

	/**
	 * Cantidad de objetos que hay en total.
	 */
	private int N;

	/**
	 * Resuelve el problema del reparto amistoso de bienes.
	 * @param data Vectores de entrada con el valor que le da cada uno de la pareja a cada valor.
	 * @return Devuelve el mínimo número de objetos que no pueden repartirse llegando a un acuerdo entre ambas partes.
	 */
	public int best(String[] data)
	{
		// Extracción de los parámetros de entrada.
		String[] vAux = data[0].split("\\p{Space}+");
		String[] wAux = data[1].split("\\p{Space}+");
		N = vAux.length;
		v = new int[N];
		w = new int[N];
		for (int i = 0; i < N; i++)
		{
			v[i] = new Integer(vAux[i]);
			w[i] = new Integer(wAux[i]);
		}

		// Se ejecuta la llamada recursiva.
		int resultadoRP = best2(good());
		/*int resultadoRE = best(0, 0, 0);

		if (resultadoRP != resultadoRE)
		{
			System.out.println("HA FALLADO");
			System.exi9t(-1);
		}
		else
		{
			System.out.println("ok");
		}*/

		return resultadoRP;
	}

	/**
	 * Solución recursiva al problema del reparto amistoso de bienes.
	 * @param suma1 Suma parcial de los elementos del primer miembro de la pareja. Debe ser 0 para que la persona en cuestión crea que el reparto es justo.
	 * @param suma2 Suma parcial de los elementos del segundo miembro de la pareja. Debe ser 0 para que la persona en cuestión crea que el reparto es justo.
	 * @param k Número de elemento que tiene que examinarse a continuación.
	 * @return Devuelve el mínimo número de objetos que no pueden repartirse haciendo 2 lotes de objetos y que los dos miembros de la pareja estén conformes.
	 */
	private int best(int suma1, int suma2, int k)
	{
		int resultado = 0;

		if (k < N)
		{
			// Si quedan elementos por examinar, aplicamos recursivamente la función suponiendo que lo sumamos, que lo restamos y que lo descartamos.
			int sumando = best(suma1 + v[k], suma2 + w[k], k + 1);
			int restando = best(suma1 - v[k], suma2 - w[k], k + 1);
			int nada = 1 + best(suma1, suma2, k + 1);

			resultado = Math.min(sumando, Math.min(restando, nada));
		}
		else
		{
			// Si al llegar al final, la suma no ha sido 0 para ambas partes, ésta no es una combinación válida y la descartamos devolviendo el máximo valor posible.
			if (suma1 != 0 || suma2 != 0)
			{
				resultado = N;
			}
		}

		return resultado;
	}

	/**
	 * Implementa un algoritmo voraz que optiene una solución aproximada al problema del reparto amistoso de bienes.
	 * Presenta un coste temporal N² · log(N).
	 * @return Devuelve el nodo con la solución obtenida.
	 */
	private Nodo good()
	{
		return new Nodo(0, 0, N, 0);
	}

	/**
	 * Resuelve el problema del reparto amistodo de bienes aplicando un algoritmo de ramificación y poda.
	 * @param solucionInicial Solución desde la que parte el algoritmo para realizar las podas.
	 * @return Devuelve el mínimo número de objetos que no pueden repartirse haciendo 2 lotes de objetos y que los dos miembros de la pareja estén conformes.
	 */
	private int best(Nodo solucionInicial)
	{
		// La mejor solución inicial será
		Nodo mejorSolucion = solucionInicial;

		// Se crea la cola de prioridad y se inserta el nodo raíz.
		PriorityQueue<Nodo> nodosVivos = new PriorityQueue<Nodo>();
		nodosVivos.add(new Nodo(0,0,0,0));

		// Mientras queden nodos.
		while (!nodosVivos.isEmpty() && nodosVivos.peek().cotaOptimista() > mejorSolucion.n)
		{
			// Extrae el siguiente nodo de la lista.
			Nodo nodoActual = nodosVivos.poll();

			// Sólo se evalúa el nodo si es potencialmente mejor que la solución actual.
			if (nodoActual.esAceptable(mejorSolucion))
			{
				// Comprueba si es una solución completa o hay que expandir el nodo.
				if (nodoActual.esCompleto())
				{
					// Se comprueba si el nodo es factible como solución al problema.
					if (nodoActual.esFactible())
					{
						// Si es mejor que la solución óptima actual, se sustituye como la solución óptima.
						mejorSolucion = nodoActual;
					}
				}
				else
				{
					// Se comprueba que, aunque no es una solución completa, es una solución posible.
					if (nodoActual.esFactible())
					{
						// Si es una solución posible y además es mejor que la solución actual, incluso despreciando los objetos que existan, se asigna como la solución óptima hasta el momento.
						if (nodoActual.cotaPesimista() > mejorSolucion.cotaPesimista())
						{
							mejorSolucion = new Nodo(0, 0, N, nodoActual.n);
						}
					}

					// Genera los 3 nodos hijos posibles y los inserta en la lista si son potencialmente mejores que la solución actual.
					Nodo sumando = new Nodo(nodoActual.suma1 + v[nodoActual.k], nodoActual.suma2 + w[nodoActual.k], nodoActual.k + 1, nodoActual.n + 1);
					Nodo restando = new Nodo(nodoActual.suma1 - v[nodoActual.k], nodoActual.suma2 - w[nodoActual.k], nodoActual.k + 1, nodoActual.n + 1);
					Nodo nada = new Nodo(nodoActual.suma1, nodoActual.suma2, nodoActual.k + 1, nodoActual.n);

					if (sumando.esAceptable(mejorSolucion))
					{
						nodosVivos.add(sumando);
					}

					if (restando.esAceptable(mejorSolucion))
					{
						nodosVivos.add(restando);
					}

					if (nada.esAceptable(mejorSolucion))
					{
						nodosVivos.add(nada);
					}
				}
			}
		}

		return N - mejorSolucion.n;
	}

	/**
	 * Resuelve el problema del reparto amistodo de bienes aplicando un algoritmo de ramificación y poda.
	 * @param solucionInicial Solución desde la que parte el algoritmo para realizar las podas.
	 * @return Devuelve el mínimo número de objetos que no pueden repartirse haciendo 2 lotes de objetos y que los dos miembros de la pareja estén conformes.
	 */
	private int best2(Nodo solucionInicial)
	{
		// La mejor solución inicial será
		Nodo mejorSolucion = solucionInicial;

		// Se crea la cola de prioridad y se inserta el nodo raíz.
		PriorityQueue<Nodo> nodosVivos = new PriorityQueue<Nodo>();
		nodosVivos.add(new Nodo(0,0,0,0));

		// Mientras queden nodos.
		while (!nodosVivos.isEmpty() && nodosVivos.peek().cotaOptimista() > mejorSolucion.n)
		{
			// Extrae el siguiente nodo de la lista.
			Nodo nodoActual = nodosVivos.poll();

			// Genera los 3 nodos hijos posibles y los inserta en la lista si son potencialmente mejores que la solución actual.
			Nodo sumando = new Nodo(nodoActual.suma1 + v[nodoActual.k], nodoActual.suma2 + w[nodoActual.k], nodoActual.k + 1, nodoActual.n + 1);
			if (sumando.esAceptable(mejorSolucion))
			{
				if (sumando.esCompleto())
				{
					if (sumando.esFactible())
					{
						mejorSolucion = sumando;
					}
				}
				else
				{
					nodosVivos.add(sumando);
				}
			}

			Nodo restando = new Nodo(nodoActual.suma1 - v[nodoActual.k], nodoActual.suma2 - w[nodoActual.k], nodoActual.k + 1, nodoActual.n + 1);
			if (restando.esAceptable(mejorSolucion))
			{
				if (restando.esCompleto())
				{
					if (restando.esFactible())
					{
						mejorSolucion = restando;
					}
				}
				else
				{
					nodosVivos.add(restando);
				}
			}
			
			Nodo nada = new Nodo(nodoActual.suma1, nodoActual.suma2, nodoActual.k + 1, nodoActual.n);
			if (nada.esAceptable(mejorSolucion))
			{
				if (nada.esCompleto())
				{
					if (nada.esFactible())
					{
						mejorSolucion = nada;
					}
				}
				else
				{
					nodosVivos.add(nada);
				}
			}
		}

		return N - mejorSolucion.n;
	}

	/**
	 * Clase auxiliar que representa un nodo del árbol de soluciones.
	 */
	private class Nodo implements Comparable<Nodo>
	{
		/**
		 * Suma con el posible reparto del miembro del primer miembro de la pareja.
		 */
		public int suma1;

		/**
		 * Suma con el posible reparto del miembro del segundo miembro de la pareja.
		 */
		public int suma2;

		/**
		 * Nivel de construcción de la solución. Hace referencia al siguiente elemento del vector de objetos que hay que explorar.
		 */
		public int k;

		/**
		 * Cantidad de elementos que ya se han repartido entre los dos lotes.
		 */
		public int n;

		/**
		 * Constructor por defecto.
		 * @param suma1 Suma inicial con el posible reparto del primer miembro de la pareja.
		 * @param suma2 Suma inicial con el posible reparto del segudo miembro de la pareja.
		 * @param k Valor inicial del nivel de construcción de la solución.
		 * @param n Número de elementos iniciales que ya se han repartido entre los dos lotes.
		 */
		public Nodo(int suma1, int suma2, int k, int n)
		{
			this.suma1 = suma1;
			this.suma2 = suma2;
			this.k = k;
			this.n = n;
		}

		/**
		 * Comprueba si la solución ya ha examinado todos los objetos.
		 * @return Devuelve verdadero si el nivel de construcción de la solución coincide con el tamaño del vector de objetos.
		 */
		public boolean esCompleto()
		{
			return k == N;
		}

		/**
		 * Comprueba si los lotes seleccionados se contrarrestan consiguiendo un reparto justo.
		 * @return Devuelve verdadero si las sumas coinciden con el valor 0.
		 */
		public boolean esFactible()
		{
			return suma1 == 0 && suma2 == 0;
		}

		/**
		 * Comprueba si el nodo que invoca el método es potencialmente mejor que el nodo recibido en los parámetros.
		 * @param nodo Nodo con el que se va a comprobar si es factible.
		 * @return Devuelve verdadero si el nodo invocante es mejor que el nodo recibido en los parámetros.
		 */
		public boolean esAceptable(Nodo nodo)
		{
			return cotaOptimista() > nodo.cotaOptimista();
		}

		/**
		 * La cota optimista calcula el valor que podría llegar a tener la solución en el mejor caso.
		 * Es decir, supone que los elementos que no ha utilizado sí van a entrar en los lotes de reparto.
		 * @return Devuelve la cantidad de elementos utilizados más la cantidad de elementos que quedan por examinar.
		 */
		public int cotaOptimista()
		{
			return n + N - k;
		}

		/**
		 * La cota pesimista calcula el valor que podrá tener la solución en el peor caso.
		 * Es decir, los elementos que ya ha utilizado nunca se "desutilizarán".
		 * @return Devuelve la cantidad de elementos que ya ha repartido.
		 */
		public int cotaPesimista()
		{
			return n;
		}

		/**
		 * Implementación del método de comparación de dos nodos de la clase Comparable para decidir qué nodo es mayor o menor.
		 * @param nodo Nodo que se va a comparar con el nodo que invocó el método.
		 * @return Devuelve 0 si los nodos son iguales, negativo si el primer nodo es mayor que el segundo, o positivo en otro caso.
		 */
		public int compareTo(Nodo nodo)
		{
			return nodo.cotaOptimista() - cotaOptimista();
		}
	}
}
