package AA;

import java.util.HashMap;
import java.util.Collection;

public class P07
{
	private int[] w;
	private int N;
	private HashMap3<Integer, Integer, Integer, Integer> tabla;

	public int best(String data)
	{
		String[] dataAux = data.split("\\p{Space}+");
		N = dataAux.length;
		w = new int[N];
		for (int i = 0; i < N; i++)
		{
			w[i] = new Integer(dataAux[i]);
		}

		tabla = new HashMap3<Integer, Integer, Integer, Integer>();

		return bestPD(0, 0, 0);
	}

	/*private int best(int k, int diferencia, int desnivel)
	{
		if (k == N)
		{
			if (Math.abs(desnivel) <= 1)
			{
				return Math.abs(diferencia);
			}
			else
			{
				return Integer.MAX_VALUE;
			}
		}
		else
		{
			return Math.min(best(k+1, diferencia+w[k], desnivel+1), best(k+1, diferencia-w[k], desnivel-1));
		}
	}*/

	private int bestPD(int k, int diferencia, int desnivel)
	{
		if (k == N)
		{
			if (Math.abs(desnivel) <= 1)
			{
				return Math.abs(diferencia);
			}
			else
			{
				return Integer.MAX_VALUE;
			}
		}
		else
		{
			int equipo1;
			if (tabla.containsKey(k+1, diferencia+w[k], desnivel+1))
			{
				equipo1 = tabla.get(k+1, diferencia+w[k], desnivel+1);
			}
			else
			{
				equipo1 = bestPD(k+1, diferencia+w[k], desnivel+1);
				tabla.put(k+1, diferencia+w[k], desnivel+1, equipo1);
			}

			int equipo2;
			if (tabla.containsKey(k+1, diferencia-w[k], desnivel-1))
			{
				equipo2 = tabla.get(k+1, diferencia-w[k], desnivel-1);
			}
			else
			{
				equipo2 = bestPD(k+1, diferencia-w[k], desnivel-1);
				tabla.put(k+1, diferencia-w[k], desnivel-1, equipo2);
			}
			
			return Math.min(equipo1, equipo2);
		}
	}

	static class HashMap3b<K1, K2, K3, V>
	{
		private HashMap<Par, V> tabla;

		public HashMap3b()
		{
			tabla = new HashMap<Par, V>();
		}

		public HashMap3b(int initialCapacity)
		{
			tabla = new HashMap<Par, V>(initialCapacity);
		}

		public HashMap3b(int initialCapacity, float loadFactor)
		{
			tabla = new HashMap<Par, V>(initialCapacity, loadFactor);
		}

		public V get(K1 key1, K2 key2, K3 key3)
		{
			return tabla.get(new Par(key1, key2, key3));
		}

		public V remove(K1 key1, K2 key2, K3 key3)
		{
			return tabla.remove(new Par(key1, key2, key3));
		}

		public V put(K1 key1, K2 key2, K3 key3, V value)
		{
			return tabla.put(new Par(key1, key2, key3), value);
		}

		public boolean containsKey(K1 key1, K2 key2, K3 key3)
		{
			return tabla.containsKey(new Par(key1, key2, key3));
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
		public HashMap3b<K1, K2, K3, V> clone()
		{
			HashMap3b<K1, K2, K3, V> copia = new HashMap3b<K1, K2, K3, V>();
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

			public Par(Object objeto1, Object objeto2, Object objeto3)
			{
				this.objeto1 = objeto1;
				this.objeto2 = objeto2;
				this.objeto3 = objeto3;
			}

			@Override
			public boolean equals(Object otroPar)
			{
				if (otroPar instanceof Par)
				{
					Par par = (Par) otroPar;
					return objeto1.equals(par.objeto1) && objeto2.equals(par.objeto2) && objeto3.equals(par.objeto3);
				}
				return false;
			}

			@Override
			public int hashCode()
			{
				int resultado = 17 * 37 + objeto1.hashCode();
				resultado = 37 * resultado + objeto2.hashCode();
				resultado = 37 * resultado + objeto3.hashCode();
				return resultado;
			}

			@Override
			public String toString()
			{
				return "<" + objeto1.toString() + ", " + objeto2.toString() + ", " + objeto3.toString() + ">";
			}
		}
	}

	static class HashMap3<K1, K2, K3, V>
	{
		private HashMap<K1, HashMap<K2, HashMap<K3, V>>> table;
		private int size;

		public HashMap3()
		{
			table = new HashMap<K1, HashMap<K2, HashMap<K3, V>>>();
			size = 0;
		}

		public V put(K1 key1, K2 key2, K3 key3, V value)
		{
			HashMap<K1, HashMap<K2, HashMap<K3, V>>> table1 = table;
			HashMap<K2, HashMap<K3, V>> table2 = null;
			HashMap<K3, V> table3 = null;

			if (!table1.containsKey(key1))
			{
				table2 = new HashMap<K2, HashMap<K3, V>>();
				table1.put(key1, table2);
			}
			else
			{
				table2 = table1.get(key1);
			}

			if (!table2.containsKey(key2))
			{
				table3 = new HashMap<K3, V>();
				table2.put(key2, table3);
			}
			else
			{
				table3 = table2.get(key2);
			}

			V previous = table3.put(key3, value);
			if (previous == null)
			{
				size++;
			}
			return previous;
		}

		public V get(K1 key1, K2 key2, K3 key3)
		{
			HashMap<K1, HashMap<K2, HashMap<K3, V>>> table1 = table;
			HashMap<K2, HashMap<K3, V>> table2 = null;
			HashMap<K3, V> table3 = null;

			if (table1.containsKey(key1))
			{
				table2 = table1.get(key1);
				if (table2.containsKey(key2))
				{
					table3 = table2.get(key2);
					return table3.get(key3);
				}
			}

			return null;
		}

		public boolean containsKey(K1 key1, K2 key2, K3 key3)
		{
			return get(key1, key2, key3) != null;
		}

		public int size()
		{
			return size;
		}

		public boolean isEmpty()
		{
			return size == 0;
		}

		public V remove(K1 key1, K2 key2, K3 key3)
		{
			HashMap<K1, HashMap<K2, HashMap<K3, V>>> table1 = table;
			HashMap<K2, HashMap<K3, V>> table2 = null;
			HashMap<K3, V> table3 = null;

			if (table1.containsKey(key1))
			{
				table2 = table1.get(key1);
				if (table2.containsKey(key2))
				{
					table3 = table2.get(key2);
					V value = table3.remove(key3);
					if (value != null)
					{
						size--;
					}
					return value;
				}
			}

			return null;
		}
	}

}