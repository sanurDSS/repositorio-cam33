package AA;

import java.util.Collection;
import java.util.HashMap;

public class P81
{
	private int N;
	private int[][] e;
	private HashMap2<Integer, Integer, Integer> tabla;
	private int[][] matriz;
	private boolean[][] activa;
	private int mejor;

	public int best(String[] data)
	{
		String[] dataAux1 = data[0].split("\\p{Space}+");
		String[] dataAux2 = data[1].split("\\p{Space}+");
		String[] dataAux3 = data[2].split("\\p{Space}+");
		N = new Integer(dataAux1.length);
		e = new int[N][3];

		int t = 0;
		for (int i = 0; i < N; i++)
		{
			e[i][0] = new Integer(dataAux1[i]);
			e[i][1] = new Integer(dataAux2[i]);
			e[i][2] = new Integer(dataAux3[i]);
			t = Math.max(t, e[i][2]);
		}

		System.out.println(t+1);
		matriz = new int[N+1][t+1];
		activa = new boolean[N+1][t+1];

		tabla = new HashMap2<Integer, Integer, Integer>();

		/*return best(0, 0);

		llamadas = 0;
		
		int voraz = voraz();
		mejor = voraz;
		int resultado = best(0, 0, 0);
		System.out.println("Voraz:     " + voraz);
		System.out.println("Resultado: " + resultado + " (" + mejor + ")");
		System.out.println("Llamadas:  " + llamadas);
		System.out.println("-----------------------\n");

		return mejor;*/

		mejor = 0;
		int resultado = best(0, 0, 0);

		System.out.println("Resultado: " + resultado);
		System.out.println("Llamadas:  " + llamadas);
		System.out.println("-----------------------\n");
		
		return resultado;
	}

	/*private int voraz()
	{
		int v = 0;
		int t = 0;
		for (int i = 0; i < N; i++)
		{
			if (e[i][2] >= e[i][0] + t)
			{
				v += e[i][1];
				t += e[i][0];
			}
		}
		return v;
	}*/

	private int llamadas;

	private int best(int k, int t, int v)
	{
		llamadas++;
		//System.out.println(k + " " + t);

		// Caso base (no quedan elementos que evaluar).
		if (k == N)
		{
			if (v > mejor)
			{
				mejor = v;
			}
			return 0;
		}
		else
		{
			// Comprobamos hasta qué valor podríamos llegar desde esta solución.
			/*int vAux = v;
			for (int i = k; i < N; i++)
			{
				if (e[i][2] >= e[i][0] + t)
					vAux += e[i][1];
			}
			if (vAux <= mejor)
			{
				// Si no es potencialmente mejor, descartamos la solución.
				return 0;
			} NO FUNCIONA, SE DEJA FUERA LA SOLUCIÓN POR ALGUNA RAZÓN*/
		
			// Nos quedamos con el mayor valor que se obtenga (trabajando o descartando el encargo).
			int caso1, caso2;
			
			// Evaluamos el caso en el que la tarea no se realiza.
			if (activa[k + 1][t])
			{
				caso1 = matriz[k + 1][t];
			}
			else
			{
				caso1 = matriz[k + 1][t] = best(k + 1, t, v);
				activa[k + 1][t] = true;
			}
			/*if (tabla.containsKey(k + 1, t))
			{
				caso1 = tabla.get(k + 1, t);
			}
			else
			{
				caso1 = best(k + 1, t);
				tabla.put(k + 1, t, caso1);
			}*/

			// Evaluamos el caso en el que la tarea se realiza (si se puede).
			if (e[k][2] >= e[k][0] + t)
			{
				if (activa[k + 1][e[k][0] + t])
				{
					caso2 = matriz[k + 1][e[k][0] + t];
				}
				else
				{
					caso2 = matriz[k + 1][e[k][0] + t] = best(k + 1, e[k][0] + t, v + e[k][1]);
					activa[k + 1][e[k][0] + t] = true;
				}
				/*if (tabla.containsKey(k + 1, e[k][0] + t))
				{
					caso2 = tabla.get(k + 1, e[k][0] + t);
				}
				else
				{
					caso2 = best(k + 1, e[k][0] + t);
					tabla.put(k + 1, e[k][0] + t, caso2);
				}*/
				caso2 += e[k][1];
			}
			else
			{
				caso2 = 0;
			}

			// Devolvemos el máximo valor obtenido entre ambos casos.
			return Math.max(caso1, caso2);
		}
	}

	/*private int best(int k, int t, int v)
	{
		llamadas++;
		//System.out.println(k + " " + t);

		// Comprobamos cuál es el máximo valor que se podría conseguir y, si
		// no es potencialmente mejor, se descarta la solución actual.
		int vPosible = v;
		for (int i = k; i < N; i++)
		{
			if (e[i][2] >= e[i][0] + t)
				vPosible += e[i][1];
		}
		if (vPosible <= mejor)
		{
			return v;
		}

		// Caso base (no quedan elementos que evaluar).
		if (k == N)
		{
			// Guardamos el resultado si se ha mejorado.
			if (v > mejor)
			{
				mejor = v;
			}
			return v;
		}
		else
		{
			// Nos quedamos con el mayor valor que se obtenga (trabajando o descartando el encargo).
			int caso1, caso2;

			// Evaluamos el caso en el que la tarea no se realiza.
			if (tabla.containsKey(k + 1, t))
			{
				caso1 = tabla.get(k + 1, t);
			}
			else
			{
				caso1 = best(k + 1, t, v);
				tabla.put(k + 1, t, caso1);
			}

			// Evaluamos el caso en el que la tarea se realiza (si se puede).
			if (e[k][2] >= e[k][0] + t)
			{
				if (tabla.containsKey(k + 1, e[k][0] + t))
				{
					caso2 = tabla.get(k + 1, e[k][0] + t);
				}
				else
				{
					caso2 = best(k + 1, e[k][0] + t, v + e[k][1]);
					tabla.put(k + 1, e[k][0] + t, caso2);
				}
			}
			else
			{
				caso2 = 0;
			}

			// Devolvemos el máximo valor obtenido entre ambos casos.
			return Math.max(caso1, caso2);
		}
	}*/

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