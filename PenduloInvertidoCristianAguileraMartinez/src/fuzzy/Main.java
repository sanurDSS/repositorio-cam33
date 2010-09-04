/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy;

/**
 *
 * @author fidel
 */
public class Main
{

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		//Aplicacion
		PhysApp app = new PhysApp();

		//Controlador de freno
		FuzzyPendulumController controller = new FuzzyPendulumController();
		app.setController(controller);

		app.start();
	}
}


