grammar plp5;

@header
{
	import java.lang.String;
}

@rulecatch
{
	catch (RecognitionException re)
	{
		reportError(re);
		System.exit(-1);
	}
}

@members
{
	public void emitErrorMessage(String msg)
	{
		System.err.println(msg);
		System.exit(-1);
	}

	// Indica si estamos dentro de la función principal "main".
	boolean enMain = false;

	// Contabiliza las etiquetas de salto del programa.
	int etiquetas = 0;
}

@lexer::members
{
	public void emitErrorMessage(String msg)
	{
		System.err.println(msg);
		System.exit(-1);
	}
}




/******************************************************************************
Analizador sintáctico, semántico y traductor 
******************************************************************************/

prog:
{
	// Creamos la tabla principal donde se insertarán las funciones.
	TablaSimbolos tablaSimbolos = new TablaSimbolos(null);
	tablaSimbolos.setConLocales(true);

	// Generamos la cabecera del fichero de salida.
	String salida = "";
	salida += ".file \"" + Main.nombreFichero + "\"";
	salida += "\n.intel_syntax";
	salida += "\npfmt: .string \"\%d\\n\"";
	salida += "\nsfmt: .string \"\%d\"";
	salida += "\n";
}
(func[tablaSimbolos]
{
	salida += $func.salida + "\n";
}
)* MAIN PARI PARD
{
	// Indicamos que hemos entrado en la función principal.
	enMain = true;

	// Se crea una nueva tabla para la función principal "main" (contendrá variables locales).
	tablaSimbolos = new TablaSimbolos(tablaSimbolos);
	tablaSimbolos.setConLocales(true);

	// Se añade la cabecera de la función principal con su registro de activación.
	salida += "\n.text";
	salida += "\n.globl main";
	salida += "\n.type main, @function";
	salida += "\nmain:";
	salida += "\n\tpush \%ebp";
	salida += "\n\tmov  \%ebp, \%esp";
}
bloque[tablaSimbolos, false]
{
	// Se añade a la salida la reserva para las variables locales de la función.
	salida += "\n\tsub  \%esp, " + (4 * tablaSimbolos.getCantidadLocales());

	// Se concatena la salida producida dentro del bloque.
	salida += $bloque.salida.replaceAll("\n", "\n\t");

	// Se añade el retorno de la función (valor 0) y se imprime la cadena.
	salida += "\n\tmov  \%eax, 0";
	salida += "\n\tmov  \%esp, \%ebp";
	salida += "\n\tpop  \%ebp";
	salida += "\n\tret";
	System.out.println(salida);
};

//##############################################################################

func [TablaSimbolos tablaSimbolos] returns [String salida] @init { $salida = ""; }:
INT ID
{
	// Comprobamos que el símbolo no existe en este ámbito.
	if (tablaSimbolos.get($ID.text, false) != null)
		Error.Error1($ID.line, $ID.pos + 1, $ID.text);

	// Creamos un nuevo ámbito para la función donde se declararán los parámetros y variables locales.
	tablaSimbolos = new TablaSimbolos(tablaSimbolos);
	tablaSimbolos.setConLocales(true);

	// Concatenamos a la salida la cabecera de la función y su registro de activación.
	$salida += "\n.text";
	$salida += "\n.globl " + $ID.text;
	$salida += "\n.type " + $ID.text + ", @function";
	$salida += "\n" + $ID.text + ":";
	$salida += "\n\tpush \%ebp";
	$salida += "\n\tmov  \%ebp, \%esp";
}
PARI args[tablaSimbolos]
{
	// Una vez contabilizados el número de parámetros, añadimos el símbolo de la función a la tabla de símbolos superior (padre).
	tablaSimbolos.getPadre().add(new Simbolo(Simbolo.FUNCION, $ID.text, tablaSimbolos.getPadre().getPosicionLocal(), $args.cantidad));
}
PARD bloque[tablaSimbolos, false]
{
	// Se añade a la salida la reserva para las variables locales de la función.
	$salida += "\n\tsub  \%esp, " + (4 * tablaSimbolos.getCantidadLocales());

	// Se concatena la salida producida dentro del bloque.
	$salida += $bloque.salida.replaceAll("\n", "\n\t");

	// Se añade el retorno por defecto de la función (valor 0).
	$salida += "\n\tmov  \%eax, 0";
	$salida += "\n\tmov  \%esp, \%ebp";
	$salida += "\n\tpop  \%ebp";
	$salida += "\n\tret";
}
;

//##############################################################################

bloque [TablaSimbolos tablaSimbolos, boolean crearAmbito] returns [String salida] @init { $salida = ""; }:
{
	// Creamos un nuevo ámbito si así se ha indicado.
	if (crearAmbito)
		tablaSimbolos = new TablaSimbolos(tablaSimbolos);
}
LLAVEI (decl[tablaSimbolos] { $bloque.salida += $decl.salida; } | instr[tablaSimbolos] { $bloque.salida += $instr.salida; })+ LLAVED;

//##############################################################################

args [TablaSimbolos tablaSimbolos] returns [int cantidad] @init { $cantidad = 0; }:
(INT ID1=ID
{
	// Comprobamos que no existe el identificador en la tabla de símbolos.
	if (tablaSimbolos.get($ID1.text, false) != null)
		Error.Error1($ID1.line, $ID1.pos + 1, $ID1.text);

	// Insertamos el símbolo del argumento e incrementamos el contador.
	tablaSimbolos.add(new Simbolo(Simbolo.ARGUMENTO, $ID1.text, 1 + $cantidad++, -1));
}
(COMA INT ID2=ID
{
	// Comprobamos que no existe el identificador en la tabla de símbolos.
	if (tablaSimbolos.get($ID2.text, false) != null)
		Error.Error1($ID2.line, $ID2.pos + 1, $ID2.text);

	// Insertamos el símbolo del argumento e incrementamos el contador.
	tablaSimbolos.add(new Simbolo(Simbolo.ARGUMENTO, $ID2.text, 1 + $cantidad++, -1));
}
)*)?;

//##############################################################################

decl [TablaSimbolos tablaSimbolos] returns [String salida] @init { $salida = ""; }:
INT ID1=ID
{
	// Comprobamos que no existe el identificador en la tabla de símbolos.
	if (tablaSimbolos.get($ID1.text, false) != null)
		Error.Error1($ID1.line, $ID1.pos + 1, $ID1.text);

	// Insertamos el símbolo de la variable local.
	tablaSimbolos.add(new Simbolo(Simbolo.LOCAL, $ID1.text, tablaSimbolos.getPosicionLocal(), -1));
}
(ASIG expr1=expr[tablaSimbolos]
{
	// Se añade el código para inicializar la variable declarada.
	$salida += $expr1.salida;
	$salida += "\npop  [\%ebp " + tablaSimbolos.get($ID1.text, true).getDesplazamientoEbp() + "]";
}
)? (COMA ID2=ID
{
	// Comprobamos que no existe el identificador en la tabla de símbolos.
	if (tablaSimbolos.get($ID2.text, false) != null)
		Error.Error1($ID2.line, $ID2.pos + 1, $ID2.text);

	// Insertamos el símbolo de la variable local.
	tablaSimbolos.add(new Simbolo(Simbolo.LOCAL, $ID2.text, tablaSimbolos.getPosicionLocal(), -1));
}
(ASIG expr2=expr[tablaSimbolos]
{
	// Se añade el código para inicializar la variable declarada.
	$salida += $expr2.salida;
	$salida += "\npop  [\%ebp " + tablaSimbolos.get($ID2.text, true).getDesplazamientoEbp() + "]";
}
)?)* PYC;

//##############################################################################

instr [TablaSimbolos tablaSimbolos] returns [String salida] @init { $salida = ""; }:
(bloque[tablaSimbolos, true] { $salida += $bloque.salida; })
|
(IF PARI expr[tablaSimbolos] PARD instr1=instr[tablaSimbolos]
{
	// Se reservan las etiquetas necesarias para la condición de salto.
	int etiqueta1 = etiquetas++;
	int etiqueta2 = etiquetas++;

	// Se añade el código de la condición y de la instrucción.
	$salida += $expr.salida;
	$salida += "\npop  \%eax";
	$salida += "\ncmp  \%eax, 0";
	$salida += "\nje   L" + etiqueta1;
	$salida += $instr1.salida;
	$salida += "\njmp  L" + etiqueta2;
	$salida += "\nL" + etiqueta1 + ":";
}
(ELSE instr2=instr[tablaSimbolos]
{
	// Si existe el "else", se añade el código.
	$salida += $instr2.salida;
}
)?
{
	// Etiqueta final para la salida del "if/else".
	$salida += "\nL" + etiqueta2 + ":";
}
)
|
(WHILE PARI expr[tablaSimbolos] PARD instr3=instr[tablaSimbolos]
{
	// Se reservan las etiquetas necesarias para la condición de salto.
	int etiqueta1 = etiquetas++;
	int etiqueta2 = etiquetas++;

	// Se añade el código de condición y de la instrucción del bucle "while".
	$salida += "\nL" + etiqueta1 + ":";
	$salida += $expr.salida;
	$salida += "\npop  \%eax";
	$salida += "\ncmp  \%eax, 0";
	$salida += "\nje   L" + etiqueta2;
	$salida += $instr3.salida;
	$salida += "\njmp  L" + etiqueta1;
	$salida += "\nL" + etiqueta2 + ":";
}
)
|
(ID
{
	// Comprobamos que el identificador ha sido declarado.
	if (tablaSimbolos.get($ID.text, true) == null)
		Error.Error2($ID.line, $ID.pos + 1, $ID.text);

	// Comprobamos que no es una función.
	if (tablaSimbolos.get($ID.text, true).getTipo() == Simbolo.FUNCION)
		Error.Error3($ID.line, $ID.pos + 1, $ID.text);
}
ASIG expr[tablaSimbolos]
{
	// Construimos la salida de la asignación.
	$salida += $expr.salida;
	$salida += "\npop  [\%ebp " + tablaSimbolos.get($ID.text, true).getDesplazamientoEbp() + "]";
}
PYC)
|
(PRINT PARI expr[tablaSimbolos]
{
	// Generamos la salida para imprimir por pantalla.
	$salida += $expr.salida;
	$salida += "\npush OFFSET FLAT:pfmt";
	$salida += "\ncall printf";
	$salida += "\nadd  \%esp, 8";
}
PARD PYC)
|
(READ PARI ID
{
	// Comprobamos que el identificador ha sido declarado.
	if (tablaSimbolos.get($ID.text, true) == null)
		Error.Error2($ID.line, $ID.pos + 1, $ID.text);

	// Comprobamos que no es una función.
	if (tablaSimbolos.get($ID.text, true).getTipo() == Simbolo.FUNCION)
		Error.Error3($ID.line, $ID.pos + 1, $ID.text);

	// Se genera la llamada de lectura.
	$salida += "\nlea  \%eax, DWORD PTR [\%ebp " + tablaSimbolos.get($ID.text, true).getDesplazamientoEbp() + "]";
	$salida += "\npush \%eax";
	$salida += "\npush OFFSET FLAT:sfmt";
	$salida += "\ncall scanf";
	$salida += "\nadd  \%esp, 8";
}
PARD PYC)
|
(RETURN
{
	// Comprobamos que la llamada no se realice desde la función principal "main".
	if (enMain)
		Error.Error6($RETURN.line, $RETURN.pos + 1);
}
expr[tablaSimbolos]
{
	// Se añade el código de retorno con la secuencia de retorno habitual.
	$salida += $expr.salida;
	$salida += "\npop  \%eax";
	$salida += "\nmov  \%esp, \%ebp";
	$salida += "\npop  \%ebp";
	$salida += "\nret";
}
PYC)
|
llamada[tablaSimbolos]
{
	// Código de la llamada (se desapila el valor que deja la llamada).
	$salida += $llamada.salida;
	$salida += "\nadd  \%esp, 4";
}
PYC;

//##############################################################################

expr [TablaSimbolos tablaSimbolos] returns [String salida] @init { $salida = ""; }:
econj1=econj[tablaSimbolos]
{
	$salida += $econj1.salida;
}
(OR econj2=econj[tablaSimbolos]
{
	int etiqueta1 = etiquetas++;
	int etiqueta2 = etiquetas++;

	$salida += $econj2.salida;
	$salida += "\npop  \%eax";
	$salida += "\npop  \%ebx";
	$salida += "\ncmp  \%eax, 0";
	$salida += "\njne   L" + etiqueta1;
	$salida += "\ncmp  \%ebx, 0";
	$salida += "\njne   L" + etiqueta1;
	$salida += "\npush DWORD PTR 0";
	$salida += "\njmp  L" + etiqueta2;
	$salida += "\nL" + etiqueta1 + ":";
	$salida += "\npush DWORD PTR 1";
	$salida += "\nL" + etiqueta2 + ":";
}
)*;

//##############################################################################

econj [TablaSimbolos tablaSimbolos] returns [String salida] @init { $salida = ""; }:
ecomp1=ecomp[tablaSimbolos]
{
	$salida += $ecomp1.salida;
}
(AND ecomp2=ecomp[tablaSimbolos]
{
	int etiqueta1 = etiquetas++;
	int etiqueta2 = etiquetas++;

	$salida += $ecomp2.salida;
	$salida += "\npop  \%eax";
	$salida += "\npop  \%ebx";
	$salida += "\ncmp  \%eax, 0";
	$salida += "\nje   L" + etiqueta1;
	$salida += "\ncmp  \%ebx, 0";
	$salida += "\nje   L" + etiqueta1;
	$salida += "\npush DWORD PTR 1";
	$salida += "\njmp  L" + etiqueta2;
	$salida += "\nL" + etiqueta1 + ":";
	$salida += "\npush DWORD PTR 0";
	$salida += "\nL" + etiqueta2 + ":";
}
)*;

//##############################################################################

ecomp [TablaSimbolos tablaSimbolos] returns [String salida] @init { $salida = ""; }:
esimple1=esimple[tablaSimbolos]
{
	$salida += $esimple1.salida;
}
(RELOP esimple2=esimple[tablaSimbolos]
{
	int etiqueta1 = etiquetas++;
	int etiqueta2 = etiquetas++;

	$salida += $esimple2.salida;
	$salida += "\npop  \%ebx";
	$salida += "\npop  \%eax";
	$salida += "\ncmp  \%eax, \%ebx";
	if ($RELOP.text.equals("=="))
		$salida += "\nje   L" + etiqueta1;
	else if ($RELOP.text.equals("!="))
		$salida += "\njne  L" + etiqueta1;
	else if ($RELOP.text.equals("<"))
		$salida += "\njl   L" + etiqueta1;
	else if ($RELOP.text.equals("<="))
		$salida += "\njle  L" + etiqueta1;
	else if ($RELOP.text.equals(">"))
		$salida += "\njg   L" + etiqueta1;
	else if ($RELOP.text.equals(">="))
		$salida += "\njge  L" + etiqueta1;
	$salida += "\npush DWORD PTR 0";
	$salida += "\njmp  L" + etiqueta2;
	$salida += "\nL" + etiqueta1 + ":";
	$salida += "\npush DWORD PTR 1";
	$salida += "\nL" + etiqueta2 + ":";
}
)*;

//##############################################################################

esimple [TablaSimbolos tablaSimbolos] returns [String salida] @init { $salida = ""; }:
(ADDOP1=ADDOP)? term1=term[tablaSimbolos]
{
	$salida += $term1.salida;
	if ($ADDOP1 != null && $ADDOP1.text.equals("-"))
	{
		$salida += "\npop  \%eax";
		$salida += "\nneg  \%eax";
		$salida += "\npush \%eax";
	}
}
(ADDOP2=ADDOP term2=term[tablaSimbolos]
{
	$salida += $term2.salida;
	$salida += "\npop  \%ebx";
	$salida += "\npop  \%eax";
	if ($ADDOP2.text.equals("+"))
		$salida += "\nadd  \%eax, \%ebx";
	else
		$salida += "\nsub  \%eax, \%ebx";
	$salida += "\npush \%eax";
}
)*;

//##############################################################################

term [TablaSimbolos tablaSimbolos] returns [String salida] @init { $salida = ""; }:
factor1=factor[tablaSimbolos]
{
	$salida += $factor1.salida;
}
(MULOP factor2=factor[tablaSimbolos]
{
	$salida += $factor2.salida;
	$salida += "\npop  \%ebx";
	$salida += "\npop  \%eax";
	if ($MULOP.text.equals("*"))
		$salida += "\nimul \%eax, \%ebx";
	else
	{
		$salida += "\nmov  \%edx, \%eax";
		$salida += "\nsar  \%edx, 31";
		$salida += "\nidiv \%ebx";
	}
	$salida += "\npush \%eax";
}
)*;

//##############################################################################

factor [TablaSimbolos tablaSimbolos] returns [String salida] @init { $salida = ""; }:
(ID
{
	// Comprobamos que el identificador ha sido declarado.
	if (tablaSimbolos.get($ID.text, true) == null)
		Error.Error2($ID.line, $ID.pos + 1, $ID.text);

	// Comprobamos que no es una función.
	if (tablaSimbolos.get($ID.text, true).getTipo() == Simbolo.FUNCION)
		Error.Error3($ID.line, $ID.pos + 1, $ID.text);

	// Construimos la salida de la referencia.
	$salida += "\npush DWORD PTR [\%ebp " + tablaSimbolos.get($ID.text, true).getDesplazamientoEbp() + "]";
}
)
|
(llamada[tablaSimbolos]
{
	$salida += $llamada.salida;
}
)
|
(NOT factor1=factor[tablaSimbolos]
{
	int etiqueta1 = etiquetas++;
	int etiqueta2 = etiquetas++;

	$salida += $factor1.salida;
	$salida += "\npop  \%eax";
	$salida += "\ncmp  \%eax, 0";
	$salida += "\nje   L" + etiqueta1;
	$salida += "\npush DWORD PTR 0";
	$salida += "\njmp  L" + etiqueta2;
	$salida += "\nL" + etiqueta1 + ":";
	$salida += "\npush DWORD PTR 1";
	$salida += "\nL" + etiqueta2 + ":";
}
)
|
(ENTERO
{
	$salida += "\npush DWORD PTR " + $ENTERO.text;
}
)
|
(PARI expr[tablaSimbolos] { $salida += $expr.salida; } PARD);
 
//##############################################################################

par [TablaSimbolos tablaSimbolos, int cantidad, int line, int pos, String text] returns [String salida] @init { $salida = ""; }:
{
	int contador = 0;
}
(expr1=expr[tablaSimbolos]
{
	contador++;
	$salida += $expr1.salida;
}
(COMA
{
	// Comprobamos que la cantidad de parámetros no se exceda.
	if (++contador > cantidad)
		Error.Error4(line, pos + 1, text);
}
expr2=expr[tablaSimbolos]
{
	$salida = $expr2.salida + $salida;
}
)*)?
{
	// Comprobamos que la cantidad de parámetros se ha alcanzado.
	if (contador != cantidad)
		Error.Error4(line, pos + 1, text);
};

//##############################################################################

llamada [TablaSimbolos tablaSimbolos] returns [String salida] @init { $salida = ""; }:
ID
{
	// Comprobamos que el identificador ha sido declarado.
	if (tablaSimbolos.get($ID.text, true) == null)
		Error.Error2($ID.line, $ID.pos + 1, $ID.text);

	// Comprobamos que es una función.
	if (tablaSimbolos.get($ID.text, true).getTipo() != Simbolo.FUNCION)
		Error.Error5($ID.line, $ID.pos + 1, $ID.text);
}
PARI par[tablaSimbolos, tablaSimbolos.get($ID.text, true).getCantidadParametros(), $ID.line, $ID.pos, $ID.text]
{
	$salida += $par.salida;
	$salida += "\ncall " + $ID.text;
	$salida += "\nadd  \%esp, " + 4 * tablaSimbolos.get($ID.text, true).getCantidadParametros();
	$salida += "\npush \%eax";
}
PARD;


/******************************************************************************
Analizador léxico
******************************************************************************/

MAIN: 'main';
INT: 'int';
IF: 'if';
ELSE: 'else';
WHILE: 'while';
PRINT: 'print';
READ: 'read';
RETURN: 'return';
LLAVEI: '{';
LLAVED: '}';
PARI: '(';
PARD: ')';
COMA: ',';
PYC: ';';
ASIG: '=';
OR: '||';
AND: '&&';
RELOP: '==' | '!=' | '<' | '>' | '<=' | '>=';
ADDOP: '+' | '-';
MULOP: '*' | '/';
NOT: '!';
ENTERO: ('0'..'9')+;
ID: ('a'..'z' | 'A'..'Z') ('a'..'z' | 'A'..'Z' | '0'..'9' | '_')*;

SEPARADOR: (('\r'? '\n') | ' ' | '\t')+ {skip();};
COMENTARIO: (('/*' .* '*/') | ('//' .* '\n')) {skip();};


