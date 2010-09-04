package AA;

import java.util.*;

/**
 * Knapsack
 */
public class P21Jorge
{

	int N, M;
	ArrayList<Integer> V;
	ArrayList<Integer> C;
	int[][] PD;
	int[] resultado;

	private void init(String values)
	{

		String[] v = values.split("\\p{Space}+");

		N = (v.length - 1) / 2;
		M = new Integer(v[0]);
		V = new ArrayList<Integer>(N);
		C = new ArrayList<Integer>(N);

		for (int i = 1; i <= N * 2; i += 2)
		{
			V.add(new Integer(v[i]));
			C.add(new Integer(v[i + 1]));
		}

		PD = new int[N][M + 1];
		for (int i = 0; i < N; i++)
		{
			for (int j = 0; j <= M; j++)
			{
				PD[i][j] = -1;
			}
		}
	}

	private int best(int it, int acc)
	{
		if (it == N || it == M)
		{
			if (acc == M)
			{
				return 0;
			}
			else
			{
				return Integer.MAX_VALUE;
			}
		}
		else if (acc < M)
		{
			if (PD[it][acc] != -1)
			{
				return PD[it][acc];
			}

			int aux = Integer.MAX_VALUE, aux2 = Integer.MAX_VALUE;
			boolean salir = false;

			for (int i = 0; i <= C.get(it) && !salir; i++)
			{
				if (i * V.get(it) + acc <= M)
				{
					aux2 = best(it + 1, acc + (i * V.get(it)));
					if (aux2 != Integer.MAX_VALUE)
					{
						aux = Math.min(aux, i + aux2);
					}
				}
				else
				{
					salir = true;
				}
			}

			PD[it][acc] = aux;

			return aux;
		}
		return Integer.MAX_VALUE;
	}

	private int[] bestSolution(int it, int acc, int[] historia)
	{
		if (it == N - 1)
		{
			int ybest = Integer.MAX_VALUE;
			int aux = Integer.MAX_VALUE;
			boolean encontrado = false;

			for (int i = 0; i <= C.get(it) && !encontrado; i++)
			{
				aux = acc + (i * V.get(it));
				if (aux == M)
				{
					historia[it] = i;
					encontrado = true;
				}
			}
			return historia;
		}
		if (it == N)
		{
			return historia;
		}

		int ybest = Integer.MAX_VALUE;
		int aux = Integer.MAX_VALUE, aux2;
		int sigAcc, bestI = 0;

		for (int i = 0; i <= C.get(it); i++)
		{
			sigAcc = acc + (i * V.get(it));
			if (sigAcc == M)
			{
				aux = PD[it][acc] + i;
				if (aux < ybest)
				{
					historia[it] = i;
					return historia;
				}
			}
			else if (sigAcc < M)
			{
				aux = PD[it + 1][sigAcc];

				if (aux != Integer.MAX_VALUE)
				{
					aux += i;
					if (aux < ybest)
					{
						historia[it] = i;
						ybest = aux;
						bestI = i;
					}
				}
			}
		}
		//System.out.println("IT: " + it + " -  YBEST: " + ybest + "- ACC: " );
		return bestSolution(it + 1, acc + (bestI * V.get(it)), historia);

	}

	public int[] bestSolution(String data)
	{
		init(data);
		best(0, 0);
		return bestSolution(0, 0, new int[N]);
	}

	public static void main(String[] args)
	{
		P21Jorge p = new P21Jorge();
		int[] solucion = p.bestSolution(args[0]);
		for (int i = 0; i < solucion.length; i++)
		{
			System.out.print(solucion[i] + " ");
		}
	}
}
