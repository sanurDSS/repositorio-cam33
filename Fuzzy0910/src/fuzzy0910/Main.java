/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fuzzy0910;

/**
 *
 * @author fidel
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Aplicacion
        PhysApp app = new PhysApp();

        //Controlador de freno
        FuzzyBrakeController controller = new FuzzyBrakeController();
        app.setController(controller);
        
        app.start();
    }

}


