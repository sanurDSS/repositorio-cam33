import java.util.ArrayList;
import java.util.Iterator;

/**
 * Clase que representa una lista de símbolos y pertenece también a la clase Simbolo.
 * Es un conjunto de símbolos que representa un ámbito en el que los símbolos se van insertando
 * en orden secuencial según aparecen en el código.
 */
public class TablaSimbolos implements Simbolo
{
	/**
	 * Lista de símbolos de la tabla.
	 */
	private ArrayList<Simbolo> simbolos;

	/**
	 * Referencia a la tabla de símbolos que contiene a esta tabla de símbolos.
	 * Si no existe esa tabla de símbolos, entonces es una referencia nula y significa que esta tabla es una raíz.
	 */
	private TablaSimbolos padre;

	/**
	 * Constructor por defecto.
	 */
	public TablaSimbolos()
	{
		simbolos = new ArrayList<Simbolo>();
		padre = null;
	}

	/**
	 * Constructor sobre cargado que permite indicar cuál es la tabla de símbolos padre.
	 * @param padre Referencia a la tabla de símbolos que contiene a la tabla de símbolos que se va a crear.
	 */
	public TablaSimbolos(TablaSimbolos padre)
	{
		simbolos = new ArrayList<Simbolo>();
		this.padre = padre;
	}

	/**
	 * Añade un símbolo a la lista de símbolos de la tabla.
	 * @param simbolo Nuevo símbolo que se va a añadir.
	 */
	public void anadirSimbolo(Simbolo simbolo)
	{
		simbolos.add(simbolo);
	}

	/**
	 * Comprueba si el símbolo cumple las restricciones necesarias para ser incluido en la tabla.
	 * Estas condiciones son:
	 *     -No se ha declarado dos veces la misma variable.
	 *     -No se está utilizando un nombre de clase/método como si fuera una variable.
	 *     -No se está utilizando una variable no declarada.
	 *     -No hay dos identificadores iguales en el mismo ámbito.
	 * Si no se cumple alguna restricción, se producirá una salida de error.
	 * @param simbolo Símbolo con va a ser evaluado.
	 */
	public void comprobarSimbolo(Simbolo simbolo)
	{
		// Si es una declaración de variable repetida (definida en el mismo ámbito), hay que cascar.
		if (simbolo instanceof Declaracion)
		{
			Declaracion declaracion = (Declaracion) simbolo;
			if (variableDeclarada(declaracion.identificador.lexema, true))
			{
				Error.Error5(declaracion.identificador);
			}
			else
			{
				if (identificadorOcupado(declaracion.identificador.lexema, true))
				{
					Error.Error5(declaracion.identificador);
				}
			}
		}

		// Si es una instrucción, se comprueba que las variables estén definidas.
		else if (simbolo instanceof Instruccion)
		{
			Instruccion instruccion = (Instruccion) simbolo;

			// Se comprueba si la variable está declarada.
			if (!variableDeclarada(instruccion.identificador.lexema, false))
			{
				// Se comprueba si la variable corresponde a una clase o método.
				if (!identificadorOcupado(instruccion.identificador.lexema, false))
				{
					Error.Error6(instruccion.identificador);
				}
				else
				{
					Error.Error7(instruccion.identificador);
				}
			}

			// Si el factor asignado es un identificador, se realizan las mismas comprobaciones.
			if (instruccion.factor != null && instruccion.factor.tipo == Token.IDENTIFICADOR)
			{
				// Se comprueba si la variable está declarada.
				if (!variableDeclarada(instruccion.factor.lexema, false))
				{
					// Se comprueba si la variable corresponde a una clase o método.
					if (!identificadorOcupado(instruccion.factor.lexema, false))
					{
						Error.Error6(instruccion.factor);
					}
					else
					{
						Error.Error7(instruccion.factor);
					}
				}
			}
		}

		// Si es una clase, comprueba que su identificador no esté ya usado en este ámbito.
		else if (simbolo instanceof Clase)
		{
			Clase clase = (Clase) simbolo;

			if (identificadorOcupado(clase.identificador.lexema, true))
			{
				Error.Error5(clase.identificador);
			}
		}

		// Si es una método, comprueba que su identificador no esté ya usado en este ámbito.
		else if (simbolo instanceof Metodo)
		{
			Metodo metodo = (Metodo) simbolo;

			if (identificadorOcupado(metodo.identificador.lexema, true))
			{
				Error.Error5(metodo.identificador);
			}
		}
	}

	/**
	 * Comprueba si se está modificando alguna variable no-local.
	 * @return Devuelve verdadero si todas las variables que modifican su valor han sido declaradas en este ámbito y no en un ámbito externo.
	 */
	public boolean esConstante()
	{
		boolean constante = true;

		// Recorremos todas los símbolos en busca de un identificador (en la parte izquierda de una instrucción) no declarado en este ámbito.
		Iterator<Simbolo> i = simbolos.iterator();
		while (i.hasNext() && constante)
		{
			Simbolo simbolo = i.next();
			if (simbolo instanceof Instruccion)
			{
				Instruccion instruccion = (Instruccion) simbolo;

				if (!variableDeclarada(instruccion.identificador.lexema, true))
				{
					constante = false;
				}
			}
		}

		return constante;
	}

	/**
	 * Implementación del método de conversión a cadena que devuelve la tabla de símbolos
	 * en un formato adecuado para la salida por pantalla.
	 * @param nivel Indica el nivel de indentado que tendrá el código en la salida.
	 * @return Devuelve una cadena de caracteres con el formato correcto.
	 */
	public String formatear(int nivel)
	{
		// Se construirá la salida recorriendo todos los símbolos y concatenando su cadena de salida.
		String aux = "";

		// Primero se muestran las declaraciones de variable.s
		Iterator<Simbolo> i = simbolos.iterator();
		while (i.hasNext())
		{
			Simbolo simbolo = i.next();

			if (simbolo instanceof Declaracion)
			{
				Declaracion declaracion = (Declaracion) simbolo;

				// Si el símbolo es una declaración, se comprueba que esté usada.
				if (variableUtilizada(declaracion.identificador.lexema))
				{
					aux += declaracion.formatear(nivel);
				}
			}
		}

		// Después el resto de elementos.
		i = simbolos.iterator();
		while (i.hasNext())
		{
			Simbolo simbolo = i.next();

			if (!(simbolo instanceof Declaracion))
			{
				aux += simbolo.formatear(nivel);
			}
		}
		
		return aux;
	}

	/**
	 * Comprueba si un identificador está usado en este ámbito o en un ámbito superior.
	 * @param identificador Nombre del identificador que va a comprobarse.
	 * @param local Indica si la búsqueda se realizará sólo en un ámbito local o también en los ámbitos superiores.
	 * @return Devuelve verdadero si está usado.
	 */
	public boolean identificadorOcupado(String identificador, boolean local)
	{
		boolean usado = false;

		// Comprobamos si el identificador está siendo utilizado en este ámbito.
		Iterator<Simbolo> i = simbolos.iterator();
		while (i.hasNext() && !usado)
		{
			usado = i.next().identificadorOcupado(identificador, local);
		}

		// Si no lo está, comprobamos en el entorno superior.
		if (!usado && padre != null && !local)
		{
			usado = padre.identificadorOcupado(identificador, local);
		}

		return usado;
	}

	/**
	 * Comprueba si una variable está declarada en este ámbito o en un ámbito superior.
	 * @param identificador Nombre del identificador de la variable que va a comprobarse.
	 * @param local Indica si la búsqueda se realizará sólo en un ámbito local o también en los ámbitos superiores.
	 * @return Devuelve verdadero si está declarada.
	 */
	public boolean variableDeclarada(String variable, boolean local)
	{
		boolean declarada = false;

		// Comprobamos si la variable está declarada en este ámbito.
		Iterator<Simbolo> i = simbolos.iterator();
		while (i.hasNext() && !declarada)
		{
			declarada = i.next().variableDeclarada(variable, local);
		}
		
		if (!declarada && padre != null && !local)
		{
			declarada = padre.variableDeclarada(variable, local);
		}
		
		return declarada;
	}

	/**
	 * Comprueba si una variable está siendo utilizada en alguna operación de asignación.
	 * @param variable Nombre de la variable que se va a comprobar.
	 * @return Devuelve verdadero si la variable está siendo usada.
	 */
	public boolean variableUtilizada(String variable)
	{
		boolean utilizada = false;

		// Comprobamos si la variable está siendo utilizada.
		Iterator<Simbolo> i = simbolos.iterator();
		while (i.hasNext() && !utilizada)
		{
			Simbolo simbolo = i.next();
			utilizada = simbolo.variableUtilizada(variable);
		}

		return utilizada;
	}
}
