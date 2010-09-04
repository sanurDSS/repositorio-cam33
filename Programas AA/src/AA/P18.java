package AA;

import java.util.HashMap;
import java.util.Collection;

public class P18
{
	private int D;
	private int N;
	private int[] n;

	private int maximoActual;
	private HashMap4<Integer, Integer, Integer, Integer, Integer> tabla;

	public int best(String data)
	{
		String[] dataAux = data.split("\\p{Space}+");
		N = new Integer(dataAux[0]);
		D = new Integer(dataAux[3]);
		n = new int[N];
		for (int i = 0; i < N; i++)
		{
			n[i] = new Integer(dataAux[i + 4]);
		}

		maximoActual = Integer.MIN_VALUE;
		tabla = new HashMap4<Integer, Integer, Integer, Integer, Integer>();

		return best(0, new Integer(dataAux[1]), new Integer(dataAux[2]), 0, 0);
	}

	int podas = 0;


	// Recursivo con ramificación y poda (se añade un parámetro más) y programación dinámica (pero sin tener en cuenta ese parámetro).
	private int best(int k, int T1, int T2, int diferencia, int total)
	{
		// Si la llamada recursiva no es potencialmente mejor que la solución obtenida hasta ahora, se descarta.
		int suma = 0;
		for (int i = k; i < N; i++)
		{
			if (n[i] <= T1 || n[i] <= T2)
				suma += n[i];
		}
		int cota = total + Math.min(T1 + T2, suma);
		if (cota <= maximoActual || Math.abs(diferencia) > D + (N - k))
		{
			//System.out.println(diferencia + " " + (N - k))
			/*podas++;

			if (podas > 10000 && podas % 10000 == 0)
			{
				System.out.println("podando: " + podas);
			}*/
			return Integer.MIN_VALUE;
		}

		if (k == N)
		{
			if (Math.abs(diferencia) <= D)
			{
				if (total > maximoActual)
				{
					maximoActual = total;
				}
				return total;
			}
			else
			{
				return Integer.MIN_VALUE;
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
				caso1 = best(k + 1, T1, T2, diferencia, total);
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
					caso2 = best(k + 1, T1 - n[k], T2, diferencia + 1, total + n[k]);
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
					caso3 = best(k + 1, T1, T2 - n[k], diferencia - 1, total + n[k]);
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