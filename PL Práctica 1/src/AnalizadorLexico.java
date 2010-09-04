import java.io.RandomAccessFile;

public class AnalizadorLexico
{
	/**
	 * Vector que indica la cantidad de estados del AFD.
	 * Si su valor es verdadero, indica que es un estado de aceptación.
	 */
	private static final boolean[] estados = {false, true, true, true, true, true, true, false, false, true, true, false};

	/**
	 * Indica cuál es el estado inicial.
	 */
	private static final int ESTADO_INICIAL = 0;

	/**
	 * Indica cuál es el estado de absorción.
	 * Sólo hay un estado de absorción.
	 */
	private static final int ESTADO_ABSORCION = 11;

	/**
	 * Flujo de entrada del fichero que se va a leer.
	 */
	private RandomAccessFile flujoEntrada;

	/**
	 * Fila por la que se está leyendo en el fichero.
	 */
	private int fila;

	/**
	 * Columna por la que se está leyendo en el fichero.
	 */
	private int columna;

	/**
	 * Constructor por defecto.
	 * @param flujoEntrada Flujo de entrada de acceso aleatorio que debe estar abierto y listo para leer.
	 */
	public AnalizadorLexico(RandomAccessFile flujoEntrada)
	{
		this.flujoEntrada = flujoEntrada;
		fila = 1;
		columna = 1;
	}

	/**
	 * Obtiene el siguien token desde el flujo de entrada.
	 * Si se produce un error léxico, finaliza la ejecución del programa. Si no, devuelve
	 * un token con un lexema y una fila y columna de comienzo en el fichero.
	 * Además, si se alcanza el fin de fichero, el token es del tipo EOF.
	 * @return Devuelve el siguiente token desde el flujo de entrada.
	 * @throws java.io.IOException
	 */
	public Token siguienteToken() throws java.io.IOException
	{
		// Declaración e inicialización de las variable locales.
		Token token = new Token();
		boolean tokenEncontrado = false;
		int estadoActual = ESTADO_INICIAL;
		String pendiente = "";
		char caracter = '$';

		// Repetimos el bucle hasta que se llegue al final del fichero o se entre en el estado de absorción.
		while (flujoEntrada.getFilePointer() < flujoEntrada.length() && estadoActual != ESTADO_ABSORCION)
		{
			// Se lee el caracter.
			caracter = (char) flujoEntrada.readByte();

			// Si lo leído es una /, busco un * y, si lo encuentro, busco otro * y otra /.
			// Si ya se ha leído un caracter anterior, primero se intentará completar el token en curso, dejando el comentario pendiente para la siguiente petición de token.
			if (caracter == '/' && pendiente.length() == 0 && !tokenEncontrado)
			{
				char caracterAux = (char) flujoEntrada.readByte();
				if (caracterAux == '*')
				{
					// Se incrementa el número de columnas en 2 unidades, una para la / y otra para el *.
					columna += 2;

					// Se recorre el fichero en busca de * y /.
					boolean cierreEncontrado = false;
					while (flujoEntrada.getFilePointer() < flujoEntrada.length() && !cierreEncontrado)
					{
						caracterAux = (char) flujoEntrada.readByte();
						if (caracterAux == '*')
						{
							columna++;
							caracterAux = (char) flujoEntrada.readByte();
							if (caracterAux == '/')
							{
								columna++;
								cierreEncontrado = true;
							}
							else
							{
								flujoEntrada.seek(flujoEntrada.getFilePointer() - 1);
							}
						}
						else
						{
							if (caracterAux == '\n')
							{
								columna = 1;
								fila++;
							}
							else
							{
								columna++;
							}
						}
					}

					// Si lee todo el fichero y no se encuentra el cierre, se produce un error inexperado.
					if (!cierreEncontrado)
					{
						Error.Error2();
					}
					else
					{
						// Si el cierre está al final del fichero, se sale del bucle del token; si no, se lee el siguiente caracter como si el comentario no hubiera existido.
						if (flujoEntrada.getFilePointer() >= flujoEntrada.length())
							break;
						else
							continue;
					}
				}
				else
				{
					flujoEntrada.seek(flujoEntrada.getFilePointer() - 1);
				}
			}

			// Los separadores que se encuentren al principio de la secuencia se ignoran de la secuencia de caracteres pendientes.
			// Es decir, si no hemos encontrado ningún token y la secuencia pendiente está vacía, significa que estamos leyendo un separador al principio de la petición de token.
			if (esSeparador(caracter) && (pendiente.length() == 0 && !tokenEncontrado))
			{
				// Sólo se contabilizan los saltos de línea al principio de la secuencia. Ya que si encontramos un salto después
				// de obtener un token válido, éste volverá a introducirse en el fichero para leerlo en la siguiete petición de token y habría que decrementar el número de filas otra vez.
				if (caracter == '\n')
				{
					columna = 1;
					fila++;
				}
				else
				{
					columna++;
				}
			}
			else
			{
				// Guardamos el símbolo leído y realizamos un cambio de estado.
				pendiente += caracter;
				estadoActual = delta(estadoActual, caracter);

				// Si el nuevo estado es de aceptación, actualizamos el token encontrado.
				if (estados[estadoActual])
				{
					tokenEncontrado = true;
					token.lexema += pendiente;
					token.tipo = estadoActual;
					if (token.tipo == Token.IDENTIFICADOR)
					{
						if (token.lexema.equals("class"))
							token.tipo = Token.CLASS;
						else if (token.lexema.equals("double"))
							token.tipo = Token.DOUBLE;
						else if (token.lexema.equals("void"))
							token.tipo = Token.VOID;
					}
					pendiente = "";
				}
			}
		}

		// Comprobamos si se ha leído un token correctamente.
		if (tokenEncontrado)
		{
			// Si se ha encontrado un token, devolvemos al fichero la secuencia leída que estaba pendiente.
			flujoEntrada.seek(flujoEntrada.getFilePointer() - pendiente.length());

			// Establecemos la fila y columna del token e incrementamos el número de columna según la cantidad de caracteres leídas en el token.
			token.fila = fila;
			token.columna = columna;
			columna += token.lexema.length();
		}
		else
		{
			// Comprobamos si se ha llegado al final del fichero.
			if (flujoEntrada.getFilePointer() >= flujoEntrada.length() && pendiente.length() == 0)
			{
				token.tipo = Token.EOF;
			}
			else
			{
				// Actualizamos el número de columna según cuántos caracteres hayan quedado pendientes.
				//columna += pendiente.length() - 1;

				// Si no es el final del fichero y no hemos encontrado un token, se ha producido un error léxico.
				//Error.Error1(fila, columna, caracter);
				Error.Error1(fila, columna, pendiente.charAt(0));
			}
		}
		
		return token;
	}

	/**
	 * Comprueba si un caracter es una letra entre 'a' y 'z', mayúscula o minúscula.
	 * @param caracter Caracter que se va a comprobar.
	 * @return Devuelve verdadero si es una letra.
	 */
	private boolean esLetra(char caracter)
	{
		if (('a' <= caracter && caracter <= 'z') || ('A' <= caracter && caracter <= 'Z'))
			return true;
		else
			return false;
	}

	/**
	 * Comprueba si un caracter es un dígito entre 0 y 9.
	 * @param caracter Caracter que se va a comprobar.
	 * @return Devuelve verdadero si es un número.
	 */
	private boolean esNumero(char caracter)
	{
		if ('0' <= caracter && caracter <= '9')
			return true;
		else
			return false;
	}

	/**
	 * Comprueba si un caracter es un separador: \n, \t, \r o espacio en blanco.
	 * @param caracter Caracter que se va a comprobar.
	 * @return Devuelve verdadero si es separador.
	 */
	private boolean esSeparador(char caracter)
	{
		if (caracter == ' ' || caracter == '\n' || caracter == '\t' || caracter == '\r')
			return true;
		else
			return false;
	}

	/**
	 * Función de transición. Dado un estado actual y un símbolo de transición, se obtiene el nuevo estado de llegada.
	 * @param estado Estado de salida.
	 * @param caracter Caracter de transición.
	 * @return Devuelve el estado de llegada.
	 */
	private int delta(int estado, char caracter)
	{
		switch (estado)
		{
			// Estado inicial.
			case 0:
				if (caracter == '(')
					return 1;
				else if (caracter == ')')
					return 2;
				else if (caracter == ';')
					return 3;
				else if (caracter == '=')
					return 4;
				else if (caracter == '{')
					return 5;
				else if (caracter == '}')
					return 6;
				else if (esNumero(caracter))
					return 7;
				else if (esLetra(caracter))
					return 10;
				break;

			// Estados para determinar los números reales.
			case 7:
				if (caracter == '.')
					return 8;
				else if (esNumero(caracter))
					return 7;
				break;
			case 8:
				if (esNumero(caracter))
					return 9;
				break;
			case 9:
				if (esNumero(caracter))
					return 9;
				break;

			// Estados para determinar los identificadores.
			case 10:
				if (esLetra(caracter))
					return 10;
				else if (esNumero(caracter))
					return 10;
				break;
		}
		
		// Llegados a este punto, significa que no existe transición desde el estado y el símbolo indicado y se alcanza el estado de absorción.
		return ESTADO_ABSORCION;
	}
}
