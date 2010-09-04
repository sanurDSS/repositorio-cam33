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
			String genero = "";
			if (tipoAux.esArray) genero += "ARRAY ";
			if (tipoAux.esMetodo) genero += "METODO ";
			if (tipoAux.esClase) genero += "CLASE ";
			if (tipoAux.esAtributo) genero += "ATRIBUTO ";
			if (tipoAux.esParametro) genero += "PARAMETRO ";
			if (tipoAux.esObjeto) genero += "OBJETO ";
			cadena += tipoAux.tipo + " \t" + tipoAux.tipoBase + " \t" + tipoAux.dimension + " \t" + genero + System.getProperty("line.separator");
		}
		cadena += "_________________________________________________________________" + System.getProperty("line.separator");
		return cadena;
	}

	public TablaTipos()
	{
		tipos = new ArrayList<Tipo>();

		addTipo(0, -1);
		addTipo(0, -1);
		addTipo(0, -1);
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
				if (tipoSimple == BOOL)
				{
					return "bool";
				}
				else
				{
					return getTablaSimbolos(tipoSimple).getNombreClase();
				}
			}
		}
	}

	/**
	 * Añade un nuevo tipo según los parámetros indicados.
	 * @param tipoBase Número del tipo base para el tipo.
	 * @param dimension Cantidad de dimensiones.
	 * @return Devuelve el número del tipo. Este número de tipo coincide con la cantidad de tipos que hay incrementando 1.
	 */
	public int addTipo(int tipoBase, int dimension)
	{
		tipos.add(new Tipo(tipos.size() + 1, tipoBase, dimension));
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

	/**
	 * @param tipo Número del tipo que se quiere conocer.
	 * @return Devuelve verdadero si es una clase, falso si no lo es el tipo indicado es incorrecto.
	 */
	public boolean getEsClase(int tipo)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				return tipoAux.esClase;
			}
		}
		return false;
	}

	/**
	 * Modifica el valor de "esClase" del tipo indicado.
	 * @param tipo Número del tipo que se va a modificar.
	 * @param esArray Nuevo valor para indicar si es una clase o no.
	 * @return Devuelve el estado anterior o falso si el tipo indicado era incorrecto.
	 */
	public boolean setEsClase(int tipo, boolean esClase)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				boolean esClaseAnterior = tipoAux.esClase;
				tipoAux.esClase = esClase;
				return esClaseAnterior;
			}
		}
		return false;
	}

	/**
	 * @param tipo Número del tipo que se quiere conocer.
	 * @return Devuelve verdadero si es una clase, falso si no lo es el tipo indicado es incorrecto.
	 */
	public boolean getEsMetodo(int tipo)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				return tipoAux.esMetodo;
			}
		}
		return false;
	}

	/**
	 * Modifica el valor de "esClase" del tipo indicado.
	 * @param tipo Número del tipo que se va a modificar.
	 * @param esArray Nuevo valor para indicar si es una clase o no.
	 * @return Devuelve el estado anterior o falso si el tipo indicado era incorrecto.
	 */
	public boolean setEsMetodo(int tipo, boolean esMetodo)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				boolean anterior = tipoAux.esMetodo;
				tipoAux.esMetodo = esMetodo;
				return anterior;
			}
		}
		return false;
	}

	/**
	 * @param tipo Número del tipo que se quiere conocer.
	 * @return Devuelve verdadero si es una clase, falso si no lo es el tipo indicado es incorrecto.
	 */
	public boolean getEsAtributo(int tipo)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				return tipoAux.esAtributo;
			}
		}
		return false;
	}

	/**
	 * Modifica el valor de "esClase" del tipo indicado.
	 * @param tipo Número del tipo que se va a modificar.
	 * @param esArray Nuevo valor para indicar si es una clase o no.
	 * @return Devuelve el estado anterior o falso si el tipo indicado era incorrecto.
	 */
	public boolean setEsAtributo(int tipo, boolean esAtributo)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				boolean anterior = tipoAux.esAtributo;
				tipoAux.esAtributo = esAtributo;
				return anterior;
			}
		}
		return false;
	}

	/**
	 * @param tipo Número del tipo que se quiere conocer.
	 * @return Devuelve verdadero si es una clase, falso si no lo es el tipo indicado es incorrecto.
	 */
	public boolean getEsParametro(int tipo)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				return tipoAux.esParametro;
			}
		}
		return false;
	}

	/**
	 * Modifica el valor de "esClase" del tipo indicado.
	 * @param tipo Número del tipo que se va a modificar.
	 * @param esArray Nuevo valor para indicar si es una clase o no.
	 * @return Devuelve el estado anterior o falso si el tipo indicado era incorrecto.
	 */
	public boolean setEsParametro(int tipo, boolean esParametro)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				boolean anterior = tipoAux.esParametro;
				tipoAux.esParametro = esParametro;
				return anterior;
			}
		}
		return false;
	}

	/**
	 * @param tipo Número del tipo que se quiere conocer.
	 * @return Devuelve 0 si no tiene dimensión; mayor que 0 si sí tiene, -1 si el tipo indicado no existe.
	 */
	public TablaSimbolos getTablaSimbolos(int tipo)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				return tipoAux.tablaSimbolos;
			}
		}
		return null;
	}

	/**
	 * Modifica la dimensión del tipo indicado.
	 * @param tipo Número del tipo que se va a modificar.
	 * @param dimension Nueva dimensión.
	 * @return Devuelve la dimensión anterior o -1 si el tipo indicado no existe.
	 */
	public TablaSimbolos setTablaSimbolos(int tipo, TablaSimbolos tablaSimbolos)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				TablaSimbolos anterior = tipoAux.tablaSimbolos;
				tipoAux.tablaSimbolos = tablaSimbolos;
				return anterior;
			}
		}
		return null;
	}

	/**
	 * @param tipo Número del tipo que se quiere conocer.
	 * @return Devuelve 0 si no tiene dimensión; mayor que 0 si sí tiene, -1 si el tipo indicado no existe.
	 */
	public int getRetorno(int tipo)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				return tipoAux.retorno;
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
	public int setRetorno(int tipo, int retorno)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				int anterior = tipoAux.dimension;
				tipoAux.retorno = retorno;
				return anterior;
			}
		}
		return -1;
	}

	/**
	 * @param tipo Número del tipo que se quiere conocer.
	 * @return Devuelve verdadero si es una clase, falso si no lo es el tipo indicado es incorrecto.
	 */
	public boolean getVisibilidad(int tipo)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				return tipoAux.visibilidad;
			}
		}
		return false;
	}

	/**
	 * Modifica el valor de "esClase" del tipo indicado.
	 * @param tipo Número del tipo que se va a modificar.
	 * @param esArray Nuevo valor para indicar si es una clase o no.
	 * @return Devuelve el estado anterior o falso si el tipo indicado era incorrecto.
	 */
	public boolean setVisibilidad(int tipo, boolean visibilidad)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				boolean anterior = tipoAux.visibilidad;
				tipoAux.visibilidad = visibilidad;
				return anterior;
			}
		}
		return false;
	}

	/**
	 * @param tipo Número del tipo que se quiere conocer.
	 * @return Devuelve verdadero si es una clase, falso si no lo es el tipo indicado es incorrecto.
	 */
	public boolean getEsObjeto(int tipo)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				return tipoAux.esObjeto;
			}
		}
		return false;
	}

	/**
	 * Modifica el valor de "esClase" del tipo indicado.
	 * @param tipo Número del tipo que se va a modificar.
	 * @param esArray Nuevo valor para indicar si es una clase o no.
	 * @return Devuelve el estado anterior o falso si el tipo indicado era incorrecto.
	 */
	public boolean setEsObjeto(int tipo, boolean esObjeto)
	{
		Iterator<Tipo> i = tipos.iterator();
		while (i.hasNext())
		{
			Tipo tipoAux = i.next();
			if (tipoAux.tipo == tipo)
			{
				boolean anterior = tipoAux.esObjeto;
				tipoAux.esObjeto = esObjeto;
				return anterior;
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
	public boolean esClase;
	public boolean esMetodo;
	public boolean esAtributo;
	public boolean esParametro;
	public boolean esObjeto;
	
	public TablaSimbolos tablaSimbolos;
	public int retorno;
	public boolean visibilidad;

	public Tipo(int tipo, int tipoBase, int dimension)
	{
		this.tipo = tipo;
		this.tipoBase = tipoBase;
		this.dimension = dimension;
		
		this.esArray = false;
		this.esClase = false;
		this.esMetodo = false;
		this.esAtributo = false;
		this.esParametro = false;
		this.esObjeto = false;
		
		this.tablaSimbolos = null;
		this.retorno = -1;
		this.visibilidad = true;
	}
}
