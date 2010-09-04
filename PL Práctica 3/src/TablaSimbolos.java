import java.util.ArrayList;
import java.util.Iterator;

class TablaSimbolos
{
	private ArrayList<Simbolo> simbolos;
	private TablaSimbolos padre;
	private static int cantidadLocales = 0;

	/**
	 * Obtiene la posición local de la variable. Es decir, posición en este ámbito.
	 * Además, incrementa en 1 la cantidad de variables locales.
	 * @return Devuelve un entero con el número de posición local.
	 */
	public int getPosicionLocal()
	{
		return cantidadLocales++;
	}

	/**
	 * Crea una nueva tabla de símbolos.
	 * @param padre Ámbito superior desde el que se creó la tabla. Cuando se cree el primer ámbito, este valor valdrá "null".
	 */
	public TablaSimbolos(TablaSimbolos padre)
	{
		simbolos = new ArrayList<Simbolo>();
		this.padre = padre;
	}

	/**
	 * Busca en la tabla de símbolos y devuelve el símbolo con el identificador indicado.
	 * @param identificador Lexema del identificador del símbolo que se está buscando.
	 * @param global Si es verdadero, busca el símbolo de manera recursiva en los ámbitos superiores. Si es falso, sólo busca en el ámbito actual.
	 * @return Devuelve el símbolo si existe o "null" en otro caso.
	 */
	public Simbolo getSimbolo(String identificador, boolean global)
	{
		Iterator<Simbolo> i = simbolos.iterator();
		while (i.hasNext())
		{
			Simbolo simboloAux = i.next();
			if (simboloAux.getLexema().equals(identificador))
				return simboloAux;
		}

		if (global && padre != null)
			return padre.getSimbolo(identificador, global);
		else
			return null;
	}

	/**
	 * Añade un símbolo al ámbito actual.
	 * @param simbolo Símbolo que se va a añadir.
	 */
	public void addSimbolo(Simbolo simbolo)
	{
		simbolos.add(simbolo);
	}

	/**
	 * @return Devuelve una copia de la lista de símbolos.
	 */
	public ArrayList<Simbolo> getSimbolos()
	{
		return new ArrayList<Simbolo>(simbolos);
	}

	/**
	 * @return Devuelve un entero con la cantidad de símbolos que hay en este ámbito (sin considerar los superiores).
	 */
	public int getTotalSimbolos()
	{
		return simbolos.size();
	}
}
