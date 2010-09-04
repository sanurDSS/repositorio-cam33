package AA;

import java.util.HashMap;
import java.util.Collection;

public class P08
{
	private float[] v;
	private int[] v100;
	private int[] vSqrt;
	private int N;
	private HashMap2<Integer, Integer, Integer> tabla;

	public int best(String data)
	{
		String[] dataAux = data.split("\\p{Space}+");
		N = dataAux.length;
		v = new float[N];
		v100 = new int[N];
		vSqrt = new int[N];
		for (int i = 0; i < N; i++)
		{
			v[i] = new Float(dataAux[i]);

			// En vez de realizar las operaciones continuamente, se realizan ahora y después únicamente se accede al vector.
			v100[i] = (int) Math.round(100 * v[i]);
			vSqrt[i] = (int) Math.round(100 / Math.sqrt(1 - v[i] * v[i]));
		}

		tabla = new HashMap2<Integer, Integer, Integer>();

		return bestPD(0, 0);
	}

	/*private int best(int x, int y)
	{
		if (x == N)
		{
			if (Math.abs(y) < 20)
			{
				return 0;
			}
			else
			{
				return Integer.MAX_VALUE;
			}
		}
		else
		{
			int caso1 = best(x + 1, y + (int) (100 * v[x]));
			if (caso1 != Integer.MAX_VALUE)
				caso1 += 100;

			int caso2 = best(x + 1, y);
			if (caso2 != Integer.MAX_VALUE)
				caso2 += (int) (100 / Math.sqrt(1 - v[x] * v[x]));

			return Math.min(caso1, caso2);
		}
	}*/

	private int bestPD(int x, int y)
	{
		System.out.println(y);
		if (x == N)
		{
			if (Math.abs(y) < 20)
			{
				return 0;
			}
			else
			{
				return Integer.MAX_VALUE;
			}
		}
		else
		{
			// Caso en el que se deja a la deriva.
			int caso1;
			if (tabla.containsKey(x + 1, y + v100[x]))
			{
				caso1 = tabla.get(x + 1, y + v100[x]);
			}
			else
			{
				caso1 = bestPD(x + 1, y + v100[x]);
				tabla.put(x + 1, y + v100[x], caso1);
			}
			if (caso1 != Integer.MAX_VALUE)
				caso1 += 100;

			// Caso en el que se mantiene el rumbo.
			int caso2;
			if (tabla.containsKey(x + 1, y))
			{
				caso2 = tabla.get(x + 1, y);
			}
			else
			{
				caso2 = bestPD(x + 1, y);
				tabla.put(x + 1, y, caso2);
			}
			if (caso2 != Integer.MAX_VALUE)
				caso2 += vSqrt[x];

			return Math.min(caso1, caso2);
		}
	}
}

class HashMap2<K1, K2, V>
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