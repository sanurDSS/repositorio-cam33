package AA;

import java.util.Collection;
import java.util.HashMap;

public class P20
{
	/**
	 * Saldo total en la cuenta.
	 */
	private int C;

	/**
	 * Número de pagarés disponibles este año.
	 */
	private int N;

	/**
	 * Valor de los pagarés y valor en un supuesto adelanto.
	 */
	private int[][] n;

	/**
	 * Pagos que hay que hacer en el año (siempre son 12 pagos).
	 */
	private int[] p;

	private HashMap2<Integer, Integer, Integer> tabla;

	public int best(String data)
	{
		// Extraemos los parámetros de entrada.
		String[] dataAux = data.split("\\p{Space}+");
		C = new Integer(dataAux[0]);
		N = new Integer(dataAux[1]);
		n = new int[N][12];
		for (int i = 0; i < 12*N; i++)
		{
			n[i/12][i%12] = new Integer(dataAux[i + 14]);
			/*
			// Para que los meses posteriores al vencimiento marquen la cantidad total del pagaré, en vez de 0.
			if (n[i/12][i%12] == 0)
			{
				n[i/12][i%12] = n[i/12][i%12-1];
			}*/
		}
		p = new int[12];
		for (int i = 0; i < 12; i++)
		{
			p[i] = new Integer(dataAux[i + 2]);
		}

		// Imprimimos los datos de entrada para comprobar que son correctos.
		/*System.out.println("C: " + C);
		System.out.println("N: " + N);
		System.out.print("P: ");
		for (int i = 0; i < 12; i++)
		{
			System.out.print(p[i] + " ");
		}
		System.out.println();
		System.out.println("n:");
		for (int i = 0; i < N; i++)
		{
			for (int j = 0; j < 12; j++)
			{
				System.out.print(n[i][j] + " ");
			}
			System.out.println();
		}*/

		tabla = new HashMap2<Integer, Integer, Integer>();

		return best(0, C, new boolean[N]);
	}

	private int best(int k, int c, boolean[] m)
	{
		if (k == 12)
		{
			return c;
		}
		else
		{
			// Cobramos los pagarés que no se hayan cobrado y caduquen este mes.
			for (int i = 0; i < N; i++)
			{
				if (!m[i])
				{
					if (n[i][k] > 0)
					{
						if (k == 11)
						{
							c += n[i][k];
							m[i] = true;
						}
						else
						{
							if(n[i][k + 1] == 0)
							{
								c += n[i][k];
								m[i] = true;
							}
						}
					}
				}
			}

			int maximo = Integer.MIN_VALUE;

			// Caso 1: Se paga con el dinero de la cuenta sin cobrar ningún pagaré.
			if (c > p[k])
				maximo = Math.max(maximo, best(k + 1, c - p[k], m));

			// Caso 2: Se cobran los posibles pagarés.
			for (int i = 0; i < N; i++)
			{
				// Comprobamos si el pagaré ya se ha cobrado.
				if (!m[i])
				{
					// Comprobamos que el pagaré no haya caducado.
					if (n[i][k] > 0)
					{
						// Comprobamos que exista un saldo positivo en el mes.
						if (c + n[i][k] - p[k] > 0)
						{
							boolean[] mAux = m.clone();
							mAux[i] = true;

							maximo = Math.max(maximo, best(k + 1, c + n[i][k] - p[k], mAux));
						}
					}
				}
			}

			return maximo;
		}
	}

	/*private int best(int k, Auxiliar auxiliar)
	{
		if (k == N)
		{
			// Comprobamos que, para cada mes, hay un saldo positivo en la cuenta.
			for (int i = 0; i < 12; i++)
			{
				// Comprobamos el saldo positivo del mes.
				if (auxiliar.a[i] > p[i])
				{
					// Si era el último mes, guardamos lo sobrante a la cuenta; si no, pasamos lo sobrante al mes siguiente.
					if (i < 11)
					{
						auxiliar.a[i + 1] += auxiliar.a[i] - p[i];
					}
					else
					{
						return auxiliar.a[i] - p[i];
					}
				}
				else
				{
					return Integer.MIN_VALUE;
				}
			}
			return Integer.MIN_VALUE;
		}
		else
		{
			int maximo = Integer.MIN_VALUE;

			for (int i = 0; i < 12; i++)
			{
				if (n[k][i] > 0)
				{
					Auxiliar auxiliarAux = new Auxiliar();
					auxiliarAux.a = auxiliar.a.clone();
					auxiliarAux.a[i] += n[k][i];

					int aux;
					if (tabla.containsKey(k + 1, auxiliarAux))
					{
						aux = tabla.get(k + 1, auxiliarAux);
					}
					else
					{
						aux = best(k + 1, auxiliarAux);
						tabla.put(k + 1, auxiliarAux, aux);
					}

					maximo = Math.max(maximo, aux);
				}
				else
				{
					break;
				}
			}

			return maximo;
		}
	}*/

	class Auxiliar
	{
		public int[] a;

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof Auxiliar)
			{
				Auxiliar otro = (Auxiliar) o;
				for (int i = 0; i < 12; i++)
				{
					if (otro.a[i] != a[i])
					{
						return false;
					}
				}

				return true;

			}
			return false;
		}

		@Override
		public int hashCode()
		{
			int hashCode = 0;
			for (int i = 0; i < 12; i++)
			{
				hashCode += i*a[i];
			}
			return hashCode;
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

	/*private int best(int k, int[] a)
	{
		if (k == N)
		{
			// Comprobamos que, para cada mes, hay un saldo positivo en la cuenta.
			for (int i = 0; i < 12; i++)
			{
				// Comprobamos el saldo positivo del mes.
				if (a[i] > p[i])
				{
					// Si era el último mes, guardamos lo sobrante a la cuenta; si no, pasamos lo sobrante al mes siguiente.
					if (i < 11)
					{
						a[i + 1] += a[i] - p[i];
					}
					else
					{
						return a[i] - p[i];
					}
				}
				else
				{
					return Integer.MIN_VALUE;
				}
			}
			return Integer.MIN_VALUE;
		}
		else
		{
			int maximo = Integer.MIN_VALUE;

			for (int i = 0; i < 12; i++)
			{
				if (n[k][i] > 0)
				{
					int[] aAux = a.clone();
					aAux[i] += n[k][i];
					maximo = Math.max(maximo, best(k + 1, aAux));
				}
				else
				{
					break;
				}
			}

			return maximo;
		}
	}*/
}
