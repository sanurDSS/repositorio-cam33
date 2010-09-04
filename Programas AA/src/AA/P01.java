package AA;

public class P01
{
	/**
	 * Vector interno que almacena la longitud de la barra y los puntos de corte.
	 */
	private static int[] fragmentos;

	/**
	 * Matriz que almacena las posibles combinaciones de corte de la barra.
	 * Cuando se calcule un corte, se almacena en la matriz y no se vuelve a calcular.
	 */
	private static int[][] resultado;

	/**
	 * Indica si una determinada combinación de corte ya ha sido calculada o
	 * hay que calcularla.
	 */
	private static boolean[][] existe;

	/**
	 * Resuelve el problema de la barra.
	 * @param data cadena de caracteres que indica la longitud de la barra y los puntos de corte
	 * @return el mínimo valor de trocear la barra
	 */
	public static int best(String data)
	{
		// Extraemos los puntos de corte y convertimos de String a int.
		String[] fragmentosAux = data.split("\\p{Space}+");
		int numFragmentos = fragmentosAux.length;

		fragmentos = new int[numFragmentos];
		for (int i = 0; i < numFragmentos; i++)
		{
			fragmentos[i] = new Integer(fragmentosAux[i]);
		}

		// Inicializamos las matrices.
		resultado = new int[numFragmentos][numFragmentos];
		existe = new boolean[numFragmentos][numFragmentos];

		return best(0, numFragmentos-1);
	}

	/**
	 * Función auxiliar para evitar convertir de String a int en cada llamada recursiva.
	 * @param primerCorte un entero que indica dónde comienza el fragmento de barra
	 * @param ultimoCorte un entero que indica dónde termina el fragmento de barra
	 * @return el mínimo valor de trocear la barra entre esos puntos
	 */
	private static int best(int primerCorte, int ultimoCorte)
	{
		// Caso base: no hay que hacer ningún corte más.
		if (ultimoCorte - primerCorte == 2)
		{
			return fragmentos[ultimoCorte] - fragmentos[primerCorte];
		}
		else
		{
			// Sí hay que cortar.
			if (ultimoCorte - primerCorte > 2)
			{
				// Probamos todos los casos posibles de corte y nos quedamos con el mínimo.
				int minimo = Integer.MAX_VALUE;
				for (int i = primerCorte+1; i < ultimoCorte; i++)
				{
					// Calculamos cuánto cuesta trocear la barra izquierda, que es resultado de cortar en el punto "i".
					int fragmento1;
					if (existe[primerCorte][i])
					{
						fragmento1 = resultado[primerCorte][i];
					}
					else
					{
						fragmento1 = best(primerCorte, i);
						resultado[primerCorte][i] = fragmento1;
						existe[primerCorte][i] = true;
					}

					// Calculamos cuánto cuesta trocear la barra derecha, que es resultado de cortar en el punto "i".
					int fragmento2;
					if (existe[i][ultimoCorte])
					{
						fragmento2 = resultado[i][ultimoCorte];
					}
					else
					{
						fragmento2 = best(i, ultimoCorte);
						resultado[i][ultimoCorte] = fragmento2;
						existe[i][ultimoCorte] = true;
					}

					// Nos quedamos con el mínimo valor obtenido después de cortaren cada punto "i".
					if (fragmento1 + fragmento2 < minimo)
					{
						minimo = fragmento1 + fragmento2;
					}
				}

				// Devolvemos lo que cuesta cortar la barra actual más la suma de trocear los dos fragmentos resultantes en el mejor caso.
				return minimo + fragmentos[ultimoCorte] - fragmentos[primerCorte];
			}
			else
			{
				return 0;
			}
		}
	}
}
