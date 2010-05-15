package tia;

public class Hiperplano
{
	private static final int DIMENSIONES = 3087;
	private static final int MIN_VALUE = 0;
	private static final int MAX_VALUE = 255;
	
	private double coeficientes[];
	private double terminoIndependiente;
	
	/**
	 * Constructor por defecto que genera un hiperplano aleatorio según los límites establecidos en las constantes de la clase.
	 */
	public Hiperplano()
	{
		coeficientes = new double[DIMENSIONES];
		terminoIndependiente = 0;
		
		// Generamos aleatoriamente un vector normal de dirección para el hiperplano.
		for (int i = 0; i < DIMENSIONES; i++)
		{
			coeficientes[i] = Math.random() * ((Math.random() < 0.5) ? -1 : 1);
		}
		
		// Calculamos aleatoriamente un punto en el espacio (dentro de los límites) para obtener el valor del término independiente.
		for (int i = 0; i < DIMENSIONES; i++)
		{
			terminoIndependiente -= coeficientes[i] * (((int) (Math.random() * 1000)) % (MAX_VALUE + 1));
		}
	}
	
	/**
	 * Evalúa un punto en el plano.
	 * @param p Punto con tantas dimensiones como el plano tiene.
	 * @return Devuelve el resultado de comprobar a qué lado del hiperplano queda el punto.
	 */
	public double h(int[] p)
	{
		double resultado = 0;
		
		for (int i = 0; i < coeficientes.length; i++)
		{
			resultado += coeficientes[i] * p[i];
		}
		
		if (resultado + terminoIndependiente > 0)
			return 1;
		else
			return -1;
		//return resultado + terminoIndependiente;
	}
}
