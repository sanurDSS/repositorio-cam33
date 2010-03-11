package javavis.jip2d.util;

/* Class Mascara to keep a circular mask*/

public class Mascara {
	public int radio;
	public int maxArea;
	public boolean mascara[][];

	public Mascara(int r) {
		radio = r;
		mascara = new boolean[2 * r][2 * r];
		maxArea = 0;

		for (int i = 0; i < 2 * r; i++) {
			for (int j = 0; j < 2 * r; j++) {
				if ((i - r) * (i - r) + (j - r) * (j - r) < r * r) {
					mascara[i][j] = true;
					maxArea++;
				}
				else mascara[i][j] = false;
			}
		}
	}
}
