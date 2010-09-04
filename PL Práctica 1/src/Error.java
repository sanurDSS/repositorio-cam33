/**
 * Clase auxiliar para el tratamiento de errores.
 */
public class Error
{
	/**
	 * Lanza el error 0.
	 * Este error se considera un error crítico que nunca debería aparecer.
	 *
	 * "Error 0: error crítico: <mensaje>"
	 *
	 * @param mensaje Mensaje que se incluye en el error.
	 */
	public static void Error0(String mensaje)
	{
		System.err.println("Error 0: error crítico: " + mensaje);
		System.exit(-1);
	}

	/**
	 * Lanza el error 1.
	 * Aparece cuando se lee un caracter que impide formar un token.
	 *
	 * "Error 1 (<fila>,<columna>): caracter '<caracter>' incorrecto"
	 * 
	 * @param fila Número de la fila donde se ha producido el error.
	 * @param columna Número de la columna donde se ha producido el error.
	 * @param caracter Caracter que ha producido el error.
	 */
	public static void Error1(int fila, int columna, char caracter)
	{
		System.err.println("Error 1 (" + fila + "," + columna + "): caracter '" + caracter + "' incorrecto");
		System.exit(-1);
	}

	/**
	 * Lanza el error 2.
	 * Aparece cuando el fichero acaba inesperadamente.
	 *
	 * "Error 2: fin de fichero inesperado"
	 */
	public static void Error2()
	{
		System.err.println("Error 2: fin de fichero inesperado");
		System.exit(-1);
	}

	/**
	 * Lanza el error 3.
	 * Aparece cuando se encuentra un token distinto al que se estaba esperando.
	 *
	 * "Error 3 (<token.fila>,<token.columna>): encontrado '<token.lexema>', esperaba <esperaba>"
	 * 
	 * @param token Token que se ha obtenido.
	 * @param esperaba Lista de alguno de los tokens que se esperaba obtener.
	 */
	public static void Error3(Token token, String esperaba)
	{
		String aux = "Error 3 (" +  token.fila + "," + token.columna + "): encontrado '" + token.lexema + "', esperaba " + esperaba;
		System.err.println(aux);
		System.exit(-1);
	}

	/**
	 * Lanza el error 4.
	 * Aparece cuando se produce el fin de fichero pero se esperaba algún token.
	 *
	 * "Error 4: encontrado fin de fichero, esperaba <esperaba>"
	 *
	 * @param esperaba Lista de alguno de los tokens que se esperaba obtener.
	 */
	public static void Error4(String esperaba)
	{
		String aux = "Error 4: encontrado " + Token.subcadenas[Token.EOF] + ", esperaba " + esperaba;
		System.err.println(aux);
		System.exit(-1);
	}

	/**
	 * Lanza el error 5.
	 * Aparece cuando se intenta declarar un identificador que ya estaba declarado en este ámbito.
	 *
	 * "Error 5 (<token.fila>,<token.columna>): '<token.lexema>' ya existe en este ambito"
	 *
	 * @param token Token que se corresponde a ese identificador repetido.
	 */
	public static void Error5(Token token)
	{
		String aux = "Error 5 (" +  token.fila + "," + token.columna + "): '" + token.lexema + "' ya existe en este ambito";
		System.err.println(aux);
		System.exit(-1);
	}

	/**
	 * Lanza el error 6.
	 * Aparece cuando se utiliza una variable en una instrucción y ésta no ha sido declarada.
	 *
	 * "Error 6 (<token.fila>,<token.columna>): '<token.lexema>' no ha sido declarado"
	 *
	 * @param token Token de la variable que no ha sido declarada.
	 */
	public static void Error6(Token token)
	{
		String aux = "Error 6 (" +  token.fila + "," + token.columna + "): '" + token.lexema + "' no ha sido declarado";
		System.err.println(aux);
		System.exit(-1);
	}

	/**
	 * Lanza el error 7.
	 * Aparece cuando se utiliza un identificador en una instrucción pero éste se corresponde con un método o clase.
	 *
	 * "Error 7 (<token.fila>,<token.columna>): '<token.lexema>' no es una variable"
	 *
	 * @param token Token que se ha utilizado como una variable pero es otro tiop de símbolo.
	 */
	public static void Error7(Token token)
	{
		String aux = "Error 7 (" +  token.fila + "," + token.columna + "): '" + token.lexema + "' no es una variable";
		System.err.println(aux);
		System.exit(-1);
	}
}
