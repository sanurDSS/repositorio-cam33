package AA;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Iterator;

public class P03
{
	public static int[] tiemposM1;
	public static int[] tiemposM2;
	
	public static Integer[] bestSolution(String[] data)
	{
		// Declaración de variables.
		String[] tiemposM1Aux = data[0].split("\\p{Space}+");
		String[] tiemposM2Aux = data[1].split("\\p{Space}+");
		int N = tiemposM1Aux.length;
		
		// Convierte de String a int.
		tiemposM1 = new int[N];
		tiemposM2 = new int[N];
		for (int i = 0; i < N; i++)
		{
			tiemposM1[i] = new Integer(tiemposM1Aux[i]);
			tiemposM2[i] = new Integer(tiemposM2Aux[i]);
		}

		Nodo.N = N;
		Nodo.tiemposEjecucionM1 = tiemposM1;
		Nodo.tiemposEjecucionM2 = tiemposM2;

		// Obtenemos una solución voraz.
		Nodo solucionVoraz = Nodo.voraz();
		System.out.println("Solución voraz  (" + solucionVoraz.cotaOptimista + "): " + solucionVoraz.toString());

		// Partiendo de la solución voraz, podamos el árbol de soluciones para obtener una solución óptima.
		Nodo mejorSolucion = Nodo.RyP(solucionVoraz);
		System.out.println("Solución óptima (" + mejorSolucion.cotaOptimista + "): " + mejorSolucion.toString());

		return mejorSolucion.estado;
	}
}

class Nodo implements Comparable<Nodo>
{
	/**
	 * Obtiene una solución cercana a la solución óptima aplicando un algoritmo voraz.
	 * @return Devuelve una solución buena.
	 */
	public static Nodo voraz()
	{
		int tiempoEjecucionM1 = 0;
		int tiempoEjecucionM2 = 0;

		// Recorremos todas las tareas asignándoselas a la máquina menos saturada.
		Nodo solucion = new Nodo();
		for (int i = 0; i < N; i++)
		{
			if (Math.abs(tiempoEjecucionM1 + tiemposEjecucionM1[i] - tiempoEjecucionM2) < Math.abs(tiempoEjecucionM2 + tiemposEjecucionM2[i] - tiempoEjecucionM1))
			{
				solucion.estado[i] = 1;
				tiempoEjecucionM1 += tiemposEjecucionM1[i];
			}
			else
			{
				solucion.estado[i] = 2;
				tiempoEjecucionM2 += tiemposEjecucionM2[i];
			}
		}
		solucion.k = N;
		if (tiempoEjecucionM1 > tiempoEjecucionM2)
			solucion.cotaOptimista = solucion.cotaPesimista = tiempoEjecucionM1;
		else
			solucion.cotaOptimista = solucion.cotaPesimista = tiempoEjecucionM2;

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

		while (!nodosVivos.isEmpty())
		{
			Nodo nodoActual = nodosVivos.poll();

			if (nodoActual.esCompleto())
			{
				if (nodoActual.esAceptable(mejorSolucion))
				{
					mejorSolucion = nodoActual;
				}
			}
			else
			{
				Iterator<Nodo> i = nodoActual.generarHijos().iterator();
				while (i.hasNext())
				{
					Nodo hijo = i.next();
					if (hijo.esAceptable(mejorSolucion))
					{
						nodosVivos.add(hijo);
					}
				}
			}
		}

		return mejorSolucion;
	}

	public static int N = 0;
	public static int[] tiemposEjecucionM1 = new int[0];
	public static int[] tiemposEjecucionM2 = new int[0];

	public int k;
	public Integer[] estado;

	/**
	 * El valor de la solución no puede ser mayor que la cota optimista.
	 * Si el nodo es completo, el valor y la cota optimista coinciden.
	 */
	public int cotaOptimista;

	/**
	 * El valor de la solución no puede ser menor que la cota pesimista.
	 * Si el nodo es completo, el valor y la cota pesimista coinciden.
	 */
	public int cotaPesimista;

	/**
	 * Constructor por defecto.
	 * Inicializa el nodo con sus valores por defecto.
	 */
	public Nodo()
	{
		k = 0;
		estado = new Integer[N];
		calcularCotas();
		//cotaOptimista = Integer.MAX_VALUE;
		//cotaPesimista = 0;
	}

	/**
	 * Constructor de copia.
	 */
	public Nodo(Nodo nodo)
	{
		k = nodo.k;
		estado = nodo.estado.clone();
		cotaOptimista = nodo.cotaOptimista;
		cotaPesimista = nodo.cotaPesimista;
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
	 * @return Devuelve una lista con los nodos hijos del nodo.
	 */
	public ArrayList<Nodo> generarHijos()
	{
		ArrayList<Nodo> hijos = new ArrayList<Nodo>(2);

		Nodo hijo1 = new Nodo(this);
		hijo1.estado[hijo1.k++] = 1;
		hijo1.calcularCotas();
		hijos.add(hijo1);

		Nodo hijo2 = new Nodo(this);
		hijo2.estado[hijo2.k++] = 2;
		hijo2.calcularCotas();
		hijos.add(hijo2);

		return hijos;
	}

	/**
	 * Comprueba si el nodo que invocó el método es potencialmente mejor que
	 * otro nodo de referencia.
	 * @param nodo Nodo con el que se va a comprobar si el nodo es aceptable.
	 * @return Devuelve verdadero si el nodo invocante es mejor nodo.
	 */
	public boolean esAceptable(Nodo nodo)
	{
		return cotaPesimista < nodo.cotaPesimista;
	}

	/**
	 * Compara dos nodos según sus cota optimista y su cota pesimista.
	 * @param nodo Nodo con el que se va a comparar.
	 * @return Devuelve verdadero
	 */
	public int compareTo(Nodo nodo)
	{
		//return cotaOptimista - nodo.cotaOptimista;
		//return -k + nodo.k; recorriendo en profundidad tarda 2 segundos mas
		//return k - nodo.k; recorriendo por niveles no acaba
		return cotaPesimista - nodo.cotaPesimista;
	}

	public void calcularCotas()
	{
		// Calculamos la costa pesimista.
		int tiempoEjecucionM1 = 0;
		int tiempoEjecucionM2 = 0;

		for (int i = 0; i < k; i++)
		{
			if (estado[i] == 1)
			{
				tiempoEjecucionM1 += tiemposEjecucionM1[i];
			}
			else
			{
				tiempoEjecucionM2 += tiemposEjecucionM2[i];
			}
		}

		if (k < N)
		{
			int sumatorio = 0;
			for (int i = k; i < N; i++)
			{
				sumatorio += Math.min(tiemposEjecucionM1[i], tiemposEjecucionM2[i]);
			}

			sumatorio -= Math.abs(tiempoEjecucionM1 - tiempoEjecucionM2);
			tiempoEjecucionM1 = tiempoEjecucionM2 = Math.max(tiempoEjecucionM1, tiempoEjecucionM2);

			tiempoEjecucionM1 += sumatorio/2;
			tiempoEjecucionM2 += Math.ceil(sumatorio/2.0);
		}

		if (tiempoEjecucionM1 > tiempoEjecucionM2)
			cotaPesimista = tiempoEjecucionM1;
		else
			cotaPesimista = tiempoEjecucionM2;

		// Calculamos la costa optimista.
		tiempoEjecucionM1 = 0;
		tiempoEjecucionM2 = 0;

		for (int i = 0; i < k; i++)
		{
			if (estado[i] == 1)
			{
				tiempoEjecucionM1 += tiemposEjecucionM1[i];
			}
			else
			{
				tiempoEjecucionM2 += tiemposEjecucionM2[i];
			}
		}

		for (int i = k; i < N; i++)
		{
			if (Math.abs(tiempoEjecucionM1 + tiemposEjecucionM1[i] - tiempoEjecucionM2) < Math.abs(tiempoEjecucionM2 + tiemposEjecucionM2[i] - tiempoEjecucionM1))
			{
				estado[i] = 1;
				tiempoEjecucionM1 += tiemposEjecucionM1[i];
			}
			else
			{
				estado[i] = 2;
				tiempoEjecucionM2 += tiemposEjecucionM2[i];
			}
		}

		if (tiempoEjecucionM1 > tiempoEjecucionM2)
			cotaOptimista = tiempoEjecucionM1;
		else
			cotaOptimista = tiempoEjecucionM2;
	}

	/**
	 * Obtiene la solución en formato alfanumérico.
	 * @return un String con la asignación de tareas a cada máquina y su valor de cota
	 */
	@Override
	public String toString()
	{
		String aux = cotaPesimista + " " + cotaOptimista + " {";
		for (int i = 0; i < N; i++)
		{
			if (i > 0)
			{
				aux += ", ";
			}
			if (estado[i] != null)
				aux += estado[i];
			else
				aux += "-";
		}
		aux += "}";

		return aux;
	}
}
