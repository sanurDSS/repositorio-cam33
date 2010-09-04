public class Error
{
	public static void Error1(int fila, int columna, String lexema)
	{
		System.err.println("Error 1 (" + fila + "," + columna + "): '" + lexema + "' ya declarado");
		System.exit(-1);
	}

	public static void Error2(int fila, int columna, String lexema)
	{
		System.err.println("Error 2 (" + fila + "," + columna + "): simbolo '" + lexema + "' no ha sido declarado");
		System.exit(-1);
	}

	public static void Error3(int fila, int columna, String lexema)
	{
		System.err.println("Error 3 (" + fila + "," + columna + "): funcion '" + lexema + "' usada como variable");
		System.exit(-1);
	}

	public static void Error4(int fila, int columna, String lexema)
	{
		System.err.println("Error 4 (" + fila + "," + columna + "): numero erroneo de parametros para '" + lexema + "'");
		System.exit(-1);
	}

	public static void Error5(int fila, int columna, String lexema)
	{
		System.err.println("Error 5 (" + fila + "," + columna + "): variable '" + lexema + "' usada como funcion");
		System.exit(-1);
	}

	public static void Error6(int fila, int columna)
	{
		System.err.println("Error 6 (" + fila + "," + columna + "): aqui no puede usarse return");
		System.exit(-1);
	}
}
