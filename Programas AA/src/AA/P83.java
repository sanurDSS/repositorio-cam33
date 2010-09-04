package AA;

import java.util.HashMap;
import java.util.Collection;

public class P83
{
	private int N;
	private int[] w;
	private int mejor;
	//private HashMap2<Integer, Tripleta, Integer> tabla;

	/**
	 * Las claves del ejercicio es hacer una buena función de cota. El voraz no afecta mucho y ordenar el vector tampoco.
	 * Por otro lado, la PD es contraproducente, no se repiten muchas llamadas incluso con la clase Tripleta.
	 * @param data
	 * @return
	 */
	public int best(String data)
	{
		String[] dataAux = data.split("\\p{Space}+");
		N = dataAux.length;
		w = new int[N];
		for (int i = 0; i < N; i++)
		{
			w[i] = new Integer(dataAux[i]);
		}

		quicksort(w, 0, N - 1);

		mejor = voraz();
		//System.out.println("Voraz: " + mejor);
		//tabla = new HashMap2<Integer, Tripleta, Integer>();
		
		return gab(0, 0, 0, 0);
	}

	/*private static class Tripleta
	{
		private int[] x;
		private int hashCode;

		public Tripleta(int x1, int x2, int x3)
		{
			x = new int[3];
			x[0] = x1;
			x[1] = x2;
			x[2] = x3;
			quicksort(x, 0, 2);
			hashCode = -1;
		}


		@Override
		public boolean equals(Object o)
		{
			if (o instanceof Tripleta)
			{
				Tripleta t = (Tripleta) o;
				return x[0] == t.x[0] && x[1] == t.x[1] && x[2] == t.x[2];
			}
			return false;
		}

		@Override
		public int hashCode()
		{
			if (hashCode == -1)
			{
				hashCode = x[0] * 17 + x[1] * 73 + x[2] * 1729;
			}
			return hashCode;
		}
	}*/

	static void quicksort(int[] vector, int primero, int ultimo)
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

	private int voraz()
	{
		int x1, x2, x3;
		x1 = x2 = x3 = 0;
		for (int i = 0; i < N; i++)
		{
			// sumo en el que me mantenga la diferencia más pequeña
			int dif1 = Math.max(x1 + w[i], Math.max(x2, x3)) - Math.min(x1 + w[i], Math.min(x2, x3));
			int dif2 = Math.max(x1, Math.max(x2 + w[i], x3)) - Math.min(x1, Math.min(x2 + w[i], x3));
			int dif3 = Math.max(x1, Math.max(x2, x3 + w[i])) - Math.min(x1, Math.min(x2, x3 + w[i]));

			if (dif1 < dif2 && dif1 < dif3)
				x1 += w[i];
			else if (dif2 < dif3)
				x2 += w[i];
			else
				x3 += w[i];
		}
		return Math.max(x1, Math.max(x2, x3)) - Math.min(x1, Math.min(x2, x3));
	}

	private int lowerBound(int n, int x1, int x2, int x3)
	{
		// Se suma el valor de los objetos restantes.
		int suma = 0;
		for (int i = n; i < N; i++)
		{
			suma += w[i];
		}
		
		//System.out.println("Suma: " + suma);
		//System.out.println("Inicio: " + x1 + " " + x2 + " " + x3);

		// Ordenamos los elementos de mayor a menor.
		int mayor = Math.max(x1, Math.max(x2, x3));
		int menor = Math.min(x1, Math.min(x2, x3));
		int intermedio = x3;
		if (x2 >= x1 && x1 >= x3 || x3 >= x1 && x1 >= x2)
			intermedio = x1;
		else if (x1 >= x2 && x2 >= x3 || x3 >= x2 && x2 >= x1)
			intermedio = x2;

		//System.out.println("Caso 0: " + mayor + " " + intermedio + " " + menor);
		//System.out.println();

		// Se intentan igualar los 3 elementos el máximo posible.
		if (suma >= intermedio - menor)
		{
			suma -= intermedio - menor;
			menor = intermedio;

			//System.out.println("Caso 1: " + mayor + " " + intermedio + " " + menor);

			// Lo que le sobre a la suma se divide entre los 2 más pequeños (o los 3 si son iguales los 3).
			if (mayor > intermedio)
			{
				if (suma >= (mayor - intermedio) * 2)
				{
					suma -= (mayor - intermedio) * 2;
					intermedio = menor = mayor;

					mayor += Math.ceil(suma/3.0);
					intermedio += suma/3;
					menor += suma/3;

					//System.out.println("Caso 2: " + mayor + " " + intermedio + " " + menor);
				}
				else
				{
					intermedio += Math.ceil(suma/2.0);
					menor += suma/2;

					//System.out.println("Caso 3: " + mayor + " " + intermedio + " " + menor);
				}
			}
			else
			{
				//System.out.println("Caso 4: " + mayor + " " + intermedio + " " + menor);
				mayor += Math.ceil(suma/3.0);
				intermedio += suma/3;
				menor += suma/3;
			}
		}
		else
		{
			menor += suma;
		}

		/*suma = 0;
		for (int i = n; i < N; i++)
		{
			suma += w[i];
		}
		// Se reparte la suma de 1 en 1 a la menor persona.
		while (suma > 0)
		{
			if (x1 < x2 && x1 < x3)
				x1++;
			else if (x2 < x3)
				x2++;
			else
				x3++;

			suma--;
		}*/

		//System.out.println(x1 + " " + x2 + " " + x3 + " (" + (x1 + x2 + x3) + ")");
		//System.out.println(mayor + " " + intermedio + " " + menor + " (" + (mayor + intermedio + menor) + ")");

		// Se devuelve la mínima diferencia posible (que posiblemente sea más, ya que estamos suponiendo muchos objetos de valor 1).
		//int whi = Math.max(x1, Math.max(x2, x3)) - Math.min(x1, Math.min(x2, x3));
		int ifs = mayor - menor;

		/*if (whi != ifs)
		{
			System.out.println("Siguen siendo distintos");
			System.out.println("Siguen siendo distintos");
			System.out.println("Siguen siendo distintos");
			System.out.println("Siguen siendo distintos");
			System.out.println("Siguen siendo distintos");
			System.out.println(whi + " " + ifs);
		}

		if (mejor == 4 && ifs <= mejor)
		{
			System.out.println("Ahora res1 es: " + ifs);
		}*/

		return ifs;
	}

	private int gab(int n, int x1, int x2, int x3)
	{
		// Si no es potencialmente mejor, descartamos la llamada recursiva.
		if (lowerBound(n, x1, x2, x3) >= mejor)
		{
			return Integer.MAX_VALUE;
		}

		if (n == N)
		{
			// Almacenamos el resultado si es mejor que el anterior.
			int resultado = Math.max(x1, Math.max(x2, x3)) - Math.min(x1, Math.min(x2, x3));
			if (resultado < mejor)
			{
				mejor = resultado;
			}
			return mejor;
		}
		else
		{
			int minimo = Integer.MAX_VALUE;

			minimo = Math.min (minimo, gab(n + 1, x1 + w[n], x2, x3));
			minimo = Math.min (minimo, gab(n + 1, x1, x2 + w[n], x3));
			minimo = Math.min (minimo, gab(n + 1, x1, x2, x3 + w[n]));

			return minimo;
			/*int caso1;
			if (tabla.containsKey(n + 1, new Tripleta(x1 + w[n], x2, x3)))
			{
				caso1 = tabla.get(n + 1, new Tripleta(x1 + w[n], x2, x3));
			}
			else
			{
				caso1 = gab(n + 1, x1 + w[n], x2, x3);
				tabla.put(n + 1, new Tripleta(x1 + w[n], x2, x3), caso1);
			}

			int caso2;
			if (tabla.containsKey(n + 1, new Tripleta(x1, x2 + w[n], x3)))
			{
				caso2 = tabla.get(n + 1, new Tripleta(x1, x2 + w[n], x3));
			}
			else
			{
				caso2 = gab(n + 1, x1, x2 + w[n], x3);
				tabla.put(n + 1, new Tripleta(x1, x2 + w[n], x3), caso2);
			}

			int caso3;
			if (tabla.containsKey(n + 1, new Tripleta(x1, x2, x3 + w[n])))
			{
				caso3 = tabla.get(n + 1, new Tripleta(x1, x2, x3 + w[n]));
			}
			else
			{
				caso3 = gab(n + 1, x1, x2, x3 + w[n]);
				tabla.put(n + 1, new Tripleta(x1, x2, x3 + w[n]), caso3);
			}

			return Math.min(caso1, Math.min(caso2, caso3));*/
		}
	}

	// Algoritmo recursivo.
	/*private int gab(int n, int x1, int x2, int x3)
	{
		if (n == N)
		{
			return Math.max(x1, Math.max(x2, x3)) - Math.min(x1, Math.min(x2, x3));
		}
		else
		{
			int minimo = Integer.MAX_VALUE;

			minimo = Math.min (minimo, gab(n + 1, x1 + w[n], x2, x3));
			minimo = Math.min (minimo, gab(n + 1, x1, x2 + w[n], x3));
			minimo = Math.min (minimo, gab(n + 1, x1, x2, x3 + w[n]));
			
			return minimo;
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
