package javavis.jip2d.util;

import java.util.ArrayList;

/**
*It implements a class with auxiliar methods to work with
*circumferences in the FHoughCirc function and in the cuentaMonedas application.<BR>
 */

public class Circumference extends Object {
	/*** X coordinate of the center of circumference */
	public int centroX;
	/*** Y coordinate of the center of circumference */
	public int centroY;

	/**
	 * Radius of the circumference (The value has to be integer type because we are worked with pixels)
	 */
	public int radio;

	/**
	*It constructs a circumference without its values are started.
	*/
	public Circumference() {
	}

	/**
	*It constructs a circumference with its member variables start with the received values
	*as parameters.
	*/
	public Circumference(int cx, int cy, int r) {
		centroX = cx;
		centroY = cy;
		radio = r;
	}

	/**
	  *It returns the radius of circumference, it is calculated from its center 
	  *and a point which belong to the circumference.<BR>	  
	*/
	public static int calculatesRadius(int cent_X, int cent_Y, int coord_x, int coord_y) {
		int c, d;
		c = coord_x - cent_X;
		d = coord_y - cent_Y;
		return (int) Math.sqrt(c * c + d * d);
	}

	/**
	  *It returns all points that form the perimeter of circumference, from the centre and the
	  *radius of the circumference.<BR>
	*/
	public static ArrayList<Integer> getPoints(Circumference circ) {
		int min_x, max_x, a;
		int temp2 = 0, temp = 0;

		//Se calcula el value maximo y minimo que podra tener 'x' en la ecuacion de la circunferencia.
		min_x = circ.centroX - circ.radio;
		max_x = circ.centroX + circ.radio;

		ArrayList<Integer> puntos = new ArrayList<Integer>();

		/* Con este primer bucle se recorre la parte superior de la circunferencia sustituyendo
		valores en 'x' y obteniendo su correspondiente en 'y'.*/
		for (int X = min_x; X <= max_x; X++) {
			a = X - circ.centroX;
			temp2 = (int) Math.sqrt(circ.radio * circ.radio - a * a);
			temp = temp2 + circ.centroY;

			puntos.add(X);
			puntos.add(temp);
		}

		/* Al igual que el bucle anterior, sustituimos valores de 'x' pero esta vez para obtener la parte
		inferior de la circunferencia, ya que la raiz cuadrada devuelve el resultado de signo
		positivo y negativo.*/
		for (int X = max_x - 1; X >= min_x + 1; X--) {
			a = X - circ.centroX;
			temp2 = (int) Math.sqrt(circ.radio * circ.radio - a * a);
			//En este bucle se usa el resultado negativo de la raiz cuadrada.
			temp = (-temp2 + circ.centroY);

			puntos.add(X);
			puntos.add(temp);
		}
		return puntos;
	}
}
