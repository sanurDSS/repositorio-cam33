import org.antlr.runtime.*;

public class Main
{
	public static String nombreFichero;
	public static void main(String[] args) throws Exception
	{
		nombreFichero = args[0];
		CharStream input = new ANTLRFileStream(args[0]);
		plp3Lexer lex = new plp3Lexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lex);
		plp3Parser parser = new plp3Parser(tokens);
		parser.s();
	}
}
