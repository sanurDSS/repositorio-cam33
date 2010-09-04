public class Main
{
	public static void main(String[] args)
	{
		try
		{
			String nomFichero = args[0];
			new AnalizadorSintacticoSemanticoTraductor(nomFichero);
		}
		catch (Exception e)
		{
			Error.Error0(e.getMessage());
		}
	}
}