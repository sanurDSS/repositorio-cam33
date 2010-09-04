package AA;

import java.util.HashMap;
import java.util.Collection;

public class P16
{
	private int N;
	private int[] a;
	private HashMap2<Integer, Integer, Integer> tabla;

	public int best(String data)
	{
		String[] dataAux = data.split("\\p{Space}+");
		N = new Integer(dataAux.length);
		a = new int[N];
		for (int i = 0; i < N; i++)
		{
			a[i] = new Integer(dataAux[i]);
		}
		tabla = new HashMap2<Integer, Integer, Integer>();


		return best(0, Integer.MIN_VALUE);
	}

	private int best(int k, int u)
	{
		if (k == N)
		{
			return 0;
		}
		else
		{
			int caso1;
			if (tabla.containsKey(k + 1, u))
			{
				caso1 = tabla.get(k + 1, u);
			}
			else
			{
				caso1 = best(k + 1, u);
				tabla.put(k + 1, u, caso1);
			}

			if (a[k] >= u)
			{
				int caso2;
				if (tabla.containsKey(k + 1, a[k]))
				{
					caso2 = tabla.get(k + 1, a[k]);
				}
				else
				{
					caso2 = best(k + 1, a[k]);
					tabla.put(k + 1, a[k], caso2);
				}

				return Math.max(caso1, caso2 + 1);
			}
			else
			{
				return caso1;
			}
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