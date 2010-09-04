package Damas;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 *
 * @author mireia
 */
public class Tablero
{
	final int tamaño = 8;

	//Datos del tablero
	private int m_tablero[][];

	/**
	 * Constructor del tablero
	 * @param anchura Anchura del tablero
	 * @param altura Altura del tablero
	 */
	public Tablero(int tamaño)
	{
		//Crea el tablero
		m_tablero = new int[tamaño][tamaño];

		//Inicializa el tablero
		inicializarTablero();
	}

	/**
	 * Constsructor de Copia del tablero
	 * @param original Tablero del cual realizar la copia
	 */
	public Tablero(Tablero original)
	{
		//Crea el tablero
		m_tablero = new int[tamaño][tamaño];

		//Copia cada casilla del tablero
		for (int i = 0; i < tamaño; i++)
		{
			for (int j = 0; j < tamaño; j++)
			{
				m_tablero[i][j] = original.m_tablero[i][j];
			}
		}
	}

	/**
	 * Devuelve el tamaño
	 */
	public int tamaño()
	{
		return tamaño;

	}

	/**
	 * Devuelve a quién pertenece la casilla especificada.
	 * 0 está vacía
	 * 1 pertenece al jugador 1 (blancas)
	 * 2 pertenece al jugador 2 (negras)
	 */
	public int obtenerCasilla(int i, int j)
	{
		if ((i >= 0) && (i < 8) && (j >= 0) && (j < 8))
		{
			return m_tablero[i][j];
		}
		else
		{
			return -1;
		}
	}

	/**
	 * Cambiar valor casilla
	 */
	public void cambiarCasilla(int i, int j, int valor)
	{
		if (i >= 0 && i < tamaño && j >= 0 && j < tamaño)
		{
			m_tablero[i][j] = valor;
		}
	}

	//Limpia el tablero. Deja todas las casillas vacías.
	public void limpiarTablero()
	{
		for (int i = 0; i < tamaño; i++)
		{
			for (int j = 0; j < tamaño; j++)
			{
				m_tablero[i][j] = 0;
			}
		}
	}

	public void inicializarTablero()
	{
		//En primer lugar vacía todo el tablero
		limpiarTablero();

		//Dibuja las fichas blancas
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < tamaño; j++)
			{
				if (i % 2 == 0)
				{
					if (j % 2 == 0)
					{
						m_tablero[j][i] = 2;
					}
				}
				else
				{
					if (j % 2 != 0)
					{
						m_tablero[j][i] = 2;
					}
				}
			}
		}

		//Dibuja las fichas negras
		for (int i = tamaño - 1; i > tamaño - 4; i--)
		{
			for (int j = 0; j < tamaño; j++)
			{
				if (i % 2 == 0)
				{
					if (j % 2 == 0)
					{
						m_tablero[j][i] = 1;
					}
				}
				else
				{
					if (j % 2 != 0)
					{
						m_tablero[j][i] = 1;
					}
				}
			}
		}
	}

	/**
	 * Comprueba si existen movimientos posibles para un jugador.
	 * @param jugador Jugador que tiene el turno para el que comprueba si existen movimientos.
	 * @return movimiento True si quedan movimientos posibles.
	 */
	public boolean quedanMovimientos(int jugador)
	{
		int ficha;
		boolean movimiento;
		movimiento = false;

		//Recorre todas las casillas del tablero
		for (int i = 0; i < tamaño() && !movimiento; i++)
		{
			for (int j = 0; j < tamaño() && !movimiento; j++)
			{
				ficha = obtenerCasilla(j, i);
				//Si existe una ficha en la casilla
				if (ficha != 0)
				{
					//Si la ficha es del jugador
					if (ficha == jugador)
					{   //Si hay movimiento posible
						if (movimientoPosible(j, i, ficha) != -1)
						{
							movimiento = true;
						}
					}
				}
			}
		}
		return movimiento;
	}

	/**
	 * Calcula si la ficha que ocupa la columna y fila indicada tiene algún
	 * movimiento posible.
	 */
	public int movimientoPosible(int columna, int fila, int jugador)
	{
		int contrario;

		if (jugador == 1)
		{
			contrario = 2;
		}
		else
		{
			contrario = 1;
		}

		//Comprueba que la ficha pertenece al jugador que le toca jugar
		if (obtenerCasilla(columna, fila) == jugador)
		{
			//Si la ficha pertenece al jugador, comprueba si puede hacer alguna jugada
			//Si es una ficha blanca, la ficha puede mover para arriba en diagonal
			if (jugador == 1)
			{
				if (fila == 0)
				{
					return -1;
				}

				if (fila == 1)
				{
					if (columna == tamaño() - 1)
					{
						if ((obtenerCasilla(columna - 1, fila - 1) == 0))
						{
							return 1;
						}
					}
					else
					{
						if ((obtenerCasilla(columna - 1, fila - 1) == 0) || (obtenerCasilla(columna + 1, fila - 1) == 0))
						{
							return 1;
						}
					}
				}

				if ((columna == 0) && (fila > 1))
				{
					if ((obtenerCasilla(columna + 1, fila - 1) == 0) || (obtenerCasilla(columna + 1, fila - 1) == contrario && obtenerCasilla(columna + 2, fila - 2) == 0))
					{
						return 1;
					}
				}

				if ((columna == 1) && (fila > 1))
				{
					if ((obtenerCasilla(columna - 1, fila - 1) == 0) || (obtenerCasilla(columna + 1, fila - 1) == 0) || (obtenerCasilla(columna + 1, fila - 1) == contrario && obtenerCasilla(columna + 2, fila - 2) == 0))
					{
						return 1;
					}
				}

				if ((columna == tamaño() - 1) && (fila > 1))
				{
					if ((obtenerCasilla(columna - 1, fila - 1) == 0) || (obtenerCasilla(columna - 1, fila - 1) == contrario && obtenerCasilla(columna - 2, fila - 2) == 0))
					{
						return 1;
					}
				}

				if ((columna == tamaño() - 2) && (fila > 1))
				{
					if ((obtenerCasilla(columna - 1, fila - 1) == 0) || (obtenerCasilla(columna + 1, fila - 1) == 0) || (obtenerCasilla(columna - 1, fila - 1) == contrario && obtenerCasilla(columna - 2, fila - 2) == 0))
					{
						return 1;
					}
				}

				if (columna > 1 && columna < tamaño() - 2 && fila > 1)
				{
					if ((obtenerCasilla(columna - 1, fila - 1) == 0) || (obtenerCasilla(columna + 1, fila - 1) == 0) || (obtenerCasilla(columna - 1, fila - 1) == contrario && obtenerCasilla(columna - 2, fila - 2) == 0) || (obtenerCasilla(columna + 1, fila - 1) == contrario && obtenerCasilla(columna + 2, fila - 2) == 0))
					{
						return 1;
					}
				}

			}

			// Si es una ficha negra, puede mover hacia bajo en diagonal
			if (jugador == 2)
			{
				if (fila == tamaño() - 1)
				{
					return -1;
				}

				if (fila == tamaño() - 2)
				{
					if (columna == tamaño() - 1)
					{
						if ((obtenerCasilla(columna - 1, fila + 1) == 0))
						{
							return 1;
						}
					}
					else
					{
						if ((obtenerCasilla(columna - 1, fila + 1) == 0) || (obtenerCasilla(columna + 1, fila + 1) == 0))
						{
							return 1;
						}
					}
				}

				if ((columna == 0) && (fila < tamaño() - 2))
				{
					if ((obtenerCasilla(columna + 1, fila + 1) == 0) || (obtenerCasilla(columna + 1, fila + 1) == contrario && obtenerCasilla(columna + 2, fila + 2) == 0))
					{
						return 1;
					}
				}

				if ((columna == 1) && (fila < tamaño() - 2))
				{
					if ((obtenerCasilla(columna - 1, fila + 1) == 0) || (obtenerCasilla(columna + 1, fila + 1) == 0) || (obtenerCasilla(columna + 1, fila + 1) == contrario && obtenerCasilla(columna + 2, fila + 2) == 0))
					{
						return 1;
					}
				}

				if ((columna == tamaño() - 2) && (fila < tamaño() - 2))
				{
					if ((obtenerCasilla(columna - 1, fila + 1) == 0) || (obtenerCasilla(columna + 1, fila + 1) == 0) || (obtenerCasilla(columna - 1, fila + 1) == contrario && obtenerCasilla(columna - 2, fila + 2) == 0))
					{
						return 1;
					}
				}

				if ((columna == tamaño() - 1) && (fila < tamaño() - 2))
				{
					if ((obtenerCasilla(columna - 1, fila + 1) == 0) || (obtenerCasilla(columna - 1, fila + 1) == contrario && obtenerCasilla(columna - 2, fila + 2) == 0))
					{
						return 1;
					}
				}

				if (columna > 1 && columna < tamaño() - 2 && fila < tamaño() - 2)
				{
					if ((obtenerCasilla(columna - 1, fila + 1) == 0) || (obtenerCasilla(columna + 1, fila + 1) == 0) || (obtenerCasilla(columna - 1, fila + 1) == contrario && obtenerCasilla(columna - 2, fila + 2) == 0) || (obtenerCasilla(columna + 1, fila + 1) == contrario && obtenerCasilla(columna + 2, fila + 2) == 0))
					{
						return 1;
					}
				}
			}
		}
		return -1;
	}

	/**
	 * Comprueba si es una tirada válida.
	 * @param filAnt Fila donde se encuentra la ficha
	 * @param colAnt Columna donde se encuentra la ficha
	 * @param columna Columna donde se desea mover
	 * @param fila Fila donde se desea mover
	 * @param jugador Jugador que tiene el turno
	 */
	public int movimientoValido(int filAnt, int colAnt, int fila, int columna, int jugador)
	{
		int contrario;

		if (jugador == 1)
		{
			contrario = 2;
		}
		else
		{
			contrario = 1;
		}

		//Si la casilla a la que se intenta mover está ocupada
		if (obtenerCasilla(columna, fila) != 0)
		{
			return -1;
		}

		//Si es una ficha blanca, la ficha puede mover para arriba en diagonal
		if (jugador == 1)
		{
			if (colAnt == 0)
			{
				if (fila == filAnt - 1 && columna == colAnt + 1)
				{
					return 1;
				}

				if ((obtenerCasilla(colAnt + 1, filAnt - 1) == contrario) && (fila == filAnt - 2 && columna == colAnt + 2))
				{
					return 1;
				}
			}

			if (colAnt == tamaño() - 1)
			{
				if (fila == filAnt - 1 && columna == colAnt - 1)
				{
					return 1;
				}

				if ((obtenerCasilla(colAnt - 1, filAnt - 1) == contrario) && (fila == filAnt - 2 && columna == colAnt - 2))
				{
					return 1;
				}
			}

			if (colAnt > 0 && colAnt < tamaño() - 1 && filAnt > 1)
			{
				if ((fila == filAnt - 1) && (columna == colAnt + 1 || columna == colAnt - 1))
				{
					return 1;
				}

				if ((obtenerCasilla(colAnt - 1, filAnt - 1) == contrario) && (fila == filAnt - 2 && columna == colAnt - 2))
				{
					return 1;
				}

				if ((obtenerCasilla(colAnt + 1, filAnt - 1) == contrario) && (fila == filAnt - 2 && columna == colAnt + 2))
				{
					return 1;
				}
			}

			if (filAnt == 1)
			{
				if ((fila == filAnt - 1) && (columna == colAnt + 1 || columna == colAnt - 1))
				{
					return 1;
				}
			}

			if (filAnt == 0)
			{
				return -1;
			}
		}

		// Si es una ficha negra, puede mover hacia bajo en diagonal
		if (jugador == 2)
		{
			if (colAnt == 0)
			{
				if (fila == filAnt + 1 && columna == colAnt + 1)
				{
					return 1;
				}

				if ((obtenerCasilla(colAnt + 1, filAnt + 1) == contrario) && (fila == filAnt + 2 && columna == colAnt + 2))
				{
					return 1;
				}
			}

			if (colAnt == tamaño() - 1)
			{
				if (fila == filAnt + 1 && columna == colAnt - 1)
				{
					return 1;
				}

				if ((obtenerCasilla(colAnt - 1, filAnt + 1) == contrario) && (fila == filAnt + 2 && columna == colAnt - 2))
				{
					return 1;
				}
			}

			if (colAnt > 0 && colAnt < tamaño() - 1 && filAnt < tamaño() - 2)
			{
				if ((fila == filAnt + 1) && (columna == colAnt + 1 || columna == colAnt - 1))
				{
					return 1;
				}

				if ((obtenerCasilla(colAnt - 1, filAnt + 1) == contrario) && (fila == filAnt + 2 && columna == colAnt - 2))
				{
					return 1;
				}

				if ((obtenerCasilla(colAnt + 1, filAnt + 1) == contrario) && (fila == filAnt + 2 && columna == colAnt + 2))
				{
					return 1;
				}
			}

			if (filAnt == tamaño() - 2)
			{
				if ((fila == filAnt + 1) && (columna == colAnt + 1 || columna == colAnt - 1))
				{
					return 1;
				}
			}

			if (filAnt == tamaño())
			{
				return -1;
			}
		}
		return -1;
	}

	/**
	 * Mueve las fichas a partir de una posición actual.
	 * @param filAnt
	 * @param colAnt
	 * @param columna
	 * @param fila
	 * @param jugador
	 * @return
	 */
	public int hacerTirada(Movimiento movimiento, int jugador)
	{
		int valor;
		valor = -1;
		//Posición actual de la ficha
		int filAnt = 0;
		int colAnt = 0;
		//Posición donde se desea poner la ficha
		int fila = 0;
		int columna = 0;

		filAnt = movimiento.getInicial().getX();
		colAnt = movimiento.getInicial().getY();
		fila = movimiento.getFinal().getX();
		columna = movimiento.getFinal().getY();


		//Calcula si el movimiento es posible
		if (movimientoValido(filAnt, colAnt, fila, columna, jugador) == 1)
		{
			//Si el movimiento es posible
			//Borra la ficha anterior
			cambiarCasilla(colAnt, filAnt, 0);

			//Coloca la ficha en la nueva posición
			cambiarCasilla(columna, fila, jugador);

			//Comprueba si ha matado alguna ficha para borrarla
			if (Math.abs(columna - colAnt) > 1 && Math.abs(fila - filAnt) > 1)
			{
				//Si tiran las blancas
				if (jugador == 1)
				{
					if (columna > colAnt)
					{
						cambiarCasilla(colAnt + 1, filAnt - 1, 0);
					}
					else
					{
						cambiarCasilla(colAnt - 1, filAnt - 1, 0);
					}

					return 2; //Para indicar que ha matado a una ficha negra
				}
				else //Si tiran las negras
				{
					if (columna > colAnt)
					{
						cambiarCasilla(colAnt + 1, filAnt + 1, 0);
					}
					else
					{
						cambiarCasilla(colAnt - 1, filAnt + 1, 0);
					}
					return 1; //Para indicar que ha matado a una ficha blanca
				}
			}
			valor = 0;
		}
		return valor;
	}

	/**
	 * Comprueba qué movimientos puede hacer un jugador.
	 * @param jugador Blancas: 1, Negras, 2.
	 * @return Devuelve una lista con los posibles movimientos que puede hacer el jugador indicado.
	 */
	public ArrayList<Movimiento> movimientosPosibles(int jugador)
	{
		ArrayList<Movimiento> movimientos = new ArrayList<Movimiento>();
		ListIterator<Movimiento> iterador = movimientos.listIterator();

		// Recorremos el tablero en busca de las fichas del jugador indicado y devolvemos sus posibles movimientos.
		if (jugador == 1)
		{
			// Jugador blanco.
			for (int fila = 0; fila < 8; fila++)
			{
				for (int columna = 0; columna < 8; columna++)
				{
					if (obtenerCasilla(columna, fila) == 1)
					{
						// Hay cuatro movimientos posibles (2 sin matar y 2 matando).
						if (obtenerCasilla(columna-1, fila-1) == 0)
						{
							iterador.add(new Movimiento(columna, fila, columna-1, fila-1));
						}
						else if (obtenerCasilla(columna-1, fila-1) == 2 && obtenerCasilla(columna-2, fila-2) == 0)
						{
							iterador.add(new Movimiento(columna, fila, columna-2, fila-2));
						}
						if (obtenerCasilla(columna+1, fila-1) == 0)
						{
							iterador.add(new Movimiento(columna, fila, columna+1, fila-1));
						}
						else if (obtenerCasilla(columna+1, fila-1) == 2 && obtenerCasilla(columna+2, fila-2) == 0)
						{
							iterador.add(new Movimiento(columna, fila, columna+2, fila-2));
						}
					}
				}
			}
		}
		else
		{
			// Jugador negro.
			for (int fila = 7; fila >= 0; fila--)
			{
				for (int columna = 7; columna >= 0; columna--)
				{
					if (obtenerCasilla(columna, fila) == 2)
					{
						// Hay cuatro movimientos posibles (2 sin matar y 2 matando).
						if (obtenerCasilla(columna+1, fila+1) == 0)
						{
							iterador.add(new Movimiento(columna, fila, columna+1, fila+1));
						}
						else if (obtenerCasilla(columna+1, fila+1) == 1 && obtenerCasilla(columna+2, fila+2) == 0)
						{
							iterador.add(new Movimiento(columna, fila, columna+2, fila+2));
						}
						if (obtenerCasilla(columna-1, fila+1) == 0)
						{
							iterador.add(new Movimiento(columna, fila, columna-1, fila+1));
						}
						else if (obtenerCasilla(columna-1, fila+1) == 1 && obtenerCasilla(columna-2, fila+2) == 0)
						{
							iterador.add(new Movimiento(columna, fila, columna-2, fila+2));
						}
					}
				}
			}
		}
		
		return movimientos;
	}

	/**
	 * Genera una cadena de caracteres con el tablero (columnas x filas).
	 * @return Cadena de caracteres con el resultado del tablero.
	 */
	@Override
	public String toString()
	{
		String auxiliar = "";
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				auxiliar += m_tablero[i][j];
			}
			auxiliar += System.getProperty("line.separator");
		}

		return auxiliar;
	}

	/**
	 * Calcula la puntuación de los jugadores.
	 * @return Devuelve un vector de tamaño 2. El primer valor es la puntuación de las blancas y el segundo de las negras.
	 */
	public int[] puntacionJugadores()
	{
		int[] puntuacion = {0, 0};
		int cantidadFichasBlancas = 0;
		int cantidadFichasNegras = 0;

		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				if (obtenerCasilla(j, i) == 1)
				{
					cantidadFichasBlancas++;
					switch(i)
					{
						case 0:
							puntuacion[0] = puntuacion[0] + 3;
							break;

						case 1:
							puntuacion[0] = puntuacion[0] + 2;
							break;

						case 2:
							puntuacion[0] = puntuacion[0] + 1;
							break;
					}
				}
				else if (obtenerCasilla(j, i) == 2)
				{
					cantidadFichasNegras++;
					switch(i)
					{
						case 7:
							puntuacion[1] = puntuacion[1] + 3;
							break;

						case 6:
							puntuacion[1] = puntuacion[1] + 2;
							break;

						case 5:
							puntuacion[1] = puntuacion[1] + 1;
							break;
					}
				}
			}
		}

		puntuacion[0] = puntuacion[0] + (12 - cantidadFichasNegras);
		puntuacion[1] = puntuacion[1] + (12 - cantidadFichasBlancas);
		
		return puntuacion;
	}

	/**
	 * Función de evaluación que sólo premia la puntuación general del tablero.
	 * @return Devuelve el valor del tablero.
	 */
	public int funcionEvaluacion1()
	{
		int valor = 0;

		// Sumamos y restamos las puntuciaones de los jugadores.
		int[] puntuaciones = puntacionJugadores();
		valor += puntuaciones[0];
		valor -= puntuaciones[1];

		return valor;
	}

	/**
	 * Función de evaluación que premia la cantidad de fichas.
	 * @return Devuelve el valor del tablero.
	 */
	public int funcionEvaluacion2()
	{
		int valor = 0;

		// Cantidad de fichas de cada jugador.
		int cantidadBlancas = 0;
		int cantidadNegras = 0;
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				if (obtenerCasilla(i, j) == 1)
					cantidadBlancas++;
				else if (obtenerCasilla(i, j) == 2)
					cantidadNegras++;
		valor += cantidadBlancas - cantidadNegras;

		return valor;
	}

	/**
	 * Función de evaluación que premia que las fichas estén en los extremos del tablero.
	 */
	public int funcionEvaluacion3()
	{
		float valor = 0;

		// Sumamos y restamos las puntuciaones de los jugadores.
		int[] puntuaciones = puntacionJugadores();
		valor += puntuaciones[0];
		valor -= puntuaciones[1];

		// Recorremos los extremos del tablero, sumando cuando sean blancas o restando cuando sean negras.
		if (obtenerCasilla(0, 2) == 1) valor += 0.5;
		else if (obtenerCasilla(0, 2) == 2) valor -= 0.5;
		if (obtenerCasilla(0, 4) == 1) valor += 0.5;
		else if (obtenerCasilla(0, 4) == 2) valor -= 0.5;
		if (obtenerCasilla(7, 3) == 1) valor += 0.5;
		else if (obtenerCasilla(7, 3) == 2) valor -= 0.5;
		if (obtenerCasilla(7, 5) == 1) valor += 0.5;
		else if (obtenerCasilla(7, 5) == 2) valor -= 0.5;

		return (int) valor;
	}

	/**
	 * Función de evaluación que premia que las fichas estén por la parte central.
	 * @return Devuelve el valor del tablero.
	 */
	public int funcionEvaluacion4()
	{
		float valor = 0;

		// Sumamos y restamos las puntuciaones de los jugadores.
		int[] puntuaciones = puntacionJugadores();
		valor += puntuaciones[0];
		valor -= puntuaciones[1];

		// Recorremos la parte central del tablero, sumando cuando sean blancas o restando cuando sean negras.
		if (obtenerCasilla(2, 4) == 1) valor += 0.5;
		else if (obtenerCasilla(2, 4) == 2) valor -= 0.5;
		if (obtenerCasilla(3, 3) == 1) valor += 0.5;
		else if (obtenerCasilla(3, 3) == 2) valor -= 0.5;
		if (obtenerCasilla(4, 4) == 1) valor += 0.5;
		else if (obtenerCasilla(4, 4) == 2) valor -= 0.5;
		if (obtenerCasilla(5, 3) == 1) valor += 0.5;
		else if (obtenerCasilla(5, 3) == 2) valor -= 0.5;

		return (int) valor;
	}

	// Variable estática que indica qué función de evaluación se va a utilizar.
	// Entre 1 y 4.
	public static int funcionEvaluacion = 1;

	/**
	 * Ésta es la función de evaluación que calcula el valor del tablero según
	 * el jugador blanco. Se intenta que ganen las blancas, es decir, las blancas
	 * buscarán un tablero con el valor mayor y las negras un tablero con el valor menor.
	 * @return Devuelve el valor del tablero.
	 */
	public int funcionEvaluacion()
	{
		switch (funcionEvaluacion)
		{
			case 1:
				return funcionEvaluacion1();
			case 2:
				return funcionEvaluacion2();
			case 3:
				return funcionEvaluacion3();
			default:
				return funcionEvaluacion4();
		}
	}

}