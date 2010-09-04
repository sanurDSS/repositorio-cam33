package AA;

import java.util.HashMap;
import java.util.Collection;

/*Trucos del problema:
	Sólo hay que aplicar la recursividad tantas veces como indique "<discos usados hasta el momento> + 1".
	Se +1 por si el programa se introduce en un nuevo disco vacío.

	Si además, se ordena la cantidad de MB metidos en cada disco, es fácil hacer un equals/hashcode que aproveche la PD.
	Porque al ordenar, luego coincide muy fácilmente 2 tarrinas iguales.abstract

	Y finalmente, lo más importante, fue añadir la poda. Cuando se consigue una solución, ya no intente buscar más.

	*/
public class P10
{
	private int[] w;
	private int N;
	private HashMap2<Integer, Tarrina, Integer> tabla;

	private int minimaConseguida;

	public int best(String data)
	{
		String[] dataAux = data.split("\\p{Space}+");
		N = dataAux.length;
		minimaConseguida = N;
		w = new int[N];
		for (int i = 0; i < N; i++)
		{
			w[i] = new Integer(dataAux[i]);
		}

		tabla = new HashMap2<Integer, Tarrina, Integer>();

		Integer[] discos = new Integer[N];
		for (int i = 0; i < N; i++)
		{
			discos[i] = 0;
		}

		//return best(0, discos);
		return bestPD_RyP(0, new Tarrina());
	}

	/*private int best(int k, Integer[] discos)
	{
		// Imprimimos la entrada por pantalla.
		System.out.print(k + " {" + discos[0]);
		for (int i = 1; i < N; i++)
		{
			System.out.print(", " + discos[i]);
		}
		System.out.println("}");
		
		if (k == N)
		{
			// Devolvemos la cantidad de discos usados.
			int usados = 0;
			int suma = 0;
			for (int i = 0; i < N; i++)
			{
				suma += w[i];
				if (discos[i] > 0)
					usados++;
			}

			// Comprobamos que el mínimo es posible.
			int minimoAbsoluto = (int) Math.ceil(suma/700.0f);
			if (usados < minimoAbsoluto)
			{
				System.out.println("Usados: " + usados + " menor que " + minimoAbsoluto + "... N=" + N);
			}
			return usados;
		}
		else
		{
			int minimos = N;
			for (int i = 0; i < N; i++)
			{
				if (discos[i] + w[k] <= 700)
				{
					Integer[] discosCopia = discos.clone();
					discosCopia[i] += w[k];
					minimos = Math.min(minimos, best(k + 1, discosCopia));
				}
			}
			return minimos;
		}
	}*/

	private int bestPD_RyP(int k, Tarrina tarrina)
	{
		// Realizamos una poda si no es posible mejorar la cantidad mínima ya obtenida.
		if (tarrina.usados >= minimaConseguida)
		{
			return N;
		}
		
		int cantidad = N;
		if (k == N)
		{
			cantidad = tarrina.usados;

			// Comprobamos si esta cantidad es menor que la que ya teníamos.
			if (minimaConseguida > cantidad)
			{
				minimaConseguida = cantidad;
			}
		}
		else
		{
			// Intentamos introducir el programa k en los discos ya usados o en uno nuevo si no cabe.
			for (int i = 0; i <= tarrina.usados; i++)
			{
				if (tarrina.discos[i] + w[k] <= 700)
				{
					// Generamos la nueva tarrina como resultado de agregar el programa k al disco i.
					Tarrina tarrinaAux = new Tarrina(tarrina);
					if (tarrinaAux.discos[i] == 0)
						tarrinaAux.usados++;
					tarrinaAux.discos[i] += w[k];

					// Se ordena la ocupación de discos para que el equals() coincida.
					quicksort(tarrinaAux.discos, 0, N - 1);

					// Intentamos extraer el resultado desde la tabla. Si no está, se calcula.
					int aux;
					if (tabla.containsKey(k + 1, tarrinaAux))
					{
						aux = tabla.get(k + 1, tarrinaAux);
					}
					else
					{
						aux = bestPD_RyP(k + 1, tarrinaAux);
						tabla.put(k + 1, tarrinaAux, aux);
					}
					cantidad = Math.min(cantidad, aux);
				}
			}
		}
		return cantidad;
	}

	void quicksort(Integer[] vector, int primero, int ultimo)
	{
		int i = primero, j = ultimo;
		int pivote = (vector[primero] + vector[ultimo]) / 2;
		int auxiliar;

		do
		{
			while (vector[i] > pivote)
			{
				i++;
			}
			while (vector[j] < pivote)
			{
				j--;
			}

			if (i <= j)
			{
				auxiliar = vector[j];
				vector[j] = vector[i];
				vector[i] = auxiliar;
				i++;
				j--;
			}

		}
		while (i <= j);

		if (primero < j)
		{
			quicksort(vector, primero, j);
		}
		if (ultimo > i)
		{
			quicksort(vector, i, ultimo);
		}
	}

	class Tarrina
	{
		public Integer[] discos;
		public int usados;

		public Tarrina()
		{
			usados = 0;
			discos = new Integer[N];
			for (int i = 0; i < N; i++)
			{
				discos[i] = 0;
			}
		}

		public Tarrina(Tarrina tarrina)
		{
			usados = tarrina.usados;
			discos = tarrina.discos.clone();
		}

		@Override
		public boolean equals(Object otro)
		{
			boolean iguales = false;

			if (otro instanceof Tarrina)
			{
				Tarrina otra = (Tarrina) otro;

				if (usados == otra.usados)
				{
					iguales = true;
					for (int i = 0; i < N && iguales; i++)
					{
						if (!discos[i].equals(otra.discos[i]))
						{
							iguales = false;
						}
					}
				}
			}
			return iguales;
		}

		@Override
		public int hashCode()
		{
			int suma = usados;
			for (int i = 0; i < N; i++)
			{
				suma += (i+1)*discos[i];
			}
			return suma*usados;
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
