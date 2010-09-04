package AA;

import java.util.HashMap;
import java.util.Collection;

public class P09
{
	private int[] c;
	private int N;
	private int M;
	private HashMap2<Integer, Integer, Integer> tabla;

	public int best(String[] data)
	{
		M = new Integer(data[0]);
		String[] dataAux = data[1].split("\\p{Space}+");
		N = dataAux.length;
		c = new int[N];
		for (int i = 0; i < N; i++)
		{
			c[i] = new Integer(dataAux[i]);
		}

		tabla = new HashMap2<Integer, Integer, Integer>();

		return bestPD(0, M);
	}

	private int best(int k, int litros)
	{
		if (k == N)
		{
			if (litros <= 0)
				return -litros;
			else
				return Integer.MAX_VALUE;
		}
		else
		{
			return Math.min(best(k + 1, litros - c[k]), best(k + 1, litros));
		}
	}

	private int bestPD(int k, int litros)
	{
		if (k == N)
		{
			if (litros <= 0)
				return -litros;
			else
				return Integer.MAX_VALUE;
		}
		else
		{
			int caso1;
			if (tabla.containsKey(k + 1, litros - c[k]))
			{
				caso1 = tabla.get(k + 1, litros - c[k]);
			}
			else
			{
				caso1 = bestPD(k + 1, litros - c[k]);
				tabla.put(k + 1, litros - c[k], caso1);
			}

			int caso2;
			if (tabla.containsKey(k + 1, litros))
			{
				caso2 = tabla.get(k + 1, litros);
			}
			else
			{
				caso2 = bestPD(k + 1, litros);
				tabla.put(k + 1, litros, caso2);
			}
			
			return Math.min(caso1, caso2);
		}
	}

	static class HashMap2<K1, K2, V>
	{
		private HashMap<Par, V> tabla;

		public HashMap2()
		{
			tabla = new HashMap<Par, V>();
		}

		public HashMap2(int initialCapacity)
		{
			tabla = new HashMap<Par, V>(initialCapacity);
		}

		public HashMap2(int initialCapacity, float loadFactor)
		{
			tabla = new HashMap<Par, V>(initialCapacity, loadFactor);
		}

		public V get(K1 key1, K2 key2)
		{
			return tabla.get(new Par(key1, key2));
		}

		public V remove(K1 key1, K2 key2)
		{
			return tabla.remove(new Par(key1, key2));
		}

		public V put(K1 key1, K2 key2, V value)
		{
			return tabla.put(new Par(key1, key2), value);
		}

		public boolean containsKey(K1 key1, K2 key2)
		{
			return tabla.containsKey(new Par(key1, key2));
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
		public HashMap2<K1, K2, V> clone()
		{
			HashMap2<K1, K2, V> copia = new HashMap2<K1, K2, V>();
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

			public Par(Object objeto1, Object objeto2)
			{
				this.objeto1 = objeto1;
				this.objeto2 = objeto2;
			}

			@Override
			public boolean equals(Object otroPar)
			{
				if (otroPar instanceof Par)
				{
					Par par = (Par) otroPar;
					return objeto1.equals(par.objeto1) && objeto2.equals(par.objeto2);
				}
				return false;
			}

			@Override
			public int hashCode()
			{
				int resultado = 17 * 37 + objeto1.hashCode();
				resultado = 37 * resultado + objeto2.hashCode();
				return resultado;
			}

			@Override
			public String toString()
			{
				return "<" + objeto1.toString() + ", " + objeto2.toString() + ">";
			}
		}
	}
}