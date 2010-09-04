/**
 * Interfaz que representa un símbolo del código.
 * Se podrá comprobar si una variable ha sido declarada y utilizada o si un identificador está ocupado dentro del símbolo.
 */
public interface Simbolo
{
	/**
	 * Obtiene el símbolo en el formato adecuado para imprimir por pantalla.
	 * @param nivel Indica el nivel de indentación con el que se tiene que formatear el código.
	 * @return Devuelve una cadena de caracteres con el código del símbolo.
	 */
	public String formatear(int nivel);

	/**
	 * Comprueba si un identificador está ocupado o libre en un símbolo.
	 * @param identificador Nombre del identificador que se va a comprobar.
	 * @param local Indica si la comprobación se realizará en un ámbito local o también en un ámbito superior.
	 * @return Devuelve verdadero si existe otro símbolo que ya tiene ese identificador.
	 */
	public boolean identificadorOcupado(String identificador, boolean local);

	/**
	 * Comprueba si una variable está declarada. No comprueba en ámbitos más profundos, sólo en el actual o en el superior si así se indica.
	 * @param variable Nombre de la variable que se va a comprobar.
	 * @param local Indica si la comprobación se realizará en un ámbito local o también en un ámbito superior.
	 * @return Devuelve verdadero si está declarada.
	 */
	public boolean variableDeclarada(String variable, boolean local);

	/**
	 * Comprueba si una variable está utilizada.
	 * @param variable Nombre de la variable que se va a comprobar.
	 * @return Devuelve verdadero si la variable está siendo utilizada dentro del símbolo.
	 */
	public boolean variableUtilizada(String variable);
}
