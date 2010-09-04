/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.awt.Color;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Iterator;

/**
 *
 * @author mireia
 */
public class Jugador
{
	/**
	 * Vector que contiene las 81 variables del Sudoku.
	 */
	private Variable[] variables;

	/**
	 * Se llama desde la clase Interfaz para ejecutar FC.
	 * Al final de la función la solución del tablero se debe encontrar en tablero.
	 * @param tablero Tablero sobre el que se calculará la solución.
	 * @param ac3 Indica si debe ejecutarse primero AC3.
	 */
	public void ejecutarFC(Tablero tablero, boolean ac3)
	{
		// Se crea el vector con las variables y se inicializa su dominio si está restringido.
		variables = new Variable[81];
		for (int i = 0; i < 81; i++)
		{
			// Comprobamos si la casilla tiene un valor predeterminado para restringir su dominio.
			if (tablero.getCasilla(i/9, i%9) == 0 || tablero.getColor(i/9, i%9) != Color.DARK_GRAY)
			{
				variables[i] = new Variable();
				tablero.setCasilla(0, i/9, i%9);
			}
			else
				variables[i] = new Variable(tablero.getCasilla(i/9, i%9));
		}

		// Aplicamos el algoritmo AC3 para obtener un sudoku consistente.
		if (!ac3 || AC3())
		{
			// Ejecutamos el algoritmo Forward Checking.
			if (FC(0))
			{
				// Si ha encontrado una solución, trasladamos el valor de cada variable al tablero.
				int contador = 0;
				for (int i = 0; i < 9; i++)
				{
					for (int j = 0; j < 9; j++)
					{
						if (tablero.getCasilla(i, j) != variables[contador].getValor())
						{
							tablero.setCasilla(variables[contador].getValor(), i, j);
							tablero.setColor(new Color(0, 140, 230), i, j);
						}

						contador++;
					}
				}
			}
		}
	}

	/**
	 * Algoritmo Forward Checking.
	 * Es un algoritmo recursivo que se ejecuta sucesivamente sobre las variables del problema, dándole valor
	 * a cada una de ellas.
	 * @param i Valor de la variable que se va a examinar.
	 * @return Devuelve verdadero si se ha establecido un valor para cada variable posterior a la variable i.
	 */
	private boolean FC(int i)
	{
		for (int k = 0; k < variables[i].getNumFactibles(); k++)
		{
			// Extraemos el primer valor factible y lo establecemos como el valor de la variable.
			Integer factible = variables[i].getFactible(k);
			variables[i].setValor(factible);

			// Si era la última variable del conjunto, significa que hemos encontrado una solución al problema.
			if (i == 80)
			{
				return true;
			}
			else
			{
				// Realizamos una poda en las variables relacionadas con la variable actual.
				if (forward(factible, i))
				{
					// Si la poda se ha realizado corectamente, aplicamos la recursividad.
					if (FC(i+1))
					{
						// Si la recursividad es correcta, devolvemos correcto.
						return true;
					}
				}

				// Llegados a este punto significa que el valor escogido para la variable no fue satisfactorio.
				// Hay que restaurar la poda anterior y continuar con la siguiente variable del dominio factible.
				restaurar(i);
			}
		}
		return false;
	}

	/**
	 * Se realiza una poda en todas las variable no examinadas (futuras).
	 * @param factible Valor del dominio factible que va a ser podado.
	 * @param variable Variable culpable de la poda.
	 * @return Devuelve verdadero si, al realizar la poda, todas las variables tienen todavía un valor factible al menos.
	 */
	private boolean forward(int factible, int variable)
	{
		// Extraemos la fila y columna de la variable y la fila y la columna del bloque al que pertenece.
		int fila = variable/9;
		int columna = variable%9;
		int filaBloque = fila/3*3;
		int columnaBloque = columna/3*3;

		// A las variables de la fila de la variable "variable" que sean futuras les podamos el valor "factible", siendo el responsable "variable".
		for (int i = columna; i < 9; i++)
		{
			int variableAux = fila*9+i;
			if (variableAux != variable)
			{
				variables[variableAux].podarFactible(factible, variable);
				if (!variables[variableAux].tieneFactibles())
				{
					return false;
				}
			}
		}

		// También a las variables de la misma columna.
		for (int i = fila; i < 9; i++)
		{
			int variableAux = i*9+columna;
			if (variableAux != variable)
			{
				variables[variableAux].podarFactible(factible, variable);
				if (!variables[variableAux].tieneFactibles())
				{
					return false;
				}
			}
		}

		// También a la variable del mismo bloque.
		for (int i = filaBloque; i < filaBloque+3; i++)
		{
			for (int j = columnaBloque; j < columnaBloque+3; j++)
			{
				int variableAux = i*9+j;
				if (variableAux > variable)
				{
					variables[variableAux].podarFactible(factible, variable);
					if (!variables[variableAux].tieneFactibles())
					{
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Restaura los elementos podados. Los cambia desde el vector de elementos podados al vector de elementos factibles.
	 * Se debe indicar qué variable fue la responsable de la poda. Se recorren las variables implicadas en la poda y se
	 * restaura aquellas cuyo culpable sea la variable pasada en los argumentos.
	 * @param variable Variable que fue responsable de la poda.
	 */
	private void restaurar(int variable)
	{
		// Extraemos la fila y columna de la variable y la fila y la columna del bloque al que pertenece.
		int fila = variable/9;
		int columna = variable%9;
		int filaBloque = fila/3*3;
		int columnaBloque = columna/3*3;

		// A las variables de la fila de la variable "variable" que sean futuras les restauramos los elementos del dominio podados por la variable "variable".
		for (int i = columna; i < 9; i++)
		{
			int variableAux = fila*9+i;
			if (variableAux != variable)
			{
				variables[variableAux].recuperarPodado(variable);
			}
		}

		// También a las variables de la misma columna.
		for (int i = fila; i < 9; i++)
		{
			int variableAux = i*9+columna;
			if (variableAux != variable)
			{
				variables[variableAux].recuperarPodado(variable);
			}
		}

		// También a la variable del mismo bloque.
		for (int i = filaBloque; i < filaBloque+3; i++)
		{
			for (int j = columnaBloque; j < columnaBloque+3; j++)
			{
				int variableAux = i*9+j;
				if (variableAux != variable)
				{
					variables[variableAux].recuperarPodado(variable);
				}
			}
		}
	}

	/**
	 * Recorre las aristas (restricciones) entre variables eliminando los valores que causan inconsistencia del dominio de cada variable.
	 * El algoritmo sólo tendrá efecto si se han establecidos valores predeterminados a las variables del problema.
	 * @param variables Conjunto de variables a las que se aplica el algoritmo.
	 * @return Devuelve verdadero si es posible que exista una solución. Devuelve falso si el problema no tiene solución.
	 */
	private boolean AC3()
	{		
		// Se crea el conjunto de aristas.
		ArrayList<Arista> aristas = new ArrayList<Arista>();

		// Se introducen las aristas que representan las restricciones binarias de las variables.
		for (int i = 0; i < 81; i++)
		{
			// Introducimos las 24 aristas de la variable i.
			aristas.addAll(Arista.getAristas(i, false));
		}

		// Repetimos el bucle mientras queden aristas.
		while (!aristas.isEmpty())
		{
			// Extraemos la primera arista de la lista.
			Arista aristaActual = aristas.remove(0);
			boolean cambio = false;

			// Recorremos su dominio factible.
			for (int i = 0; i < variables[aristaActual.primera].getNumFactibles(); i++)
			{
				// Comprobamos que, al darle el valor "i" a la variable "aristaActual.primera", la variable "aristaActual.segunda" todavía tiene elementos en factibles en su dominio.
				Integer factible = variables[aristaActual.primera].getFactible(i);

				// Si sólo tiene un elemento factible y es el que acabamos de asignar, no es una arista consistente.
				if (variables[aristaActual.segunda].tieneFactible(factible) && variables[aristaActual.segunda].getNumFactibles() == 1)
				{
					// Si no es consistente, eliminamos ese valor factible del dominio de la variable "aristaActual.primera". Al hacerlo, permitimos que la variable "aristaActual.segunda" pueda tener
					variables[aristaActual.primera].eliminarFactible(factible);
					i--;

					// Marcamos que ha ocurrido un cambio en el dominio de esa variable.
					cambio = true;
				}
			}

			// Comprobamos que la variable todavía tenga algún elemento en su dominio factible.
			if (!variables[aristaActual.primera].tieneFactibles())
			{
				return false;
			}

			// Comprobamos si se modificó el dominio de la variable.
			if (cambio)
			{
				// Reinsertamos todas las aristas en las que se implique esa variable como "segunda".
				Iterator<Arista> it = Arista.getAristas(aristaActual.primera, false).iterator();
				while (it.hasNext())
				{
					Arista aristaAux = it.next();

					// Sólo se inserta la arista si no estaba ya.
					if (!aristas.contains(aristaAux))
					{
						aristas.add(aristaAux);
					}
				}
			}
		}

		return true;
	}
}

/**
 * Clase que representa una variable del problema del Sudoku.
 * Permite almacenar el dominio factible de la variable y los elementos podados de ese dominio.
 */
class Variable
{
	/**
	 * Valor de la variable.
	 */
	private int valor;

	/**
	 * Lista que contiene los elementos factibles del dominio de la variable.
	 */
	private ArrayList<Integer> factibles;

	/**
	 * Lista de correspondencia que contiene los elementos podados del dominio de la variable.
	 * Para cada elemento podado, se almacena también el índice de la variable que fue responsable de la poda.
	 * Por lo tanto, cada entrada de esta lista de correspondencia tiene dos componentes: el elemento podado y la variable culpable de la poda.
	 */
	private TreeMap<Integer, Integer> podados;

	/**
	 * Constructor por defecto.
	 */
	public Variable()
	{
		valor = 0;
		factibles = new ArrayList<Integer>();
		podados = new TreeMap<Integer, Integer>();

		anadirFactible(1);
		anadirFactible(2);
		anadirFactible(3);
		anadirFactible(4);
		anadirFactible(5);
		anadirFactible(6);
		anadirFactible(7);
		anadirFactible(8);
		anadirFactible(9);
	}

	/**
	 * Constructor sobrecargado que recibe el valor obligatorio que debe tomar la variable.
	 * El dominio factible de la variable queda restringido a ese valor.
	 * @param valor Valor que tendrá la variable si existe solución al problema.
	 */
	public Variable(int valor)
	{
		this.valor = valor;
		factibles = new ArrayList<Integer>();
		podados = new TreeMap<Integer, Integer>();
		
		anadirFactible(valor);
	}

	/**
	 * Establece un valor para la variable.
	 * @param valor Número entero entre 0 y 9. Si es 0, significa que se está definiendo su valor predeterminado.
	 */
	public void setValor(int valor)
	{
		if (1 <= valor && valor <= 9)
		{
			this.valor = valor;
		}
	}

	/**
	 * Comprueba cuál es el valor de la variable.
	 * @return Devuelve un valor entre 0 y 9. 0 es el valor predeterminado; significa que no se ha asignado ninguno todavía.
	 */
	public int getValor()
	{
		return valor;
	}

	/**
	 * Comprueba si la variable tiene algún valor factible en su dominio.
	 * @return Devuelve verdadero si se le puede asignar todavía algún valor. Falso si su conjunto de valores posibles está vacío.
	 */
	public boolean tieneFactibles()
	{
		return factibles.size() > 0;
	}

	/**
	 * Obtiene la cardinalidad del conjunto de elementos factibles del dominio.
	 * @return Devuelve un número entero con el tamaño del dominio factible.
	 */
	public int getNumFactibles()
	{
		return factibles.size();
	}

	/**
	 * Obtiene el elemento factible del dominio número k.
	 * @param k Índice del elemento factible que se quiere extraer.
	 * @return Devuelve un número entero si existe el elemento factible número k. Si no, devuelve null.
	 */
	public Integer getFactible(int k)
	{
		return factibles.get(k);
	}

	/**
	 * Añade un nuevo valor factible al dominio.
	 * @param factible Número entre 1 y 9 obligatoriamente.
	 * @return Devuelve verdadero si lo ha añadido o falso si no lo hace. Si lo añade, es porque ya estaba en el dominio factible.
	 */
	public boolean anadirFactible(int factible)
	{
		if (!factibles.contains(new Integer(factible)))
		{
			factibles.add(new Integer(factible));
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Elimina un valor del dominio.
	 * @param factible Valor del dominio que va a ser eliminado.
	 * @return Devuelve verdadero si lo ha eliminado o falso si no lo ha hecho. Si no lo elimina, es porque no estaba en el dominio.
	 */
	public boolean eliminarFactible(int factible)
	{
		if (factibles.contains(new Integer(factible)))
		{
			factibles.remove(new Integer(factible));
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Comprueba si un elemento está dentro del dominio factible.
	 * @param factible Valor que se quiere comprobar.
	 * @return Devuelve verdadero si el valor pasado en los argumentos está en el dominio factible de la variable.
	 */
	public boolean tieneFactible(int factible)
	{
		return factibles.contains(new Integer(factible));
	}

	/**
	 * Elimina un valor del dominio factible y lo almacena en el dominio podado, por si fuera necesario recuperarlo.
	 * @param factible Valor que va a ser trasladado desde el dominio factible al dominio podado.
	 * @param variable Número de la variable (entre 0 y 80) que ha ocasionado el traslado.
	 * @return Sólo devuelve verdadero si realiza el cambio. Si no lo hace, es porque el valor factible no estaba en el dominio factible o ya estaba en el dominio podado.
	 */
	public boolean podarFactible(int factible, int variable)
	{
		if (eliminarFactible(factible))
		{
			if (!podados.containsKey(factible))
			{
				podados.put(factible, variable);
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * Elimina un valor del dominio podado y lo devuelve al dominio factible.
	 * @param variable Número de la variable (entre 0 y 80) que ocasionó la poda inicial.
	 * @return Devuelve verdadero si ha realizado el cambio.
	 */
	public boolean recuperarPodado(int variable)
	{
		for (int i = 1; i <= 9; i++)
		{
			if (podados.containsKey(new Integer(i)))
				if (podados.get(new Integer(i)) == variable)
				{
					podados.remove(new Integer(i));
					factibles.add(new Integer(i));
					return true;
				}
		}
		
		return false;
	}
}

/**
 * Clase que representa una relación binaria entre dos variables.
 */
class Arista
{
	/**
	 * Índice del vector de variables con la posición en la que se encuentra la variable de salida de la arista.
	 * Entre 0 y 80 inclusive.
	 */
	public final int primera;

	/**
	 * Índice del vector de variables con la posición en la que se encuentra la variable de llegada de la arista.
	 * Entre 0 y 80 inclusive.
	 */
	public final int segunda;

	/**
	 * Constructor por defecto.
	 * Hay que especificar la pareja de variables de la arista que están relacionadas.
	 * @param primera Índice de la variable de salida.
	 * @param segunda Índice de la variable de llegada.
	 */
	public Arista(int primera, int segunda)
	{
		this.primera = primera;
		this.segunda = segunda;
	}

	/**
	 * Genera todas las aristas de una variable.
	 * @param variable Variable sobre la que se calcularán sus aristas.
	 * @param primera Indica si la variable estará como "primera" o como "segunda" de la arista.
	 * @return Devuelve una lista de aristas.
	 */
	public static ArrayList<Arista> getAristas(int variable, boolean primera)
	{
		ArrayList<Arista> aristas = new ArrayList<Arista>();

		// Extraemos la fila y columna de la variable y la fila y la columna del bloque al que pertenece.
		int fila = variable/9;
		int columna = variable%9;
		int filaBloque = fila/3*3;
		int columnaBloque = columna/3*3;

		// Añadimos las aristas que implique las variables de la fila de la variable "variable".
		for (int i = 0; i < 9; i++)
		{
			int variableAux = fila*9+i;
			if (variableAux != variable)
			{
				if (primera)
					aristas.add(new Arista(variable, variableAux));
				else
					aristas.add(new Arista(variableAux, variable));
			}
		}

		// También las aristas con las variables de la misma columna.
		for (int i = 0; i < 9; i++)
		{
			int variableAux = i*9+columna;
			if (variableAux != variable)
			{
				if (primera)
					aristas.add(new Arista(variable, variableAux));
				else
					aristas.add(new Arista(variableAux, variable));
			}
		}

		// También las aristas con las variables del mismo bloque.
		for (int i = filaBloque; i < filaBloque+3; i++)
		{
			for (int j = columnaBloque; j < columnaBloque+3; j++)
			{
				int variableAux = i*9+j;
				if (variableAux != variable)
				{
					if (primera)
						aristas.add(new Arista(variable, variableAux));
					else
						aristas.add(new Arista(variableAux, variable));
				}
			}
		}

		return aristas;
	}

	/**
	 * Sobreescritura del método de igualdad.
	 * Se utiliza para saber si la arista se encuentra dentro de una colección de datos.
	 * Comprueba si los índices de las variables son iguales.
	 * @param o Objeto del tipo Arista con el que se va a comparar.
	 * @return Devuelve verdadero si las aristas son iguales.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Arista)
		{
			Arista arista = (Arista) o;
			return primera == arista.primera && segunda == arista.segunda;
		}
		else
			return false;
	}

	/**
	 * Sobreescritura del método que calcula el valor hash de la arista.
	 * Sólo se sobreescribe para que sea coherente con el método de igualdad que también se ha sobreescrito.
	 * @return Devuelve el valor entero de multipicar los índices de ambas variables.
	 */
	@Override
	public int hashCode()
	{
		return primera*segunda;
	}
}