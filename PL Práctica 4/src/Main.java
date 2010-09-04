import org.antlr.runtime.*;

public class Main
{
	public static String nombreFichero;
	public static void main(String[] args) throws Exception
	{
		nombreFichero = args[0];
		CharStream input = new ANTLRFileStream(args[0]);
		plp4Lexer lex = new plp4Lexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lex);
		plp4Parser parser = new plp4Parser(tokens);
		parser.s();
	}
}
