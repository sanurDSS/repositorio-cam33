package AA;

public class P05
{
	/**
	 * Vector que indica el número de segmentos que puede traducirse comenzando desde la palabra del primer índice.
	 */
	private int[][] w;
	
	/**
	 * Vector que indica la posición del último 1 de cada fila del vector w.
	 */
	private int[] v;

	/**
	 * Tabla que almacena el resultado de las sucesivas llamadas recursivas según el parámetro de entrada k.
	 */
	private int[] tabla;

	/**
	 * Cantidad de palabras en la frase.
	 */
	private int N;

	/**
	 * Resuelve el problema de las memorias de traducción.
	 * @param data Vector de cadenas en el que se indica, para cada palabra, hasta qué otra palabra puede traducirse esa subfrase.
	 * @return Devuelve el mínimo valor de traducciones que hay que hacer en la frase.
	 */
	public int best(String[] data)
	{
		// Valores iniciales de entrada de la función.
		N = data[0].length();
		w = new int[N][N];
		v = new int[N];
		for (int i = 0; i < N; i++)
		{
			for (int j = i; j < N; j++)
			{
				w[i][j] = new Integer(data[i].substring(j, j + 1));
				if (w[i][j] == 1)
				{
					v[i] = j;
				}
			}
		}

		// Inicialización de la tabla de programación dinámica.
		tabla = new int[N];

		return best(0);
	}

	private int best(int k)
	{
		int resultado = 0;
		
		if (k < N)
		{
			// Si existe, se extrae el resultado de la tabla.
			resultado = tabla[k];

			if (resultado == 0)
			{		
				// Si no existe, se aplica la recursividad suponiendo que se traduce la frase en todos sus segmentos posibles, quedándonos con el mínimo.
				resultado = best(k + 1);
				for (int j = k + 1; j <= v[k]; j++)
				{
					if (w[k][j] == 1)
					{
						resultado = Math.min(resultado, best(j + 1));
					}
				}

				resultado++;
				tabla[k] = resultado;
			}
		}

		return resultado;
	}
}