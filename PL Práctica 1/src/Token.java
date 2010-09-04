public class Token
{
	/**
	 * Número de fila del fichero en la que aparece el token.
	 */
	public int fila;

	/**
	 * Número de columna del fichero en la que aparece el token.
	 */
	public int columna;

	/**
	 * Secuencia de caracteres del token.
	 */
	public String lexema;

	/**
	 * Tipo del token.
	 */
	public int tipo;

	/**
	 * Cadena de caracteres que representa el token de cada tipo.
	 */
	public static final String[] subcadenas  = {"final de fichero", "'('", "')'", "';'", "'='", "'{'", "'}'", "", "", "real", "identificador", "'class'", "'double'", "'void'"};

	/**
	 * Constante que representa el tipo de fin de fichero.
	 */
	public static final int EOF = 0;

	/**
	 * Constante que representa el tipo del paréntesis izquierdo.
	 * Su valor se corresponde con el estado de aceptación del AFD que reconoce el token.
	 */
	public static final int PARENTESIS_IZQUIERDO = 1;

	/**
	 * Constante que representa el tipo del paréntesis derecho.
	 * Su valor se corresponde con el estado de aceptación del AFD que reconoce el token.
	 */
	public static final int PARENTESIS_DERECHO = 2;

	/**
	 * Constante que representa el tipo del punto y coma.
	 * Su valor se corresponde con el estado de aceptación del AFD que reconoce el token.
	 */
	public static final int PUNTO_Y_COMA = 3;

	/**
	 * Constante que representa el tipo asignación.
	 * Su valor se corresponde con el estado de aceptación del AFD que reconoce el token.
	 */
	public static final int ASIGNACION = 4;

	/**
	 * Constante que representa el tipo de la llave izquierda.
	 * Su valor se corresponde con el estado de aceptación del AFD que reconoce el token.
	 */
	public static final int LLAVE_IZQUIERDA = 5;

	/**
	 * Constante que representa el tipo de la llave derecha.
	 * Su valor se corresponde con el estado de aceptación del AFD que reconoce el token.
	 */
	public static final int LLAVE_DERECHA = 6;

	/**
	 * Constante que representa el tipo del número real.
	 * Su valor se corresponde con el estado de aceptación del AFD que reconoce el token.
	 */
	public static final int REAL = 9;

	/**
	 * Constante que representa el tipo identificador.
	 * Su valor se corresponde con el estado de aceptación del AFD que reconoce el token.
	 */
	public static final int IDENTIFICADOR = 10;

	/**
	 * Constante que representa el tipo de la palabra reservada "class".
	 */
	public static final int CLASS = 11;

	/**
	 * Constante que representa el tipo de la palabra reservada "double".
	 */
	public static final int DOUBLE = 12;

	/**
	 * Constante que representa el tipo de la palabra reservada "void".
	 */
	public static final int VOID = 13;

	/**
	 * Constructor por defecto.
	 */
	public Token()
	{
		fila = 0;
		columna = 0;
		lexema = "";
		tipo = EOF;
	}
}