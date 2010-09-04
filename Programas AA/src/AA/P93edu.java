package AA;

import java.util.ArrayList;
import java.util.HashMap;

public class P93edu
{

	int Peso;
	int N;
	//int indice;
	ArrayList<Integer> Pesos;
	HashMap<Integer, HashMap> TablaIndice;
	HashMap<Integer, Integer> TablaPeso;

	////////////////
	// Clase Nodo //////////////////////////////////////////////////
	////////////////
	public class Nodo
	{

		int valor;
		int mejor_opcion;
		int n;
		int p;

		public Nodo()
		{
			mejor_opcion = -9999;
			n = -1;
			p = -9999;
			valor = 9999;
		}

		public Nodo(Nodo nodo)
		{
			mejor_opcion = nodo.mejor_opcion;
			valor = nodo.valor;
			n = nodo.n;
			p = nodo.p;
		}
	}

	/////////////////////////////////////////////////////////////////
	private void init(String datos)
	{
		String[] d = datos.split("\\p{Space}+");

		TablaIndice = new HashMap<Integer, HashMap>();
		Peso = new Integer(d[0]);
		N = d.length - 1;
		Pesos = new ArrayList<Integer>(N);

		for (int i = 1; i < d.length; i++)
		{
			Pesos.add(new Integer(d[i]));
		}
	}

	private Nodo best(int n, int p)
	{
		Nodo resultado = new Nodo();
		boolean existeValor = false;

		if (p == Peso)
		{
			resultado.mejor_opcion = -9999;
			resultado.n = n;
			resultado.p = p;
			resultado.valor = 0;

			if (!TablaIndice.containsKey(n))
			{
				TablaIndice.put(n, new HashMap<Integer, Nodo>());
			}
			TablaIndice.get(n).put(p, resultado);
		}
		else if (n == 0)
		{
			resultado.valor = 9999;
		}
		else
		{
			if (TablaIndice.containsKey(n))
			{
				if (TablaIndice.get(n).containsKey(p))
				{
					resultado = new Nodo((Nodo) TablaIndice.get(n).get(p));
					//debug(resultado);
					//System.out.println("Cambio padre a " + padre);System.out.println("");
					existeValor = true;
				}
			}

			if (!existeValor)
			{
				Nodo resultado1 = best(n - 1, p + Pesos.get(N - n));
				Nodo resultado2 = best(n - 1, p - Pesos.get(N - n));
				Nodo resultado3 = best(n - 1, p);

				if (resultado1.valor < resultado2.valor && resultado1.valor < resultado3.valor)
				{
					resultado.valor = resultado1.valor + 1;
					resultado.mejor_opcion = Pesos.get(N - n);
				}
				else if (resultado2.valor < resultado3.valor)
				{
					resultado.valor = resultado2.valor + 1;
					resultado.mejor_opcion = (-1) * Pesos.get(N - n);
				}
				else
				{
					resultado.valor = resultado3.valor;
					resultado.mejor_opcion = 0;
				}

				resultado.n = n;
				resultado.p = p;

				if (!TablaIndice.containsKey(n))
				{
					TablaIndice.put(n, new HashMap<Integer, Nodo>());
				}
				TablaIndice.get(n).put(p, resultado);

				//System.out.println ("( " + n + ", " + p + " )");
			}
		}

		//debug(resultado);
		return (resultado);
	}

	/**
	 * @param data Array of strings containing N integer weights (data[0]),
	 * N integer values (data[1]) and the integer knapsack capacity (data[2])
	 * @return best value with items weighting not more than M
	 */
	public ArrayList<Integer> bestSolution(String datos)
	{
		ArrayList<Integer> resultado = new ArrayList<Integer>();
		Nodo solucion = new Nodo();

		// Extraemos los valores de las cadenas
		/////////////////////////////////////////
		init(datos);

		// Obtenemos el mejor
		/////////////////////////////////////////
		solucion = best(N, 0);
		//System.out.println ("SOLUCION ; "); debug(solucion);

		// Obtenemos el array en función del último valor
		///////////////////////////////////////////////////
		int valor = 0;
		Nodo n = new Nodo();

		n = (Nodo) TablaIndice.get(N).get(valor); // Cogemos el nodo inicial

		for (int i = N - 1; i >= 0 && n.mejor_opcion != -9999; i--)
		{
			//debug (n);

			if (n.mejor_opcion != 0)
			{
				resultado.add(n.mejor_opcion); // Añadimos el siguiente paso en el camino
			}
			valor = valor + n.mejor_opcion;
			n = (Nodo) TablaIndice.get(i).get(valor);
		}

		return (resultado);
	}

	public static void main(String args) // Cuidado por si hay que recibir en el main como un vector de Strings
	{
		P93edu p = new P93edu();
		System.out.println(p.bestSolution(args));
	}
}
