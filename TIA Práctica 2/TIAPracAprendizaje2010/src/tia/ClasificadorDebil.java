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
		for (Cara i : conjuntoEntrenamiento)
		{
			if (hiperplano.h(i.getData()) != i.getTipo())
				error += i.getProbabilidad();
		}
		valorConfianza = 0.5 * Math.log10((1 - error) / error);
	}
	
	public double h(int[] p)
	{
		return hiperplano.h(p);
	}
}
