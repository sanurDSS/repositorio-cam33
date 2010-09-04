package AA;

import java.util.PriorityQueue;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;

public class P21
{
	public int[] bestSolution(String data)
	{
		String[] dataAux = data.split("\\p{Space}+");
		Nodo.M = new Integer(dataAux[0]);
		Nodo.N = new Integer((dataAux.length - 1)/2);
		Nodo.v = new int[Nodo.N];
		Nodo.c = new int[Nodo.N];
		//Nodo.monedas = new ArrayList<Moneda>(Nodo.N);
		for (int i = 0; i < Nodo.N; i++)
		{
			Nodo.v[i] = new Integer(dataAux[2*i+1]);
			Nodo.c[i] = new Integer(dataAux[2*i+2]);

			//Moneda moneda = new Moneda();
			//moneda.indice = i;
			//moneda.valor = Nodo.v[i];
			//moneda.cantidad = Nodo.c[i];
			//Nodo.monedas.add(moneda);
		}
		//Collections.sort(Nodo.monedas);

		// Salida por pantalla del problema.
		/*System.out.println();
		for (int i = 0; i < Nodo.N; i++)
		{
			System.out.println("Moneda: " + Nodo.monedas.get(i).valor);
		}
		System.out.println("M: " + Nodo.M);
		System.out.println("N: " + Nodo.N);
		System.out.print("v: ");
		for (int i = 0; i < Nodo.N; i++)
		{
			System.out.print(Nodo.v[i] + " ");
		}
		System.out.println();
		System.out.print("c: ");
		for (int i = 0; i < Nodo.N; i++)
		{
			System.out.print(Nodo.c[i] + " ");
		}
		System.out.println();*/

		//Nodo voraz = Nodo.voraz();
		Nodo.resultadoExacto = P84Aux.best(data);
		//System.out.println("Resultado exacto: " + Nodo.resultadoExacto);
		Nodo nodo = new Nodo();
		nodo.cota = Nodo.resultadoExacto;
		nodo.usadas = Nodo.resultadoExacto;
		nodo.total = Nodo.M;
		nodo.k = Nodo.N;
		Nodo solucion = Nodo.RyP(nodo);

		
		//System.out.println("Voraz   : " + voraz.toString());
		//System.out.println("Solución: " + solucion.toString());

		return solucion.cantidades;
	}

	static class Moneda implements Comparable<Moneda>
	{
		public int indice;
		public int valor;
		public int cantidad;

		public int compareTo(Moneda moneda)
		{
			return - valor + moneda.valor;
		}
	}

	static class Nodo implements Comparable<Nodo>
	{
		/**
		 * Obtiene una solución cercana a la solución óptima aplicando un algoritmo voraz.
		 * @return Devuelve una solución buena.
		 */
		/*public static Nodo voraz()
		{
			Nodo nodo = new Nodo();
			nodo.k = N;
			nodo.cota = 0;
			nodo.total = 0;

			// Se recorren las monedas de menor a mayor, obteniendo así la cantidad máxima de monedas
			// que se podrían obtener en el peor caso. Así, se descartan todas las soluciones que tengan
			// más monedas que la solución del voraz.
			// Es decir, se van cogiendo monedas, desde las más pequeñas hasta las más grandes hasta alcanzar
			// (o sobrepasar) el valor M que debe devolver la máquina.
			for (int i = N - 1; i >= 0; i--)
			{
				Moneda moneda = monedas.get(i);

				// Monedas de este valor que me hacen falta para llegar a M.
				int monedasNecesarias = (int) Math.ceil((M - nodo.total)/(double) moneda.valor);

				// Se comprueba si hay suficientes monedas de este valor.
				if (moneda.cantidad >= monedasNecesarias)
				{
					nodo.total = M;
					nodo.cantidades[moneda.indice] = monedasNecesarias;
					nodo.cota += monedasNecesarias;
				}
				else
				{
					// Si no hay suficientes, se cogen todas y se comprueba la siguiente moneda.
					nodo.total += (moneda.cantidad) * moneda.valor;
					nodo.cantidades[moneda.indice] = moneda.cantidad;
					nodo.cota += moneda.cantidad;
				}
			}

			nodo.usadas = nodo.cota;

			return nodo;
		}*/

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

			int nodos = 0, podas = 0, cambios = 0;

			while (!nodosVivos.isEmpty())
			{
				Nodo nodoActual = nodosVivos.poll();

				//System.out.println(nodoActual);
				nodos++;

				if (nodoActual.esCompleto())
				{
					if (nodoActual.esFactible())// && (mejorSolucion == null || nodoActual.esAceptable(mejorSolucion)))
					{
						//System.out.println(nodoActual);
						cambios++;
						mejorSolucion = nodoActual;
						return nodoActual;
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
						else
						{
							podas++;
						}
					}
				}
			}

			/*System.out.println("Nodos: " + nodos);
			System.out.println("Podas: " + podas);
			System.out.println("Cambios: " + cambios);*/

			return mejorSolucion;
		}

		public static int M;
		public static int N;
		public static int[] c;
		public static int[] v;
		//public static ArrayList<Moneda> monedas;
		public static int resultadoExacto;

		public int k;
		public int[] cantidades;
		public int usadas;
		public int total;

		public int cota;

		/**
		 * Constructor por defecto.
		 * Inicializa el nodo con sus valores por defecto.
		 */
		public Nodo()
		{
			k = 0;
			cantidades = new int[N];
			usadas = 0;
			total = 0;
			cota = 0;
			calcularCotas();
		}

		/**
		 * Constructor de copia.
		 */
		public Nodo(Nodo nodo)
		{
			k = nodo.k;
			cantidades = nodo.cantidades.clone();
			usadas = nodo.usadas;
			total = nodo.total;
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
			ArrayList<Nodo> hijos = new ArrayList<Nodo>(c[k] + 1);

			// Comprobamos que esta solución parcial no sea ya una solución factible.
			if (total < M)
			{
				for (int i = 0; i <= c[k]; i++)
				{
					if (total+v[k]*i <= M)
					{
						Nodo hijo = new Nodo(this);
						hijo.usadas += i;
						hijo.cantidades[k] += i;
						hijo.total += v[k]*i;
						hijo.k++;
						hijo.calcularCotas();
						hijos.add(hijo);
					}
				}
			}
			else
			{
				// Si ya es factible, terminamos la solución y la reinsertamos.
				k = N;
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
			return total == M && usadas == resultadoExacto;
			//return k == N && total == M;
		}

		/**
		 * Comprueba si el nodo que invocó el método es potencialmente mejor que
		 * otro nodo de referencia.
		 * @param nodo Nodo con el que se va a comprobar si el nodo es aceptable.
		 * @return Devuelve verdadero si el nodo invocante es mejor nodo.
		 */
		public boolean esAceptable(Nodo nodo)
		{
			//return cota < nodo.cota;
			//return usadas <= resultadoExacto;
			return cota <= resultadoExacto;
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
			//return - usadas + nodo.usadas;
			//return k - nodo.k;
			//return cota - nodo.cota;
			return - total + nodo.total;
			//return + Math.abs(M - total) - Math.abs(M - nodo.total);
		}

		public void calcularCotas()
		{
			int cota1, cota2, cota3;

			/*cota1 = usadas;

			int totalAux = total;
			Iterator<Moneda> it = monedas.iterator();
			while (it.hasNext() && totalAux < M)
			{
				Moneda moneda = it.next();

				// Monedas de este valor que me hacen falta para llegar a M.
				int monedasNecesarias = (int) Math.ceil((M - totalAux)/(double) moneda.valor);

				// Se comprueba si hay suficientes monedas de este valor.
				if (moneda.cantidad - cantidades[moneda.indice] >= monedasNecesarias)
				{
					totalAux = M;
					cota1 += monedasNecesarias;
				}
				else
				{
					// Si no hay suficientes, se cogen todas y se comprueba la siguiente moneda.
					totalAux += (moneda.cantidad - cantidades[moneda.indice]) * moneda.valor;
					cota1 += moneda.cantidad - cantidades[moneda.indice];
				}
			}*/

			cota2 = usadas;
			int totalAux = total;

			// Recorremos los tipos de monedas que faltan por analizar y ordenamos de mayor a menor.
			ArrayList<Moneda> monedasRestantes = new ArrayList<Moneda>(N - k);
			for (int i = k; i < N; i++)
			{
				Moneda moneda = new Moneda();
				moneda.cantidad = c[i];
				moneda.valor = v[i];
				moneda.indice = i;

				monedasRestantes.add(moneda);
			}
			Collections.sort(monedasRestantes);

			// Recorremos las monedas de mayor a menor, cogiendo todas las que haya hasta alcanzar totalAux.
			Iterator<Moneda> it = monedasRestantes.iterator();
			while (it.hasNext() && totalAux < M)
			{
				Moneda moneda = it.next();

				// Monedas de este valor que me hacen falta para llegar a M.
				int monedasNecesarias = (int) Math.ceil((M - totalAux)/(double) moneda.valor);

				// Se comprueba si hay suficientes monedas de este valor.
				if (moneda.cantidad - cantidades[moneda.indice] >= monedasNecesarias)
				{
					totalAux = M;
					cota2 += monedasNecesarias;
				}
				else
				{
					// Si no hay suficientes, se cogen todas y se comprueba la siguiente moneda.
					totalAux += (moneda.cantidad - cantidades[moneda.indice]) * moneda.valor;
					cota2 += moneda.cantidad - cantidades[moneda.indice];
				}
			}

			/*int maximaMoneda = 0;
			for (int i = 0; i < N; i++)
				maximaMoneda = Math.max(maximaMoneda, v[i]);
			cota3 = usadas + (int) Math.ceil((M - total)/(double) maximaMoneda);*/

			//System.out.println(cota2 + " " + cota3);

			//cota = Math.max(cota1, Math.max(cota2, cota3));
			cota = cota2;
		}

		/**
		 * Obtiene la solución en formato alfanumérico.
		 * @return un String con la asignación de tareas a cada máquina y su valor de cota
		 */
		@Override
		public String toString()
		{
			String aux = "(" + usadas + ") {";

			boolean primero = true;
			for (int i = 0; i < N; i++)
			{
				if (primero)
				{
					aux += cantidades[i];
					primero = false;
				}
				else
				{
					aux += ", " + cantidades[i];
				}
			}
			aux += "}";

			int suma = 0;
			for (int i = 0; i < N; i++)
			{
				suma += cantidades[i]*v[i];
			}
			aux += " [" + suma + "]";


			return aux;
		}
	}
}

class P84Aux
{
	private static int M;
	private static int N;
	private static int[] v;
	private static int[] c;
	
	private static int[][] tabla;
	private static boolean[][] estado;

	public static int best(String data)
	{
		// Se extraen los datos.
		String[] dataAux = data.split("\\p{Space}+");
		M = new Integer(dataAux[0]);
		N = new Integer((dataAux.length - 1)/2);
		v = new int[N];
		c = new int[N];
		for (int i = 0; i < N; i++)
		{
			v[i] = new Integer(dataAux[2*i+1]);
			c[i] = new Integer(dataAux[2*i+2]);
		}

		// Salida por pantalla del problema.
		/*System.out.println();
		for (int i = 0; i < Nodo.N; i++)
		{
			System.out.println("Moneda: " + Nodo.monedas.get(i).valor);
		}
		System.out.println("M: " + Nodo.M);
		System.out.println("N: " + Nodo.N);
		System.out.print("v: ");
		for (int i = 0; i < Nodo.N; i++)
		{
			System.out.print(Nodo.v[i] + " ");
		}
		System.out.println();
		System.out.print("c: ");
		for (int i = 0; i < Nodo.N; i++)
		{
			System.out.print(Nodo.c[i] + " ");
		}
		System.out.println();*/

		// Se inicializan las matrices de PD.
		tabla = new int[N+1][M+1];
		estado = new boolean[N+1][M+1];

		return best(0, M);
	}

	private static int best(int k, int m)
	{
		if (k == N)
		{
			if (m == 0)
			{
				return 0;
			}
			else
				return Integer.MAX_VALUE;
		}
		else
		{
			int minimo = Integer.MAX_VALUE;

			for (int i = 0; i <= c[k]; i++)
			{
				if (m - v[k]*i >= 0)
				{
					int aux;
					if (estado[k + 1][m - v[k] * i])
					{
						aux = tabla[k + 1][m - v[k] * i];
					}
					else
					{
						aux = tabla[k + 1][m - v[k] * i] = best(k + 1, m - v[k] * i);
						estado[k + 1][m - v[k] * i] = true;
					}
					if (Integer.MAX_VALUE - aux >= i)
					{
						aux += i;
					}

					minimo = Math.min(minimo, aux);
				}
			}

			return minimo;
		}
	}
}