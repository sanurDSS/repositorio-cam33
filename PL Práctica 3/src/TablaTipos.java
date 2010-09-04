import java.util.ArrayList;
import java.util.Iterator;

public class TablaTipos
{
	private ArrayList<Tipo> tipos;
	public static final int INT = 1;
	public static final int DOUBLE = 2;
	public static final int BOOL = 3;

	public static int convertTipoSimple(String tipoSimple)
	{
		if (tipoSimple.equals("int"))
		{
			return INT;
		}
		else
		{
			if (tipoSimple.equals("double"))
			{
				return DOUBLE;
			}
			else
			{
				return BOOL;
			}
		}
	}

	/**
	 * Genera la tabla de tipos orientada a depuración.
	 * @return Devuelve la tabla de tipos en un formato de tabla.
	 */
	@Override
	public String toString()
	{
		String cadena = "";
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			cadena += tipoAux.tipo + " \t" + tipoAux.tipoBase + " \t" + tipoAux.dimension + " \t" + tipoAux.esArray + System.getProperty("line.separator");
		}
		cadena += "_________________________________________________________________" + System.getProperty("line.separator");
		return cadena;
	}

	public TablaTipos()
	{
		tipos = new ArrayList<Tipo>();

		addTipo(0, 0, false);
		addTipo(0, 0, false);
		addTipo(0, 0, false);
	}

	/**
	 * Dado un número de tipo, calcula recursivamente cuál es su tipo simple.
	 * @param tipo
	 * @return
	 */
	public int getTipoSimple(int tipo)
	{
		Tipo tipoAux = getTipo(tipo);
		if (tipoAux != null)
		{
			while (tipoAux.esArray)
			{
				tipoAux = getTipo(tipoAux.tipoBase);
			}
			return tipoAux.tipoBase;
		}
		return -1;
	}

	/**
	 * Dado un tipo, calcula recursivamente cuál es su tipo simple y lo devuelve en formato String.
	 * @param tipo
	 * @return
	 */
	public String getTipoSimpleString(int tipo)
	{
		int tipoSimple = getTipoSimple(tipo);
		if (tipoSimple == INT)
		{
			return "int";
		}
		else
		{
			if (tipoSimple == DOUBLE)
			{
				return "double";
			}
			else
			{
				return "bool";
			}
		}
	}

	/**
	 * Añade un nuevo tipo según los parámetros indicados.
	 * @param tipoBase Número del tipo base para el tipo.
	 * @param dimension Cantidad de dimensiones.
	 * @param esArray Indica si es un vector o una única variable.
	 * @return Devuelve el número del tipo. Este número de tipo coincide con la cantidad de tipos que hay incrementando 1.
	 */
	public int addTipo(int tipoBase, int dimension, boolean esArray)
	{
		tipos.add(new Tipo(tipos.size() + 1, tipoBase, dimension, esArray));
		return tipos.size();
	}

	/**
	 * Dado un número de tipo, extrae el objeto Tipo.
	 * @param tipo Número del tipo.
	 * @return Devuelve un objeto Tipo o null si no lo encuentra.
	 */
	private Tipo getTipo(int tipo)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				return tipoAux;
			}

		}
		return null;
	}

	/**
	 * @param tipo Número del tipo que se quiere conocer.
	 * @return Devuelve 0 si ya es un tipo simple; mayor que 0 si es un compuesto, -1 si el tipo indicado no existe.
	 */
	public int getTipoBase(int tipo)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				return tipoAux.tipoBase;
			}

		}
		return -1;
	}

	/**
	 * Modifica el tipo base del tipo indicado.
	 * @param tipo Número del tipo que se va a modificar.
	 * @param tipoBase Nuevo tipo base.
	 * @return Devuelve el tipo base anterior o -1 si el tipo indicado no existe.
	 */
	public int setTipoBase(int tipo, int tipoBase)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				int tipoBaseAnterior = tipoAux.tipoBase;
				tipoAux.tipoBase = tipoBase;
				return tipoBaseAnterior;
			}
		}
		return -1;
	}

	/**
	 * @param tipo Número del tipo que se quiere conocer.
	 * @return Devuelve 0 si no tiene dimensión; mayor que 0 si sí tiene, -1 si el tipo indicado no existe.
	 */
	public int getDimension(int tipo)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				return tipoAux.dimension;
			}
		}
		return -1;
	}

	/**
	 * Modifica la dimensión del tipo indicado.
	 * @param tipo Número del tipo que se va a modificar.
	 * @param dimension Nueva dimensión.
	 * @return Devuelve la dimensión anterior o -1 si el tipo indicado no existe.
	 */
	public int setDimension(int tipo, int dimension)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				int dimensionAnterior = tipoAux.dimension;
				tipoAux.dimension = dimension;
				return dimensionAnterior;
			}
		}
		return -1;
	}

	/**
	 * @param tipo Número del tipo que se quiere conocer.
	 * @return Devuelve verdadero es es un array, falso si no lo es el tipo indicado es incorrecto.
	 */
	public boolean getEsArray(int tipo)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				return tipoAux.esArray;
			}
		}
		return false;
	}

	/**
	 * Modifica el valor de "esArray" del tipo indicado.
	 * @param tipo Número del tipo que se va a modificar.
	 * @param esArray Nuevo valor para indicar si es un array o no.
	 * @return Devuelve el estado anterior o falso si el tipo indicado era incorrecto.
	 */
	public boolean setEsArray(int tipo, boolean esArray)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				boolean esArrayAnterior = tipoAux.esArray;
				tipoAux.esArray = esArray;
				return esArrayAnterior;
			}
		}
		return false;
	}
}

class Tipo
{
	public int tipo;
	public int tipoBase;
	public int dimension;
	public boolean esArray;

	public Tipo(int tipo, int tipoBase, int dimension, boolean esArray)
	{
		this.tipo = tipo;
		this.tipoBase = tipoBase;
		this.dimension = dimension;
		this.esArray = esArray;
	}
}
