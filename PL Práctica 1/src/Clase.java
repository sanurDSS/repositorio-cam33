/**
 * Clase que representa una clase en el código.
 * Contiene un identificador para el nombre de la clase así como una serie de símbolos (miembros) que componen el cuerpo de la clase.
 */
public class Clase implements Simbolo
{
	/**
	 * Token que hace referencia al identificador del nombre de la clase.
	 * Permite acceder a la fila y columna del fichero.
	 */
	public Token identificador;

	/**
	 * Tabla de símbolos que representa los miembros de la clase.
	 */
	public TablaSimbolos miembros;

	/**
	 * Genera la clase como cadena de caracteres en un formato adecuado.
	 * @param nivel Indica el nivel de indentado que tendrá la salida.
	 * @return Devuelve una cadena de caracteres con la salida de la clase.
	 */
	public String formatear(int nivel)
	{
		String indentado = (nivel > 0) ? String.format("%1$-" + nivel*4 + "s", "") : "";
		String aux = "";
		aux += indentado + "class " + identificador.lexema + " {\n";
		aux += miembros.formatear(nivel + 1);
		aux += indentado + "}\n";
		return aux;
	}

	/**
	 * Comprueba si un identificador está siendo usado en la clase.
	 * Sin embargo, no comprueba en los miembros de la clase; sólo comprueba en la cabecera de la clase (es decir, en el nombre de la clase) y no profundiza en el cuerpo.
	 * @param identificador Nombre del identificador que se está comprobando.
	 * @param local Indica si la búsqueda se hará en un ámbito local o en un ámbito externo que engloba el local.
	 * @return Devuelve verdadero si el identificador en cuestión está siendo usado en el nombre de la clase.
	 */
	public boolean identificadorOcupado(String identificador, boolean local)
	{
		return this.identificador.lexema.equals(identificador);
	}

	/**
	 * Comprueba si una variable está siendo declarada en la clase.
	 * Sin embargo, no comprueba en los miembros de la clase; sólo comprueba en la cabecera de la clase y, como no es una declaración de variable, siempre devolverá falso.
	 * @param variable Nombre de la variable que se quiere comprobar.
	 * @param local Indica si la búsqueda se hará en un ámbito local o en un ámbito externo que engloba el local.
	 * @return Devuelve siempre falso.
	 */
	public boolean variableDeclarada(String variable, boolean local)
	{
		return false;
	}

	/**
	 * Comprueba si una variable está siendo utilizada dentro del cuerpo de la clase.
	 * Es decir, que aparece en la parte derecha o izquierda de una asignación dentro del cuerpo de la clase.
	 * @param variable Nombre de la variable que se va a comprobar.
	 * @return Devuelve verdadero si la variable está siendo utilizada dentro de la clase.
	 */
	public boolean variableUtilizada(String variable)
	{
		return miembros.variableUtilizada(variable);
	}
}
