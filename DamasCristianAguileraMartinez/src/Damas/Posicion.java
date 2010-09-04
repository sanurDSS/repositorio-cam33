package Damas;

/**
 *
 * @author mireia
 */
public class Posicion
{
	public int m_px; //Posición x
	public int m_py; //Posición y

	public Posicion()
	{
		m_px = 0;
		m_py = 0;
	}

	public Posicion(Posicion original)
	{
		m_px = original.m_px;
		m_py = original.m_py;
	}

	public void setX(int x)
	{
		m_px = x;
	}

	public void setY(int y)
	{
		m_py = y;
	}

	public int getX()
	{
		return m_px;
	}

	public int getY()
	{
		return m_py;
	}
}
