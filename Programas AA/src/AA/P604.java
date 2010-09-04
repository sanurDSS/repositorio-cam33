package AA;

public class P604
{
	private int N;
	private int L;
	private int M;
	private int[] v;

	private int[][] tabla;

	public int best(String data)
	{
		// Procesamiento de la entrada.
		String[] dataAux = data.split("\\p{Space}+");
		N = new Integer(dataAux[0]);
		L = new Integer(dataAux[1]);
		M = dataAux.length - 2;
		v = new int[M];
		for (int i = 0; i < M; i++)
		{
			v[i] = new Integer(dataAux[i+2]);
		}

		// Tabla para la PD.
		tabla = new int[M+1][M+1];
		for (int i = 0; i <= M; i++)
		{
			for (int j = 0; j <= M; j++)
			{
				tabla[i][j] = -1;
			}
		}

		// Finalmente se ejecuta el algoritmo con 2 parámetros.
		return best(0, N);
	}

	// Algoritmo recursivo con 2 parámetros y PD.
	private int best(int k, int n)
	{
		if (k == M)
		{
			return 0;
		}
		else
		{
			int caso1 = 0, caso2 = 0;
			if (n > 0)
			{
				if ((caso1 = tabla[Math.min(M, k + L)][n - 1]) == -1)
				{
					caso1 = tabla[Math.min(M, k + L)][n - 1] = best(Math.min(M, k + L), n - 1);
				}
				caso1 += v[k];

				if ((caso2 = tabla[k + 1][n]) == -1)
				{
					caso2 = tabla[k + 1][n] = best(k + 1, n);
				}
			}
			return Math.max(caso1, caso2);
		}
	}

	// Algoritmo recursivo con 2 parámetros.
	/*private int best(int k, int n)
	{
		if (k == M)
		{
			return 0;
		}
		else
		{
			int caso1 = 0, caso2 = 0;
			if (n > 0)
			{
				caso1 = best(Math.min(M, k + L), n - 1) + v[k];
				caso2 = best(k + 1, n);
			}
			return Math.max(caso1, caso2);
		}
	}*/

	// Algoritmo recursivo con 3 parámetros. Primera llamada: best(0, 1, N)
	/*private int best(int k, int l, int n)
	{
		if (k == M)
		{
			return 0;
		}
		else
		{
			int caso1 = 0, caso2 = 0;

			if (n > 0)
			{
				if (l == 1)
				{
					caso1 = best(k + 1, L, n - 1);
				}
				caso2 = best(k + 1, Math.max(1, l - 1), n);
			}

			return Math.max(caso1, caso2);
		}
	}*/
}
