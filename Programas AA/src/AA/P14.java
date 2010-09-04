package AA;

import java.util.HashMap;
import java.util.Collection;

public class P14
{
	private int M;
	private HashMap2<Integer, Integer, Boolean> tabla;
	public boolean winner(String data)
	{
		String[] dataAux = data.split("\\p{Space}+");
		int n = new Integer(dataAux[0]);
		int m = new Integer(dataAux[1]);
		M = new Integer(dataAux[2]);

		tabla = new HashMap2<Integer, Integer, Boolean>();

		return winner(m, n);
	}

	/**
	 * Comprueba si existe una estrategia ganadora.
	 * @param m Última cantidad de fichas retirada por tu rival.
	 * @param n Cantidad de fichas restantes en el tablero.
	 * @return Devuelve verdadero si existe una estrategia ganadora que te permite ganar.
	 */
	private boolean winner(int m, int n)
	{
		boolean winner = false;

		// Si puedo hacer la última tirada, he ganado.
		if ((n <= M && m != n) || (n - 1 == 1 && m != 1))
		//if (n == 0 || (n == 1 && m == 1)) este if tambien funciona, pero hay que devolver false si se entra en él
		{
			return true;
		}
		else
		{
			for (int i = 1; i <= Math.min(M, n) && !winner; i++)
			{
				if (i != m)
				{
					boolean aux;
					// La tabla da mejores tiempo si se introduce commo <n, m> que si se introduce como <m, n>.
					if (tabla.containsKey(n - i, i))
					{
						aux = tabla.get(n - i, i);
					}
					else
					{
						aux = winner(i, n - i);
						tabla.put(n - i, i, aux);
					}
					winner |= !aux;
				}
			}
		}

		return winner;
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