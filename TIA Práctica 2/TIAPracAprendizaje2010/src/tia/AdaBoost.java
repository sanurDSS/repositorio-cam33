package tia;

import java.util.ArrayList;

public class AdaBoost
{
	public static ClasificadorFuerte runAlgorithm(int numClasificadores, int numCandidatos, ArrayList<Cara> listaEntrenamiento)
	{		
		// Inicializamos los pesos del conjunto de entrenamiento.
		for (Cara i : listaEntrenamiento)
			i.setProbabilidad(1.0/listaEntrenamiento.size());
		
		// Buscamos T clasificadores débiles para formar un clasificador fuerte.
		ClasificadorFuerte clasificadorFuerte = new ClasificadorFuerte();
		for (int i = 0; i < numClasificadores; i++)
		{
			// 1. Generamos aleatoriamente múltiples clasificadores y escogemos el mejor candidato.
			ClasificadorDebil candidato = null;
			for (int j = 0; j < numCandidatos; j++)
			{
				ClasificadorDebil candidatoAux = new ClasificadorDebil();
				candidatoAux.entrenaClasificador(listaEntrenamiento);
				if (candidato == null || candidatoAux.getError() < candidato.getError())
					candidato = candidatoAux;
			}
			clasificadorFuerte.addClasificador(candidato);
			
			// 2. Obtenemos el valor de confianza del clasificador.
			double valorConfianza = candidato.getValorConfianza();

			// 3. Se actualizan los pesos.
			double Z = 0;
			for (Cara j : listaEntrenamiento)
				Z += j.getProbabilidad();
			for (Cara j : listaEntrenamiento)
			{
				double A;
				if (candidato.h(j) != j.getTipo())
					A = Math.pow(Math.E, valorConfianza);
				else
					A = Math.pow(Math.E, -valorConfianza);
				j.setProbabilidad(j.getProbabilidad() * A / Z);
			}
			
			// 4. Se finaliza la búsqueda si se obtiene el 100% de aciertos con el clasificador.
			int aciertos = 0;
			for (Cara j : listaEntrenamiento)
			{
				if (clasificadorFuerte.H(j) == j.getTipo())
					aciertos++;
			}
			System.out.println("Clasificador " + (i + 1) + ": " + aciertos + "/" + listaEntrenamiento.size() + " (" + (100.0 * aciertos/listaEntrenamiento.size()) + "%)");
			/*if (aciertos == listaEntrenamiento.size())
			{
				System.out.println("Obtenido el 100%");
				break;
			}*/
		}
		
		return clasificadorFuerte;
	}
}
