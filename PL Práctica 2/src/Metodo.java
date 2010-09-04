/**
 * Clase que representa un método en el código.
 * Está formado por una cabecera con el nombre del método y un cuerpo
 * que es una lista de símbolos (TablaSimbolos).
 */
public class Metodo implements Simbolo
{
	/**
	 * Identificador del método.
	 * Está contenido dentro de un Token, por lo que proporciona acceso al número de línea y columna en la que aparece.
	 */
	public TokenP1 identificador;

	/**
	 * Contiene los líneas (o símbolos) que hay dentro del método.
	 */
	public TablaSimbolos cuerpo;

	/**
	 * Obtiene el método en el formato adecuado para imprimir por pantalla.
	 * @param nivel Indica el nivel de indentación con el que se tiene que formatear el código.
	 * @return Devuelve una cadena de caracteres con el código del método.
	 */
	public String formatear(int nivel)
	{
		String indentado = (nivel > 0) ? String.format("%1$-" + nivel*4 + "s", "") : "";
		String aux = "";
		aux += indentado + "void " + identificador.lexema + " ()" + ((cuerpo.esConstante()) ? " const " : " ") + "{\n";
		aux += cuerpo.formatear(nivel + 1);
		aux += indentado + "}\n";
		return aux;
	}

	/**
	 * Comprueba si un nombre de identificador está siendo utilizado por el método.
	 * @param identificador Nombre del identificador que se quiere comprobar.
	 * @param local Indica si la comprobación se hará en el ámbito local o también en un ámbito externo (superior).
	 * @return Devuelve verdadero si el método tiene el identificador indicado.
	 */
	public boolean identificadorOcupado(String identificador, boolean local)
	{
		return this.identificador.lexema.equals(identificador);
	}

	/**
	 * Comprueba si una variable está declarada en el ámbito actual (o en un ámbito superior si así se indica).
	 * Es decir, sólo se examina este ámbito o los externos, pero no se profundiza dentro del cuerpo del método.
	 * Por lo tanto, como no se puede profundizar en el ámbito y este símbolo es del tipo "método", no hay ninguna variable declarada en este símbolo.
	 * Lo que quiere decir que este método siempre devolverá falso.
	 * @param variable Nombre de la variable que se va a comprobar.
	 * @param local Indica si el se comprobará en el ámbito local o en el ámbito superior.
	 * @return Devuelve siempre falso.
	 */
	public boolean variableDeclarada(String variable, boolean local)
	{
		return false;
	}

	/**
	 * Comprueba si una variable está siendo usada dentro del método.
	 * @param variable Nombre de la variable que se va a comprobar.
	 * @return Devuelve verdadero si la variable indicada se está usando desde dentro del método.
	 */
	public boolean variableUtilizada(String variable)
	{
		// Se considera utilizada si aparece en alguna instrucción y no está definida localmente dentro del método.
		return cuerpo.variableUtilizada(variable) && !cuerpo.variableDeclarada(variable, true);
	}
}
