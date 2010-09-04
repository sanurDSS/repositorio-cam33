public class Simbolo
{
	private int tipo;
	private String lexema;
	private int posicion;
	private int cantidadParametros;
	public static final int LOCAL = 0, ARGUMENTO = 1, FUNCION = 2;

	public Simbolo(int tipo, String lexema, int posicion, int cantidadParametros)
	{
		this.tipo = tipo;
		this.lexema = lexema;
		this.posicion = posicion;
		this.cantidadParametros = cantidadParametros;
	}

	public int getTipo()
	{
		return tipo;
	}

	public String getLexema()
	{
		return lexema;
	}

	public int getPosicion()
	{
		return posicion;
	}

	public int getCantidadParametros()
	{
		return cantidadParametros;
	}

	public String getDesplazamientoEbp()
	{
		if (tipo == LOCAL)
		{
			return "- " + (4 * posicion);
		}
		else
		{
			return "+ " + (4 * posicion + 4);
		}
	}
}
