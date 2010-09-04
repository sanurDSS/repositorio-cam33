package AA;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class P201
{
	private int D;
	private int N;
	private int[] n;
	private int T1;
	private int T2;

	public int[] bestSolution(String data)
	{
		String[] dataAux = data.split("\\p{Space}+");
		N = new Integer(dataAux[0]);
		D = new Integer(dataAux[3]);
		n = new int[N];
		for (int i = 0; i < N; i++)
		{
			n[i] = new Integer(dataAux[i + 4]);
		}
		T1 = new Integer(dataAux[1]);
		T2 = new Integer(dataAux[2]);

		Nodo.N = N;
		Nodo.D = D;
		Nodo.n = n;
		Nodo.T1 = T1;
		Nodo.T2 = T2;

		Nodo.resultadoExacto = P202Aux.best(data);
		Nodo nodo = Nodo.RyP(null);
		int[] solucion = new int[N + 1];
		solucion[0] = nodo.t1 + nodo.t2;
		for (int i = 0; i < N; i++)
		{
			solucion[i+1] = nodo.estado[i];
		}

		return solucion;
	}

	static class Nodo implements Comparable<Nodo>
	{
		/**
		 * Obtiene una solución cercana a la solución óptima aplicando un algoritmo voraz.
		 * @return Devuelve una solución buena.
		 */
		public static Nodo voraz()
		{
			return null;
		}

		//private static int nodos = 0;

		/**
		 * Obtiene la solución óptima aplicando un algoritmo de ramificación y poda.
		 * @param mejorSolucionInicial Solución desde la que parte el algoritmo. Puede resolverse con un algoritmo voraz.
		 * @return Devuelve la solución óptima al problema.
		 */
		public static Nodo RyP(Nodo mejorSolucionInicial)
		{
			Nodo mejorSolucion = mejorSolucionInicial;
			PriorityQueue<Nodo> nodosVivos = new PriorityQueue<Nodo>();
			nodosVivos.add(new Nodo());

			int nodos = 0;
			int podas = 0;
			int cambios = 0;

			while (!nodosVivos.isEmpty())
			{
				Nodo nodoActual = nodosVivos.poll();

				//System.out.println(nodoActual);
				nodos++;

				if (nodoActual.esCompleto())
				{
					if (nodoActual.esFactible() && (mejorSolucion == null || nodoActual.esAceptable(mejorSolucion)))
					{
						//System.out.println("Actualizo el nodo " + (nodoActual.t1 + nodoActual.t2) + " " + (nodoActual.cotaSuperior));
						cambios++;
						mejorSolucion = nodoActual;
						if (mejorSolucion.t1 + mejorSolucion.t2 == resultadoExacto)
						{
							return mejorSolucion;
						}
					}
				}
				else
				{
					Iterator<Nodo> i = nodoActual.generarHijos().iterator();
					while (i.hasNext())
					{
						Nodo hijo = i.next();
						if (mejorSolucion == null || hijo.esAceptable(mejorSolucion))
						{
							nodosVivos.add(hijo);
						}
						{
							podas++;
						}
					}
				}
			}

			/*System.out.println("nodos: " + nodos);
			System.out.println("podas: " + podas);
			System.out.println("cambios: " + cambios);*/

			return mejorSolucion;
		}

		public static int D;
		public static int N;
		public static int[] n;
		public static int T1, T2;
		public static int resultadoExacto;

		public int k;
		public int[] estado;
		public int t1, t2;
		public int d;

		// Cota 1: no va a ser inferior que cota 1 (en el peor caso sacará este valor).
		// Ésta no sirve de mucho, sólo para ordenar por un valor que sabemos que es posible obtener seguro.
		public int cotaInferior;

		// Cota 2: no va a ser superior que cota 2 (en el mejor caso sacará este valor).
		// Éste sirve para podar. Sabiendo que no puede sacar más, si la última solución obtenida es mejor, se descarta.
		public int cotaSuperior;

		/**
		 * Constructor por defecto.
		 * Inicializa el nodo con sus valores por defecto.
		 */
		public Nodo()
		{
			k = 0;
			estado = new int[N];
			t1 = t2 = 0;
			d = 0;
			cotaSuperior = cotaInferior = 0;
			calcularCotas();
		}

		/**
		 * Constructor de copia.
		 */
		public Nodo(Nodo nodo)
		{
			k = nodo.k;
			estado = nodo.estado.clone();
			t1 = nodo.t1;
			t2 = nodo.t2;
			d = nodo.d;
			cotaSuperior = nodo.cotaSuperior;
			cotaInferior = nodo.cotaInferior;
		}

		/**
		 * Comprueba si el nodo es un nodo completo o todavía puede volver a
		 * expandirse.
		 * @return Devuelve verdadero si el nodo está completo.
		 */
		public boolean esCompleto()
		{
			return k == N;
		}

		/**
		 * Expande el nodo generando sus posibles hijos. Sólo si el nodo es un nodo
		 * incompleto.
		 * Sólo se generan los hijos que puedan llegar a ser factibles.
		 * @return Devuelve una lista con los nodos hijos del nodo.
		 */
		public ArrayList<Nodo> generarHijos()
		{
			ArrayList<Nodo> hijos = new ArrayList<Nodo>(3);

			// Comprobamos si es posible obtener una solución factible.
			if (Math.abs(D - Math.abs(d)) <= N - k)
			{
				Nodo hijo1 = new Nodo(this);
				hijo1.k++;
				hijo1.calcularCotas();
				hijos.add(hijo1);

				if (n[k] <= T1 - t1)
				{
					Nodo hijo2 = new Nodo(this);
					hijo2.estado[hijo2.k] = 1;
					hijo2.d++;
					hijo2.t1 += n[hijo2.k];
					hijo2.k++;
					hijo2.calcularCotas();
					hijos.add(hijo2);
				}

				if (n[k] <= T2 - t2)
				{
					Nodo hijo3 = new Nodo(this);
					hijo3.estado[hijo3.k] = 2;
					hijo3.d--;
					hijo3.t2 += n[hijo3.k];
					hijo3.k++;
					hijo3.calcularCotas();
					hijos.add(hijo3);
				}
			}

			return hijos;
		}

		/**
		 * Comprueba si el nodo es factible o no.
		 * En muchos problemas este método no tiene sentido y debería devolver true.
		 * @return Devuelve verdadero si el nodos es factible.
		 */
		public boolean esFactible()
		{
			return Math.abs(d) <= D;// && (t1 + t2 == resultadoExacto);
		}

		/**
		 * Comprueba si el nodo que invocó el método es potencialmente mejor que
		 * otro nodo de referencia.
		 * @param nodo Nodo con el que se va a comprobar si el nodo es aceptable.
		 * @return Devuelve verdadero si el nodo invocante es mejor nodo.
		 */
		public boolean esAceptable(Nodo nodo)
		{
			//return cotaSuperior > nodo.cotaSuperior;
			return cotaSuperior >= resultadoExacto;
		}

		/**
		 * Compara dos nodos según sus cota optimista y su cota pesimista.
		 * @param nodo Nodo con el que se va a comparar.
		 * @return Devuelve verdadero
		 */
		public int compareTo(Nodo nodo)
		{
			//return cotaOptimista - nodo.cotaOptimista;
			//return cotaPesimista - nodo.cotaPesimista;
			return - k + nodo.k;
			//return usadas - nodo.usadas;
			//return k - nodo.k;
			//return cota - nodo.cota;
			//return + cotaSuperior - nodo.cotaSuperior;
			//return - (t1 + t2) + (nodo.t1 + nodo.t2);

			//return + Math.abs(resultado - cotaSuperior) - Math.abs(resultado - nodo.cotaSuperior);
		}

		public void calcularCotas()
		{
			cotaSuperior = t1 + t2;
			for (int i = k; i < N; i++)
			{
				if (n[i] <= T1 - t1 || n[i] <= T2 - t2)
				{
					if (t1 + t2 + n[i] <= T1 + T2)
					{
						cotaSuperior += n[i];
					}
				}
			}
			if (cotaSuperior > T1 + T2)
			{
				cotaSuperior = T1 + T2;
			}

			/*int t1Aux = t1;
			int t2Aux = t2;
			int dAux = d;
			for (int i = k; i < N; i++)
			{

			}*/
		}

		/**
		 * Obtiene la solución en formato alfanumérico.
		 * @return un String con la asignación de tareas a cada máquina y su valor de cota
		 */
		@Override
		public String toString()
		{
			String aux = String.format("%1$-4s", (t1 + t2)) + " ";

			for (int i = 0; i < k; i++)
			{
				aux += estado[i] + " ";
			}

			for (int i = k; i < N; i++)
			{
				aux += "- ";
			}

			return aux;
		}
	}
}


class P202Aux
{
	private static int D;
	private static int N;
	private static int[] n;
	private static int T1i;
	private static int T2i;

	private static int maximoActual;
	private static HashMap4<Integer, Integer, Integer, Integer, Integer> tabla;

	public static int best(String data)
	{
		String[] dataAux = data.split("\\p{Space}+");
		N = new Integer(dataAux[0]);
		D = new Integer(dataAux[3]);
		T1i = new Integer(dataAux[1]);
		T2i = new Integer(dataAux[2]);
		n = new int[N];
		for (int i = 0; i < N; i++)
		{
			n[i] = new Integer(dataAux[i + 4]);
		}

		maximoActual = Integer.MIN_VALUE;
		tabla = new HashMap4<Integer, Integer, Integer, Integer, Integer>();

		return best(0, T1i, T2i, 0);
	}

	int podas = 0;


	// Recursivo con ramificación y poda (se añade un parámetro más) y programación dinámica (pero sin tener en cuenta ese parámetro).
	private static int best(int k, int T1, int T2, int diferencia)
	{
		// Si la llamada recursiva no es potencialmente mejor que la solución obtenida hasta ahora, se descarta.
		int suma = 0;
		for (int i = k; i < N; i++)
		{
			if (n[i] <= T1 || n[i] <= T2)
				suma += n[i];
		}
		int cota = (T1i + T2i - T1 - T2) + Math.min(T1 + T2, suma);
		if (cota <= maximoActual || Math.abs(diferencia) > D + (N - k))
		{
			return 0;
		}

		if (k == N)
		{
			if (Math.abs(diferencia) <= D)
			{
				// (T1i + T2i - T1 - T2) -> es el total de cargamento que estoy transportando
				if ((T1i + T2i - T1 - T2) > maximoActual)
				{
					maximoActual = (T1i + T2i - T1 - T2);
				}
				return (T1i + T2i - T1 - T2);
			}
			else
			{
				return 0;
			}
		}
		else
		{
			// Caso en el que se descarta el bloque.
			int caso1;
			if (tabla.containsKey(k + 1, T1, T2, diferencia))
			{
				caso1 = tabla.get(k + 1, T1, T2, diferencia);
			}
			else
			{
				caso1 = best(k + 1, T1, T2, diferencia);
				tabla.put(k + 1, T1, T2, diferencia, caso1);
			}
			int maximo = caso1;

			// Caso en el que el bloque se introduce en el camión 1 (si cabe).
			if (T1 >= n[k])
			{
				int caso2;
				if (tabla.containsKey(k + 1, T1 - n[k], T2, diferencia + 1))
				{
					caso2 = tabla.get(k + 1, T1 - n[k], T2, diferencia + 1);
				}
				else
				{
					caso2 = best(k + 1, T1 - n[k], T2, diferencia + 1);
					tabla.put(k + 1, T1 - n[k], T2, diferencia + 1, caso2);
				}
				maximo = Math.max(maximo, caso2);
			}

			// Caso en el que el bloque se introduce en el camión 2 (si cabe).
			if (T2 >= n[k])
			{
				int caso3;
				if (tabla.containsKey(k + 1, T1, T2 - n[k], diferencia - 1))
				{
					caso3 = tabla.get(k + 1, T1, T2 - n[k], diferencia - 1);
				}
				else
				{
					caso3 = best(k + 1, T1, T2 - n[k], diferencia - 1);
					tabla.put(k + 1, T1, T2 - n[k], diferencia - 1, caso3);
				}
				maximo = Math.max(maximo, caso3);
			}

			return maximo;
		}
	}

	// Sólo recursivo.
	/*private int best(int k, int T1, int T2, int diferencia)
	{
		if (k == N)
		{
			if (Math.abs(diferencia) <= D)
			{
				return 0;
			}
			else
			{
				return Integer.MIN_VALUE;
			}
		}
		else
		{
			// Caso en el que se descarta el bloque.
			int maximo = best(k + 1, T1, T2, diferencia);

			// Caso en el que el bloque se introduce en el camión 1 (si cabe).
			if (T1 >= n[k])
			{
				maximo = Math.max(maximo, best(k + 1, T1 - n[k], T2, diferencia + 1) + n[k]);
			}

			// Caso en el que el bloque se introduce en el camión 2 (si cabe).
			if (T2 >= n[k])
			{
				maximo = Math.max(maximo, best(k + 1, T1, T2 - n[k], diferencia - 1) + n[k]);
			}

			return maximo;
		}
	}*/

	static class HashMap4<K1, K2, K3, K4, V>
	{
		private HashMap<Par, V> tabla;

		public HashMap4()
		{
			tabla = new HashMap<Par, V>();
		}

		public HashMap4(int initialCapacity)
		{
			tabla = new HashMap<Par, V>(initialCapacity);
		}

		public HashMap4(int initialCapacity, float loadFactor)
		{
			tabla = new HashMap<Par, V>(initialCapacity, loadFactor);
		}

		public V get(K1 key1, K2 key2, K3 key3, K4 key4)
		{
			return tabla.get(new Par(key1, key2, key3, key4));
		}

		public V remove(K1 key1, K2 key2, K3 key3, K4 key4)
		{
			return tabla.remove(new Par(key1, key2, key3, key4));
		}

		public V put(K1 key1, K2 key2, K3 key3, K4 key4, V value)
		{
			return tabla.put(new Par(key1, key2, key3, key4), value);
		}

		public boolean containsKey(K1 key1, K2 key2, K3 key3, K4 key4)
		{
			return tabla.containsKey(new Par(key1, key2, key3, key4));
		}

		public boolean containsValue(V value)
		{
			return tabla.containsValue(value);
		}

		public boolean isEmpty()
		{
			return tabla.isEmpty();
		}

		public int size()
		{
			return tabla.size();
		}

		public void clear()
		{
			tabla.clear();
		}

		@Override
		public HashMap4<K1, K2, K3, K4, V> clone()
		{
			HashMap4<K1, K2, K3, K4, V> copia = new HashMap4<K1, K2, K3, K4, V>();
			//copia.tabla = (HashMap<Par, V>) tabla.clone();
			copia.tabla = new HashMap<Par, V>(tabla);
			return copia;
		}

		public Collection<V> values()
		{
			return tabla.values();
		}

		private static class Par
		{
			private Object objeto1;
			private Object objeto2;
			private Object objeto3;
			private Object objeto4;

			public Par(Object objeto1, Object objeto2, Object objeto3, Object objeto4)
			{
				this.objeto1 = objeto1;
				this.objeto2 = objeto2;
				this.objeto3 = objeto3;
				this.objeto4 = objeto4;
			}

			@Override
			public boolean equals(Object otroPar)
			{
				if (otroPar instanceof Par)
				{
					Par par = (Par) otroPar;
					return objeto1.equals(par.objeto1) && objeto2.equals(par.objeto2) && objeto3.equals(par.objeto3) && objeto4.equals(par.objeto4);
				}
				return false;
			}

			@Override
			public int hashCode()
			{
				int resultado = 17 * 37 + objeto1.hashCode();
				resultado = 37 * resultado + objeto2.hashCode();
				resultado = 37 * resultado + objeto3.hashCode();
				resultado = 37 * resultado + objeto4.hashCode();
				return resultado;
			}

			@Override
			public String toString()
			{
				return "<" + objeto1.toString() + ", " + objeto2.toString() + ", " + objeto3.toString() + ", " + objeto4.toString() + ">";
			}
		}
	}
}