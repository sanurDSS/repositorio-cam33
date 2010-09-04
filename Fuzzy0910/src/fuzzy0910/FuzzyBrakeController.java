/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fuzzy0910;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import org.antlr.runtime.tree.Tree;

/**
 *
 * @author fidel
 */
public class FuzzyBrakeController extends BrakeController{

    String fileName = "";
    FIS fis;

    public FuzzyBrakeController(){

        fileName = System.getProperty("user.dir")+"//src/fuzzy0910/brake.fcl";
        fis = FIS.load(fileName,true);
		fis.debug = true;

        if( fis == null ) {
            System.err.println("Can't load file: '"
                                   + fileName + "'");
            return;
        }
		
		fis.chart();
    }


    //Calcula la intensidad de frenado para una velocidad y posicion dada
    float step(float vel, float dist){
        
        float brake=0f;

        // Set inputs
        fis.setVariable("vel", vel);
        fis.setVariable("dist", dist);

        // Evaluate
        fis.evaluate();

        brake = (float) fis.getVariable("brake").defuzzify();
        //System.out.println(brake + " " + dist);
        
        return brake;

    }
}


