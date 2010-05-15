package tia;

import java.util.ArrayList;

public class ClasificadorDebil
{
	private Hiperplano hiperplano;
	private double error;
	private double valorConfianza;
	
	public ClasificadorDebil()
	{
		hiperplano = new Hiperplano();
		error = Double.MAX_VALUE;
		valorConfianza = Double.MIN_VALUE;
	}
	
	public double getError()
	{
		return error;
	}
	
	public double getValorConfianza()
	{
		return valorConfianza;
	}
	
	public void entrenaClasificador(ArrayList<Cara> conjuntoEntrenamiento)
	{
		error = 0;
		for (Cara i : conjuntoEntrenamiento)
		{
			if (h(i.getData()) != i.getTipo())
				error += i.getProbabilidad();
		}
		valorConfianza = 0.5 * Math.log10((1 - error) / error);
	}
	
	public double h(int[] p)
	{
		if (hiperplano.h(p) < 0)
			return -1;
		else
			return 1;
	}
}
