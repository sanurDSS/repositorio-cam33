package AA;

import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Iterator;

public class P06
{
	public Integer[] bestSolution(String[] data)
	{
		// Se procesan los datos de entrada.
		String[] data0 = data[0].split("\\p{Space}+");
		Nodo.M = new Integer(data0[0]);
		Nodo.P = new Integer(data0[1]);
		String[] data1 = data[1].split("\\p{Space}+");
		String[] data2 = data[2].split("\\p{Space}+");
		String[] data3 = data[3].split("\\p{Space}+");
		Nodo.N = new Integer(data1.length);
		Nodo.m = new int[Nodo.N];
		Nodo.p = new int[Nodo.N];
		Nodo.c = new int[Nodo.N];
		for (int i = 0; i < Nodo.N; i++)
		{
			Nodo.m[i] = new Integer(data1[i]);
			Nodo.p[i] = new Integer(data2[i]);
			Nodo.c[i] = new Integer(data3[i]);
		}

		// Se hace un recuento de monedas de manera que, para cada posición del
		// vector de monedas, se conozca cuál es la mayor moneda de todas las
		// que quedan.
		Nodo.minMoney = new int[Nodo.N];
		Nodo.maxProteins = new int[Nodo.N];
		Nodo.minCalories = new int[Nodo.N];
		for (int j = Nodo.N - 1; j >= 0; j--)
		{
			Nodo.minMoney[j] = Integer.MAX_VALUE;
			Nodo.maxProteins[j] = 0;
			Nodo.minCalories[j] = Integer.MAX_VALUE;
			for (int i = j; i < Nodo.N; i++)
			{
				Nodo.minMoney[j] = Math.min(Nodo.m[i], Nodo.minMoney[j]);
				Nodo.maxProteins[j] = Math.max(Nodo.p[i], Nodo.maxProteins[j]);
				Nodo.minCalories[j] = Math.min(Nodo.c[i], Nodo.minCalories[j]);
			}
		}

		// Imprime los datos para comprobar que son correctos.
		/*System.out.println(Nodo.M + " " + Nodo.P);
		for (int i = 0; i < Nodo.N; i++)
			System.out.print(Nodo.m[i] + " ");
		System.out.println();
		for (int i = 0; i < Nodo.N; i++)
			System.out.print(Nodo.p[i] + " ");
		System.out.println();
		for (int i = 0; i < Nodo.N; i++)
			System.out.print(Nodo.c[i] + " ");
		System.out.println();*/

		// Calculamos la solución con un algoritmo voraz y después con ramificación y poda.
		Nodo voraz = Nodo.voraz();
		Nodo solucion = Nodo.RyP(voraz);
		int cantidad = 0;
		for (int i = 0; i < Nodo.N; i++)
			if (solucion.estado[i])
				cantidad++;

		// Imprimimos las soluciones obtenidas.
		//System.out.println(voraz.toString());
		//System.out.println(solucion.toString());

		// Convertimos la solución al formato de salida requerido.
		Integer[] resultado = new Integer[cantidad];
		int contador = 0;
		for (int i = 0; i < Nodo.N; i++)
		{
			if (solucion.estado[i])
				resultado[contador++] = i+1;
		}

		return resultado;
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
			solucion.k = N;

			boolean factible = false;
			for (int i = 0; i < N; i++)
			{
				if (solucion.money + m[i] <= M && !factible)
				{
					solucion.money += m[i];
					solucion.proteins += p[i];
					solucion.calories += c[i];
					solucion.estado[i] = true;
				}
				else
				{
					solucion.estado[i] = false;
				}

				if (solucion.money <= M && solucion.proteins >= P)
					factible = true;
			}
			solucion.cota = solucion.calories;

			if (factible)
			{
				return solucion;
			}
			else
			{
				return null;
			}
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

			//int nodos, podas, cambios;
			//nodos = podas = cambios = 0;

			while (!nodosVivos.isEmpty())
			{
				Nodo nodoActual = nodosVivos.poll();

				//nodos++;
				//System.out.println("-> " + nodoActual.toString());

				if (nodoActual.esCompleto())
				{
					if (nodoActual.esFactible() && (mejorSolucion == null || nodoActual.esAceptable(mejorSolucion)))
					{
						//System.out.println("-> " + nodoActual.toString());
						//cambios++;
						mejorSolucion = nodoActual;
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
						else
						{
							//podas++;
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
		public static int P;
		public static int N;
		public static int[] m;
		public static int[] p;
		public static int[] c;
		public static int[] minMoney;
		public static int[] maxProteins;
		public static int[] minCalories;

		public int k;
		public boolean[] estado;
		public int money;
		public int proteins;
		public int calories;

		public int cota;

		/**
		 * Constructor por defecto.
		 * Inicializa el nodo con sus valores por defecto.
		 */
		public Nodo()
		{
			k = 0;
			money = 0;
			proteins = 0;
			calories = 0;
			cota = 0;
			estado = new boolean[N];
			calcularCotas();
		}

		/**
		 * Constructor de copia.
		 */
		public Nodo(Nodo nodo)
		{
			k = nodo.k;
			money = nodo.money;
			proteins = nodo.proteins;
			calories = nodo.calories;
			cota = nodo.cota;
			estado = nodo.estado.clone();
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

			if (!esFactible())
			{
				// Si no es factible, se evalua el siguiente.
				Nodo hijo2 = new Nodo(this);
				hijo2.estado[hijo2.k++] = false;
				hijo2.calcularCotas();
				hijos.add(hijo2);

				// Comprobamos si es posible sumar el dinero sobrepasar el límite.
				if (money + m[k] <= M)
				{
					Nodo hijo1 = new Nodo(this);
					hijo1.money += m[hijo1.k];
					hijo1.proteins += p[hijo1.k];
					hijo1.calories += c[hijo1.k];
					hijo1.estado[hijo1.k++] = true;
					hijo1.calcularCotas();
					hijos.add(hijo1);
				}
			}
			else
			{
				// Si ya es factible, reencolamos el nodo ya solucionado.
				for (int i = k; i < N; i++)
					estado[i] = false;
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
			return money <= M && proteins >= P;
		}

		/**
		 * Comprueba si el nodo que invocó el método es potencialmente mejor que
		 * otro nodo de referencia.
		 * @param nodo Nodo con el que se va a comprobar si el nodo es aceptable.
		 * @return Devuelve verdadero si el nodo invocante es mejor nodo.
		 */
		public boolean esAceptable(Nodo nodo)
		{
			return cota < nodo.cota;
			//return calories < nodo.calories;
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
			//return calories - nodo.calories;
			//return cota - nodo.cota;
		}

		public void calcularCotas()
		{
			// Se calcula un alimento ideal, con muchas proteinas, barato y con pocas calorías.
			/*int minMoney = Integer.MAX_VALUE;
			int maxProteins = 0;
			int minCalories = Integer.MAX_VALUE;
			for (int i = k; i < N; i++)
			{
				minMoney = Math.min(m[i], minMoney);
				maxProteins = Math.max(p[i], maxProteins);
				minCalories = Math.min(c[i], minCalories);
			}*/
			// Ahora ese alimento ideal se calcula al principio para cada nivel de construcción k de la solución.
			// Así no se repite el cálculo para un mismo valor de k.

			// Suponemos que cogemos ese alimento ideal hasta cumplir los requisitos de la dieta.
			int auxMoney = money;
			int auxProteins = proteins;
			int auxCalories = calories;
			for (int i = k; i < N && auxProteins < P; i++)
			{
				auxMoney += minMoney[k];
				auxProteins += maxProteins[k];
				auxCalories += minCalories[k];
			}

			// Si incluso así, no alcanzamos los requisitos, es que esta dieta parcial no nos sirve.
			if (auxMoney <= M && auxProteins >= P)
			{
				cota = auxCalories;
			}
			else
			{
				cota = Integer.MAX_VALUE;
			}
		}

		/**
		 * Obtiene la solución en formato alfanumérico.
		 * @return un String con la asignación de tareas a cada máquina y su valor de cota
		 */
		@Override
		public String toString()
		{
			int caloriesAux = 0;
			for (int i = 0; i < k; i++)
			{
				if (estado[i])
					caloriesAux += c[i];
			}

			String aux = String.format("%1$-6s", (caloriesAux));

			aux += "(";
			for (int i = 0; i < k; i++)
			{
				if (estado[i])
				{
					aux += "1 ";
				}
				else
				{
					aux += "0 ";
				}
			}

			for (int i = k; i < N; i++)
			{
				aux += "- ";
			}

			aux = aux.trim();
			aux += ") {";

			boolean primero = true;
			for (int i = 0; i < k; i++)
			{
				if (estado[i])
				{
					if (primero)
					{
						aux += (i+1);
						primero = false;
					}
					else
					{
						aux += ", " + (i+1);
					}
				}
			}
			aux += "}";

			return aux;
		}
	}

}