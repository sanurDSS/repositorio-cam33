/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

/**
 *
 * @author mireia
 */
public class Main
{

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		Tablero tablero = new Tablero();

		//Crea e inicia la interfaz del juego
		Interfaz interfaz = new Interfaz(tablero);
		interfaz.setVisible(true);
	}
}
