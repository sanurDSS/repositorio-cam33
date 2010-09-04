package AA;

import java.util.HashMap;

public class P15
{
	private int M;
	private int N;
	private int[] v;
	private HashMap<Integer, Integer> tabla;
	public int best(String data)
	{
		String[] dataAux = data.split("\\p{Space}+");
		M = new Integer(dataAux[0]);
		N = new Integer(dataAux.length - 1);
		v = new int[N];
		for (int i = 0; i < N; i++)
		{
			v[i] = new Integer(dataAux[i+1]);
		}

		tabla = new HashMap<Integer, Integer>(5);

		/*System.out.println("M: " + M);
		System.out.println("N: " + N);
		System.out.print("v: ");
		for (int i = 0; i < v.length; i++)
		{
			System.out.print(v[i] + " ");
		}
		System.out.println();*/

		return best(M);
	}

	private int best(int m)
	{
		if (m == 0)
		{
			return 0;
		}
		else
		{
			int minimas = Integer.MAX_VALUE;

			// Al recorrer el bucle en el otro sentido se llenaba la pila de llamadas.
			for (int i = N - 1; i >= 0; i--)
			{
				if (m >= v[i])
				{
					// Aplicamos la recursividad (a no ser que ya tengamos calculado dicho valor).
					int minimasAux;
					if (tabla.containsKey(m - v[i]))
					{
						minimasAux = tabla.get(m - v[i]);
					}
					else
					{
						minimasAux = best(m - v[i]);
						tabla.put(m - v[i], minimasAux);
					}

					// Si no coincide con el máximo valor posible, incrementamos 1, ya que acabamos de usar una moneda de valor "i".
					if (minimasAux != Integer.MAX_VALUE)
						minimasAux++;

					minimas = Math.min(minimas, minimasAux);
				}
			}
			return minimas;
		}
	}
}