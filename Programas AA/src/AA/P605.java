package AA;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Iterator;

public class P605
{
	private int N;
	private int L;
	private int M;
	private int[] v;
	
	private int[][][] tabla;
	private boolean[][][] estado;

	public ArrayList<Integer> bestSolution(String data)
	{
		String[] dataAux = data.split("\\p{Space}+");
		N = new Integer(dataAux[0]);
		L = new Integer(dataAux[1]);
		M = dataAux.length - 2;
		v = new int[M];
		for (int i = 0; i < M; i++)
		{
			v[i] = new Integer(dataAux[i+2]);
		}

		tabla = new int[M+1][L+1][N+1];
		estado = new boolean[M+1][L+1][N+1];
		
		Nodo.N = N;
		Nodo.L = L;
		Nodo.M = M;
		Nodo.v = v;
		
		/*System.out.println("N: " + N);
		System.out.println("L: " + L);
		System.out.print("v: ");
		for (int i = 0; i < M; i++)
		{
			System.out.print(v[i] + " ");
		}
		System.out.println();*/		
		
		Nodo.resultadoExacto = best(0, 1, N);
		//System.out.println("Resultado exacto: " + Nodo.resultadoExacto);
		//Nodo voraz = Nodo.voraz();
		//System.out.println("Resultado voraz: " + voraz.toString());
		Nodo solucion = Nodo.RyP(null);
		//System.out.println("Resultado final: " + solucion.toString());
		
		ArrayList<Integer> vector = new ArrayList<Integer>();
		for (int i = 0; i < M; i++)
		{
			if (solucion.estado[i])
			{
				vector.add(i+1);
			}
		}
		return vector;
	}
	
	private int best(int k, int l, int n)
	{
		if (k == M)
		{
			return 0;
		}
		else
		{
			int caso1 = 0, caso2 = 0;
			
			if (n > 0)
			{
				if (l == 1)
				{
					if (estado[k + 1][L][n - 1])
					{
						caso1 = tabla[k + 1][L][n - 1];
					}
					else
					{
						caso1 = tabla[k + 1][L][n - 1] = best(k + 1, L, n - 1);
						estado[k + 1][L][n - 1] = true;
					}
					caso1 += v[k];				
				}
				
				if (estado[k + 1][Math.max(1, l - 1)][n])
				{
					caso2 = tabla[k + 1][Math.max(1, l - 1)][n];
				}
				else
				{
					caso2 = tabla[k + 1][Math.max(1, l - 1)][n] = best(k + 1, Math.max(1, l - 1), n);
					estado[k + 1][Math.max(1, l - 1)][n] = true;
				}
			}
			
			return Math.max(caso1, caso2);
		}
	}
	
	static class Nodo implements Comparable<Nodo>
	{
		/**
		 * Obtiene una solución cercana a la solución óptima aplicando un algoritmo voraz.
		 * @return Devuelve una solución buena.
		 */
		public static Nodo voraz()
		{
			Nodo solucion = new Nodo();
			for (int i = 0; i < M && solucion.n > 0; i++)
			{
				if (solucion.l <= 1)
				{
					solucion.l = L;
					solucion.total += v[i];
					solucion.n--;
					solucion.estado[i] = true;
				}
				else
				{
					solucion.l = Math.max(1, solucion.l - 1);
					solucion.estado[i] = false;
				}
			}
			solucion.cota = solucion.total;
			solucion.k = M;
			return solucion;
		}

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

			int nodos, podas, cambios;
			nodos = podas = cambios = 0;

			while (!nodosVivos.isEmpty())
			{
				Nodo nodoActual = nodosVivos.poll();

				nodos++;
				//System.out.println("-> " + nodoActual.toString());

				if (nodoActual.esCompleto())
				{
					//System.out.println("Nodo completo: ");
					//if (mejorSolucion == null || nodoActual.esAceptable(mejorSolucion))
					if (nodoActual.esFactible())
					{
						//System.out.println(nodoActual.toString());
						cambios++;
						mejorSolucion = nodoActual;
						return mejorSolucion;
					}
				}
				else
				{
					Iterator<Nodo> i = nodoActual.generarHijos().iterator();
					while (i.hasNext())
					{
						Nodo hijo = i.next();
						//if (mejorSolucion == null || hijo.esAceptable(mejorSolucion))
						if (hijo.esAceptable(mejorSolucion))
						{
							nodosVivos.add(hijo);
						}
						/*else
						{
							podas++;
						}*/
					}
				}
			}

			/*System.out.println("Nodos: " + nodos);
			System.out.println("Podas: " + podas);
			System.out.println("Cambios: " + cambios);*/

			return mejorSolucion;
		}

		private static int N;
		private static int L;
		private static int M;
		private static int[] v;
		
		private static int resultadoExacto;

		public int k;
		public boolean[] estado;
		public int l;
		public int n;
		public int total;
		public int cota;
		public int cotaAlternativa;

		/**
		 * Constructor por defecto.
		 * Inicializa el nodo con sus valores por defecto.
		 */
		public Nodo()
		{
			k = 0;
			estado = new boolean[M];
			l = 1;
			n = N;
			total = 0;
			cota = 0;
			cotaAlternativa = 0;
			calcularCotas();
		}

		/**
		 * Constructor de copia.
		 */
		public Nodo(Nodo nodo)
		{
			k = nodo.k;
			estado = nodo.estado.clone();
			l = nodo.l;
			n = nodo.n;
			total = nodo.total;
			cota = nodo.cota;
			cotaAlternativa = nodo.cotaAlternativa;
		}

		/**
		 * Comprueba si el nodo es un nodo completo o todavía puede volver a
		 * expandirse.
		 * @return Devuelve verdadero si el nodo está completo.
		 */
		public boolean esCompleto()
		{
			return k == M;
		}

		/**
		 * Expande el nodo generando sus posibles hijos. Sólo si el nodo es un nodo
		 * incompleto.
		 * Sólo se generan los hijos que puedan llegar a ser factibles.
		 * @return Devuelve una lista con los nodos hijos del nodo.
		 */
		public ArrayList<Nodo> generarHijos()
		{
			ArrayList<Nodo> hijos = new ArrayList<Nodo>();
			
			if (n > 0)
			{
				if (l <= 1)
				{
					Nodo hijo1 = new Nodo(this);
					hijo1.estado[hijo1.k] = true;
					hijo1.l = L;
					hijo1.n--;
					hijo1.total += v[hijo1.k];
					hijo1.k++;
					hijo1.calcularCotas();
					hijos.add(hijo1);
				}
				
				Nodo hijo2 = new Nodo(this);
				hijo2.estado[hijo2.k] = false;
				hijo2.l = Math.max(1, hijo2.l - 1);
				hijo2.k++;
				hijo2.calcularCotas();
				hijos.add(hijo2);
			}
			else
			{
				// Si ya no puede coger más elementos, lo completamos e reinsertamos.
				k = M;
				calcularCotas();
				hijos.add(this);
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
			return total == resultadoExacto;
		}

		/**
		 * Comprueba si el nodo que invocó el método es potencialmente mejor que
		 * otro nodo de referencia.
		 * @param nodo Nodo con el que se va a comprobar si el nodo es aceptable.
		 * @return Devuelve verdadero si el nodo invocante es mejor nodo.
		 */
		public boolean esAceptable(Nodo nodo)
		{
			//return cota > nodo.cota;
			return cota >= resultadoExacto;
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
			//return - k + nodo.k;
			//return calories - nodo.calories;
			//return cota - nodo.cota;
			//return -cotaAlternativa + nodo.cotaAlternativa;
			return - total + nodo.total;
		}

		public void calcularCotas()
		{
			cota = total;
			if (k < M)
			{
				int[] restantes = new int[M - k];
				for (int i = k; i < M; i++)
				{
					restantes[i - k] = v[i];
				}
				
				quicksort(restantes, 0, M - k - 1);
		
				int puedo = Math.min(n, (int) Math.ceil((M - k)/(double) L));
				for (int i = 0; i < puedo; i++)
				{
					cota += restantes[i];
				}
			}
			
			/*cotaAlternativa = total;
			for (int i = k; i < M; i++)
			{
				cotaAlternativa += v[i];
			}*/
			/*cota = total;
			for (int i = k; i < M; i++)
			{
				cota += v[i];
			}*/
		}

		/**
		 * Obtiene la solución en formato alfanumérico.
		 * @return un String con la asignación de tareas a cada máquina y su valor de cota
		 */
		@Override
		public String toString()
		{
			String aux = "";
			for (int i = 0; i < k; i++)
			{
				if (estado[i])
				{
					aux += (i+1) + " ";
				}
			}
			aux = aux.trim();
			return total + " " + n + "/" + l + " " + "{" + aux + "} (" + k + ")";
		}
	}
	
	static void quicksort(int[] vector, int primero, int ultimo)
	{
		int i = primero, j = ultimo;
		int pivote = (vector[primero] + vector[ultimo]) / 2;
		int auxiliar;

		do
		{
			while (vector[i] > pivote)
			{
				i++;
			}
			while (vector[j] < pivote)
			{
				j--;
			}

			if (i <= j)
			{
				auxiliar = vector[j];
				vector[j] = vector[i];
				vector[i] = auxiliar;
				i++;
				j--;
			}

		}
		while (i <= j);

		if (primero < j)
		{
			quicksort(vector, primero, j);
		}
		if (ultimo > i)
		{
			quicksort(vector, i, ultimo);
		}
	}
}








