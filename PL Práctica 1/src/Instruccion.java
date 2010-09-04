/**
 * Clase que representa una instrucción (asignación) en el código.
 * Está formado por la parte izquierda y la parte derecha de la asignación.
 */
public class Instruccion implements Simbolo
{
	/**
	 * Token que hace referencia al identificador de la variable de la parte izquierda de la asignación.
	 */
	public Token identificador;

	/**
	 * Token que hace referencia al factor (variable o inmediato) de la parte derecha de la asignación.
	 */
	public Token factor;

	/**
	 * Genera el código de salida de la instrucción en el formato adecuado.
	 * @param nivel Indica el nivel de indentado que tendrá el código en la salida.
	 * @return Devuelve una cadena de caracteres con la salida.
	 */
	public String formatear(int nivel)
	{
		String indentado = (nivel > 0) ? String.format("%1$-" + nivel*4 + "s", "") : "";
		return indentado + identificador.lexema + " = " + factor.lexema + ";\n";
	}

	/**
	 * Comprueba si el identificador está ocupado por algún símbolo (método, variable o clase).
	 * Como en este caso se trata de un símbolo de tipo "instrucción", este método siempre devolverá falso porque no reserva ningún identificador, sólo los usa.
	 * @param identificador Nombre del identificador que se va a comprobar.
	 * @param local Indica si la búsqueda se hará de forma local o también en ámbitos superiores.
	 * @return Devuelve siempre falso.
	 */
	public boolean identificadorOcupado(String identificador, boolean local)
	{
		return false;
	}

	/**
	 * Comprueba si una variable está declarada.
	 * Como en este caso se tata de un símbolo del tipo "instrucción", este método siempre devovlerá falso.
	 * @param variable Nombre de la variable que se va a comprobar.
	 * @param local Indica si la búsqueda se hará de forma local o también en ámbitos superiores.
	 * @return
	 */
	public boolean variableDeclarada(String variable, boolean local)
	{
		return false;
	}

	/**
	 * Comprueba si una variable está siendo utilizada en la instrucción.
	 * @param variable Nombre de la variable que se va a comprobar.
	 * @return Devuelve verdadero si la variable aparece en la parte izquierda o en la parte derecha de la asignación.
	 */
	public boolean variableUtilizada(String variable)
	{
		return identificador.lexema.equals(variable) || factor.lexema.equals(variable);
	}
}
