package AA;

import java.util.PriorityQueue;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class P82
{
	private int N;
	private int[][] e;

	public int best(String[] data)
	{
		// Se procesa la entrada.
		String[] dataAux1 = data[0].split("\\p{Space}+");
		String[] dataAux2 = data[1].split("\\p{Space}+");
		String[] dataAux3 = data[2].split("\\p{Space}+");
		N = new Integer(dataAux1.length);
		e = new int[N][3];
		for (int i = 0; i < N; i++)
		{
			e[i][0] = new Integer(dataAux1[i]);
			e[i][1] = new Integer(dataAux2[i]);
			e[i][2] = new Integer(dataAux3[i]);
		}

		/*for (int i = 0; i < N; i++)
			System.out.print(String.format("%1$-4s", e[i][0]));
		System.out.println();
		for (int i = 0; i < N; i++)
			System.out.print(String.format("%1$-4s", e[i][1]));
		System.out.println();
		for (int i = 0; i < N; i++)
			System.out.print(String.format("%1$-4s", e[i][2]));
		System.out.println();*/

		// Se establecen los valores estáticos de la clase.
		Nodo.N = N;
		Nodo.e = e;

		Nodo voraz = Nodo.voraz();
		//System.out.println("Voraz       : " + voraz.v);
		//System.out.println("························································");
		Nodo solucion = Nodo.RyP(voraz);
		//System.out.println("RyP (voraz) : " + solucion.v);
		//System.out.println("························································");

		//Nodo solucion2 = Nodo.RyP(null);
		//System.out.println("RyP         : " + solucion2.v);
		//System.out.println("########################################################\n");

		return solucion.v;
	}

	static class Encargo implements Comparable<Encargo>
	{
		private int d, v, m, indice;
		public Encargo(int d, int v, int m, int indice)
		{
			this.d = d;
			this.v = v;
			this.m = m;
			this.indice = indice;
		}

		public int compareTo(Encargo e)
		{
			return - v/d + e.v/e.d;
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
			Nodo nodo = new Nodo();
			nodo.k = N;
			for (int i = 0; i < N; i++)
			{
				if (e[i][2] >= e[i][0] + nodo.t)
				{
					nodo.v += e[i][1];
					nodo.t += e[i][0];
				}
			}
			nodo.cota = nodo.v;
			return nodo;
		}

		/**
		 * Obtiene la solución óptima aplicando un algoritmo de ramificación y poda.
		 * @param mejorSolucionInicial Solución desde la que parte el algoritmo. Puede resolverse con un algoritmo voraz. Si no, se especifica "null".
		 * @return Devuelve la solución óptima al problema.
		 */
		public static Nodo RyP(Nodo mejorSolucionInicial)
		{
			Nodo mejorSolucion = mejorSolucionInicial;
			PriorityQueue<Nodo> nodosVivos = new PriorityQueue<Nodo>();
			nodosVivos.add(new Nodo());

			HashSet<Nodo> explorados = new HashSet<Nodo>();
			explorados.add(new Nodo());

			int nodos = 0, podas = 0, cambios = 0, estaba = 0;

			while (!nodosVivos.isEmpty())
			{
				Nodo nodoActual = nodosVivos.poll();

				//System.out.println(nodoActual);
				nodos++;

				if (nodoActual.esCompleto())
				{
					if ((nodoActual.esFactible() && (mejorSolucion == null || nodoActual.esAceptable(mejorSolucion))))
					{
						cambios++;
						mejorSolucion = nodoActual;
						return mejorSolucion; // como ordena por la cota, solo encuentra 1 cuando es el optimo
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
							if (!explorados.contains(hijo))
							{
								nodosVivos.add(hijo);
								explorados.add(hijo);
							}
							else
							{
								estaba++;
							}
						}
						else
						{
							podas++;
						}
					}
				}
			}

			//System.out.println("Nodos: " + nodos);
			//System.out.println("Cambios: " + cambios);
			/*System.out.println("Podas: " + podas);
			System.out.println("Estaba: " + estaba);*/

			return mejorSolucion;
		}

		public static int N;
		public static int[][] e;

		public int k;
		public int t;
		public int v;

		public int cota;

		/**
		 * Constructor por defecto.
		 * Inicializa el nodo con sus valores por defecto.
		 */
		public Nodo()
		{
			k = 0;
			t = 0;
			v = 0;
			cota = 0;
			
			calcularCotas();
		}

		/**
		 * Constructor de copia.
		 */
		public Nodo(Nodo nodo)
		{
			k = nodo.k;
			t = nodo.t;
			v = nodo.v;
			cota = nodo.cota;
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
			ArrayList<Nodo> hijos = new ArrayList<Nodo>(2);

			Nodo hijo1 = new Nodo(this);
			hijo1.k++;
			hijo1.calcularCotas();
			hijos.add(hijo1);

			if (e[this.k][2] >= e[this.k][0] + this.t)
			{
				Nodo hijo2 = new Nodo(this);
				hijo2.t += e[hijo2.k][0];
				hijo2.v += e[hijo2.k][1];
				hijo2.k++;
				hijo2.calcularCotas();
				hijos.add(hijo2);
			}

			return hijos;
		}

		/**
		 * Comprueba si el nodo es factible o no.
		 * En muchos problemas este método no tiene sentido y debería devolver true.
		 * @return Devuelve verdadero si el nodo es factible.
		 */
		public boolean esFactible()
		{
			return true;
		}

		/**
		 * Comprueba si el nodo que invocó el método es potencialmente mejor que
		 * otro nodo de referencia.
		 * @param nodo Nodo con el que se va a comprobar si el nodo es aceptable.
		 * @return Devuelve verdadero si el nodo invocante es mejor nodo.
		 */
		public boolean esAceptable(Nodo nodo)
		{
			return cota > nodo.cota;
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
			//return usadas - nodo.usadas;
			//return k - nodo.k;
			//return cota - nodo.cota;
			//return - v + nodo.v;
			//return - aceptados + nodo.aceptados;
			return - cota + nodo.cota;
			//return - cotaSuperior + nodo.cotaSuperior;
		}

		public void calcularCotas()
		{
			cota = v;
			for (int i = k; i < N; i++)
			{
				// Compruebo que pueda sumarlo.
				if (e[i][2] >= e[i][0] + t)
				{
					cota += e[i][1];
					// Si éste y el siguiente tienen los mismos plazos pero el siguiente vale más, no lo sumo (para sumar el siguiente).
					/*if (i != N-1 && e[i][0] >= e[i+1][0] && e[i][2] == e[i+1][2] && e[i][1] < e[i+1][1])
					{
						
					}
					else
					{
						cota += e[i][1];
					}*/

					// Si puedo sumarlo, pero al sumarlo ya no puedo sumar el siguiente y resulta que el siguiente era mejor, no lo sumo.
					/*if (i != N-1 && (e[i+1][2] >= e[i+1][0] + t) && !(e[i+1][2] >= e[i][0] + e[i+1][0] + t) && e[i][1] < e[i+1][2])
					{

					}
					else
					{
						cota += e[i][1];
					}*/
				}
			}
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof Nodo)
			{
				Nodo nodo = (Nodo) o;
				return k == nodo.k && t == nodo.t && v == nodo.v;
			}
			return false;
		}

		@Override
		public int hashCode()
		{
			return k*17+t*37+v*73;
		}

		/**
		 * Obtiene la solución en formato alfanumérico.
		 * @return Devuelve una cadena con la información relevante del nodo.
		 */
		@Override
		public String toString()
		{
			return k + " " + t + " " + v;
		}
	}
}