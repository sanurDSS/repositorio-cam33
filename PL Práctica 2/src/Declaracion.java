/**
 * Clase que representa la declaración de una variable en el código.
 */
public class Declaracion implements Simbolo
{
	/**
	 * Token que representa el identificador de la variable a declarar.
	 * Permite acceder a la fila y columna en la que aparece el token.
	 */
	public TokenP1 identificador;

	/**
	 * Genera la salida de la declaración de la variable en un formato adecuado.
	 * @param nivel Indica el nivel de indentación necesario para el código de salida.
	 * @return Devuelve el código de salida en un formato correcto.
	 */
	public String formatear(int nivel)
	{
		String indentado = (nivel > 0) ? String.format("%1$-" + nivel*4 + "s", "") : "";
		return indentado + "double " + identificador.lexema + ";\n";
	}

	/**
	 * Comprueba si un identificador está siendo usado en la declaración de la variable.
	 * @param identificador Nombre del identificador que va a comprobarse.
	 * @param local Indica si la búsqueda se realizará en este ámbito o en un ámbito superior.
	 * @return Devuelve verdadero si el identificador coincide con el nombre de la variable a reservar.
	 */
	public boolean identificadorOcupado(String identificador, boolean local)
	{
		return this.identificador.lexema.equals(identificador);
	}

	/**
	 * Comprueba si una variable ha sido declarada en el símbolo.
	 * @param variable Nombre de la variable que se va a comprobar.
	 * @param local Indica si la búsqueda se realizará en este ámbito o en un ámbito superior.
	 * @return Devuelve verdadero si la variable que se va a declarar coincide con la variable indicada en los parámetros.
	 */
	public boolean variableDeclarada(String variable, boolean local)
	{
		return identificador.lexema.equals(variable);
	}

	/**
	 * Comprueba si una variable está siendo utilizada en este símbolo.
	 * Como en este caso se trata de un símbolo del tipo "declaración", nunca devolverá verdadero porque sólo se declaran variable; no se utilizan.
	 * @param variable Nombre de la variable que se va a comprobar.
	 * @return Siempre devuelve falso.
	 */
	public boolean variableUtilizada(String variable)
	{
		return false;
	}
}
