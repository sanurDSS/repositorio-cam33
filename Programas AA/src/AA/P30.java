package AA;

import java.util.Collection;
import java.util.HashMap;

public class P30
{
	private int N;
	private int[][] horarios;
	private int origen;
	private int destino;
	private int mejor;
	//private HashMap2<Integer, Integer, Integer> tabla;
	private int[][] tabla;
	private boolean[][] registrada;


	public int best(String[] data)
	{
		String[] dataAux = data[0].split("\\p{Space}+");
		origen = new Integer(dataAux[0]);
		destino = new Integer(dataAux[1]);
		N = new Integer(data.length - 1);
		horarios = new int[N][4];

		mejor = -1;
		//tabla = new HashMap2<Integer, Integer, Integer>();
		tabla = new int[N][72];
		
		for (int i = 0; i < N; i++)
		{
			dataAux = data[i + 1].split("\\p{Space}+");
			horarios[i][0] = new Integer(dataAux[0]);
			horarios[i][1] = new Integer(dataAux[1]);
			horarios[i][2] = new Integer(dataAux[2]);
			horarios[i][3] = new Integer(dataAux[3]);

			// Como mínimo llegaremos al destino en el peor vuelo que llegue allí.
			if (horarios[i][1] == destino)
			{
				mejor = Math.max(mejor, horarios[i][3]);
			}
		}
		
		return best(origen, -1);
	}

	private int best(int k, int actual)
	{
		if (k == destino)
		{
			if (actual < mejor)
				mejor = actual;
			return actual;
		}
		else
		{
			// Si no es potencialmente mejor, se descarta.
			if (actual >= mejor)
			{
				return Integer.MAX_VALUE;
			}

			// Si no estamos en el destino, pero ya se ha acabado el día, no podremos coger ningún otro avión
			// (porque todos tienen un horario de salida entre 0 y 48).
			// Nos ahorramos recorrer el bucle siguiente porque finalmente va a devolver MAX_VALUE.
			if (k != destino && actual >= 48)
			{
				return Integer.MAX_VALUE;
			}

			int minima = Integer.MAX_VALUE;
			for (int i = 0; i < N; i++)
			{
				// Comprobamos si el vuelo sale desde el aeropuerto k.
				if (horarios[i][0] == k)
				{
					// Comprobamos que la hora de salida sea posterior a mi hora de llegada.
					if (horarios[i][2] > actual)
					{
						int aux;
						/*if (tabla.containsKey(horarios[i][1], horarios[i][3]))
						{
							aux = tabla.get(horarios[i][1], horarios[i][3]);
						}
						else
						{
							aux = best(horarios[i][1], horarios[i][3]);
							tabla.put(horarios[i][1], horarios[i][3], aux);
						}*/
						if (tabla[horarios[i][1]][horarios[i][3]] > 0)
						{
							aux = tabla[horarios[i][1]][horarios[i][3]];
						}
						else
						{
							aux = tabla[horarios[i][1]][horarios[i][3]] = best(horarios[i][1], horarios[i][3]);
						}
						minima = Math.min(minima, aux);
					}
				}
			}
			return minima;
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