package AA;

public class P91
{
	/**
	 * Vector con los pesos de los objetos.
	 */
	private static int[] W;

	/**
	 * Vector con los valores de los objetos.
	 */
	private static int[] P;

	/**
	 * Matriz (N x M+1) que almacena las parejas de entrada de la función para el algoritmo con PD.
	 */
	private static int[][] pairs;

	/**
	 * Resuelve el problema de la mochila.
	 * @param data data[0]: pesos de los objetos separados por un espacio, data[1]: valor de los objetos, data[2]: capacidad de la mochila
	 * @return el máximo valor que podrían alcanzar los objetos de la mochila
	 */
	public static int best(String[] data)
	{
		// Declaración de variables.
		String[] WAux;
		String[] PAux;
		int N;
		int M;

		// Extrae y divide las cadenas de entrada en múltiples cadenas.
		WAux = data[0].split("\\p{Space}+");
		PAux = data[1].split("\\p{Space}+");

		// Valores iniciales de entrada de la función.
		N = WAux.length;
		M = new Integer(data[2]);

		// Convierte de String a int.
		W = new int[N];
		P = new int[N];
		for (int i = 0; i < N; i++)
		{
			W[i] = new Integer(WAux[i]);
			P[i] = new Integer(PAux[i]);
		}
		
		// Crea la matriz (que inicializa todos su valores a 0 por defecto) y ejecuta el algoritmom con PD.
		pairs = new int[N][M+1];
		return bestPD(N, M);
	}

	/**
	 * Función auxiliar que evita convertir de String a int en cada llamada recursiva.
	 * Resuelve el problema mediante programación dinámica. Cuando calcula la solución para
	 * un par de valores, los almacena en una tabla para ahorrar los cálculos en futuras llamadas recursivas.
	 * @param N número de objectos que están a la espera de decidir si entran o no en la mochila
	 * @param M peso que todavía cabe en la mochila
	 * @return el máximo valor que podrían alcanzar los objetos de la mochila
	 */
	private static int bestPD(int N, int M)
	{
		// Caso base.
		if (N == 0)
		{
			return 0;
		}
		else
		{
			// Se obtendrá el valor suponiendo que el objeto no se mete en la mochila y también suponiendo que el objeto sí se mete.
			int ifNotSelected = 0;
			int ifSelected = 0;

			// Si la pareja no está en la matriz, se calcula.
			if (pairs[N - 1][M] == 0)
			{
				pairs[N - 1][M] = bestPD(N - 1, M);
			}
			ifNotSelected = pairs[N - 1][M];

			// Se comprueba si el objeto cabe en la mochila.
			if (M - W[N - 1] >= 0)
			{
				// Si la pareja no está en la matriz, se calcula.
				if (pairs[N - 1][M - W[N - 1]] == 0)
				{
					pairs[N - 1][M - W[N - 1]] = bestPD(N - 1, M - W[N - 1]);
				}
				ifSelected = pairs[N - 1][M - W[N - 1]] + P[N - 1];
			}

			// Devuelve el mayor valor (dentro o fuera de la mochila).
			return Math.max(ifSelected, ifNotSelected);
		}
	}

	/**
	 * Función auxiliar que evita convertir de String a int en cada llamada recursiva.
	 * Resuelve el problema con llamadas recursiva íntegramente.
	 * @param N número de objectos que están a la espera de decidir si entran o no en la mochila
	 * @param M peso que todavía cabe en la mochila
	 * @return el máximo valor que podrían alcanzar los objetos de la mochila
	 */
	private static int bestRecursivo(int N, int M)
	{
		// Caso base.
		if (N == 0)
		{
			return 0;
		}
		else
		{
			// Se obtendrá el valor suponiendo que el objeto no se mete en la mochila y también suponiendo que el objeto sí se mete.
			int ifNotSelected = bestRecursivo(N - 1, M);
			int ifSelected = 0;
			if (M - W[N - 1] >= 0)
			{
				ifSelected = bestRecursivo(N - 1, M - W[N - 1]) + P[N - 1];
			}

			// Devuelve el mayor valor (dentro o fuera de la mochila).
			return Math.max(ifSelected, ifNotSelected);
		}
	}

	/**
	 * Función auxiliar que evita convertir de String a int en cada llamada recursiva.
	 * Presenta un algoritmo iterativo que rellena completamente la tabla para después
	 * extraer el valor que interesa.
	 * @param N número de objectos que están a la espera de decidir si entran o no en la mochila
	 * @param M peso que todavía cabe en la mochila
	 * @return el máximo valor que podrían alcanzar los objetos de la mochila
	 */
	private static int bestIterativo(int N, int M)
	{
		// Creamos el vector para las posibles combinaciones de entrada.
		// En el algoritmo recursivo con programación dinámimca la matriz es de N*M+1 y aquí es N+1*M+1.
		int[][] entradas = new int[N+1][M+1];

		// Hay que poner la primera fila y la primera columna a 0, pero java pone a 0 automáticamente todos los valores de la matriz.

		// Recorremos la matriz y la rellenamos.
		for (int i = 1; i <= N; i++)
		{
			for (int j = 1; j <= M; j++)
			{
				// Se obtendrá el valor suponiendo que el objeto no se mete en la mochila y también suponiendo que el objeto sí se mete.
				int ifNotSelected = entradas[i - 1][j];
				int ifSelected = 0;
				if (j >=  W[i - 1])
				{
					ifSelected = entradas[i - 1][j - W[i - 1]] + P[i - 1];
				}

				// Establecemos el mayor valor de los dos.
				entradas[i][j] = Math.max(ifNotSelected, ifSelected);
			}
		}

		// Devolvemos el elemento de la esquina inferior derecha.
		return entradas[N][M];
	}
}
