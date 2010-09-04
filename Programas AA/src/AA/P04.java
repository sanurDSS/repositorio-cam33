package AA;

import java.util.HashMap;
import java.util.Collection;

public class P04
{
	private HashMap2<Integer, Arbol, Integer> tabla;

	public int best(int N, String data)
	{
		int resultado;
		Arbol arbol = new Arbol(0);
		arbol.transformar(data);
		tabla = new HashMap2<Integer, Arbol, Integer>();
		resultado = best(N, arbol);
		System.out.println(data);
		System.out.println(arbol.toString());
		System.out.println(resultado);
		System.out.println("---------------------------");
		return resultado;
	}

	private int best(int N, Arbol arbol)
	{
		int resultado = 0;

		if (N > 0)
		{
			N--;
			for (int i = 0; i <= N; i++)
			{
				int r1 = 0;
				if (arbol.izquierda != null)
				{
					if (!tabla.containsKey(N - i, arbol.izquierda))
					{
						r1 = best(N - i, arbol.izquierda);
						tabla.put(N - i, arbol.izquierda, r1);
					}
					else
					{
						r1 = tabla.get(N - i, arbol.izquierda);
					}
				}

				int r2 = 0;
				if (arbol.derecha != null)
				{
					if (!tabla.containsKey(i, arbol.derecha))
					{
						r2 = best(i, arbol.derecha);
						tabla.put(i, arbol.derecha, r2);
					}
					else
					{
						r2 = tabla.get(i, arbol.derecha);
					}
				}

				resultado = Math.max(resultado, r1 + r2);
			}
			resultado += arbol.valor;
		}
		
		return resultado;
	}

	static class Arbol
	{
		public int valor;
		public int nodos;
		public int suma;
		public Arbol izquierda;
		public Arbol derecha;

		public Arbol(int valor)
		{
			this.valor = valor;
			nodos = 1;
			suma = valor;
			izquierda = null;
			derecha = null;
		}

		public Arbol()
		{
			valor = 0;
			nodos = 0;
			suma = 0;
			izquierda = null;
			derecha = null;
		}

		public Arbol(Arbol arbol)
		{
			valor = arbol.valor;
			nodos = arbol.nodos;
			suma = arbol.suma;
			izquierda = (arbol.izquierda != null) ? new Arbol(arbol.izquierda) : null;
			derecha = (arbol.derecha != null) ? new Arbol(arbol.derecha) : null;
		}

		public String transformar(String arbol)
		{
			String[] arbolAux = arbol.split("\\p{Space}+");
			valor = new Integer(arbolAux[0]);
			nodos = new Integer(arbolAux[1]);

			// Extraemos la parte que contiene los hijos del árbol.
			String subarboles = "";
			for (int i = 2; i < 2*nodos; i++)
				subarboles += arbolAux[i] + " ";
			subarboles = subarboles.trim();

			// Extraemos el resto de la cadena que no corresponde al árbol.
			String resto = "";
			for (int i = 2*nodos; i < arbolAux.length; i++)
				resto += arbolAux[i] + " ";
			resto = resto.trim();

			// Si tiene más de un nodo, significa que tiene un hijo al menos.
			if (new Integer(arbolAux[1]) > 1)
			{
				izquierda = new Arbol();
				subarboles = izquierda.transformar(subarboles);

				// Si todavía queda algo en la cadena, es que tenía 2 hijos.
				if (subarboles.length() > 0)
				{
					derecha = new Arbol();
					subarboles = derecha.transformar(subarboles);
				}
			}

			sumarNodos();

			return resto;
		}

		public int recontarNodos()
		{
			nodos = 1;

			if (izquierda != null)
			{
				nodos += izquierda.recontarNodos();
			}

			if (derecha != null)
			{
				nodos += derecha.recontarNodos();
			}

			return nodos;
		}

		public int sumarNodos()
		{
			suma = valor;

			if (izquierda != null)
			{
				suma += izquierda.sumarNodos();
			}

			if (derecha != null)
			{
				suma += derecha.sumarNodos();
			}

			return suma;

		}

		@Override
		public String toString()
		{
			String cadena = valor + " " + nodos;
			if (izquierda != null)
				cadena += " " + izquierda.toString();
			if (derecha != null)
				cadena += " " + derecha.toString();
			return cadena;
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