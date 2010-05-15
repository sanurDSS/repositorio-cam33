package tia;

import java.util.ArrayList;

public class ClasificadorFuerte
{
	private ArrayList<ClasificadorDebil> clasificadores;
	
	public ClasificadorFuerte()
	{
		clasificadores = new ArrayList<ClasificadorDebil>();
	}
	
	public void addClasificadorDebil(ClasificadorDebil clasificadorDebil)
	{
		clasificadores.add(clasificadorDebil);
	}
	
	public double H(int[] p)
	{
		double resultado = 0;
		for (ClasificadorDebil i : clasificadores)
		{
			resultado += i.getValorConfianza() * i.h(p);
		}
		if (resultado < 0)
			return -1;
		else
			return 1;
	}
}
