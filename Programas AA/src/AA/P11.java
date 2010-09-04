package AA;

public class P11
{
	private int[] tablero;
	public String sol(String data)
	{
		tablero = new int[81];
		for (int i = 0; i < 81; i++)
		{
			tablero[i] = new Integer("" + data.charAt(i + i/9));
		}

		sol(0);

		String salida = "";
		for (int i = 0; i < 81; i++)
		{
			if (i >= 9 && i % 9 == 0)
			{
				salida += "\n";
			}
			salida += tablero[i];
		}
		return salida;
	}

	private boolean sol(int i)
	{
		if (i == 81)
		{
			return true;
		}
		else
		{
			if (tablero[i] == 0)
			{
				boolean[] posibles = getPosibles(i);
				for (int posible = 0; posible < 9; posible++)
				{
					if (posibles[posible])
					{
						tablero[i] = posible + 1;
						if (sol(i + 1))
						{
							return true;
						}
					}
				}

				tablero[i] = 0;
				return false;
			}
			else
			{
				return sol(i + 1);
			}
		}
	}

	private boolean[] getPosibles(int variable)
	{
		boolean[] posibles = new boolean[9];
		for (int i = 0; i < 9; i++)
		{
			posibles[i] = true;
		}

		int fila = variable / 9;
		int columna = variable % 9;
		int filaBloque = fila / 3 * 3;
		int columnaBloque = columna / 3 * 3;

		// Eliminamos los valores de la fila ya usados.
		for (int i = 0; i < 9; i++)
		{
			int posicion = fila * 9 + i;
			if (tablero[posicion] > 0)
			{
				posibles[tablero[posicion] - 1] = false;
			}
		}

		// Eliminamos los valores de la columna ya usados.
		for (int i = 0; i < 9; i++)
		{
			int posicion = i * 9 + columna;
			if (tablero[posicion] > 0)
			{
				posibles[tablero[posicion] - 1] = false;
			}
		}

		// También a la variable del mismo bloque.
		for (int i = filaBloque; i < filaBloque + 3; i++)
		{
			for (int j = columnaBloque; j < columnaBloque + 3; j++)
			{
				int posicion = i * 9 + j;
				if (tablero[posicion] > 0)
				{
					posibles[tablero[posicion] - 1] = false;
				}
			}
		}

		return posibles;
	}
}