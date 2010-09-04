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

	/** Error 1
	 * No es posible declarar un identificador con el mismo nombre que otro en el mismo ámbito. En ese caso, debe emitirse el siguiente error:
     *
     *         Error 1 (fila,columna): 'lexema' ya existe en este ambito
	 *
     * donde la fila, la columna y el lexema son los del identificador redeclarado.
	 *
	 * 
	 * Recibe un token del identificador que ha sido redeclarado.
	 */
	public static void Error1(int fila, int columna, String lexema)
	{
		System.err.println("Error 1 (" + fila + "," + columna + "): '" + lexema + "' ya existe en este ambito");
		System.exit(-1);
	}

	/** Error 2
	 * Tampoco es posible utilizar un símbolo sin haberlo declarado previamente:
	 *
	 *         Error 2 (fila,columna): simbolo 'lexema' no ha sido declarado
	 *
	 * donde la fila, la columna y el lexema son los del identificador del símbolo no declarado.
	 *
	 *
	 * Recibe un token del identificador que ha sido usado sin declarar.
	 */
	public static void Error2(int fila, int columna, String lexema)
	{
		System.err.println("Error 2 (" + fila + "," + columna + "): simbolo '" + lexema + "' no ha sido declarado");
		System.exit(-1);
	}

	/** Error 3
	 * Las expresiones aritméticas (asociadas a los tokens  addop, mulop, postincr y postdecr) permiten operar con subexpresiones de tipo entero y
	 * real mediante las promociones de tipo oportunas. El tipo del resultado de operar dos enteros es entero, y real si alguno de los operandos es real.
	 * En el caso de que alguno de los operandos sea una subexpresión de tipo booleano se tiene que emitir el error semántico correspondiente:
	 * 
	 *         Error 3 (fila,columna): tipo incompatible en operador aritmetico 'lexema'
	 * 
	 * donde la fila, la columna y el lexema son los del operador aritmético que se ve afectado por el operando de tipo booleano.
	 * La división entre valores enteros siempre es una división entera; si alguno de los operandos es real, la división es real.
	 *
	 *
	 * Recibe un token del operador aritmético que tiene un operando booleano.
	 */
	public static void Error3(int fila, int columna, String lexema)
	{
		System.err.println("Error 3 (" + fila + "," + columna + "): tipo incompatible en operador aritmetico '" + lexema + "'");
		System.exit(-1);
	}

	/** Error 4
	 * Si el operando de los operadores lógicos (asociados a los tokens  or, and y not) es de tipo numérico, el error a emitir será:
	 *
	 *         Error 4 (fila,columna): tipo incompatible en operador logico 'lexema'
	 *
	 * donde la fila, la columna y el lexema son del operador afectado por el operando de tipo numérico.
	 * El tipo de una expresión lógica es siempre booleano.
	 *
	 *
	 * Recibe un token del operador lógico que tiene un operando no booleano.
	 */
	public static void Error4(int fila, int columna, String lexema)
	{
		System.err.println("Error 4 (" + fila + "," + columna + "): tipo incompatible en operador logico '" + lexema + "'");
		System.exit(-1);
	}

	/** Error 5
	 * El tipo de la condición indicada en la instrucción condicional (asociada al token if) y en la de bucle (segunda expresión asociada al token for) ha
	 * de ser de tipo booleano. Si la condición fuera de otro tipo el error a emitir será:
	 *
	 *         Error 5 (fila,columna): la expresion debe ser de tipo booleano en la instruccion 'lexema'
	 *
	 * donde la fila, la columna y el lexema son los del token de la instrucción (if o for).
	 *
	 * 
	 * Recibe un token de la instrucción (if o for) con una condición con valor no booleano.
	 */
	public static void Error5(int fila, int columna, String lexema)
	{
		System.err.println("Error 5 (" + fila + "," + columna + "): la expresion debe ser de tipo booleano en la instruccion '" + lexema + "'");
		System.exit(-1);
	}

	/** Error 6
	 * Las asignaciones entre elementos de tipo numérico (entero o real) están permitidas y el compilador debe generar código para realizar las conversiones
	 * de tipo necesarias. La conversión de real a entero implica quedarse con la parte entera del número real. También está permitida, obviamente, la asignación
	 * entre booleanos. Si se realiza una asignación entre elementos de tipo numérico y tipo booleano el compilador emitirá el siguiente error semántico:
	 *
	 *         Error 6 (fila,columna): tipos incompatibles en la instruccion de asignacion
	 *
	 * donde la fila y la columna son los del token asig.
	 *
	 * 
	 * Recibe un token de la instrucción de asignación con tipos incompatibles (uno booleano y el otro no).
	 */
	public static void Error6(int fila, int columna)
	{
		System.err.println("Error 6 (" + fila + "," + columna + "): tipos incompatibles en la instruccion de asignacion");
		System.exit(-1);
	}

	/** Error 7
	 * La instrucción de lectura debe ser acorde con el tipo de la referencia donde se almacenará el valor leído, en otro caso el error a emitir será:
	 *
	 *         Error 7 (fila,columna): tipos incompatibles en la instruccion de lectura
	 *
	 * donde la fila y la columna son los del token readline.
	 *
	 * 
	 * Recibe un token de la instrucción de lectura incompatible con el tipo.
	 */
	public static void Error7(int fila, int columna)
	{
		System.err.println("Error 7 (" + fila + "," + columna + "): tipos incompatibles en la instruccion de lectura");
		System.exit(-1);
	}
	
	/** Error 8
	 * En la reserva de memoria de un array el tamaño de cada dimensión ha de ser mayor que cero, en otro caso se emitirá el error:
	 *
	 *         Error 8 (fila,columna): tamanyo incorrecto
	 *
	 * donde la fila y la columna son las del corchete izquierdo de la dimensión con tamaño incorrecto.
	 *
	 * 
	 * Recibe un token del corchete izquierdo de la dimensión incorrecta (menor o igual a 0).
	 */
	public static void Error8(int fila, int columna)
	{
		System.err.println("Error 8 (" + fila + "," + columna + "): tamanyo incorrecto");
		System.exit(-1);
	}

	/** Error 9
	 * No es posible utilizar un array sin haber especificado todos los índices necesarios (tantos como dimensiones tenga el array en su declaración). Por tanto, el
	 * tipo de la subexpresión formada por el identificador y su lista de corchetes con su índice debe ser un tipo simple (entero, real o booleano). En caso de
	 * que no se especifiquen todos los índices o se utilice una variable de tipo array sin corchetes, se debe emitir el siguiente mensaje de error:
	 *
	 *         Error 9 (fila,columna): numero insuficiente de indices en el array 'lexema'
	 *
	 * donde la fila, la columna y el lexema corresponden al identificador de la variable de tipo array a la que le faltan índices.
	 *
	 * 
	 * Recibe un token del identificador del array usado de forma incorrecta.
	 */
	public static void Error9(int fila, int columna, String lexema)
	{
		System.err.println("Error 9 (" + fila + "," + columna + "): numero insuficiente de indices en el array '" + lexema + "'");
		System.exit(-1);
	}

	/** Error 10
	 * Una excepción para emitir este error es cuando utilizamos el identificador de la variable de tipo array para reservar memoria, ya que en este caso el
	 * identificador se utiliza sin índices a la izquierda de la asignación y a la derecha, con la instrucción new, es donde se debe especificar el tamaño de
	 * todas las dimensiones. En caso de que se reserve memoria para más o menos de las dimensiones con que se declaró una variable de tipo array, el error a emitir será:
	 *
	 *         Error 10 (fila,columna): numero de dimensiones incorrecto
	 *
	 * donde la fila y la columna serán las del corchete izquierdo anterior en el caso de reservar para más dimensiones de las debidas, o bien las del último
	 * corchete derecho en el caso de que falten dimensiones. No se debe emitir ningún mensaje de error en caso de hacer referencia a un array sin haber
	 * reservado memoria para él (fallará en ejecución).
	 *
	 * 
	 * Recibe un token del último corchete derecho si tiene menos dimensiones que las especificadas en la declaración.
	 *              O bien, token del corchete izquierdo si se ha sobrepasado.
	 */
	public static void Error10(int fila, int columna)
	{
		System.err.println("Error 10 (" + fila + "," + columna + "): numero de dimensiones incorrecto");
		System.exit(-1);
	}

	/** Error 11
	 * No se permite poner índices (corchetes) a variables que no sean de tipo array:
	 *
	 *         Error 11 (fila,columna): el identificador 'lexema' no es de tipo array
	 *
	 * donde la fila, la columna y el lexema son los del identificador utilizado como un array. Este error también se debe emitir en el caso de reservar
	 * memoria para una variable que no es de tipo array.
	 *
	 * 
	 * Recibe un token del identificador que no es un array pero es usado como tal.
	 */
	public static void Error11(int fila, int columna, String lexema)
	{
		System.err.println("Error 11 (" + fila + "," + columna + "): el identificador '" + lexema + "' no es de tipo array");
		System.exit(-1);
	}

	/** Error 12
	 * No se permite poner más índices de los necesarios a una variable de tipo array:
	 *
	 *         Error 12 (fila,columna): demasiados indices
	 *
	 * donde la fila y la columna son los del corchete izquierdo del primer índice sobrante. Si el índice sobrante es el primero, es decir, la variable no
	 * es de tipo array, el error a emitir será el Error 11.
	 *
	 * 
	 * Recibe un token con el corchete izquierdo que sobra en la referencia al array.
	 */
	public static void Error12(int fila, int columna)
	{
		System.err.println("Error 12 (" + fila + "," + columna + "): demasiados indices");
		System.exit(-1);
	}

	/** Error 13
	 * Un índice debe ser de tipo entero; si es de tipo real se debe convertir a entero, y si es de cualquier otro tipo se emitirá el siguiente mensaje de error:
	 *
	 *         Error 13 (fila,columna): indice de tipo incompatible
	 *
	 * donde la fila y la columna son las del corchete izquierdo del índice con tipo incompatible.
	 *
	 * 
	 * Recibe un token del corchete izquierdo que utiliza un tipo distinto a entero o real para indicar una dimensión del array.
	 */
	public static void Error13(int fila, int columna)
	{
		System.err.println("Error 13 (" + fila + "," + columna + "): indice de tipo incompatible");
		System.exit(-1);
	}

	/** Error 14
	 * Si se declara un array con un tipo simple determinado (por ejemplo, bool v[][];) cuando se reserva memoria para dicho array el tipo simple debe ser
	 * el mismo (v=new bool[2][3];), en cualquier otro caso se emitirá el siguiente mensaje de error:
	 *
	 *         Error 14 (fila,columna): tipo 'tipo_simple' incompatible con la declaración
	 *
	 * donde la fila, la columna y el 'tipo_simple' son las del tipo simple usado incorrectamente en el reserva de memoria del array.
	 *
	 * 
	 * Recibe un token del tipo simple que no coincide con la declaración a la hora de reservar memoria.
	 */
	public static void Error14(int fila, int columna, String lexema)
	{
		System.err.println("Error 14 (" + fila + "," + columna + "): tipo '" + lexema + "' incompatible con la declaracion");
		System.exit(-1);
	}

}
