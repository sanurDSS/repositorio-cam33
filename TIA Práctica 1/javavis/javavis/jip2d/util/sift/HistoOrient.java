package javavis.jip2d.util.sift;

import java.util.*;

public class HistoOrient {
	
	private float[] hist;
	
	/**
	 * @param bins numero de intervalos en el histograma
	 */
	public HistoOrient(int bins) {
		hist = new float[bins];
	}
	
	/**
	 * Incrementa un intervalo del histograma 
	 * @param orient ángulo en radianes para el que habrá que buscar el intervalo 
	 * @param cant incremento
	 */
	public void addBin(double orient, double cant) {
		int numBin;
		
		numBin = (int)(hist.length * orient / (2.0 * Math.PI));
		//por si el ángulo es justo 2PI, que no se salga del array
		if (numBin==hist.length)
			numBin--;
		hist[numBin]+=cant;
	}
	
	public float getBin(int i) {
		return hist[i];
	}
	
	/**
	 * Obtiene el/los ángulos donde el histograma tiene su/s máximos
	 * @param porc porcentaje que indica la diferencia que un valor
	 * puede tener con el máximo absoluto para ser considerado también máximo
	 * @return
	 */
	public ArrayList<Float> getMax(float porc) {
		ArrayList<Float> max;
		float valMax;
		float angMax;
		int iAnt, iSig;
		int tamHist;
		float valor;
		
		tamHist = hist.length;
		max = new ArrayList<Float>();		
		valMax = Float.MIN_VALUE;
		angMax = 0.0f;
		//buscar máximo global
		for(int i=0; i<tamHist; i++) {
			if (hist[i]>valMax) {
				angMax = (float)(i * Math.PI * 2.0 / tamHist);
				valMax = hist[i];
			}
		}
		max.add(angMax);
		//buscar otros máximos locales suficientemente cercanos al máximo
		for(int i=0; i<tamHist; i++) {
			//buscar el indice de los vecinos, para luego ver si es max. local
			if (i==0)
				iAnt = tamHist-1;
			else
				iAnt = i-1;
			if (i==tamHist-1)
				iSig = 0;
			else
				iSig = i+1;
			valor = hist[i];
			//se quita el '=' en la 2ª cond para añadir solo 1 max local si hay una
			//zona "plana" de varios max. iguales
			if ((valor>=hist[iSig])&&(valor>hist[iAnt])&&(valor>valMax*porc/100.0f)) {
				max.add((float)(i * Math.PI * 2.0 / hist.length));
			}
		}
		return max;
	}

	public void smooth() {
		hist[0]=(hist[hist.length-1]+hist[0]+hist[1])/3;
		for (int i=1; i<hist.length; i++) 
			hist[i]=(hist[i-1]+hist[i]+hist[(i+1)%hist.length])/3;
	}

}