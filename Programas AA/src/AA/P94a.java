package AA;

import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.HashMap;

public class P94a
{
	/**
	 * Almacena el extremo izquierdo de todas las calles.
	 */
	private int[] v;

	/**
	 * Almacena el extremo derecho de todas las calles.
	 */
	private int[] w;

	/**
	 * Cantidad de calles que hay en total.
	 */
	private int M;

	/**
	 * Indica la cantidad de veces que se repite un cruce en la ciudad.
	 * Es decir, la cantidad de calles que interseccionan en cada cruce.
	 */
	HashMap<Integer, Integer> cantidades;

	/**
	 * Resuelve el problema de la iluminación de las calles.
	 * @param data Vectores de entrada con el valor que le da cada uno de la pareja a cada valor.
	 * @return Devuelve el mínimo número de luces que hay que utilizar.
	 */
	public int best(String[] data)
	{
		// Extracción de los parámetros de entrada.
		M = data.length;
		v = new int[M];
		w = new int[M];
		cantidades = new HashMap<Integer, Integer>();
		for (int i = 0; i < M; i++)
		{
			String[] aux = data[i].split("\\p{Space}+");
			v[i] = new Integer(aux[0]);
			w[i] = new Integer(aux[1]);

			// Contabilizamos qué cantidad de calles aparecen en cada cruce.
			if (cantidades.containsKey(v[i]))
			{
				cantidades.put(v[i], cantidades.remove(v[i]) + 1);
			}
			else
			{
				cantidades.put(v[i], 1);
			}

			if (cantidades.containsKey(w[i]))
			{
				cantidades.put(w[i], cantidades.remove(w[i]) + 1);
			}
			else
			{
				cantidades.put(w[i], 1);
			}
		}

		// Ejecuta el algoritmo de ramificación y poda.
		return best(better());
	}

	/**
	 * Implementa un algoritmo voraz que optiene una solución aproximada al problema de la colocación de luces.
	 * Presenta un coste temporal O(M²).
	 * @return Devuelve el nodo con la solución obtenida.
	 */
	private Nodo better()
	{
		Nodo solucionVoraz = new Nodo();
		solucionVoraz.k = M;

		// Se recorre la lista de cruces de cada calle, escogiendo siempre aquél que más se repite.
		for  (int i = 0; i < M; i++)
		{
			// Hay que intentar reutilizar los puntos de luz ya existentes.
			if (solucionVoraz.luces.contains(new Integer(v[i])) || solucionVoraz.luces.contains(new Integer(w[i])))
			{
				solucionVoraz.n++;
			}
			else
			{
				// Si no había ningún punto de luz en ninguno de los extremos, escogemos el extremo que más veces se repita.
				if (cantidades.get(v[i]) > cantidades.get(w[i]))
				{
					solucionVoraz.luces.add(new Integer(v[i]));
				}
				else
				{
					solucionVoraz.luces.add(new Integer(w[i]));
				}
			}

			// Una vez seleccionado el cruce de la calle en el que poner la bombilla, se decrementa la cantidad de repeticiones de los cruces de esa calle.
			cantidades.put(v[i], cantidades.remove(v[i]) - 1);
			cantidades.put(w[i], cantidades.remove(w[i]) - 1);
		}

		solucionVoraz.calcularCotaOptimista();
		return solucionVoraz;
	}

	/**
	 * Resuelve el problema de la iluminación de calles aplicando un algoritmo de ramificación y poda.
	 * @param solucionInicial Solución desde la que parte el algoritmo para realizar las podas.
	 * @return Devuelve el mínimo número de luces que hay que instalar en la ciudad para que todas las calles tengan al menos un extremo iluminado.
	 */
	private int best(Nodo solucionInicial)
	{
		// La mejor solución inicial será
		Nodo mejorSolucion = solucionInicial;

		// Se crea la cola de prioridad y se inserta el nodo raíz.
		PriorityQueue<Nodo> nodosVivos = new PriorityQueue<Nodo>();
		Nodo nodoRaiz = new Nodo();
		nodosVivos.add(nodoRaiz);

		// Repetimos el bucle mientras queden nodos y el primero de la lista sea potencialmente mejor que la solución obtenida hasta el momento.
		while (!nodosVivos.isEmpty())
		{
			// Extrae el siguiente nodo de la lista.
			Nodo nodoActual = nodosVivos.poll();

			// Evalúo el nodo suponiendo que no coloco ninguna luz para esta calle, porque ya tiene alguna luz en algún extremo.
			if (nodoActual.luces.contains(new Integer(v[nodoActual.k])) || nodoActual.luces.contains(new Integer(w[nodoActual.k])))
			{
				// Incremento el número de calle que hay que examinar a continuación e incremento el número de calles que me he ahorrado iluminar.
				nodoActual.k++;
				nodoActual.n++;
				nodoActual.calcularCotaOptimista();

				// Compruebo que el nodo siga siendo aceptable.
				if (nodoActual.esAceptable(mejorSolucion))
				{
					// Si no está completo, reinserto el nodo en la lista; si lo está, se convierte en la mejor solución hasta el momento.
					if (nodoActual.esCompleto())
					{
						mejorSolucion = nodoActual;
					}
					else
					{
						nodosVivos.add(nodoActual);
					}
				}
			}
			else
			{
				// Si la luz del extremo izquierdo no está colocada, evalúo el nodo colocando la luz en el extremo izquierdo.
				if (!nodoActual.luces.contains(new Integer(v[nodoActual.k])))
				{
					// Creo el nodo y modifico sus atributos.
					Nodo extremoIzquierdo = nodoActual.clone();
					extremoIzquierdo.luces.add(new Integer(v[nodoActual.k]));
					extremoIzquierdo.k++;
					extremoIzquierdo.calcularCotaOptimista();

					// Compruebo que el nodo sea aceptable.
					if (extremoIzquierdo.esAceptable(mejorSolucion))
					{
						// Si no está completo, inserto el nodo en la lista; si lo está, se convierte en la mejor solución hasta el momento.
						if (extremoIzquierdo.esCompleto())
						{
							mejorSolucion = extremoIzquierdo;
						}
						else
						{
							nodosVivos.add(extremoIzquierdo);
						}
					}
				}

				// Si la luz del extremo derecho no está colocada, evalúo el nodo colocando la luz en el extremo derecho.
				if (!nodoActual.luces.contains(new Integer(w[nodoActual.k])))
				{
					// Creo el nodo y modifico sus atributos.
					Nodo extremoDerecho = nodoActual.clone();
					extremoDerecho.luces.add(new Integer(w[nodoActual.k]));
					extremoDerecho.k++;
					extremoDerecho.calcularCotaOptimista();

					// Compruebo que el nodo sea aceptable.
					if (extremoDerecho.esAceptable(mejorSolucion))
					{
						// Si no está completo, inserto el nodo en la lista; si lo está, se convierte en la mejor solución hasta el momento.
						if (extremoDerecho.esCompleto())
						{
							mejorSolucion = extremoDerecho;
						}
						else
						{
							nodosVivos.add(extremoDerecho);
						}
					}
				}
			}
		}

		return M - mejorSolucion.n;
	}

	/**
	 * Clase auxiliar que representa un nodo del árbol de soluciones.
	 */
	private class Nodo implements Comparable<Nodo>
	{
		/**
		 * Conjunto de identicadores de los cruces en los que se va a colocar una luz.
		 * No existirán elementos repetidos.
		 */
		public HashSet<Integer> luces;

		/**
		 * Nivel de construcción de la solución. Hace referencia al siguiente elemento del vector de objetos que hay que explorar.
		 * Si esta variable coincide con el valor total de objetos, nos encontramos ante un nodo completo.
		 */
		public int k;

		/**
		 * Cantidad de luces que nos hemos ahorrado instalar porque ya estaban instaladas en otro cruce.
		 */
		public int n;

		/**
		 * Indica un valor máximo que la solución actual nunca sobrepasará.
		 */
		public int cotaOptimista;

		/**
		 * Constructor por defecto.
		 */
		public Nodo()
		{
			luces = new HashSet<Integer>();
			k = 0;
			n = 0;
			calcularCotaOptimista();
		}

		/**
		 * Sobreescritura del método de clonación que crea un nuevo nodo e inicializa
		 * todos sus atributos según los atributos del nodo que invocó el método.
		 * @return Devuelve un nuevo objeto Nodo idéntico al que invocó el método.
		 */
		@Override
		public Nodo clone()
		{
			Nodo aux = new Nodo();
			//aux.luces = (HashSet<Integer>) luces.clone();
			aux.luces = new HashSet<Integer>(luces);
			aux.k = k;
			aux.n = n;
			aux.cotaOptimista = cotaOptimista;
			return aux;
		}

		/**
		 * Comprueba si la solución ya ha examinado todos los objetos.
		 * @return Devuelve verdadero si el nivel de construcción de la solución coincide con el tamaño del vector de objetos.
		 */
		public boolean esCompleto()
		{
			return k == M;
		}

		/**
		 * Comprueba si el nodo que invoca el método es potencialmente mejor que el nodo recibido en los parámetros.
		 * @param nodo Nodo con el que se va a comprobar si es factible.
		 * @return Devuelve verdadero si el nodo invocante es mejor que el nodo recibido en los parámetros.
		 */
		public boolean esAceptable(Nodo nodo)
		{
			return cotaOptimista > nodo.cotaOptimista;
		}

		/**
		 * La cota optimista calcula el valor que podría llegar a tener la solución en el mejor caso.
		 * Cuanto menor sea esta cota optimista, más efectiva será la poda. Sin embargo, no puede ser inferior
		 * a la solución real, o podríamos descartar la solución óptima.
		 *
		 * Esta función calcula el máximo número de luces que pueden ahorrarse partiendo desde la solución actual.
		 * Es decir, realiza una aproximación de qué focos tendrán que instalarse obligatoriamente y que no podrá evitarse su instalación.
		 *
		 * @return Devuelve la cantidad de focos que se nos ahorraremos colocar en el mejor caso.
		 */
		public int cotaOptimista()
		{
			// Se crea una variable auxiliar donde se almacenarán los identificadores de los cruces en los que se ha colocado una luz.
			//HashSet<Integer> lucesAux = (HashSet<Integer>) luces.clone();
			HashSet<Integer> lucesAux = new HashSet<Integer>(luces);

			// Indica qué cantidad de focos nos ahorraremos colocar en las calles restantes (desde la calle k hasta M-1).
			int nAux = 0;

			for (int i = k; i < M; i++)
			{
				// Las calles que ya tengan un foco se sumarán al cómputo de luces ahorradas.
				if (lucesAux.contains(new Integer(v[i])) || lucesAux.contains(new Integer(w[i])))
				{
					nAux++;
				}
				else
				{
					// Si no tiene en ningún cruce, supondremos que instalamos el foco en ambos, y no incrementaremos el cómputo de luces ahorradas.
					lucesAux.add(new Integer(v[i]));
					lucesAux.add(new Integer(w[i]));
				}
			}
			return n + nAux;
		}

		/**
		 * Calcula la cota optimista y la asigna a una variable para evitar tiempo de cálculo.
		 */
		public void calcularCotaOptimista()
		{
			cotaOptimista = cotaOptimista();
		}

		/**
		 * Implementación del método de comparación de dos nodos de la clase Comparable para decidir qué nodo es mayor o menor.
		 * @param nodo Nodo que se va a comparar con el nodo que invocó el método.
		 * @return Devuelve 0 si los nodos son iguales, negativo si el primer nodo es mayor que el segundo, o positivo en otro caso.
		 */
		public int compareTo(Nodo nodo)
		{
			return nodo.cotaOptimista - cotaOptimista;
		}
	}
}
