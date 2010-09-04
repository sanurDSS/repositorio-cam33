import java.util.ArrayList;

class TablaSimbolos
{
	private ArrayList<Simbolo> simbolos;
	private TablaSimbolos padre;
	private int cantidadLocales;
	private boolean conLocales;

	/**
	 * Obtiene la posición local de la variable. Es decir, posición en este ámbito.
	 * Además, incrementa en 1 la cantidad de variables locales.
	 * @return Devuelve un entero con el número de posición local.
	 */
	public int getPosicionLocal()
	{
		if (conLocales)
			return 1 + cantidadLocales++;
		else
			return padre.getPosicionLocal();
	}

	public int getCantidadLocales()
	{
		if (conLocales)
			return cantidadLocales;
		else
			return padre.getCantidadLocales();
	}

	public boolean getConLocales()
	{
		return conLocales ;
	}

	public void setConLocales(boolean conLocales)
	{
		this.conLocales = conLocales;
	}

	/**
	 * Crea una nueva tabla de símbolos.
	 * @param padre Ámbito superior desde el que se creó la tabla. Cuando se cree el primer ámbito, este valor valdrá "null".
	 */
	public TablaSimbolos(TablaSimbolos padre)
	{
		simbolos = new ArrayList<Simbolo>();
		this.padre = padre;
		cantidadLocales = 0;
		conLocales = false;
	}

	public TablaSimbolos getPadre()
	{
		return padre;
	}

	/**
	 * Busca en la tabla de símbolos y devuelve el símbolo con el identificador indicado.
	 * @param identificador Lexema del identificador del símbolo que se está buscando.
	 * @param global Si es verdadero, busca el símbolo de manera recursiva en los ámbitos superiores. Si es falso, sólo busca en el ámbito actual.
	 * @return Devuelve el símbolo si existe o "null" en otro caso.
	 */
	public Simbolo get(String identificador, boolean global)
	{
		for (Simbolo i : simbolos)
			if (i.getLexema().equals(identificador))
				return i;

		return (global && padre != null) ? padre.get(identificador, global) : null;
	}

	/**
	 * Añade un símbolo al ámbito actual.
	 * @param simbolo Símbolo que se va a añadir.
	 */
	public void add(Simbolo simbolo)
	{
		simbolos.add(simbolo);
	}

	@Override
	public String toString()
	{
		int nivel = 0;
		ArrayList<Simbolo> simbolosAux = simbolos;
		TablaSimbolos padreAux = padre;
		while (padreAux != null)
		{
			simbolosAux.addAll(0, padreAux.simbolos);
			padreAux = padreAux.padre;
			nivel++;
		}

		String aux = "";
		aux += "Nivel: " + nivel + "\n";
		aux += "__________________________________________________________________________\n";
		aux += "│     Tipo     |    Identificador    |    Posición    |    Parámetros    │\n";
		aux += "│┈┈┈┈┈┈┈┈┈┈┈┈┈┈│┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈│┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈│┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈│\n";

		for (Simbolo i : simbolosAux)
		{
			String tipo = "";
			switch (i.getTipo())
			{
				case Simbolo.LOCAL: tipo = "LOCAL"; break;
				case Simbolo.ARGUMENTO: tipo = "ARGUMENTO"; break;
				case Simbolo.FUNCION: tipo = "FUNCION"; break;
			}

			aux += "│  " + rellenar(tipo, " ", 10, 1) + "  │  " + rellenar(i.getLexema(), " ", 17, 1) + "  │  " + rellenar("" + i.getPosicion(), " ", 12, 0)  + "  │  " + rellenar("" + ((i.getCantidadParametros() < 0) ? "-" : i.getCantidadParametros()), " ", 14, 0) + "  │\n";
		}
		aux += "‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾\n";

		return aux;
	}

	private String rellenar(String cadena, String simbolo, int minimo, int centrado)
	{
		boolean izquierda = true;
		while (cadena.length() < minimo)
		{
			if (centrado == -1)
				cadena = simbolo + cadena;
			else if (centrado == 1)
				cadena = cadena + simbolo;
			else
			{
				if (izquierda)
					cadena = simbolo + cadena;
				else
					cadena = cadena + simbolo;
				izquierda = !izquierda;
			}
		}
		return cadena;
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
