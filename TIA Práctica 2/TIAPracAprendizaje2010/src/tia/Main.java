/**
 * 
 */
package tia;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author dviejo
 *
 */
public class Main
{

	private String rutaDir;
	private File[] files;
	//Parametros AdaBoost
	private double testRate;
	private int NUM_CANDIDATOS;
	private int NUM_CLASIFICADORES;
	private boolean VERBOSE;

	private ArrayList<Cara> listaEntrenamiento;
	private ArrayList<Cara> listaTest;

	public Main()
	{
		rutaDir = "";
		testRate = 0.1;
		NUM_CANDIDATOS = 1000;
		NUM_CLASIFICADORES = 20;
		VERBOSE = false;
	}

	public void Init()
	{
		int cont;
		System.out.println("TIA. Practica de aprendizaje 2010");

		// Cargamos todas las imágenes de las caras en la lista.
		getFileNames(rutaDir);
		listaEntrenamiento = new ArrayList<Cara>();
		for (cont = 0; cont < files.length; cont++)
		{
			if (!files[cont].isDirectory())
			{
				listaEntrenamiento.add(new Cara(files[cont]));
			}
		}
		
		// Se reordena aleatoriamente la lista de caras.
		Collections.shuffle(listaEntrenamiento);
	
		// Se divide entre un conjunto de aprendizaje y un conjunto de test.
		listaTest = new ArrayList<Cara>();
		int totalCaras = listaEntrenamiento.size();
		for (int i = 0; i < totalCaras * testRate; i++)
		{
			listaTest.add(listaEntrenamiento.remove(0));
		}
		
		// Inicializamos los pesos del conjunto de entrenamiento.
		for (Cara i : listaEntrenamiento)
			i.setProbabilidad(1.0/listaEntrenamiento.size());
		
		System.out.println("Clasificadores débiles: " + NUM_CLASIFICADORES);
		System.out.println("Candidatos aleatorios:  " + NUM_CANDIDATOS);
		
		// Buscamos T clasificadores débiles para formar un clasificador fuerte.
		ClasificadorFuerte clasificadorFuerte = new ClasificadorFuerte();
		for (int i = 0; i < NUM_CLASIFICADORES; i++)
		{
			// 1. Generamos aleatoriamente múltiples clasificadores y escogemos el que mejor candidato.
			ClasificadorDebil clasificador = null;
			for (int j = 0; j < NUM_CANDIDATOS; j++)
			{
				ClasificadorDebil candidato = new ClasificadorDebil();
				candidato.entrenaClasificador(listaEntrenamiento);
				if (clasificador == null || candidato.getError() < clasificador.getError())
					clasificador = candidato;
			}
			clasificadorFuerte.addClasificadorDebil(clasificador);
			
			// 2. Obtenemos el valor de confianza del clasificador.
			double valorConfianza = clasificador.getValorConfianza();

			// 3. Se actualizan los pesos.
			double Z = 0;
			for (Cara j : listaEntrenamiento)
				Z += j.getProbabilidad();
			Z = Z / listaEntrenamiento.size();
			for (Cara j : listaEntrenamiento)
			{
				double A;
				if (clasificador.h(j.getData()) != j.getTipo())
					A = Math.pow(Math.E, valorConfianza);
				else
					A = Math.pow(Math.E, -valorConfianza);
				j.setProbabilidad(j.getProbabilidad() * A / Z);
			}
			
			// 4. Se finaliza la búsqueda si se obtiene el 100% de aciertos con el clasificador.
			int aciertos = 0;
			for (Cara j : listaEntrenamiento)
			{
				if (clasificadorFuerte.H(j.getData()) == j.getTipo())
					aciertos++;
			}
			if (aciertos == listaEntrenamiento.size())
				i = NUM_CLASIFICADORES;
		}
		
		// Evaluamos el error de entrenamiento.
		double aciertosEntrenamiento = 0;
		for (Cara j : listaEntrenamiento)
			if (clasificadorFuerte.H(j.getData()) == j.getTipo())
				aciertosEntrenamiento++;

		// Evaluamos el error de test.
		double aciertosTest = 0;
		for (Cara j : listaTest)
			if (clasificadorFuerte.H(j.getData()) == j.getTipo())
				aciertosTest++;
		
		System.out.println("Entrenamiento: " + (100*aciertosEntrenamiento/listaEntrenamiento.size()) + "%");
		System.out.println("Test:          " + (100*aciertosTest/listaTest.size()) + "%");
	}

	public void setRuta(String r)
	{
		rutaDir = r;
	}

	public void setRate(double t)
	{
		testRate = t;
	}

	public void setNumCandidatos(int t)
	{
		NUM_CANDIDATOS = t;
	}

	public void setNumClasificadores(int c)
	{
		NUM_CLASIFICADORES = c;
	}

	public void setVerbose(boolean v)
	{
		VERBOSE = v;
	}

	private void getFileNames(String ruta)
	{
		File directorio = new File(ruta);
		if (!directorio.isDirectory())
		{
			throw new RuntimeException("La ruta debe ser un directorio");
		}
		ImageFilter filtro = new ImageFilter();
		files = directorio.listFiles(filtro);
	}

	/**
	 * Analiza los parametros y lanza la función
	 * @param args
	 */
	public static void main(String[] args)
	{
		int cont;
		Main programa;
		String option;
		boolean maluso = false;
		boolean ruta = false;
		int paso = 2;

		programa = new Main();

		for (cont = 0; cont < args.length && !maluso; cont += paso)
		{
			option = args[cont];
			if (option.charAt(0) == '-')
			{
				switch (option.charAt(1))
				{
					case 'd':
						programa.setRuta(args[cont + 1]);
						paso = 2;
						ruta = true;
						break;
					case 't':
						programa.setRate(Double.parseDouble(args[cont + 1]));
						paso = 2;
						break;
					case 'T':
						programa.setNumCandidatos(Integer.parseInt(args[cont + 1]));
						paso = 2;
						break;
					case 'c':
						programa.setNumClasificadores(Integer.parseInt(args[cont + 1]));
						paso = 2;
						break;
					case 'v':
						programa.setVerbose(true);
						paso = 1;
						break;
					default:
						maluso = true;
				}
			}
			else
			{
				maluso = true;
			}
		}

		if (!maluso && ruta)
		{
			programa.Init();
		}
		else
		{
			System.err.println("Lista de parametros incorrecta");
			System.err.println("Uso: java Main -d ruta [-t testrate] [-T maxT] [-c numClasificadores] [-v]");
		}

	}
}
