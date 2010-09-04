/**
 * Clase auxiliar para el tratamiento de errores.
 */
public class Error
{
	/**
	 * Lanza el error 0.
	 * Este error se considera un error crítico que nunca debería aparecer.
	 *
	 * "Error 0: error crítico: mensaje"
	 *
	 * @param mensaje Mensaje que se incluye en el error.
	 */
	public static void Error0(String mensaje)
	{
		System.err.println("Error 0: error crítico: " + mensaje);
		System.exit(-1);
	}

	/** <b>Error 1</b>
	 * <p>No es posible declarar un identificador con el mismo nombre que otro en el mismo ámbito. En ese caso, debe emitirse el siguiente error:</p>
     *
     *         <p><code><center>Error 1 (fila,columna): 'lexema' ya existe en este ambito</center></code></p>
	 *
     * <p>donde la fila, la columna y el lexema son los del identificador redeclarado.</p>
	 *
	 * 
	 * </p>Recibe un token del identificador que ha sido redeclarado.</p>
	 */
	public static void Error1(int fila, int columna, String lexema)
	{
		System.err.println("Error 1 (" + fila + "," + columna + "): '" + lexema + "' ya existe en este ambito");
		System.exit(-1);
	}

	/** <b>Error 2</b>
	 * <p>Tampoco es posible utilizar un símbolo sin haberlo declarado previamente:</p>
	 *
	 *         <p><code><center>Error 2 (fila,columna): simbolo 'lexema' no ha sido declarado</center></code></p>
	 *
	 * <p>donde la fila, la columna y el lexema son los del identificador del símbolo no declarado.</p>
	 *
	 *
	 * <p>Recibe un token del identificador que ha sido usado sin declarar.</p>
	 */
	public static void Error2(int fila, int columna, String lexema)
	{
		System.err.println("Error 2 (" + fila + "," + columna + "): simbolo '" + lexema + "' no ha sido declarado");
		System.exit(-1);
	}

	/** <b>Error 3</b>
	 * <p>Las expresiones aritméticas (asociadas a los tokens  addop, mulop, postincr y postdecr) permiten operar con subexpresiones de tipo entero y
	 * real mediante las promociones de tipo oportunas. El tipo del resultado de operar dos enteros es entero, y real si alguno de los operandos es real.
	 * En el caso de que alguno de los operandos sea una subexpresión de tipo booleano se tiene que emitir el error semántico correspondiente:</p>
	 * 
	 *         <p><code><center>Error 3 (fila,columna): tipo incompatible en operador aritmetico 'lexema'</center></code></p>
	 * 
	 * <p>donde la fila, la columna y el lexema son los del operador aritmético que se ve afectado por el operando de tipo booleano.
	 * La división entre valores enteros siempre es una división entera; si alguno de los operandos es real, la división es real.</p>
	 *
	 *
	 * <p>Recibe un token del operador aritmético que tiene un operando booleano.</p>
	 */
	public static void Error3(int fila, int columna, String lexema)
	{
		System.err.println("Error 3 (" + fila + "," + columna + "): tipo incompatible en operador aritmetico '" + lexema + "'");
		System.exit(-1);
	}

	/** <b>Error 4</b>
	 * <p>Si el operando de los operadores lógicos (asociados a los tokens  or, and y not) es de tipo numérico, el error a emitir será:</p>
	 *
	 *         <p><code><center>Error 4 (fila,columna): tipo incompatible en operador logico 'lexema'</center></code></p>
	 *
	 * <p>donde la fila, la columna y el lexema son del operador afectado por el operando de tipo numérico.
	 * El tipo de una expresión lógica es siempre booleano.</p>
	 *
	 *
	 * <p>Recibe un token del operador lógico que tiene un operando no booleano.</p>
	 */
	public static void Error4(int fila, int columna, String lexema)
	{
		System.err.println("Error 4 (" + fila + "," + columna + "): tipo incompatible en operador logico '" + lexema + "'");
		System.exit(-1);
	}

	/** <b>Error 5</b>
	 * <p>El tipo de la condición indicada en la instrucción condicional (asociada al token if) y en la de bucle (segunda expresión asociada al token for) ha
	 * de ser de tipo booleano. Si la condición fuera de otro tipo el error a emitir será:</p>
	 *
	 *         <p><code><center>Error 5 (fila,columna): la expresion debe ser de tipo booleano en la instruccion 'lexema'</center></code></p>
	 *
	 * <p>donde la fila, la columna y el lexema son los del token de la instrucción (if o for).</p>
	 *
	 * 
	 * <p>Recibe un token de la instrucción (if o for) con una condición con valor no booleano.</p>
	 */
	public static void Error5(int fila, int columna, String lexema)
	{
		System.err.println("Error 5 (" + fila + "," + columna + "): la expresion debe ser de tipo booleano en la instruccion '" + lexema + "'");
		System.exit(-1);
	}

	/** <b>Error 6</b>
	 * <p>Las asignaciones entre elementos de tipo numérico (entero o real) están permitidas y el compilador debe generar código para realizar las conversiones
	 * de tipo necesarias. La conversión de real a entero implica quedarse con la parte entera del número real. También está permitida, obviamente, la asignación
	 * entre booleanos. Si se realiza una asignación entre elementos de tipo numérico y tipo booleano el compilador emitirá el siguiente error semántico:</p>
	 *
	 *         <p><code><center>Error 6 (fila,columna): tipos incompatibles en la instruccion de asignacion</center></code></p>
	 *
	 * <p>donde la fila y la columna son los del token asig.</p>
	 *
	 * 
	 * <p>Recibe un token de la instrucción de asignación con tipos incompatibles (uno booleano y el otro no).</p>
	 */
	public static void Error6(int fila, int columna)
	{
		System.err.println("Error 6 (" + fila + "," + columna + "): tipos incompatibles en la instruccion de asignacion");
		System.exit(-1);
	}

	/** <b>Error 7</b>
	 * <p>La instrucción de lectura debe ser acorde con el tipo de la referencia donde se almacenará el valor leído, en otro caso el error a emitir será:</p>
	 *
	 *         <p><code><center>Error 7 (fila,columna): tipos incompatibles en la instruccion de lectura</center></code></p>
	 *
	 * <p>donde la fila y la columna son los del token readline.</p>
	 *
	 * 
	 * <p>Recibe un token de la instrucción de lectura incompatible con el tipo.</p>
	 */
	public static void Error7(int fila, int columna)
	{
		System.err.println("Error 7 (" + fila + "," + columna + "): tipos incompatibles en la instruccion de lectura");
		System.exit(-1);
	}
	
	/** <b>Error 8</b>
	 * <p>En la reserva de memoria de un array el tamaño de cada dimensión ha de ser mayor que cero, en otro caso se emitirá el error:</p>
	 *
	 *         <p><code><center>Error 8 (fila,columna): tamanyo incorrecto</center></code></p>
	 *
	 * <p>donde la fila y la columna son las del corchete izquierdo de la dimensión con tamaño incorrecto.</p>
	 *
	 * 
	 * <p>Recibe un token del corchete izquierdo de la dimensión incorrecta (menor o igual a 0).</p>
	 */
	public static void Error8(int fila, int columna)
	{
		System.err.println("Error 8 (" + fila + "," + columna + "): tamanyo incorrecto");
		System.exit(-1);
	}

	/** <b>Error 9</b>
	 * <p>No es posible utilizar un array sin haber especificado todos los índices necesarios (tantos como dimensiones tenga el array en su declaración). Por tanto, el
	 * tipo de la subexpresión formada por el identificador y su lista de corchetes con su índice debe ser un tipo simple (entero, real o booleano). En caso de
	 * que no se especifiquen todos los índices o se utilice una variable de tipo array sin corchetes, se debe emitir el siguiente mensaje de error:</p>
	 *
	 *         <p><code><center>Error 9 (fila,columna): numero insuficiente de indices en el array 'lexema'</center></code></p>
	 *
	 * <p>donde la fila, la columna y el lexema corresponden al identificador de la variable de tipo array a la que le faltan índices.</p>
	 *
	 * 
	 * <p>Recibe un token del identificador del array usado de forma incorrecta.</p>
	 */
	public static void Error9(int fila, int columna, String lexema)
	{
		System.err.println("Error 9 (" + fila + "," + columna + "): numero insuficiente de indices en el array '" + lexema + "'");
		System.exit(-1);
	}

	/** <b>Error 10</b>
	 * <p>Una excepción para emitir este error es cuando utilizamos el identificador de la variable de tipo array para reservar memoria, ya que en este caso el
	 * identificador se utiliza sin índices a la izquierda de la asignación y a la derecha, con la instrucción new, es donde se debe especificar el tamaño de
	 * todas las dimensiones. En caso de que se reserve memoria para más o menos de las dimensiones con que se declaró una variable de tipo array, el error a emitir será:</p>
	 *
	 *         <p><code><center>Error 10 (fila,columna): numero de dimensiones incorrecto</center></code></p>
	 *
	 * <p>donde la fila y la columna serán las del corchete izquierdo anterior en el caso de reservar para más dimensiones de las debidas, o bien las del último
	 * corchete derecho en el caso de que falten dimensiones. No se debe emitir ningún mensaje de error en caso de hacer referencia a un array sin haber
	 * reservado memoria para él (fallará en ejecución).</p>
	 *
	 * 
	 * <p>Recibe un token del último corchete derecho si tiene menos dimensiones que las especificadas en la declaración.
	 *              O bien, token del corchete izquierdo si se ha sobrepasado.</p>
	 */
	public static void Error10(int fila, int columna)
	{
		System.err.println("Error 10 (" + fila + "," + columna + "): numero de dimensiones incorrecto");
		System.exit(-1);
	}

	/** <b>Error 11</b>
	 * <p>No se permite poner índices (corchetes) a variables que no sean de tipo array:</p>
	 *
	 *         <p><code><center>Error 11 (fila,columna): el identificador 'lexema' no es de tipo array</center></code></p>
	 *
	 * <p>donde la fila, la columna y el lexema son los del identificador utilizado como un array. Este error también se debe emitir en el caso de reservar
	 * memoria para una variable que no es de tipo array.</p>
	 *
	 * 
	 * <p>Recibe un token del identificador que no es un array pero es usado como tal.</p>
	 */
	public static void Error11(int fila, int columna, String lexema)
	{
		System.err.println("Error 11 (" + fila + "," + columna + "): el identificador '" + lexema + "' no es de tipo array");
		System.exit(-1);
	}

	/** <b>Error 12</b>
	 * <p>No se permite poner más índices de los necesarios a una variable de tipo array:</p>
	 *
	 *         <p><code><center>Error 12 (fila,columna): demasiados indices</center></code></p>
	 *
	 * <p>donde la fila y la columna son los del corchete izquierdo del primer índice sobrante. Si el índice sobrante es el primero, es decir, la variable no
	 * es de tipo array, el error a emitir será el Error 11.</p>
	 *
	 * 
	 * <p>Recibe un token con el corchete izquierdo que sobra en la referencia al array.</p>
	 */
	public static void Error12(int fila, int columna)
	{
		System.err.println("Error 12 (" + fila + "," + columna + "): demasiados indices");
		System.exit(-1);
	}

	/** <b>Error 13</b>
	 * <p>Un índice debe ser de tipo entero; si es de tipo real se debe convertir a entero, y si es de cualquier otro tipo se emitirá el siguiente mensaje de error:</p>
	 *
	 *         <p><code><center>Error 13 (fila,columna): indice de tipo incompatible</center></code></p>
	 *
	 * <p>donde la fila y la columna son las del corchete izquierdo del índice con tipo incompatible.</p>
	 *
	 * 
	 * <p>Recibe un token del corchete izquierdo que utiliza un tipo distinto a entero o real para indicar una dimensión del array.</p>
	 */
	public static void Error13(int fila, int columna)
	{
		System.err.println("Error 13 (" + fila + "," + columna + "): indice de tipo incompatible");
		System.exit(-1);
	}

	/** <b>Error 14</b>
	 * <p>Si se declara un array con un tipo simple determinado (por ejemplo, bool v[][];) cuando se reserva memoria para dicho array el tipo simple debe ser
	 * el mismo (v=new bool[2][3];), en cualquier otro caso se emitirá el siguiente mensaje de error:</p>
	 *
	 *         <p><code><center>Error 14 (fila,columna): tipo 'tipo_simple' incompatible con la declaración</center></code></p>
	 *
	 * <p>donde la fila, la columna y el 'tipo_simple' son las del tipo simple usado incorrectamente en el reserva de memoria del array.</p>
	 *
	 * 
	 * <p>Recibe un token del tipo simple que no coincide con la declaración a la hora de reservar memoria.</p>
	 */
	public static void Error14(int fila, int columna, String lexema)
	{
		System.err.println("Error 14 (" + fila + "," + columna + "): tipo '" + lexema + "' incompatible con la declaracion");
		System.exit(-1);
	}














	/** <b>Error 15</b>
	 *
	 * <p>En un programa tiene que haber un método Main y solo uno. En caso de no existir dicho método, al alcanzar el final del programa se debe emitir el siguiente error:</p>
	 *
	 *         <p><code><center>Error 15: debe existir un unico metodo Main</center></code></p>
	 *
	 * <p>En el caso de encontrar un segundo Main en un mismo ámbito, se debe emitir el Error 15 y no el Error 1 de la práctica 3.</p>
	 *
	 */
	public static void Error15()
	{
		System.err.println("Error 15: debe existir un unico metodo Main");
		System.exit(-1);
	}

	/** <b>Error 16</b>
	 *
	 * <p>Si un identificador se usa en un contexto incorrecto (por ejemplo el nombre de una clase o método se usa como si fuera una variable o por el contrario se utiliza un identificador
	 * que no es una clase para hacer new) se emitirá el siguiente error semántico:</p>
	 *
	 *         <p><code><center>Error 16 (fila,columna): identificador 'lexema' usado incorrectamente</center></code></p>
	 *
	 * <p>donde la fila y la columna son los de la aparición incorrecta del identificador. En caso de conflicto entre el Error 11 y el Error 16, se emitirá el Error 11.</p>
	 *
	 */
	public static void Error16(int fila, int columna, String lexema)
	{
		System.err.println("Error 16 (" + fila + "," + columna + "): identificador '" + lexema + "' usado incorrectamente");
		System.exit(-1);
	}

	/** <b>Error 17</b>
	 *
	 * <p>El número de parámetros que aparecen en la llamada a un método ha de coincidir con los indicados en su declaración (ni más ni menos). Si se utilizan más parámetros de los
	 * requeridos el compilador emitirá el siguiente error semántico:</p>
	 *
	 *         <p><code><center>Error 17 (fila,columna): sobran parametros</center></code></p>
	 *
	 * <p>donde la fila y la columna son las del token anterior (paréntesis izquierdo o coma) al primer parámetro sobrante de dicho método. Por el contrario si se utilizan menos parámetros
	 * de los requeridos, el compilador emitirá el error </p>
	 *
	 *         <p><code><center>Error 18 (fila,columna): faltan parametros</center></code></p>
	 *
	 * <p>donde la fila y la columna son las del paréntesis derecho de dicho método.</p>
	 *
	 */
	public static void Error17(int fila, int columna)
	{
		System.err.println("Error 17 (" + fila + "," + columna + "): sobran parametros");
		System.exit(-1);
	}

	/** <b>Error 18</b>
	 *
	 * <p>El número de parámetros que aparecen en la llamada a un método ha de coincidir con los indicados en su declaración (ni más ni menos). Si se utilizan más parámetros de los
	 * requeridos el compilador emitirá el siguiente error semántico:</p>
	 *
	 *         <p><code><center>Error 17 (fila,columna): sobran parametros</center></code></p>
	 *
	 * <p>donde la fila y la columna son las del token anterior (paréntesis izquierdo o coma) al primer parámetro sobrante de dicho método. Por el contrario si se utilizan menos parámetros
	 * de los requeridos, el compilador emitirá el error </p>
	 *
	 *         <p><code><center>Error 18 (fila,columna): faltan parametros</center></code></p>
	 *
	 * <p>donde la fila y la columna son las del paréntesis derecho de dicho método.</p>
	 *
	 */
	public static void Error18(int fila, int columna)
	{
		System.err.println("Error 18 (" + fila + "," + columna + "): faltan parametros");
		System.exit(-1);
	}

	/** <b>Error 19</b>
	 *
	 * <p>El compilador debe producir un error si se utiliza la instrucción return dentro del método Main:</p>
	 *
	 *         <p><code><center>Error 19 (fila,columna): aqui no puede usarse return</center></code></p>
	 *
	 * <p>donde la fila y la columna son las del inicio de la palabra reservada return.</p>
	 *
	 */
	public static void Error19(int fila, int columna)
	{
		System.err.println("Error 19 (" + fila + "," + columna + "): aqui no puede usarse return");
		System.exit(-1);
	}

	/** <b>Error 20</b>
	 *
	 * <p>El tipo de la expresión que acompaña a una instrucción return ha de convertirse siempre al tipo que devuelve el método si los criterios generales de compatibilidad de tipos lo permiten
	 * (véase la práctica anterior); en caso de no poder realizar la conversión el error a emitir será:</p>
	 *
	 *         <p><code><center>Error 20 (fila,columna): valor devuelto de tipo incompatible</center></code></p>
	 *
	 * <p>donde la fila y la columna son las de inicio de la palabra reservada return.</p>
	 *
	 */
	public static void Error20(int fila, int columna)
	{
		System.err.println("Error 20 (" + fila + "," + columna + "): valor devuelto de tipo incompatible");
		System.exit(-1);
	}

	/** <b>Error 21</b>
	 *
	 * <p>Dado que todos los argumentos de un método son de tipo real, si el tipo de la expresión del parámetro es de tipo entero se debe generar código para realizar dicha conversión, mientras
	 * que si es de tipo booleano se debe emitir el error:</p>
	 *
	 *         <p><code><center>Error 21 (fila,columna): tipo incompatible en el parametro</center></code></p>
	 *
	 * <p>donde la fila y la columna son las de la coma anterior al parámetro de tipo booleano, excepto si se trata del primer parámetro en cuyo caso la fila y la columna son las del paréntesis
	 * izquierdo.</p>
	 *
	 */
	public static void Error21(int fila, int columna)
	{
		System.err.println("Error 21 (" + fila + "," + columna + "): tipo incompatible en el parametro");
		System.exit(-1);
	}

	/** <b>Error 22</b>
	 *
	 * <p>No se permite la declaración de arrays de objetos. Si en la declaración de variables de tipo objeto se utilizan índices se debe emitir el error:</p>
	 *
	 *         <p><code><center>Error 22 (fila,columna): no se permite la declaracion de arrays de objetos</center></code></p>
	 *
	 * <p>donde la fila y la columna corresponden al primer corchete izquierdo encontrado en la declaración.</p>
	 *
	 */
	public static void Error22(int fila, int columna)
	{
		System.err.println("Error 22 (" + fila + "," + columna + "): no se permite la declaracion de arrays de objetos");
		System.exit(-1);
	}

	/** <b>Error 23</b>
	 *
	 * <p>Si se declara un objeto de una clase (por ejemplo CLASE1 obj1;) cuando se reserva memoria para dicho objeto la clase debe ser la misma (obj1=new CLASE1();), en cualquier otro
	 * caso se emitirá el siguiente mensaje de error:</p>
	 *
	 *         <p><code><center>Error 23 (fila,columna): objeto 'lexema' no es de clase 'lexema2'</center></code></p>
	 *
	 * <p>donde la fila, la columna y lexema son los del identificador del objeto para el que se reserva memoria, y lexema2 es el lexema de la clase utilizada incorrectamente. En el caso
	 * de que lexema no fuera un objeto, o bien lexema2 no fuera una clase el error a emitir será el Error 16.</p>
	 *
	 */
	public static void Error23(int fila, int columna, String lexema, String lexema2)
	{
		System.err.println("Error 23 (" + fila + "," + columna + "): objeto '" + lexema + "' no es de clase '" + lexema2 + "'");
		System.exit(-1);
	}

	/** <b>Error 24</b>
	 *
	 * <p>No se puede acceder a variables o métodos de instancia de la propia clase que contiene el método Main desde el propio método Main ya que el método Main es estático (el único del programa) y por tanto
	 * no se puede acceder a variables o métodos de instancia desde él directamente, pero claro sí se puede a través de un objeto. En caso de acceder directamente a algún miembro de instancia de la propia clase
	 * desde el método Main se emitirá el siguiente mensaje de error:</p>
	 *
	 *         <p><code><center>Error 24 (fila,columna): miembro 'lexema' no accesible desde Main</center></code></p>
	 *
	 * <p>donde la fila, la columna y lexema son los del miembro no accesible. En caso de conflicto entre este error y el Error 16, prevalecerá el Error 16.</p>
	 *
	 */
	public static void Error24(int fila, int columna, String lexema)
	{
		System.err.println("Error 24 (" + fila + "," + columna + "): miembro '" + lexema + "' no accesible desde Main");
		System.exit(-1);
	}

	/** <b>Error 25</b>
	 *
	 * <p>No se permite la escritura o lectura de variables de tipo objeto, en este caso el mensaje de error a emitir sería:</p>
	 *
	 *         <p><code><center>Error 25 (fila,columna): la referencia es de tipo objeto</center></code></p>
	 *
	 * <p>donde la fila y la columna son las del token writeline o readline, según el caso.</p>
	 *
	 */
	public static void Error25(int fila, int columna)
	{
		System.err.println("Error 25 (" + fila + "," + columna + "): la referencia es de tipo objeto");
		System.exit(-1);
	}

	/** <b>Error 26</b>
	 *
	 * <p>No se permite la operación con variables de tipo objeto, en este caso el mensaje de error a emitir será el correspondiente de la práctica 3:</p>
	 *
	 *         <p><code><center>Error 3 (fila,columna): tipo incompatible en operador aritmetico 'lexema'</center></code></p>
	 *
	 *         <p><code><center>Error 4 (fila,columna): tipo incompatible en operador logico 'lexema'</center></code></p>
	 *
	 * <p>Si se trata del operador relacional el mensaje de error a emitir será:</p>
	 *
	 *         <p><code><center>Error 26 (fila,columna): tipo incompatible en operador relacional 'lexema'</center></code></p>
	 *
	 * <p>donde la fila, la columna y el lexema son los del token correspondiente al operador en cuestión.</p>
	 *
	 */
	public static void Error26(int fila, int columna, String lexema)
	{
		System.err.println("Error 26 (" + fila + "," + columna + "): tipo incompatible en operador relacional '" + lexema + "'");
		System.exit(-1);
	}
}
