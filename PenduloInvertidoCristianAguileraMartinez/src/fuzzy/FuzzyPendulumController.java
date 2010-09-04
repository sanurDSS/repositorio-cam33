/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy;

import net.sourceforge.jFuzzyLogic.FIS;

/**
 *
 * @author fidel
 */
public class FuzzyPendulumController
{
	FIS fis;

	public FuzzyPendulumController()
	{		
		fis = FIS.load(getClass().getResourceAsStream("pendulo.fcl"), true);

		if (fis == null)
		{
			System.err.println("Can't load file: 'pendulo.fcl'");
			return;
		}
	}

	//Calcula la intensidad de frenado para una velocidad y posicion dada
	float step(float velocidad, float angulo, float posicion, float velocidadlineal)
	{
		fis.setVariable("velocidad", velocidad);
		fis.setVariable("velocidadlineal", velocidadlineal);
		fis.setVariable("angulo", angulo);
		fis.setVariable("posicion", posicion);

		fis.evaluate();
		return (float) fis.getVariable("aceleracion").defuzzify();
	}
}


