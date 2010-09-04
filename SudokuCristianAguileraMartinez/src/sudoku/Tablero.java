/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author mireia
 */
public class Tablero
{

	//Fichero del que se va a crear el tablero
	private File archivo;
	//Datos del tablero
	private int m_tablero[][];
	private Color m_tableroColores[][];

	//Constructor del tablero
	public Tablero()
	{
		archivo = null;

		//Crea el tablero
		m_tablero = new int[9][9];
		m_tableroColores = new Color[9][9];

		//Inicializa el tablero
		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				m_tablero[i][j] = 0;
				m_tableroColores[i][j] = Color.DARK_GRAY;
			}
		}

	}

	@Override
	public String toString()
	{
		String aux = "";
		for (int i = 0; i < 9; i++)
		{
			String fila = "";
			for (int j = 0; j < 9; j++)
			{
				fila += (m_tablero[i][j] + " ");
			}
			aux += fila.trim() + "\n";
		}
		return aux;
	}

	/**
	 * Constsructor de Copia del tablero
	 * @param original Tablero del cual realizar la copia
	 */
	public Tablero(Tablero original)
	{
		archivo = original.archivo;

		//Crea el tablero
		m_tablero = new int[9][9];
		m_tableroColores = new Color[9][9];

		//Inicializa el tablero
		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				m_tablero[i][j] = original.m_tablero[i][j];
				m_tableroColores[i][j] = original.m_tableroColores[i][j];
			}
		}
	}

	/**
	 * Devuelve el valor de una casilla
	 * @param i
	 * @param j
	 * @return
	 */
	public int getCasilla(int i, int j)
	{
		return m_tablero[i][j];
	}

	/**
	 * Indica el valor de una casilla
	 */
	public void setCasilla(int valor, int fila, int columna)
	{
		m_tablero[fila][columna] = valor;
	}

	public Color getColor(int i, int j)
	{
		return m_tableroColores[i][j];
	}

	public void setColor(Color color, int fila, int columna)
	{
		m_tableroColores[fila][columna] = color;
	}

	/**
	 * Indicar el nombre de un fichero
	 */
	public void setFichero(File fichero)
	{
		archivo = fichero;
	}

	/**
	 * Devuelve el fichero
	 */
	public File getFichero()
	{
		return archivo;
	}

	public boolean Completo()
	{
		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; i < 9; i++)
			{
				if (m_tablero[i][j] == 0)
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Carga en m_tablero el sudoku leído desde un fichero
	 */
	public void CargarTablero()
	{
		FileReader fr = null;
		String sCadena;
		String delimitador = " ";
		int i;

		try
		{
			//Abre el fichero
			fr = new FileReader(archivo.getPath());
			BufferedReader bf = new BufferedReader(fr);

			try
			{
				i = 0;
				//Mientras queden líneas en el fichero las lee
				while ((sCadena = bf.readLine()) != null)
				{
					//Separa los diferentes números de la cadena que ha leído
					String[] numeros = sCadena.split(delimitador);

					for (int j = 0; j < numeros.length; j++)
					{
						m_tablero[i][j] = Integer.parseInt(numeros[j]);
						m_tableroColores[i][j] = Color.DARK_GRAY;
					}

					i++;
				}
				//Si falla la lectura del fichero
			}
			catch (IOException e1)
			{
				System.out.println("Error en la lectura del fichero:" + archivo.getName());
			}
			//Si falla la apertura del fichero
		}
		catch (FileNotFoundException e2)
		{
			System.out.println("Error al abrir el fichero: " + archivo.getName());
		}
		finally
		{
			// Cerramos el fichero en finally porque así nos aseguramos que se cierra tanto si todo ha ido bien, como
			// si ha saltado alguna excepción
			try
			{
				if (null != fr)
				{
					fr.close();
				}
			}
			catch (Exception e3)
			{
				System.out.println("Error al cerrar el fichero: " + archivo.getName());
			}
		}
	}

	/**
	 * Deja todo el tablero con valor 0
	 */
	public void LimpiarTablero()
	{
		//Recorre el tablero
		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				m_tablero[i][j] = 0;
				m_tableroColores[i][j] = Color.DARK_GRAY;
			}
		}
	}

	/**
	 * Comprueba si el tablero está vacío
	 */
	public boolean TableroVacio()
	{
		boolean vacio;
		vacio = true;

		//Recorre el tablero
		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				if (m_tablero[i][j] != 0)
				{
					vacio = false;
				}
			}
		}

		return vacio;
	}

	/**
	 * Comprueba si la solución dada es correcta. Cada cuadrícula, cada fila y cada columna debe contener los números del 1 a 9
	 */
	public boolean TableroCorrecto()
	{
		int casilla;
		boolean correcto;
		correcto = true;

		//Recorre todo el tablero
		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				casilla = m_tablero[i][j];

				if (casilla == 0)
				{
					return false;
				}

				//Comprueba que ese número no esté repetido en la fila
				for (int z = 0; z < 9; z++)
				{
					if (j != z)
					{
						if (casilla == m_tablero[i][z])
						{
							correcto = false;
							return correcto;
						}
					}
				}

				//Comprueba que ese número no esté repetido en la columna
				for (int z = 0; z < 9; z++)
				{
					if (i != z)
					{
						if (casilla == m_tablero[z][j])
						{
							correcto = false;
							return correcto;
						}
					}
				}

				//Comprueba que ese número no esté repetido en el bloque
				for (int z = i/3*3; z < i/3*3+3; z++)
				{
					for (int y = j/3*3; y < j/3*3+3; y++)
					{
						if (z!=i && y!=j)
						{
							if (casilla == m_tablero[z][y])
							{
								correcto = false;
								return correcto;
							}
						}
					}
				}
			}
		}
		return correcto;
	}

	/**
	 * Comprueba que los elementos introducidos en el tablero no colisionan entre sí.
	 * Es decir, comprueba que no haya dos números repetidos en la misma fila, columna o bloque.
	 * @return Devuelve verdadero si los números introducidos hasta ahora no proporcionan un tablero incorrecto.
	 */
	public Integer[] TableroCorrectoParcialmente()
	{
		int casilla;
		Integer[] incorrectas = null;

		//Recorre todo el tablero
		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				casilla = m_tablero[i][j];

				if (casilla == 0)
				{
					continue;
				}

				//Comprueba que ese número no esté repetido en la fila
				for (int z = 0; z < 9; z++)
				{
					if (j != z)
					{
						if (casilla == m_tablero[i][z])
						{
							incorrectas = new Integer[4];
							incorrectas[0] = i;
							incorrectas[1] = j;
							incorrectas[2] = i;
							incorrectas[3] = z;
						}
					}
				}

				//Comprueba que ese número no esté repetido en la columna
				for (int z = 0; z < 9; z++)
				{
					if (i != z)
					{
						if (casilla == m_tablero[z][j])
						{
							incorrectas = new Integer[4];
							incorrectas[0] = i;
							incorrectas[1] = j;
							incorrectas[2] = z;
							incorrectas[3] = j;
						}
					}
				}

				//Comprueba que ese número no esté repetido en el bloque
				for (int z = i/3*3; z < i/3*3+3; z++)
				{
					for (int y = j/3*3; y < j/3*3+3; y++)
					{
						if (z!=i && y!=j)
						{
							if (casilla == m_tablero[z][y])
							{
								incorrectas = new Integer[4];
								incorrectas[0] = i;
								incorrectas[1] = j;
								incorrectas[2] = z;
								incorrectas[3] = y;
							}
						}
					}
				}
			}
		}
		return incorrectas;
	}
}
