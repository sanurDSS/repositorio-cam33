public class Simbolo
{
	private int tipo;
	private String lexema;
	private int fila;
	private int columna;
	private int posicionLocal;

	public Simbolo(int tipo, String lexema, int fila, int columna, int posicionLocal)
	{
		this.tipo = tipo;
		this.lexema = lexema;
		this.fila = fila;
		this.columna = columna;
		this.posicionLocal = posicionLocal;
	}

	public int getTipo()
	{
		return tipo;
	}

	public void setTipo(int tipo)
	{
		this.tipo = tipo;
	}

	public String getLexema()
	{
		return lexema;
	}

	public void setLexema(String lexema)
	{
		this.lexema = lexema;
	}
	
	public int getFila()
	{
		return fila;
	}
	
	public void setFila(int fila)
	{
		this.fila = fila;
	}
	
	public int getColumna()
	{
		return columna;
	}
	
	public void setColumna(int columna)
	{
		this.columna = columna;
	}

	public int getPosicionLocal()
	{
		return posicionLocal;
	}

	public void setPosicionLocal(int posicionLocal)
	{
		this.posicionLocal = posicionLocal;
	}
}