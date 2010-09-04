package Damas;

import java.util.ArrayList;

/**
 *
 * @author mireia
 */
public class Juego
{
	//Crea el tablero de juego.
	public Tablero m_tablero;

	//Para saber si estamos jugando.
	public boolean m_jugando = false;

	//Mensaje que se mostrará en el juego:
	//1. Turno del jugador X.
	//2. Ha ganado el jugador X.
	//3. Empate.
	//4. Error. Colocación de ficha incorrecta.
	public int m_mensaje;

	//Para almacenar las fichas que ha matado cada jugador
	public int m_puntosB;
	public int m_puntosN;

	//Para almacenar los puntos que tiene cada jugador
	public int m_totalB;
	public int m_totalN;

	//Para saber a quién le toca el turno, 1 blancas, 2 negras
	private int m_turno;

	//Tiempo máximo de respuesta del ordenador
	public int m_tiempoMaximo;

	//Los jugadores máquina
	private JugadorMaquina m_maquina1, m_maquina2;

	//Indica el modo de juego.
	//1. Humano contra humano.
	//2. Humano contra máquina.
	//3. Máquina contra máquina.
	private int m_modoJuego;

	/**
	 * Constructor del juego.
	 * Crea el tablero, especifica el turno de juego (si empieza a jugar el jugador 1 o 2), si los jugadores son humanos o la máquina.
	 */
	public Juego(int tamaño, int tiempoMaximo, int modoJ, int turnoJugador)
	{
		m_tablero = new Tablero(tamaño);
		m_tiempoMaximo = tiempoMaximo;
		m_turno = turnoJugador;
		m_modoJuego = modoJ;
		m_jugando = false;
		m_puntosB = 0;
		m_puntosN = 0;
		m_totalB = 0;
		m_totalN = 0;
	}

	/*************************************************************
	//Funciones de acceso a las variables de juego
	 *************************************************************/
	//Devuelve si estamos jugando
	public boolean getJugando()
	{
		return m_jugando;
	}

	//Devuelve a quién le toca turno
	public int getTurno()
	{
		return m_turno;
	}

	//Devuelve el modo de juego
	public int getModo()
	{
		return m_modoJuego;
	}

	/**
	 * Inicializa el juego. Guarda el modo de juego y crea los jugadores máquina.
	 * @param modoJ
	 */
	public void setJuego(int modoJ)
	{
		//Guarda el modo de juego
		m_modoJuego = modoJ;

		//Si el modo de juego es Humano contra máquina, crea una máquina que jugará como jugador 2 (negras).
		if (m_modoJuego == 2)
		{
			m_maquina1 = new JugadorMaquina(2);
		}

		//Si el modo de juego es Maquina contra máquina, crea 2 máquinas, una juega como jugador 1 (blancas) y otra como 2 (negras).
		//Para esto se utiliza el parámetro.
		if (m_modoJuego == 3)
		{
			m_maquina1 = new JugadorMaquina(1);
			m_maquina2 = new JugadorMaquina(2);
		}

		//Indica que ya empieza el juego.
		m_jugando = true;
		//Indica que se muestre el mensaje de turno.
		m_mensaje = 1;

		//Inicializa los puntos de los jugadores
		m_puntosB = 0;
		m_puntosN = 0;
		m_totalB = 0;
		m_totalN = 0;
	}

	/**
	 * Controla el juego de los jugadores máquina.
	 */
	public void controlJuego()
	{
		int resultado;

		//Si el modo de juego es humano contra máquina.
		if (m_modoJuego == 2)
		{
			//Si le toca el turno a la máquina.
			if (m_turno == 2)
			{
				//Realiza la jugada la máquina.
				resultado = jugadaMaquina(m_maquina1);
				//Si el juego no ha terminado cambia de turno.
				if (m_jugando)
				{
					cambiaTurno();
				}
			}
		}

		//Si el modo de juego es máquina contra máquina.
		if (m_modoJuego == 3)
		{
			//Si le toca el turno a la máquina que juega como jugador 1 (blancas).
			if (m_turno == 1)
			{
				resultado = jugadaMaquina(m_maquina1);
			}

			//Si le toca el turno a la máquina que juega como jugador 2 (negras).
			if (m_turno == 2)
			{
				resultado = jugadaMaquina(m_maquina2);
			}

			//Si el juego no ha terminado, cambia de turno
			if (m_jugando)
			{
				cambiaTurno();
			}
		}
	}

	/**
	 * El jugador humano realiza una tirada si es su turno, y se puede colocar la ficha en la columna especificada.
	 * Devuelve 0 si todo ha ido correctamente.
	 * @param filAnt Fila en la que estaba la ficha
	 * @param colAnt  Columna en la que estaba la ficha
	 * @param columna Columna donde se desea mover
	 * @param fila Fila donde se desea mover
	 * @param jugador Indica si el humano juega como jugador 1 (blancas) o como jugador 2 (negras)
	 * @return
	 */
	public int jugadaHumano(int filAnt, int colAnt, int columna, int fila, int jugador)
	{
		int resultado = 0;
		int contrario;
		int fin;
		Movimiento movimiento = new Movimiento();

		if (jugador == 1)
		{
			contrario = 2;
		}
		else
		{
			contrario = 1;
		}

		//Si le toca su turno.
		if (m_turno == jugador)
		{

			//Crea el movimiento con los valores que se le han pasado por parámetro
			movimiento.setInicial(filAnt, colAnt);
			movimiento.setFinal(fila, columna);

			//Introduce la ficha en el tablero
			resultado = m_tablero.hacerTirada(movimiento, jugador);

			//Si la colocación de la ficha ha sido correcta, calcula si ha matado alguna ficha
			if (resultado != -1)
			{
				if (resultado == 1)
				{
					m_puntosN++;
				}

				if (resultado == 2)
				{
					m_puntosB++;
				}

			}
			//Comprobar si ha terminado la partida
			fin = finPartida();

			if (fin != 0)
			{
				//Si ha terminado la partida muestra mensaje de fin
				m_jugando = false;
				if (fin == 1)
				{
					m_mensaje = 2;
				}
				else if (fin == 2)
				{
					m_mensaje = 3;
				}
				else if (fin == 3)
				{
					m_mensaje = 6;
				}
			}
		}
		return resultado;
	}

	/**
	 * Crea un thread donde se ejecutará JugadorMaquina para que calcule la jugada.
	 * @param jugador JugadorMaquina que está jugando.
	 * @return Si todo ha ido correctamente devuelve 0.
	 */
	public int jugadaMaquina(JugadorMaquina jugador)
	{
		int resultado = 0;
		int fin;
		Movimiento movimiento;

		//Si es el turno del jugador.
		if (m_turno == jugador.m_jugador)
		{
			//Se duplica el tablero.
			jugador.isDone(false);
			jugador.asignarTablero(m_tablero);
			jugador.m_movimiento = null;

			//Se crea e inicia el thread para que el jugador máquina calcule la jugada.
			Thread myThread = new Thread(jugador);
			myThread.start();

			//Se espera al thread como mucho el tiempo específicado en m_tiempoMaximo.
			long timeStart = System.currentTimeMillis();
			long elapsed = 0;

			while (!jugador.isDone() && (elapsed < m_tiempoMaximo))
			{
				elapsed = System.currentTimeMillis() - timeStart;
			}

			// Si no ha finalizado el thread, se mata.
			if (elapsed >= m_tiempoMaximo && !jugador.isDone())
			{
				myThread.stop();
				myThread = null;
				System.out.println("   [ABORTADO] Se mata el hilo porque se excedió el tiempo límite de cálculo.");

				// Comprobamos si el hilo ha llegado a calcular algún hilo; si no, elegimos el primer movimiento y fuera.
				if (jugador.m_movimiento == null)
				{
					System.out.println("   [AVISO] Se abortó el hilo antes de calcular al menos un movimiento. Se elegirá el primer movimiento posible.");
					ArrayList<Movimiento> movimientos = m_tablero.movimientosPosibles(jugador.m_jugador);
					jugador.m_movimiento = movimientos.get(0).swap();
				}
			}

			//Se obtiene la jugada obtenida por JugadorMaquina
			movimiento = jugador.m_movimiento;

			//Introduce la ficha en el tablero
			resultado = m_tablero.hacerTirada(movimiento, jugador.m_jugador);

			//Si la colocación de la ficha ha sido correcta, comprueba si ha matado alguna ficha
			if (resultado != -1)
			{
				if (resultado == 1)
				{
					m_puntosN++;
				}

				if (resultado == 2)
				{
					m_puntosB++;
				}

				//Comprobar si ha terminado la partida
				fin = finPartida();
				if (fin != 0)
				{
					//Si ha terminado la partida muestra mensaje de fin
					m_jugando = false;
					if (fin == 1)
					{
						m_mensaje = 2;
					}
					else if (fin == 2)
					{
						m_mensaje = 3;
					}
					else if (fin == 3)
					{
						m_mensaje = 6;
					}
				}
			}
			else //Si la ficha se coloca incorrectamente el juego termina.
			{
				m_mensaje = 4;
				m_jugando = false;
			}
		}
		return resultado;
	}

	/**
	 * Cambia el turno de jugador. Se utiliza después de cada jugada.
	 */
	public void cambiaTurno()
	{
		if (m_turno == 1)
		{
			m_turno = 2;
		}
		else
		{
			m_turno = 1;
		}
		m_mensaje = 1;
		calcularPuntos();
	}

	/**
	 * Reinicializa todas las variables para empezar un nuevo juego.
	 */
	public void reiniciarJuego()
	{
		m_tablero.inicializarTablero();
		m_turno = 1;
		m_mensaje = 0;
		m_jugando = false;
		m_puntosB = 0;
		m_puntosN = 0;
		m_totalB = 0;
		m_totalN = 0;
	}

	/**
	 * Comprueba si la partida ha terminado.
	 * @return 0 La partida no ha terminado
	 * @return 1 La partida ha terminado y han ganado las blancas
	 * @return 2 La partida ha terminado y han ganado las negras
	 * @return 3 La partida ha terminado y ha habido empate
	 */
	public int finPartida()
	{
		int blancas;
		int negras;
		int fin;
		int ficha;
		fin = 0;
		blancas = 0;
		negras = 0;

		//Ganan las negras porque ha matado a todas las blancas
		if (m_puntosB == 12)
		{
			fin = 2;
		}

		//Ganan las blancas porque ha matado a todas las negras
		if (m_puntosN == 12)
		{
			fin = 1;
		}

		//Si uno de los jugadores se queda sin posibles movimientos, gana
		//el que más puntos tenga en ese momento
		//Para cada una de has fichas del tablero debe comprobar si tiene movimientos
		for (int i = 0; i < m_tablero.tamaño(); i++)
		{
			for (int j = 0; j < m_tablero.tamaño(); j++)
			{
				ficha = m_tablero.obtenerCasilla(j, i);
				//Si existe una ficha en la casilla
				if (ficha != 0)
				{
					//Si la ficha es blanca
					if (ficha == 1)
					{   //Si hay movimiento posible
						if (m_tablero.movimientoPosible(j, i, ficha) != -1)
						{
							blancas++;
						}
					}
					else //La ficha es negra
					{
						if (m_tablero.movimientoPosible(j, i, ficha) != -1)
						{
							negras++;
						}
					}
				}

				//Como ambos colores tienen algún movimiento posible la partida no ha terminado
				if (blancas > 0 && negras > 0)
				{
					return 0;
				}
			}
		}

		//Si no hay más movimientos posibles gana el que más puntos tenga hasta ese momento
		if (blancas == 0 || negras == 0)
		{
			calcularPuntos();
			//Si las blancas han matado a más negras ganan las blancas.
			if (m_totalB > m_totalN)
			{
				fin = 1;
			}
			else //Si las negras han matado a más blancas ganan las negras.
			if (m_totalB < m_totalN)
			{
				fin = 2;
			}
			else //Si la puntuación es la misma hay empate.
			{
				fin = 3;
			}
		}
		return fin;
	}

	/**
	 * Función que calcula la puntuación de cada jugador.
	 * Guarda la puntuación en las variables m_totalB y m_totalN.
	 */
	public void calcularPuntos()
	{
		int ficha;
		int blancas = 0;
		int negras = 0;


		//Por cada ficha blanca que esté en la fila 0 se suman 3 puntos
		for (int j = 0; j < m_tablero.tamaño(); j++)
		{
			ficha = m_tablero.obtenerCasilla(j, 0);
			//Si existe una ficha en la casilla
			if (ficha != 0)
			{
				//Si la ficha es blanca
				if (ficha == 1)
				{
					blancas = blancas + 3;
				}
			}
		}

		//Por cada ficha blanca que esté en la fila 1 se suman 2 puntos
		for (int j = 0; j < m_tablero.tamaño(); j++)
		{
			ficha = m_tablero.obtenerCasilla(j, 1);
			//Si existe una ficha en la casilla
			if (ficha != 0)
			{
				//Si la ficha es blanca
				if (ficha == 1)
				{
					blancas = blancas + 2;
				}
			}
		}


		//Por cada ficha blanca que esté en la fila 2 se suma 1 punto
		for (int j = 0; j < m_tablero.tamaño(); j++)
		{
			ficha = m_tablero.obtenerCasilla(j, 2);
			//Si existe una ficha en la casilla
			if (ficha != 0)
			{
				//Si la ficha es blanca
				if (ficha == 1)
				{
					blancas = blancas + 1;
				}
			}
		}

		//Por cada ficha negra que esté en la fila 7 se suman 3 puntos
		for (int j = 0; j < m_tablero.tamaño(); j++)
		{
			ficha = m_tablero.obtenerCasilla(j, 7);
			//Si existe una ficha en la casilla
			if (ficha != 0)
			{
				//Si la ficha es negra
				if (ficha == 2)
				{
					negras = negras + 3;
				}
			}
		}


		//Por cada ficha negra que esté en la fila 6 se suman 2 puntos
		for (int j = 0; j < m_tablero.tamaño(); j++)
		{
			ficha = m_tablero.obtenerCasilla(j, 6);
			//Si existe una ficha en la casilla
			if (ficha != 0)
			{
				//Si la ficha es negra
				if (ficha == 2)
				{
					negras = negras + 2;
				}
			}
		}

		//Por cada ficha negra que esté en la fila 5 se suma 1 punto
		for (int j = 0; j < m_tablero.tamaño(); j++)
		{
			ficha = m_tablero.obtenerCasilla(j, 5);
			//Si existe una ficha en la casilla
			if (ficha != 0)
			{
				//Si la ficha es negra
				if (ficha == 2)
				{
					negras = negras + 1;
				}
			}
		}

		int[] puntuaciones = m_tablero.puntacionJugadores();

		//Al resultado de los puntos de la ficha hay que sumarle las fichas que ha matado
		m_totalB = blancas + m_puntosB;
		m_totalN = negras + m_puntosN;

		if (puntuaciones[0] != m_totalB || puntuaciones[1] != m_totalN)
		{
			System.out.println("la hemos liaooooooooooo, no coincide la puntuación!!!!!");
			System.out.println(puntuaciones[0] + " == " + m_totalB);
			System.out.println(puntuaciones[1] + " == " + m_totalN);
			System.exit(666);
		}
	}
}

   