import org.antlr.runtime.*;

public class Main
{
	public static String nombreFichero;
	public static void main(String[] args) throws Exception
	{
		nombreFichero = args[0];
		CharStream input = new ANTLRFileStream(args[0]);
		plp5Lexer lex = new plp5Lexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lex);
		plp5Parser parser = new plp5Parser(tokens);
		parser.prog();
	}
}
