package AA;

import java.util.HashMap;

public class P02
{
	/**
	 * N objetos.
	 * Vectores con los valores y pesos de los objetos.
	 */
	private int N;
	private int[] v;
	private int[] p;

	/**
	 * M mochilas.
	 * Vector con la capacidad de cada mochila.
	 */
	private int M;
	private int[] m;

	private int mejor;

	/**
	 * Resuelve el problema de la mochila (con múltiples mochilas).
	 * @param data data[0]: valores de los objetos separados por un espacio, data[1]: pesos de los objetos, data[2]: capacidades de las mochilas separadas por un espacio
	 * @return el máximo valor que podrían alcanzar los objetos de las mochilas
	 * @return
	 */
	public int bestChoice(String[] data)
	{
		String[] vAux = data[0].split("\\p{Space}+");
		N = vAux.length;
		v = new int[N];
		for (int i = 0; i < N; i++)
			v[i] = new Integer(vAux[i]);

		String[] pesosAux = data[1].split("\\p{Space}+");
		p = new int[N];
		for (int i = 0; i < N; i++)
			p[i] = new Integer(pesosAux[i]);

		String[] mAux = data[2].split("\\p{Space}+");
		M = mAux.length;
		m = new int[M];
		for (int i = 0; i < M; i++)
			m[i] = new Integer(mAux[i]);

		mejor = goodChoice(m);
		return bestChoice(0, m, 0);
	}

	private int goodChoice(int[] m)
	{
		int resultado = 0;
		int[] mAux = m.clone();
		
		for (int i = 0; i < N; i++)
		{
			for (int j = 0; j < M; j++)
			{
				if (p[i] <= mAux[j])
				{
					mAux[j] -= p[i];
					resultado += v[i];
					break;
				}
			}
		}
		System.out.println("resultado: " + resultado);
		return resultado;
	}

	private int bestChoice(int k, int[] m, int t)
	{
		// Calculamos hasta qué valor podría llegar la solución.
		int restante = 0;
		for (int i = k; i < N; i++)
		{
			for (int j = 0; j < M; j++)
			{
				if (p[i] <= m[j])
				{
					restante += v[i];
				}
			}
		}

		// Si no es potencialmente mejor que la solución actual, la descartamos.
		if (t + restante <= mejor)
		{
			return mejor;
		}

		// Caso base (se ha encontrado una solución completa).
		if (k == N)
		{
			// Si la solución encontrada es mejor que la anterior, se sustituye.
			if (t > mejor)
			{
				mejor = t;
			}
			return mejor;
		}
		else
		{
			// Calculamos el valor suponiendo que se desecha el objeto.
			int maximo = bestChoice(k + 1, m.clone(), t);

			// Calculamos el valor suponiendo que metemos el objeto en cada mochila en la que quepa.
			for (int i = 0; i < M; i++)
			{
				if (p[k] <= m[i])
				{
					int[] mAux = m.clone();
					mAux[i] -= p[k];
					maximo = Math.max(maximo, bestChoice(k + 1, mAux, t + v[k]));
				}
			}

			// Devolvemos el máximo valor encontrado en todos los casos posibles.
			return maximo;
		}
	}
}

class P02conPD
{
	/**
	 * Vector con los valores de los objetos.
	 */
	private static int[] v;

	/**
	 * Vector con los pesos de los objetos.
	 */
	private static int[] p;

	private static HashMap<KnapsackSet, Integer> estados[];

	/**
	 * Resuelve el problema de la mochila (con múltiples mochilas).
	 * @param data data[0]: valores de los objetos separados por un espacio, data[1]: pesos de los objetos, data[2]: capacidades de las mochilas separadas por un espacio
	 * @return el máximo valor que podrían alcanzar los objetos de las mochilas
	 * @return
	 */
	public static int bestChoice(String[] data)
	{
		String[] vAux = data[0].split("\\p{Space}+");
		int N = vAux.length;
		v = new int[N];
		for (int i = 0; i < N; i++)
			v[i] = new Integer(vAux[i]);

		String[] pesosAux = data[1].split("\\p{Space}+");
		p = new int[N];
		for (int i = 0; i < N; i++)
			p[i] = new Integer(pesosAux[i]);

		String[] mAux = data[2].split("\\p{Space}+");
		int M = mAux.length;
		KnapsackSet ks = new KnapsackSet(M);
		for (int i = 0; i < M; i++)
			ks.set(i, new Integer(mAux[i]));

		estados = new HashMap[N];
		for (int i = 0; i < N; i++)
		{
			estados[i] = new HashMap<KnapsackSet, Integer>();
		}

		return bestChoice(N, ks);
	}

	/**
	 * Método auxiliar para evitar la conversión de String a int en cada llamada.
	 * @param N cantidad de objetos que quedan en la mochila
	 * @param m capacidad restante de cada mochila (es una clase que reúne todas las capacidades de los objetos que un único objeto)
	 * @return la mayor valor que se puede transportar en el conjunto de mochilas
	 */
	private static int bestChoice(int N, KnapsackSet ks)
	{
		// Caso base. Si no quedan elementos, devuelve 0.
		if (N == 0)
		{
			return 0;
		}
		else
		{
			// Calcularemos el valor de los objetos tanto si cogemos el objeto actual o lo desechamos.
			int ifNotSelected = 0;
			int ifSelected = 0;

			// Comprobamos si la pareja (N-1, m) ya está en la tabla para extraerla o calcularla.
			if (estados[N-1].containsKey(ks))
			{
				ifNotSelected = estados[N-1].get(ks);
			}
			else
			{
				ifNotSelected = bestChoice(N-1, ks);
				estados[N-1].put(ks, ifNotSelected);
			}

			// Comprobamos en qué mochila hay que meterlo para obtener un mejor resultado.
			for (int i = 0; i < ks.size(); i++)
			{
				int ifSelectedAux = 0;

				// Comprobamos si cabe en la mochila nº "i".
				if (ks.get(i) >= p[N-1])
				{
					// Copiamos el nodos y restamos el peso del objeto a la capacidad de la mochila nº "i".
					KnapsackSet nodoAux = new KnapsackSet(ks);
					nodoAux.set(i, nodoAux.get(i)-p[N-1]);

					// Comprobamos si la pareja (N-1, nodoAux) ya está en la tabla para extraerla o calcularla.
					if (estados[N-1].containsKey(nodoAux))
					{
						ifSelectedAux = estados[N-1].get(nodoAux) + v[N-1];
					}
					else
					{
						ifSelectedAux = bestChoice(N-1, nodoAux);
						estados[N-1].put(nodoAux, ifSelectedAux);
						ifSelectedAux += v[N-1];
					}
				}

				// Nos quedamos con el mejor resultado hasta ahora.
				if (ifSelectedAux > ifSelected)
					ifSelected = ifSelectedAux;
			}

			// Devolvemos la mejor situación posible: seleccionándolo o ignorándolo.
			if (ifNotSelected > ifSelected)
				return ifNotSelected;
			else
				return ifSelected;
		}
	}
}





























/**
 * Clase auxiliar que representa un conjunto de mochilas con una capacidad distinta cada una de ellas.
 */
class KnapsackSet implements Comparable
{
	/**
	 * Vector interno que almacena cada capacidad de cada mochila.
	 */
	private int[] m;

	/**
	 * Cantidad de mochilas del conjunto.
	 */
	private int tamano;

	/**
	 * Código hash del objeto.
	 * Se recalcula siempre que se modifique el objeto.
	 */
	private int hashCode;

	/**
	 * Constructor por defecto.
	 * @param tamano el tamaño del conjunto de mochilas
	 */
	public KnapsackSet(int tamano)
	{
		this.tamano = tamano;
		m = new int[tamano];
		hashCode = reHashCode();
	}

	/**
	 * Constructor de copia
	 * @param nodo mochila original que va a ser copiada
	 */
	public KnapsackSet(KnapsackSet nodo)
	{
		tamano = nodo.tamano;
		m = nodo.m.clone();
		hashCode = reHashCode();
	}

	/**
	 * Modifica la capacidad de la mochila indicada.
	 * @param i mochila a modificar
	 * @param valor nueva capacidad para la mochila
	 */
	public void set(int i, int valor)
	{
		m[i] = valor;
		hashCode = reHashCode();
	}

	/**
	 * Obtiene la capacidad de la mochila indicada.
	 * @param i número de mochila de la que se quiere conocer su capacidad
	 * @return la capacidad de dicha mochila
	 */
	public int get(int i)
	{
		return m[i];
	}

	/**
	 * Obtiene el tamaño del conjunto de mochilas.
	 * @return un entero con la cantidad de mochilas
	 */
	public int size()
	{
		return tamano;
	}

	/**
	 * Sobreescritura del método de comparación.
	 * Dos conjuntos de mochilas son iguales si tienen exactamente la misma cantidad de mochilas y coinciden sus cantidades.
	 * Si tienen la misma cantidad de mochilas pero distintas capacidades, el conjunto mayor
	 * será el primero que supere al otro en alguna capacidad de alguna mochila.
	 * Si un conjunto tiene más mochilas, será mayor; si no, menor.
	 * @param objeto el objeto con el que se va a comparar
	 * @return 0 si son iguales, 1 si el invocante es mayor, -1 en otro caso
	 */
	public int compareTo(Object objeto)
	{
		if (objeto != null)
		{
			KnapsackSet nodo = (KnapsackSet) objeto;
			if (tamano == nodo.tamano)
			{
				for (int i = 0; i < tamano; i++)
				{
					if (m[i] > nodo.m[i])
						return 1;
					else if (m[i] < nodo.m[i])
						return -1;
				}
				return 0;
			}
			else
			{
				if (tamano > nodo.tamano)
					return 1;
				else
					return -1;
			}
		}
		else
		{
			return 1;
		}
	}

	/**
	 * Sobreescritura del método de igualdad.
	 * Dos conjuntos de mochilas son iguales si tienen exactamente la misma cantidad de mochilas y coinciden sus cantidades.
	 * @param objeto el objeto con el que se va a comparar
	 * @return verdadero si son iguales, falso en otro caso
	 */
	@Override
	public boolean equals(Object objeto)
	{
		return (objeto instanceof KnapsackSet) && compareTo(objeto) == 0;
	}

	/**
	 * Sobreescribe el método que obtiene el hash del objeto.
	 * Concatena todas las capacidades de todas las mochilas y devuelve el hash de esa cadena.
	 * @return un entero que es el código hash del objeto
	 */
	@Override
	public int hashCode()
	{
		return hashCode;
	}

	private int reHashCode()
	{
		String aux = "";
		for (int i = 0; i < tamano; i++)
		{
			aux += m[i];
		}
		return aux.hashCode();
	}
}
