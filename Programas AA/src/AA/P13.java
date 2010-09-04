package AA;

public class P13
{
	private int[] n;
	private int N;
	private int R;

	public int best(String[] data)
	{
		R = new Integer(data[0]);
		N = new Integer(data.length - 1);
		n = new int[N];
		for (int i = 0; i < N; i++)
		{
			n[i] = new Integer(data[i+1]);
		}

		exacto = false;

		return best(n);
	}

	private boolean exacto;

	int best(int[] valores)
	{
		if (exacto)
		{
			return R;
		}
		
		if (valores.length == 1)
		{
			if (valores[0] == R)
				exacto = true;
			return valores[0];
		}
		else
		{
			// Inicialmente establecemos el primer número como mejor solución.
			int resultado = valores[0];
			int diferencia = Math.abs(R - valores[0]);

			// Recorremos todos los valores formando parejas entre el primero y el "i".
			// Para cada pareja, comprobamos qué operación de las 4 nos da menor diferencia.
			for (int i = 1; i < valores.length; i++)
			{
				// Operación de suma.
				int[] nuevosValoresS = quitar(valores, i);
				nuevosValoresS[0] = valores[0] + valores[i];
				int suma = best(nuevosValoresS);
				if (Math.abs(R - suma) < diferencia)
				{
					resultado = suma;
					diferencia = Math.abs(R - suma);
				}

				// Operación de producto.
				int[] nuevosValoresP = quitar(valores, i);
				nuevosValoresP[0] = valores[0] * valores[i];
				int producto = best(nuevosValoresP);
				if (Math.abs(R - producto) < diferencia)
				{
					resultado = producto;
					diferencia = Math.abs(R - producto);
				}

				// Operación de resta (sólo si la diferencia es positiva).
				if (Math.abs(valores[0] - valores[i]) > 0)
				{
					int[] nuevosValoresR = quitar(valores, i);
					nuevosValoresR[0] = Math.abs(valores[0] - valores[i]);
					int resta = best(nuevosValoresR);
					if (Math.abs(R - resta) < diferencia)
					{
						resultado = resta;
						diferencia = Math.abs(R - resta);
					}
				}

				// Operación de división (sólo si el resultado es un valor entero).
				if (valores[0] % valores[i] == 0 || valores[i] % valores[0] == 0)
				{
					int[] nuevosValoresD = quitar(valores, i);
					if (valores[0] % valores[i] == 0)
						nuevosValoresD[0] = Math.abs(valores[0] / valores[i]);
					else
						nuevosValoresD[0] = Math.abs(valores[i] / valores[0]);
					int division = best(nuevosValoresD);
					if (Math.abs(R - division) < diferencia)
					{
						resultado = division;
						diferencia = Math.abs(R - division);
					}
				}
			}

			// Finalmente, probamos el caso en el que el primer valor es quitado; es decir, no se usa.
			int[] nuevosValoresQ = quitar(valores, 0);
			int quitar = best(nuevosValoresQ);
			if (Math.abs(R - quitar) < diferencia)
			{
				resultado = quitar;
				diferencia = Math.abs(R - quitar);
			}
			
			return resultado;
		}
	}

	/**
	 * Elimina una posición del vector según el índice indicado.
	 * @param vector Vector al que se le va a quitar un elemento.
	 * @param indice Posición del vector que se va a suprimir.
	 * @return Devuelve un vector nuevo sin el elemento indicado. Es decir, es 1 unidad más pequeño que el original.
	 */
	private int[] quitar(int[] vector, int indice)
	{
		int[] vectorAux = new int[vector.length - 1];
		for (int i = 0; i < indice; i++)
		{
			vectorAux[i] = vector[i];
		}
		for (int i = indice + 1; i < vector.length; i++)
		{
			vectorAux[i - 1] = vector[i];
		}
		return vectorAux;
	}

	private void imprimir(int[] valores)
	{
		for (int i = 0; i < valores.length; i++)
		{
			System.out.print(valores[i] + " ");
		}
		System.out.println();
	}
}