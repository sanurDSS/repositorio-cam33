package Damas;

/**
 *
 * @author mireia
 */
public class Main
{
	//Tamaño del tablero
	static final int tamaño = 8;
	
	//Tiempo de espera máximo para una jugada
	static final int m_tiempoMaximo = 1500;

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		//Crea el juego
		Juego m_juego = new Juego(tamaño, m_tiempoMaximo, 0, 1);

		//Crea e inicia la interfaz del juego
		Interfaz interfaz = new Interfaz(m_juego);
		interfaz.setVisible(true);
	}
}
