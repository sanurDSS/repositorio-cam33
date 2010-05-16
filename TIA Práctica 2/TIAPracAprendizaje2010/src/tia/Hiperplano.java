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
		
		// Normalizamos el vector.
		double modulo = 0;
		for (int i = 0; i < DIMENSIONES; i++)
			modulo += coeficientes[i] * coeficientes[i];
		modulo = Math.sqrt(modulo);
		for (int i = 0; i < DIMENSIONES; i++)
			coeficientes[i] /= modulo;
		
		// Calculamos aleatoriamente un punto en el espacio (dentro de los límites 0-255) para obtener el valor del término independiente.
		for (int i = 0; i < DIMENSIONES; i++)
		{
			terminoIndependiente -= coeficientes[i] * (((int) (Math.random() * 1000)) % (MAX_VALUE + 1));
		}
	}
	
	/**
	 * Evalúa un punto en el plano.
	 * Genera el resultado de comprobar a qué lado del hiperplano queda el punto.
	 * @param punto Punto con tantas dimensiones como el plano.
	 * @return Devuelve 0 si el punto está contenido en el plano. Un valor positivo si está por encima o negativo si está por debajo del plano.
	 */
	public double evaluar(int[] punto)
	{
		double resultado = 0;
		
		for (int i = 0; i < coeficientes.length; i++)
		{
			resultado += coeficientes[i] * punto[i];
		}
		
		return resultado + terminoIndependiente;
	}
}
