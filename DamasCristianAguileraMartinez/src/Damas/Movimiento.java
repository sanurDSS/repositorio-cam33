package Damas;

/**
 *
 * @author mireia
 */
public class Movimiento
{
	public Posicion m_inicial;
	public Posicion m_final;

	public Movimiento()
	{
		m_inicial = new Posicion();
		m_final = new Posicion();
	}

	public Movimiento(int filai, int columnai, int filaf, int columnaf)
	{
		m_inicial = new Posicion();
		m_final = new Posicion();

		m_inicial.setX(filai);
		m_inicial.setY(columnai);

		m_final.setX(filaf);
		m_final.setY(columnaf);
	}

	public Movimiento(Movimiento original)
	{
		m_inicial.m_px = original.m_inicial.m_px;
		m_inicial.m_py = original.m_inicial.m_py;
		m_final.m_px = original.m_final.m_px;
		m_final.m_py = original.m_final.m_py;
	}

	public void setInicial(int x, int y)
	{
		m_inicial.setX(x);
		m_inicial.setY(y);
	}

	public void setFinal(int x, int y)
	{
		m_final.setX(x);
		m_final.setY(y);
	}

	public Posicion getInicial()
	{
		return m_inicial;
	}

	public Posicion getFinal()
	{
		return m_final;
	}

	public Movimiento swap()
	{
		int aux = m_inicial.m_px;
		m_inicial.m_px = m_inicial.m_py;
		m_inicial.m_py = aux;

		aux = m_final.m_px;
		m_final.m_px = m_final.m_py;
		m_final.m_py = aux;

		return this;
	}

	@Override
	public String toString()
	{
		return "(" + m_inicial.m_px + ", " + m_inicial.m_py + ") -> (" + m_final.m_px + ", " + m_final.m_py + ")";
	}
}
