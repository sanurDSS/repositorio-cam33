package AA;

public class P84
{
	private int M;
	private int N;
	private int[] v;
	private int[] c;

	private int[][] tabla;
	private boolean[][] estado;

	public int best(String data)
	{
		// Se extraen los datos.
		String[] dataAux = data.split("\\p{Space}+");
		M = new Integer(dataAux[0]);
		N = new Integer((dataAux.length - 1)/2);
		v = new int[N];
		c = new int[N];
		for (int i = 0; i < N; i++)
		{
			v[i] = new Integer(dataAux[2*i+1]);
			c[i] = new Integer(dataAux[2*i+2]);
		}

		// Salida por pantalla del problema.
		/*System.out.println();
		for (int i = 0; i < Nodo.N; i++)
		{
			System.out.println("Moneda: " + Nodo.monedas.get(i).valor);
		}
		System.out.println("M: " + Nodo.M);
		System.out.println("N: " + Nodo.N);
		System.out.print("v: ");
		for (int i = 0; i < Nodo.N; i++)
		{
			System.out.print(Nodo.v[i] + " ");
		}
		System.out.println();
		System.out.print("c: ");
		for (int i = 0; i < Nodo.N; i++)
		{
			System.out.print(Nodo.c[i] + " ");
		}
		System.out.println();*/

		// Se inicializan las matrices de PD.
		tabla = new int[N+1][M+1];
		estado = new boolean[N+1][M+1];

		return best(0, M);
	}

	private int best(int k, int m)
	{
		if (k == N)
		{
			if (m == 0)
			{
				return 0;
			}
			else
				return Integer.MAX_VALUE;
		}
		else
		{
			int minimo = Integer.MAX_VALUE;

			for (int i = 0; i <= c[k]; i++)
			{
				if (m - v[k]*i >= 0)
				{
					int aux;
					if (estado[k + 1][m - v[k] * i])
					{
						aux = tabla[k + 1][m - v[k] * i];
					}
					else
					{
						aux = tabla[k + 1][m - v[k] * i] = best(k + 1, m - v[k] * i);
						estado[k + 1][m - v[k] * i] = true;
					}
					if (Integer.MAX_VALUE - aux >= i)
					{
						aux += i;
					}

					minimo = Math.min(minimo, aux);
				}
			}

			return minimo;
		}
	}
}