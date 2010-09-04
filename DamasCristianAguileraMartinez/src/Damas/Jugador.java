package Damas;

/**
 *
 * @author mireia
 */
public abstract class Jugador implements Runnable
{
	//Próxima jugada
	public Movimiento m_movimiento;

	//Indica si juega como jugador 1 (blancas) o 2 (negras)
	public int m_jugador;

	//Indica si ha terminado el análisis
	private boolean m_fin;
	
	//Tablero para analizar
	public Tablero m_tablero;

	//Constructor
	public Jugador(int jugador)
	{
		m_jugador = jugador;
	}

	public synchronized boolean isDone()
	{
		return m_fin;
	}

	public synchronized void isDone(boolean fin)
	{
		m_fin = fin;
	}

	//Dispone el tablero para el análisis
	public void asignarTablero(Tablero t)
	{
		m_tablero = new Tablero(t);
	}
}
