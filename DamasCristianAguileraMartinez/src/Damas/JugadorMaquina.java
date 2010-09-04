package Damas;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 *
 * @author mireia
 */
public class JugadorMaquina extends Jugador
{
	// Profundidad hasta la que se va a desarrollar el árbol de juego
	// El nivel mínimo es el nivel desde el que comienza a ejecutarse el algoritmo de búsqueda.
	// El nivel máximo es el nivel hasta el que se ejecuta.
	// Cuando se necesite encontrar un movimiento, se ejecutará el algoritmmo en
	// diferentes niveles, comenzando desde el nivel más bajo hasta que se
	// exceda el tiempo de ejecución o se alcance el nivel máximo.
	// NO SE DEBEN MODIFICAR ESTOS VALORES. DEBE HACERSE DESDE LA INTERFAZ
	public static int NIVEL_MINIMO = 0;
	public static int NIVEL_MAXIMO = 0;

	// Algoritmo a utilizar.
	public static int ALGORITMO = 0;

	// Enumeración de los algoritmos.
	public static int MINIMAX = 0;
	public static int ALFABETA = 1;
	public static int SSS = 2;

	// Los algoritmos modificarán este atributo, indicando qué movimiento han
	// decidido escoger.
	public int m_ramaLlegada;

	// Constructor
	public JugadorMaquina(int jugador)
	{
		super(jugador);
		m_movimiento = new Movimiento();
	}

	// Función que se ejecuta en el thread.
	public void run()
	{
		// Obtenemos el número de movimientos posibles.
		ArrayList<Movimiento> movimientos = m_tablero.movimientosPosibles(m_jugador);

		// Mostramos el mensaje por pantalla indicando qué algoritmo se va a ejecutar y hasta qué nivel explora.
		String algoritmo = "minimax";
		if (ALGORITMO == ALFABETA)
			algoritmo = "alfa-beta";
		else if (ALGORITMO == SSS)
			algoritmo = "SSS*";
		System.out.println("(" + algoritmo + ") Sucesivos niveles alcanzados:");

		// Ejecutamos el algoritmo seleccionado desde el NIVEL_MINIMO hasta el NIVEL_MAXIMO.
		// Si el tiempo se acaba, se detiene el hilo y el último movimiento calculado es el que se efectúa.
		for (int i = NIVEL_MINIMO; i <= NIVEL_MAXIMO; i++)
		{
			// Comprobamos qué algoritmo hay que ejecutar. Nos dejará en el atributo m_ramaLlegada qué movimiento ha escodigo.
			if (ALGORITMO == MINIMAX)
				minimax(m_tablero, m_jugador, 1, i);
			else
				if (ALGORITMO == ALFABETA)
					alfaBeta(m_tablero, m_jugador, 1, i, Integer.MIN_VALUE, Integer.MAX_VALUE);
				else
					SSS(m_tablero, m_jugador, i);

			// Finalmente guardamos el movimiento en la variable m_movimiento.
			Movimiento movimientoAux = new Movimiento(movimientos.get(m_ramaLlegada).getInicial().m_py, movimientos.get(m_ramaLlegada).getInicial().m_px, movimientos.get(m_ramaLlegada).getFinal().m_py, movimientos.get(m_ramaLlegada).getFinal().m_px);
			m_movimiento = movimientoAux;
			System.out.println("    " + i);
		}
		
		// Indicamos al proceso padre que ya hemos terminado de calcular el movimiento.
		isDone(true);
	}

	/**
	 * Ejecuta una serie de pruebas para comprobar que los algoritmos funcionan correctamente.
	 */
	public void pruebasEvaluacion()
	{
		long tiempoMM = 0;
		long tiempoAB = 0;
		long tiempoSSS = 0;
		long tiempoActual = 0;
		int nivel = 5;
		int cantidadPruebas = 10;

		System.out.println("--------");
		for (int i = 0; i < cantidadPruebas; i++)
		{
			// Generamos el tablero aleatoriamente.
			Tablero pruebas = new Tablero(8);
			for (int j = 0; j < 8; j++)
			{
				for (int k = 0; k < 8; k++)
				{
					pruebas.cambiarCasilla(j, k, ((int) (Math.random()*100))%3);
				}
			}

			// Ejecutamos el algoritmo Minimax.
			tiempoActual = System.currentTimeMillis();
			int valorMM = minimax(pruebas, 2, 1, nivel);
			tiempoMM += System.currentTimeMillis() - tiempoActual;

			// Ejecutamos el algoritmo Alfa-beta.
			tiempoActual = System.currentTimeMillis();
			int valorAB = alfaBeta(pruebas, 2, 1, nivel, Integer.MIN_VALUE, Integer.MAX_VALUE);
			tiempoAB += System.currentTimeMillis() - tiempoActual;

			// Ejecutamos el algoritmo SSS*.
			tiempoActual = System.currentTimeMillis();
			int valorSSS = SSS(pruebas, 2, nivel);
			tiempoSSS += System.currentTimeMillis() - tiempoActual;

			// Mostramos el resultado de los 3 algoritmos y comprobamos el resultado.
			System.out.print("Prueba (" + String.format("%04d", i) + "): " + String.format("%1$#4s", valorMM) + " " + String.format("%1$#4s", valorAB) + " " + String.format("%1$#4s", valorSSS));
			if (valorMM != valorAB || valorAB != valorSSS)
			{
				System.out.println(" FALLO");
				break;
			}
			else
			{
				System.out.println(" OK");
			}
		}

		// Mostramos los resultados de los cálculos.
		/*float total = 0;
		for (int i = 0; i < 6; i++)
		{
			total += numCasos[i];
		}
		System.out.println("Nodos explorados: " + total);
		for (int i = 0; i < 6; i++)
		{
			System.out.println("  caso " + i + ": " + numCasos[i] + " (" + (numCasos[i]/total*100) + "%)");
		}

		long totalTiempo = 0;
		for (int i = 0; i < 6; i++)
		{
			totalTiempo += tiempoCasos[i];
		}
		System.out.println("Tiempo total: " + totalTiempo/1000.0 + "s");
		for (int i = 0; i < 6; i++)
		{
			System.out.println("  caso " + i + ": " + tiempoCasos[i]/1000.0 + "s (" + (tiempoCasos[i]/(float)totalTiempo*100) + "%)");
		}*/

		// Mostramos los tiempos de ejecución de cada algoritmo.
		System.out.println("Tiempo de algoritmos: " + (tiempoMM + tiempoAB + tiempoSSS)/1000.0);
		System.out.println("  MM:   " + (tiempoMM/1000.0) + " (" + (tiempoMM/(float) (tiempoMM + tiempoAB + tiempoSSS)*100) + "%)");
		System.out.println("  AB:   " + (tiempoAB/1000.0) + " (" + (tiempoAB/(float) (tiempoMM + tiempoAB + tiempoSSS)*100) + "%)");
		System.out.println("  SSS*: " + (tiempoSSS/1000.0) + " (" + (tiempoSSS/(float) (tiempoMM + tiempoAB + tiempoSSS)*100) + "%)");

		/*System.out.println("Llamadas Alfa-beta: " + contadorAB);
		System.out.println("Podas Alfa-beta   : " + contadorPodas);*/
	}

	/**
	 * Implementación del algoritmo Alfa-beta.
	 * @param tablero Tablero sobre el que se va a calcular el movimiento.
	 * @param jugador Jugador que tiene que realizar el movimiento.
	 * @param nivelActual Nivel actual del algoritmo (1 cuando sea la llamada principal; >1 cuando sea una llamada recursiva).
	 * @param nivelesTotales Número de niveles que explorará el algoritmo.
	 * @param alpha Valor alfa.
	 * @param beta Valor beta.
	 * @return Devuelve el valor minimax del mejor estado posible.
	 */
	public int alfaBeta(Tablero tablero, int jugador, int nivelActual, int nivelesTotales, int alpha, int beta)
	{
		// Obtenemos todos los posibles movimientos que se pueden realizar para el jugador indicado.
		ArrayList<Movimiento> movimientos = tablero.movimientosPosibles(jugador);
		int numHijos = movimientos.size();

		// Caso base.
		if (nivelActual == nivelesTotales || numHijos == 0)
		{
			return tablero.funcionEvaluacion();
		}
		else
		{
			if (jugador == 1)
			{
				// Recorremos los hijos y calculamos recursivamente su valor minimax, devolviendo el mayor.
				for (int i = 0; i < numHijos; i++)
				{
					// Generamos el tablero para el movimiento "i".
					Tablero hijoi = new Tablero(tablero);
					hijoi.hacerTirada(movimientos.get(i).swap(), 1);

					// Obtenemos el valor minimax del nodo y actualizamos alpha si es mayor.
					int alphaAux = alfaBeta(hijoi, 2, nivelActual+1, nivelesTotales, alpha, beta);
					if (alphaAux > alpha)
					{
						alpha = alphaAux;
						// Si estamos en la raiz, actualizamos la rama de llegada.
						if (nivelActual == 1)
						{
							m_ramaLlegada = i;
						}
					}

					// Comprobamos si es necesario investigar el resto de ramas.
					if (alpha >= beta)
					{
						alpha = beta;
						break;
					}
				}

				// Devolvemos el valor minimax.
				return alpha;
			}
			else
			{
				// Recorremos los hijos y calculamos recursivamente su valor minimax, devolviendo el menor.
				for (int i = 0; i < numHijos; i++)
				{
					// Generamos el tablero para el movimiento "i".
					Tablero hijoi = new Tablero(tablero);
					hijoi.hacerTirada(movimientos.get(i).swap(), 2);

					// Obtenemos el valor minimax del nodo y actualizamos beta si es menor.
					int betaAux = alfaBeta(hijoi, 1, nivelActual+1, nivelesTotales, alpha, beta);
					if (betaAux < beta)
					{
						beta = betaAux;
						// Si estamos en la raiz, actualizamos la rama de llegada.
						if (nivelActual == 1)
						{
							m_ramaLlegada = i;
						}
					}

					// Comprobamos si es necesario investigar el resto de ramas.
					if (alpha >= beta)
					{
						beta = alpha;
						break;
					}
				}

				// Devolvemos el valor minimax.
				return beta;
			}
		}
	}

	/**
	 * Implementación del algoritmo Minimax.
	 * @param tablero Tablero sobre el que se va a calcular el movimiento.
	 * @param jugador Jugador que tiene que realizar el movimiento.
	 * @param nivelActual Nivel actual del algoritmo (1 cuando sea la llamada principal; >1 cuando sea una llamada recursiva).
	 * @param nivelesTotales Número de niveles que explorará el algoritmo.
	 * @return Devuelve el valor minimax del mejor estado posible.
	 */
	public int minimax(Tablero tablero, int jugador, int nivelActual, int nivelesTotales)
	{
		// Obtenemos todos los posibles movimientos que se pueden realizar para el jugador indicado.
		ArrayList<Movimiento> movimientos = tablero.movimientosPosibles(jugador);
		int numHijos = movimientos.size();

		// Caso base.
		if (nivelActual == nivelesTotales || numHijos == 0)
		{
			return tablero.funcionEvaluacion();
		}
		else
		{
			// Comprobamos si es el jugador 1 o 2.
			if (jugador == 1)
			{
				// Generamos y recorremos todos sus hijos en busca del máximo minimax.
				int minimax = Integer.MIN_VALUE;
				for (int i = 0; i < numHijos; i++)
				{
					// Copia el tablero y realiza la tirada.
					Tablero hijoi = new Tablero(tablero);
					hijoi.hacerTirada(movimientos.get(i).swap(), 1);

					// Se calcula el valor minimax del tablero aplicando la recursividad.
					int minimaxAnt = minimax;
					minimax = Math.max(minimax, minimax(hijoi, 2, nivelActual+1, nivelesTotales));

					// Si el valor se ha modificado y estamos en el nivel 1, significa que este cambio es significativo para el nodo raíz; actualizammos la rama de llegada.
					if (minimaxAnt != minimax && nivelActual == 1)
					{
						m_ramaLlegada = i;
					}
				}
				return minimax;
			}
			else
			{
				// Generamos y recorremos todos sus hijos en busca del mínimo minimax.
				int minimax = Integer.MAX_VALUE;
				for (int i = 0; i < numHijos; i++)
				{
					// Copia el tablero y realiza la tirada.
					Tablero hijoi = new Tablero(tablero);
					hijoi.hacerTirada(movimientos.get(i).swap(), 2);

					// Se calcula el valor minimax del tablero aplicando la recursividad.
					int minimaxAnt = minimax;
					minimax = Math.min(minimax, minimax(hijoi, 1, nivelActual+1, nivelesTotales));

					// Si el valor se ha modificado y estamos en el nivel 1, significa que este cambio es significativo para el nodo raíz; actualizammos la rama de llegada.
					if (minimaxAnt != minimax && nivelActual == 1)
					{
						m_ramaLlegada = i;
					}
				}
				return minimax;
			}
		}
	}

	/**
	 * Implementación del algoritmo SSS*.
	 * @param tablero Tablero sobre el que se va a calcular el movimiento.
	 * @param jugador Jugador que tiene que realizar el movimiento.
	 * @param nivelesTotales Número de niveles que explorará el algoritmo.
	 * @return Devuelve el valor minimax del mejor estado posible.
	 */
	public int SSS(Tablero tablero, int jugador, int nivelesTotales)
	{
		// Almacena quién va a mover realmente. Además, si el jugador es el número 2,
		// tendrá que invertirse el valor que devuelve la función de evaluación del tablero
		// ya que ese valor está preparado para una victoria del jugador 1.
		int jugadorInicial = jugador;
		int invertirMinimax = (jugadorInicial == 1) ? 1 : -1;

		// Creamos la lista de nodos e insertamos el nodo raíz.
		ArrayList<NodoSSS> nodosVivos = new ArrayList<NodoSSS>();
		NodoSSS raiz = new NodoSSS(new Tablero(tablero), NodoSSS.EstadoSSS.VIVO, Integer.MAX_VALUE, null, 1, jugador);
		raiz.etiqueta = "0";
		nodosVivos.add(raiz);
		
		// Comenzamos el bucle que no terminará hasta que encontremos el valor minimax.
		while (!nodosVivos.isEmpty())
		{
			// Extraemos el nodo que va a ser procesado en esta iteración.
			NodoSSS cima = nodosVivos.remove(0);
			
			// Si es el nodo raíz y ya está solucionado, devolvemos su valor.
			if (cima == raiz && raiz.estado == NodoSSS.EstadoSSS.SOLUCIONADO)
			{
				return cima.h*invertirMinimax;
			}
			else
			{
				// Comprobamos si el nodo está vivo.
				if (cima.estado == NodoSSS.EstadoSSS.VIVO)
				{
					ArrayList<Movimiento> movimientos = cima.tablero.movimientosPosibles(cima.jugador);
					int jugadorContrario = (cima.jugador == 1) ? 2 : 1;

					// Comprobamos si es un nodo no-terminal (nodo hoja).
					if (cima.nivel < nivelesTotales && movimientos.size() > 0)
					{
						// Comprobamos si es nodo MAX.
						if (cima.jugador == jugadorInicial)
						{
							numCasos[0]++;
							long tiempoActual = System.currentTimeMillis();
							
							// Insertamos todos los hijos de derecha a izquierda en la cabeza de la lista.
							for (int i = movimientos.size()-1; i >= 0; i--)
							{
								Tablero hijo = new Tablero(cima.tablero);
								hijo.hacerTirada(movimientos.get(i).swap(), cima.jugador);
								NodoSSS nodo = new NodoSSS(hijo, NodoSSS.EstadoSSS.VIVO, cima.h, cima, cima.nivel+1, jugadorContrario);
								nodo.numHijo = i;
								nodo.numHermanos = movimientos.size();
								nodo.etiqueta = cima.etiqueta + "." + i;
								nodosVivos.add(0, nodo);
							}

							tiempoCasos[0] += System.currentTimeMillis() - tiempoActual;
						}
						else /* MIN */
						{
							numCasos[1]++;
							long tiempoActual = System.currentTimeMillis();
							
							// Insertamos el hijo izquierdo en la cabeza de la lista.
							Tablero hijo = new Tablero(cima.tablero);
							hijo.hacerTirada(movimientos.get(0).swap(), cima.jugador);
							NodoSSS nodo = new NodoSSS(hijo, NodoSSS.EstadoSSS.VIVO, cima.h, cima, cima.nivel+1, jugadorContrario);
							nodo.numHijo = 0;
							nodo.numHermanos = movimientos.size();
							nodo.etiqueta = cima.etiqueta + ".0";
							nodosVivos.add(0, nodo);

							tiempoCasos[1] += System.currentTimeMillis() - tiempoActual;
						}
					}
					else /* TERMINAL */
					{
						numCasos[2]++;
						long tiempoActual = System.currentTimeMillis();

						// Si es terminal, cambiamos su estado a solucionado y actualizamos su valor h.
						cima.estado = NodoSSS.EstadoSSS.SOLUCIONADO;
						cima.h = Math.min(cima.h, cima.tablero.funcionEvaluacion()*invertirMinimax);

						// Reinsertamos el nodo según su valor h.
						boolean insertado = false;
						ListIterator<NodoSSS> iterador = nodosVivos.listIterator();
						while (iterador.hasNext())
						{
							if (iterador.next().h <= cima.h)
							{
								iterador.previous();
								iterador.add(cima);
								insertado = true;
								break;
							}
						}
						if (!insertado)
						{
							iterador.add(cima);
						}

						tiempoCasos[2] += System.currentTimeMillis() - tiempoActual;
					}
				}
				else /* SOLUCIONADO */
				{
					// Comprobamos si es nodo MAX.
					if (cima.jugador == jugadorInicial)
					{
						// Comprobamos si no es el último hijo de su padre (extremo de la derecha).
						if (cima.numHijo < cima.numHermanos-1)
						{
							numCasos[3]++;
							long tiempoActual = System.currentTimeMillis();
							
							// Insertamos el hermano de la derecha.
							NodoSSS hermano = new NodoSSS(new Tablero(cima.padre.tablero), NodoSSS.EstadoSSS.VIVO, cima.h, cima.padre, cima.nivel, cima.jugador);
							hermano.numHijo = cima.numHijo + 1;
							hermano.numHermanos = cima.numHermanos;
							hermano.etiqueta = hermano.padre.etiqueta + "." + hermano.numHijo;
							nodosVivos.add(0, hermano);

							// Como hemos copiado el tablero del padre, falta hacer la tirada de este hijo/hermano en el tablero padre.
							ArrayList<Movimiento> movimientos = hermano.padre.tablero.movimientosPosibles(hermano.padre.jugador);
							hermano.tablero.hacerTirada(movimientos.get(hermano.numHijo).swap(), hermano.padre.jugador);

							tiempoCasos[3] += System.currentTimeMillis() - tiempoActual;
						}
						else /* ÚLTIMO HERMANO */
						{
							numCasos[4]++;
							long tiempoActual = System.currentTimeMillis();
							
							// Actualizamos el nodo padre y lo reinsertamos.
							NodoSSS padre = cima.padre;
							padre.h = cima.h;
							padre.estado = NodoSSS.EstadoSSS.SOLUCIONADO;
							nodosVivos.add(0, padre);

							// Si el nodo padre del nodo que estaba en la cima es el nodo raíz, actualizamos la rama de llegada.
							if (padre == raiz)
								m_ramaLlegada = cima.numHijo;

							tiempoCasos[4] += System.currentTimeMillis() - tiempoActual;
						}
					}
					else /* MIN */
					{
						numCasos[5]++;
						long tiempoActual = System.currentTimeMillis();
						
						// Obtenemos el nodo padre y la cantidad de hijos que podría tener actualmente en la lista.
						NodoSSS padre = cima.padre;

						// Eliminamos los sucesores de este padre.
						ListIterator<NodoSSS> iterador = nodosVivos.listIterator();
						while (iterador.hasNext())
						{
							if (iterador.next().etiqueta.startsWith(padre.etiqueta))
							{
								iterador.remove();
							}
						}
						
						// Actualizamos el nodo padre y lo reinsertamos.
						padre.h = cima.h;
						padre.estado = NodoSSS.EstadoSSS.SOLUCIONADO;
						nodosVivos.add(0, padre);

						// Si el nodo padre del nodo que estaba en la cima es el nodo raíz, actualizamos la rama de llegada.
						if (padre == raiz)
							m_ramaLlegada = cima.numHijo;

						tiempoCasos[5] += System.currentTimeMillis() - tiempoActual;
					}
				}
			}			
		}

		return 0;
	}

	public static int[] numCasos = new int[6];
	public static long[] tiempoCasos = new long[6];
}

/**
 * Nodo que utiliza la implementación del algoritmom SSS*.
 * Implementa "Comparable" para permitir ordenar los nodos en una cola de prioridad PriorityQueue.
 */
class NodoSSS implements Comparable
{
	/**
	 * Tablero asignado al nodo.
	 */
	public Tablero tablero;

	/**
	 * Enumerado que almacena los posibles estados de un NodoSSS.
	 * Puede ser VIVO o SOLUCIONADO.
	 * Un nodo puede estar SOLUCIONADO si es un nodo hoja o, aunque no lo sea,
	 * ya se ha explorado su descendencia.
	 */
	public enum EstadoSSS {VIVO, SOLUCIONADO};

	/**
	 * Estado del nodo: VIVO o SOLUCIONADO.
	 */
	public EstadoSSS estado;

	/**
	 * Valor de heurística del nodo.
	 */
	public int h;

	/**
	 * Nodo padre del nodo.
	 * Si es el nodo raíz, el valor tiene que ser nulo.
	 */
	public NodoSSS padre;

	/**
	 * Indica el número de hijo que es (desde 0 hasta la cantidad de hijos que tiene su padre).
	 */
	public int numHijo;

	/**
	 * Indica la cantidad de hijos que tiene su padre (incluido él).
	 */
	public int numHermanos;

	/**
	 * Indica a qué altura está el nodo.
	 */
	public int nivel;

	/**
	 * Indica a qué jugador corresponde el nodo, es decir, qué jugador tiene que tirar
	 * en el siguiente movimiento del tablero.
	 * Según el jugador que comenzó la partida, se considerará nodo MAX o
	 * nodo MIN.
	 */
	public int jugador;

	/**
	 * Etiqueta del nodo que utiliza la notación decimal de Dewey.
	 * Por ejemplo: 0 para el padre, 0.0 para el hijo izquierdo y 0.1 para el derecho, ...
	 */
	public String etiqueta;

	/**
	 * Constructor por defecto.
	 * @param tablero el tablero asignado al nodo
	 * @param estado el estado del nodo (VIVO o MUERTO)
	 * @param h valor de heurística
	 * @param padre el nodo padre del nodo (null si es el nodo raíz)
	 * @param nivel la altura a la que se encuentra el nodo en el árbol (desde 1 hasta el nivel máximo)
	 * @param jugador jugador al que corresponde el nodo
	 */
	public NodoSSS(Tablero tablero, EstadoSSS estado, int h, NodoSSS padre, int nivel, int jugador)
	{
		this.tablero = tablero;
		this.estado = estado;
		this.h = h;
		this.padre = padre;
		this.numHijo = -1;
		this.numHermanos = 0;
		this.nivel = nivel;
		this.jugador = jugador;
		this.etiqueta = "";
	}

	/**
	 * Implementación del método de comparación de la interfaz Comparable.
	 * Compara dos nodos según su estado, su valor de heurística y el número de hijo según sus hermanos.
	 * El método está preparado para que en una PriorityQueue<NodoSSS> los nodos queden ordenados de la siguiente forma:
	 *  - primero los nodos vivos y después los solucionados
	 *  - si dos nodos tienen el mismo estado, primero aparecen los de mayor h
	 *  - si dos nodos tienen el mismo estado y mismo valor h, primero los que sean un número de hijo menor
	 * @param objeto el nodo (NodoSSS) con el que se va a comparar
	 * @return 0 si son iguales, 1 si el nodo que invocó el método es mayor, -1 si es menor
	 */
	public int compareTo(Object objeto)
	{
		NodoSSS nodo = (NodoSSS) objeto;
		if (estado == nodo.estado)
		{
			if (h > nodo.h)
			{
				return -1;
			}
			else
			{
				if (h < nodo.h)
				{
					return 1;
				}
				else
				{
					if (numHijo < nodo.numHijo)
					{
						return -1;
					}
					else
					{
						if (numHijo > nodo.numHijo)
						{
							return 1;
						}
						else
						{
							return 0;
						}
					}
				}
			}
		}
		else
		{
			if (estado == EstadoSSS.VIVO)
				return -1;
			else
				return 1;
		}
	}

	/**
	 * Sobreescribe el método que convierte el nodo a cadena de caracteres.
	 * @return una cadena con la etiqueta, el estado del nodo y el valor de heurística del nodo
	 */
	@Override
	public String toString()
	{
		return "(" + etiqueta + ", " + ((estado == EstadoSSS.VIVO) ? "V" : "S") + ", " + h + ")";
	}
}
