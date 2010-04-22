/**
 * 
 */
package tia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dviejo
 *
 */
public class Main {

	private String rutaDir;
	private File []files;

	//Parametros AdaBoost
	private double testRate;
	private int NUM_CANDIDATOS; 
	private int NUM_CLASIFICADORES;
	private boolean VERBOSE;	
	
	private List<ClasificadorDebil> clasificadores;
	private ArrayList<Cara> listaAprendizaje;
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
		
		getFileNames(rutaDir);
		listaAprendizaje = new ArrayList<Cara>();
		for(cont=0;cont<files.length;cont++)
		{
			if(!files[cont].isDirectory())
			{
				listaAprendizaje.add(new Cara(files[cont]));
			}
		}
		System.out.println(listaAprendizaje.size()+ " imágenes encontradas");

		//TODO Añadir el código de la práctica

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
			throw new RuntimeException("La ruta debe ser un directorio");
		ImageFilter filtro = new ImageFilter();
		files = directorio.listFiles(filtro);
	}
	/**
	 * Analiza los parametros y lanza la función
	 * @param args
	 */
	public static void main(String[] args) {
		int cont;
		Main programa;
		String option;
		boolean maluso = false;
		boolean ruta = false;
		int paso = 2;
		
		programa = new Main();

		for(cont = 0; cont < args.length && !maluso; cont+=paso)
		{
			option = args[cont];
			if(option.charAt(0) == '-')
			{
				switch(option.charAt(1))
				{
				case 'd':
					programa.setRuta(args[cont+1]);
					paso = 2;
					ruta = true;
					break;
				case 't':
					programa.setRate(Double.parseDouble(args[cont+1]));
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
			else maluso = true;
		}
		
		if(!maluso && ruta)
			programa.Init();
		else
		{
			System.err.println("Lista de parametros incorrecta");
			System.err.println("Uso: java Main -d ruta [-t testrate] [-T maxT] [-c numClasificadores] [-v]");
		}

	}

}
