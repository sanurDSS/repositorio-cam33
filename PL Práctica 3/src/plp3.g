grammar plp3;

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

	public TablaTipos tablaTipos;

	public String getTipoCIL(String tipoSimple)
	{
		if (tipoSimple.equals("double"))
			return "float64";
		else if (tipoSimple.equals("int"))
			return "int32";
		else
			return "bool";
	}

	public String getSaltoCIL(String relOp)
	{
		if (relOp.equals("=="))
			return "beq";
		else if (relOp.equals(">="))
			return "bge";
		else if (relOp.equals(">"))
			return "bgt";
		else if (relOp.equals("<="))
			return "ble";
		else if (relOp.equals("<"))
			return "blt";
		else
			return "bne.un";
	}

	public int etiquetas;
	public int indices;

	public int max(int a, int b) {return Math.max(a, b);}
	public int max(int a, int b, int c) {return max(a, max(b, c));}
	public int max(int a, int b, int c, int d) {return max(a, max(b, max(c, d)));}
	public int max(int a, int b, int c, int d, int e) {return max(a, max(b, max(c, max(d, e))));}
	public int max(int a, int b, int c, int d, int e, int f) {return max(a, max(b, max(c, max(d, max(e, f)))));}
	public int max(int a, int b, int c, int d, int e, int f, int g) {return max(a, max(b, max(c, max(d, max(e, max(f, g))))));}
	public int max(int a, int b, int c, int d, int e, int f, int g, int h) {return max(a, max(b, max(c, max(d, max(e, max(f, max(g, h)))))));}
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

//##############################################################################

s:
{
	// Generamos la tabla de tipo que se inicializa, por defecto, con un tipo entero(1), real(2) y booleano(3).
	tablaTipos = new TablaTipos();
	etiquetas = 1;
	indices = 1;

	String salida = "";

	salida += ".assembly extern mscorlib {}";
	salida += "\n.assembly '" + Main.nombreFichero.substring(0, Main.nombreFichero.lastIndexOf(".")) + "' {}";
	salida += "\n";
}
clase
{
	salida += $clase.salida;
	System.out.println(salida);
}
;

//##############################################################################

clase returns [String salida]:
CLASS SINGLE LLAVEI
{
	$salida = "";
	$salida += "\n.class 'Single' extends [mscorlib]System.Object";
	$salida += "\n{";

	$salida += "\n\t.method public specialname rtspecialname instance void .ctor() cil managed";
	$salida += "\n\t{";
	$salida += "\n\t\t.maxstack 1";
	$salida += "\n\t\tldarg.0";
	$salida += "\n\t\tcall instance void [mscorlib]System.Object::.ctor()";
	$salida += "\n\t\tret";
	$salida += "\n\t}";
}
metodo LLAVED
{
	$salida += $metodo.salida;
	$salida += "\n}";
}
;

//##############################################################################

metodo returns [String salida]:
PUBLIC STATIC VOID MAIN PARI PARD
{
	TablaSimbolos tablaSimbolos = new TablaSimbolos(null);

	$salida = "";
	$salida += "\n.method static public void " + $MAIN.text + " () cil managed";
	$salida += "\n{";
	$salida += "\n\t.entrypoint";
}
bloque[tablaSimbolos]
{
	$salida += "\n\t.locals(" + $bloque.locals + ")";
	$salida += "\n\t.maxstack " + $bloque.maxstack;

	// Como es el contenido de un método, añadimos un nivel de tabulación.
	$salida += $bloque.salida.replaceAll("\n", "\n\t");

	$salida += "\n\tret";
	$salida += "\n}";

	// Como es un método y está dentro de una clase, añadimos un nivel de tabulación.
	$salida = $salida.replaceAll("\n", "\n\t");
}
;

//##############################################################################

tiposimple returns [int line, int pos, String text]:
(INT {$line = $INT.line; $pos = $INT.pos; $text = $INT.text;}) |
(DOUBLE {$line = $DOUBLE.line; $pos = $DOUBLE.pos; $text = $DOUBLE.text;}) |
(BOOL{$line = $BOOL.line; $pos = $BOOL.pos; $text = $BOOL.text;});

//##############################################################################

decl [TablaSimbolos tablaSimbolos] returns [String locals]:
tiposimple varid1=varid[tablaSimbolos, $tiposimple.text]
{
	// Establecemos el tipo de la variable para CIL.
	$locals = $varid1.locals;
}
(COMA varid2=varid[tablaSimbolos, $tiposimple.text]
{
	// Concatenamos otra variable más al ".locals".
	$locals += ", " + $varid2.locals;
}
)* PYC;

//##############################################################################

varid [TablaSimbolos tablaSimbolos, String tipoSimple] returns [String locals]:
ID
{
	// Se comprueba que el identificador no esté ya en uso.
	if (tablaSimbolos.getSimbolo($ID.text, false) != null)
		Error.Error1($ID.line, $ID.pos + 1, $ID.text);

	// Se introduce en la tabla el tipo que tendrá el símbolo en la tabla de símbolos.
	int tipo = tablaTipos.addTipo(TablaTipos.convertTipoSimple(tipoSimple), -1, false);
	Simbolo simbolo = new Simbolo(tipo, $ID.text, $ID.line, $ID.pos + 1, tablaSimbolos.getPosicionLocal());
	tablaSimbolos.addSimbolo(simbolo);

	// Comprobamos si es un array.
	boolean esArray = false;
}
(CORI CORD
{
	// Introduzco una nueva línea para representar que es una nueva dimensión.
	// También hay que modificar la anterior, que ahora apunta a esta nueva línea y además "es array".
	int nuevoTipo = tablaTipos.addTipo(TablaTipos.convertTipoSimple(tipoSimple), -1, false);
	tablaTipos.setEsArray(tipo, true);
	tablaTipos.setTipoBase(tipo, nuevoTipo);

	// Si hubieran más dimensiones en el array, habría que volver a modificar la última línea (es decir, la que acaba de introducirse).
	tipo = nuevoTipo;

	// Indicamos que es un array (porque hay que añadir [] al .locals).
	esArray = true;
}
)*
{
	$locals = getTipoCIL(tipoSimple) + ((esArray) ? "[]" : "");
}
;

//##############################################################################

declins [TablaSimbolos tablaSimbolos] returns [String locals, String salida, int maxstack] @init{$locals = $salida = ""; $maxstack = 0;}:
(instr[tablaSimbolos]
{
	$locals += (($locals.equals("") || $instr.locals.equals("")) ? "" : ", ") + $instr.locals;
	$salida += $instr.salida;
	$maxstack = max($maxstack, $instr.maxstack);
} |
decl[tablaSimbolos]
{
	$locals += (($locals.equals("") || $decl.locals.equals("")) ? "" : ", ") + $decl.locals;
})*;

//##############################################################################

bloque [TablaSimbolos tablaSimbolos] returns [String locals, String salida, int maxstack] @init{$salida="";}:
LLAVEI
{
	// Creamos la nueva tabla de este ámbito.
	tablaSimbolos = new TablaSimbolos($tablaSimbolos);
}
declins[tablaSimbolos]
{
	// Concatenamos el ".locals".
	$locals = $declins.locals;

	// Concatenamos la salida.
	$salida = $declins.salida;

	// Calculamos el maxstack.
	$maxstack = $declins.maxstack;
}
LLAVED;

//##############################################################################

instr [TablaSimbolos tablaSimbolos] returns [String locals, String salida, int maxstack] @init {$locals = $salida = ""; $maxstack = 0;}:
bloque[tablaSimbolos]
{
	// Concatenamos el ".locals".
	$locals = $bloque.locals;

	// Concatenamos la salida.
	$salida = $bloque.salida;

	// Calculamos el maxstack.
	$maxstack = $bloque.maxstack;
}
|
//------------------------------------------------------------------------------
(IF
{
	// Creamos la nueva tabla de este ámbito.
	TablaSimbolos tablaSimbolosIf = new TablaSimbolos($tablaSimbolos);
}
PARI expr[tablaSimbolos]
{
	// Comprobamos que la expresión es booleana.
	if (!$expr.tipoSimple.equals("bool"))
		Error.Error5($IF.line, $IF.pos + 1, $IF.text);
}
PARD instr1=instr[tablaSimbolosIf]
{
	// Concatenamos el ".locals".
	$locals = $instr1.locals;

	// Concatenamos la salida.
	$salida = $expr.salida;
	$salida += "\nldc.i4.0";
	$salida += "\nbeq L" + etiquetas;
	$salida += $instr1.salida;
	$salida += "\nbr L" + (etiquetas + 1);
	$salida += "\nL" + etiquetas + ":";

	String saltoFinal = "\nL" + (etiquetas + 1) + ":";
	etiquetas += 2;


	// Calculamos el maxstack.
	$maxstack = max($expr.maxstack, $instr1.maxstack, 2);
}
(ELSE
{
	// Creamos la nueva tabla de este ámbito.
	TablaSimbolos tablaSimbolosElse = new TablaSimbolos($tablaSimbolos);
}
instr2=instr[tablaSimbolosElse]
{
	// Concatenamos el ".locals".
	$locals += (($locals.equals("") || $instr2.locals.equals("")) ? "" : ", ") + $instr2.locals;

	// Concatenamos la salida.
	$salida += $instr2.salida;

	// Si está la parte ELSE, recalculamos el maxstack.
	$maxstack = max($maxstack, $instr2.maxstack);
}
)?
{
	// Concatenamos el último salto (que hemos tenido que generar antes de propagar la ejecución dentro del ELSE).
	$salida += saltoFinal;
}
)|
//------------------------------------------------------------------------------
(FOR
{
	// Creamos la nueva tabla de este ámbito.
	TablaSimbolos tablaSimbolosFor = new TablaSimbolos($tablaSimbolos);
}
PARI INT ID
{
	// Se introduce en la tabla tipo que tendrá el símbolo en la tabla de símbolos.
	int tipo = tablaTipos.addTipo(1, -1, false);
	Simbolo simbolo = new Simbolo(tipo, $ID.text, $ID.line, $ID.pos + 1, tablaSimbolosFor.getPosicionLocal());
	tablaSimbolosFor.addSimbolo(simbolo);

	// Establecemos el tipo de la variable para CIL.
	$locals = "int32";
}
ASIG expr1=expr[tablaSimbolos]
{
	// Comprobamos que la expresión es numérica.
	if ($expr1.tipoSimple.equals("bool"))
		Error.Error6($ASIG.line, $ASIG.pos + 1);
}
PYC expr2=expr[tablaSimbolosFor]
{
	// Comprobamos que la expresión es booleana.
	if (!$expr2.tipoSimple.equals("bool"))
		Error.Error5($FOR.line, $FOR.pos + 1, $FOR.text);
}
PYC expr3=expr[tablaSimbolosFor] PARD instr3=instr[tablaSimbolosFor]
{
	// Concatenamos el ".locals".
	$locals += (($locals.equals("") || $instr3.locals.equals("")) ? "" : ", ") + $instr3.locals;

	// Concatenamos la salida.
	$salida = $expr1.salida;
	$salida += "\nconv.i4";
	$salida += "\nstloc " + tablaSimbolosFor.getSimbolo($ID.text, true).getPosicionLocal();

	$salida += "\nL" + etiquetas + ":";
	$salida += $expr2.salida;
	$salida += "\nldc.i4.1";
	$salida += "\nbeq L" + (etiquetas + 1);
	$salida += "\nbr L" + (etiquetas + 2);

	$salida += "\nL" + (etiquetas + 1) + ":";
	$salida += $instr3.salida;
	$salida += $expr3.salida;
	$salida += "\npop";
	$salida += "\nbr L" + etiquetas;

	$salida += "\nL" + (etiquetas + 2) + ":";

	etiquetas += 3;

	// Calculamos el maxstack.
	$maxstack = max($expr1.maxstack, $expr2.maxstack, $instr3.maxstack, $expr3.maxstack, 2);
}
) |
//------------------------------------------------------------------------------
(ref[tablaSimbolos] cambio[tablaSimbolos, $ref.tipoSimple])
{
	// Comprobamos si es una variable o un vector y generamos la salida y el maxstack.
	if ($ref.codigoIndice.equals(""))
	{
		$salida = $cambio.salida;
		if ($ref.tipoSimple.equals("double"))
			$salida += "\nconv.r8";
		else
			$salida += "\nconv.i4";
		$salida += "\nstloc " + $ref.posicionLocal;

		$maxstack = $cambio.maxstack;
	}
	else
	{
		$salida += "\nldloc " + $ref.posicionLocal;
		$salida += $ref.codigoIndice;
		$salida += $cambio.salida;
		if ($ref.tipoSimple.equals("double"))
		{
			$salida += "\nconv.r8";
			$salida += "\nstelem.r8";
		}
		else
		{
			$salida += "\nconv.i4";
			$salida += "\nstelem.i4";
		}

		$maxstack = max(1 + $ref.maxstack, 2 + $cambio.maxstack, 3);
	}

	// Hay que comprobar si la referencia es una variable o un array (ya que la instrucción CIL varía).
	// Si es una variable, ref devolverá el identificador. Si es un array, devuelve además el código para obtener su posición.
	// Más adelante también habrá que ver si es un atributo (stfld).
}
|
//------------------------------------------------------------------------------
(ID
{
	// Se comprueba que el identificador esté declarado.
	if (tablaSimbolos.getSimbolo($ID.text, true) == null)
		Error.Error2($ID.line, $ID.pos + 1, $ID.text);

	// Se comprueba que la variable sea un array.
	if (!tablaTipos.getEsArray(tablaSimbolos.getSimbolo($ID.text, true).getTipo()))
		Error.Error11($ID.line, $ID.pos + 1, $ID.text);
}
ASIG NEW tiposimple
{
	// Se extrae el tipo para comprobar posteriormente que la cantidad de índices coincide.
	int tipo = tablaSimbolos.getSimbolo($ID.text, true).getTipo();

	// Guardamos la posición del último corchete por si se exceden los índices y hay que mostrar el mensaje de error.
	int CORDline = 0;
	int CORDpos = 0;

	// Contabilizamos la cantidad del vector.
	int tamano = 0;
}
(CORI
{
	// Se comprueba que la cantidad de índices coincide.
	if (tablaTipos.getEsArray(tipo))
	{
		tipo = tablaTipos.getTipoBase(tipo);
	}
	else
	{
		// No hay más índices pero se ha introducido otro.
		Error.Error10($CORI.line, $CORI.pos + 1);
	}
}
ENTERO
{
	// Se comprueba que el tamaño no es 0 o negativo.
	if (Integer.parseInt($ENTERO.text) < 1)
		Error.Error8($CORI.line, $CORI.pos + 1);

	// Actualizamos la dimensión del tipo.
	tablaTipos.setDimension(tipo, Integer.parseInt($ENTERO.text));

	// Contabilizamos la cantidad del vector.
	if (tamano == 0)
	{
		tamano = Integer.parseInt($ENTERO.text);
	}
	else
	{
		tamano *= Integer.parseInt($ENTERO.text);
	}
}
CORD
{
	// Actualizamos la posición del último corchete derecho encontrado.
	CORDline = $CORD.line;
	CORDpos = $CORD.pos;
}
)+
{
	// Se comprueba que no faltan índices.
	if (tablaTipos.getEsArray(tipo))
	{
		// Hay menos índices de los necesarios porque sigue diciendo que es un array y ya no vamos a encontrar más.
		Error.Error10(CORDline, CORDpos + 1);
	}

	// Se comprueba que el tipo simple coincide.
	if (TablaTipos.convertTipoSimple($tiposimple.text) != tablaTipos.getTipoSimple(tablaSimbolos.getSimbolo($ID.text, true).getTipo()))
		Error.Error14($tiposimple.line, $tiposimple.pos + 1, $tiposimple.text);

	// Generamos la salida.
	$salida += "\nldc.i4 " + tamano;
	if ($tiposimple.text.equals("double"))
		$salida += "\nnewarr [mscorlib]System.Double";
	else
		$salida += "\nnewarr [mscorlib]System.Int32";
	$salida += "\nstloc " + tablaSimbolos.getSimbolo($ID.text, true).getPosicionLocal();

	// Calculamos el maxstack.
	$maxstack = 1;
}
PYC) |
//------------------------------------------------------------------------------
(WRITELINE PARI expr4=expr[tablaSimbolos] PARD PYC
{
	$salida = $expr4.salida;
	if ($expr4.tipoSimple.equals("int"))
		$salida += "\ncall void [mscorlib]System.Console::WriteLine(int32)";
	else if ($expr4.tipoSimple.equals("double"))
		$salida += "\ncall void [mscorlib]System.Console::WriteLine(float64)";
	else
		$salida += "\ncall void [mscorlib]System.Console::WriteLine(bool)";

	$maxstack = $expr4.maxstack;
}
);

//##############################################################################

cambio [TablaSimbolos tablaSimbolos, String tipoSimple] returns [String salida, int maxstack] @init{$salida = "";}:
(ASIG expr[tablaSimbolos]
{
	// Comprobamos que los tipos sean compatibles (o numéricos o booleanos).
	if (($tipoSimple.equals("bool") && !$expr.tipoSimple.equals("bool")) || (!$tipoSimple.equals("bool") && $expr.tipoSimple.equals("bool")))
		Error.Error6($ASIG.line, $ASIG.pos + 1);

	// Concatenamos la salida.
	$salida = $expr.salida;

	// Calculamos el maxstack.
	$maxstack = $expr.maxstack;
}
PYC) |
//------------------------------------------------------------------------------
(PUNTO READLINE
{
	// Comprobamos que la lectura sea del mismo tipo.
	if (!$READLINE.text.startsWith($tipoSimple))
		Error.Error7($READLINE.line, $READLINE.pos + 1);

	// Concatenamos la salida.
	$salida = "\ncall string [mscorlib]System.Console::ReadLine()";
	if ($tipoSimple.equals("int"))
		$salida += "\ncall int32 [mscorlib]System.Int32::Parse(string)";
	else if ($tipoSimple.equals("double"))
		$salida += "\ncall float64 [mscorlib]System.Double::Parse(string)";
	else
		$salida += "\ncall bool [mscorlib]System.Boolean::Parse(string)";

	// Calculamos el maxstack.
	$maxstack = 1;
}
PYC);

//##############################################################################

expr [TablaSimbolos tablaSimbolos] returns [String tipoSimple, String salida, int maxstack] @init{$salida = "";}:
eand1=eand[tablaSimbolos]
{
	$tipoSimple = $eand1.tipoSimple;
	$salida = $eand1.salida;
	$maxstack = $eand1.maxstack;
}
(OR
{
	// Comprobamos que la operación pueda realizarse.
	if (!$tipoSimple.equals("bool"))
		Error.Error4($OR.line, $OR.pos + 1, $OR.text);
}
eand2=eand[tablaSimbolos]
{
	// Comprobamos que la operación pueda realizarse.
	if (!$eand2.tipoSimple.equals("bool"))
		Error.Error4($OR.line, $OR.pos + 1, $OR.text);

	// Concatenamos el código a la salida.
	$salida += $eand2.salida;
	$salida += "\nor";

	// Calculamos el maxstack.
	$maxstack = max($maxstack, 1 + $eand2.maxstack);
}
)*;

//##############################################################################

eand [TablaSimbolos tablaSimbolos] returns [String tipoSimple, String salida, int maxstack] @init{$salida = "";}:
erel1=erel[tablaSimbolos]
{
	$tipoSimple = $erel1.tipoSimple;
	$salida = $erel1.salida;
	$maxstack = $erel1.maxstack;
}
(AND
{
	// Comprobamos que la operación pueda realizarse.
	if (!$tipoSimple.equals("bool"))
		Error.Error4($AND.line, $AND.pos + 1, $AND.text);
}
erel2=erel[tablaSimbolos]
{
	// Comprobamos que la operación pueda realizarse.
	if (!$erel2.tipoSimple.equals("bool"))
		Error.Error4($AND.line, $AND.pos + 1, $AND.text);

	// Concatenamos el código a la salida.
	$salida += $erel2.salida;
	$salida += "\nmul";

	// Calculamos el maxstack.
	$maxstack = max($maxstack, 1 + $erel2.maxstack);
}
)*;

//##############################################################################

erel [TablaSimbolos tablaSimbolos] returns [String tipoSimple, String salida, int maxstack] @init{$salida = "";}:
esum1=esum[tablaSimbolos]
{
	$tipoSimple = $esum1.tipoSimple;
	$salida = $esum1.salida;
	$maxstack = $esum1.maxstack;
}
(RELOP esum2=esum[tablaSimbolos]
{
	// Si finalmente aparece la relación de comparación (<, >, <=, >=), entonces la expresión devuelve un booleano seguro.
	$tipoSimple = "bool";

	// Concatenamos la salida.
	$salida += "\nconv.r8";
	$salida += $esum2.salida;
	$salida += "\nconv.r8";
	$salida += "\n" + getSaltoCIL($RELOP.text) + (" L" + etiquetas);
	$salida += "\nldc.i4.0";
	$salida += "\nbr L" + (etiquetas + 1);
	$salida += "\nL" + etiquetas + ":";
	$salida += "\nldc.i4.1";
	$salida += "\nL" + (etiquetas + 1) + ":";
	etiquetas += 2;

	// Calculamos el maxstack.
	$maxstack = max($maxstack, 1 + $esum2.maxstack, 2);
}
)*;

//##############################################################################

esum [TablaSimbolos tablaSimbolos] returns [String tipoSimple, String salida, int maxstack] @init{$salida = "";}:
term1=term[tablaSimbolos]
{
	$tipoSimple = $term1.tipoSimple;
	$salida = $term1.salida;
	$maxstack = $term1.maxstack;
}
(ADDOP
{
	// Comprobamos que la operación pueda realizarse.
	if ($tipoSimple.equals("bool"))
		Error.Error3($ADDOP.line, $ADDOP.pos + 1, $ADDOP.text);
}
term2=term[tablaSimbolos]
{
	// Comprobamos que la operación pueda realizarse.
	if ($term2.tipoSimple.equals("bool"))
		Error.Error3($ADDOP.line, $ADDOP.pos + 1, $ADDOP.text);

	// Como es numérico, comprobamos si alguno de los dos es real para que el resultado sea real.
	if ($tipoSimple.equals("double") || $term2.tipoSimple.equals("double"))
	{
		$tipoSimple = "double";
	}

	// Concatenamos el código a la salida.
	if ($tipoSimple.equals("double"))
	{
		$salida += "\nconv.r8";
		$salida += $term2.salida;
		$salida += "\nconv.r8";
	}
	else
	{
		$salida += $term2.salida;
	}
	if ($ADDOP.text.equals("+"))
		$salida += "\nadd";
	else
		$salida += "\nsub";

	// Calculamos el maxstack.
	$maxstack = max($maxstack, 1 + $term2.maxstack, 2);
}
)*;

//##############################################################################

term [TablaSimbolos tablaSimbolos] returns [String tipoSimple, String salida, int maxstack] @init{$salida = "";}:
factor1=factor[tablaSimbolos]
{
	$tipoSimple = $factor1.tipoSimple;
	$salida = $factor1.salida;
	$maxstack = $factor1.maxstack;
}
(MULOP
{
	// Comprobamos que la multiplicación pueda realizarse.
	if ($tipoSimple.equals("bool"))
		Error.Error3($MULOP.line, $MULOP.pos + 1, $MULOP.text);
}
factor2=factor[tablaSimbolos]
{
	// Comprobamos que la multiplicación pueda realizarse.
	if ($factor2.tipoSimple.equals("bool"))
		Error.Error3($MULOP.line, $MULOP.pos + 1, $MULOP.text);

	// Como es numérico, comprobamos si alguno de los dos es real para que el resultado sea real.
	if ($tipoSimple.equals("double") || $factor2.tipoSimple.equals("double"))
	{
		$tipoSimple = "double";
	}

	// Concatenamos el código a la salida.
	if ($tipoSimple.equals("double"))
	{
		$salida += "\nconv.r8";
		$salida += $factor2.salida;
		$salida += "\nconv.r8";
	}
	else
	{
		$salida += $factor2.salida;
	}
	if ($MULOP.text.equals("*"))
		$salida += "\nmul";
	else
		$salida += "\ndiv";

	// Calculamos el maxstack.
	$maxstack = max($maxstack, 1 + $factor2.maxstack, 2);
}
)*;

//##############################################################################

factor [TablaSimbolos tablaSimbolos] returns [String tipoSimple, String salida, int maxstack] @init{$salida = "";}:
base[tablaSimbolos]
{
	$tipoSimple = $base.tipoSimple;
	$salida = $base.salida;
	$maxstack = $base.maxstack;
}
|
//------------------------------------------------------------------------------
(NOT factor1=factor[tablaSimbolos]
{
	// Comprobamos que factor es de tipo bool.
	if (!$factor1.tipoSimple.equals("bool"))
		Error.Error4($NOT.line, $NOT.pos + 1, $NOT.text);

	// La operación "not" siempre es de tipo "bool" (y si no es, ya habremos salido con mensaje de error).
	$tipoSimple = "bool";

	// Concatenamos la salida.
	$salida = $factor1.salida;
	$salida += "\nldc.i4.1";
	$salida += "\nxor";

	// Calculamos el maxstack.
	$maxstack = max($factor1.maxstack, 2);
}
) |
//------------------------------------------------------------------------------
(PARI ADDOP factor2=factor[tablaSimbolos]
{
	// Comprobamos que el factor es de tipo numérico.
	if ($factor2.tipoSimple.equals("bool"))
		Error.Error3($ADDOP.line, $ADDOP.pos + 1, $ADDOP.text);

	// Propagamos el tipo hacia arriba.
	$tipoSimple = $factor2.tipoSimple;

	// Concatenamos la salida.
	$salida = $factor2.salida;
	$maxstack = max($factor2.maxstack, 1);
	if ($ADDOP.text.equals("-"))
	{
		$salida += "\nneg";
	}
}
PARD);

//##############################################################################

base [TablaSimbolos tablaSimbolos] returns [String tipoSimple, String salida, int maxstack] @init{$salida = "";}:
ENTERO{$tipoSimple = "int"; $salida = "\nldc.i4 " + $ENTERO.text; $maxstack = 1;} |
REAL{$tipoSimple = "double"; $salida = "\nldc.r8 " + $REAL.text; $maxstack = 1;} |
BOOLEANO{$tipoSimple = "bool"; $salida = "\nldc.i4 " + (($BOOLEANO.text.equals("True")) ? "1" : "0"); $maxstack = 1;} |
//------------------------------------------------------------------------------
(PARI expr[tablaSimbolos]{$tipoSimple = $expr.tipoSimple; $salida = $expr.salida; $maxstack = $expr.maxstack;} PARD) |
//------------------------------------------------------------------------------
(ref[tablaSimbolos]
{
	$tipoSimple = $ref.tipoSimple;
	$salida = "\nldloc " + $ref.posicionLocal;
	$maxstack = 1;

	// Si la referencia es un array, hay que añadir también el índice y el acceso al vector.
	if (!$ref.codigoIndice.equals(""))
	{
		$salida += "\n.locals(int32 'indice" + indices + "')";         //añadida
		$salida += $ref.codigoIndice;
		$salida += "\ndup";                                            //añadida
		$salida += "\nstloc 'indice" + indices + "'";                  //añadida

		if ($tipoSimple.equals("double"))
			$salida += "\nldelem.r8";
		else
			$salida += "\nldelem.i4";

		$maxstack = max(1 + $ref.maxstack, 3);
	}
} (
{
	// Variables para saber qué operador se utilizó.
	int POSTline = 0;
	int POSTpos = 0;
	String POSTtext = "";
}
(POSTINCR{POSTline = $POSTINCR.line; POSTpos = $POSTINCR.pos; POSTtext = $POSTINCR.text;} |
POSTDECR{POSTline = $POSTDECR.line; POSTpos = $POSTDECR.pos; POSTtext = $POSTDECR.text;})
{
	// Comprobamos que se puede aplicar ++/--.
	if ($tipoSimple.equals("bool"))
		Error.Error3(POSTline, POSTpos + 1, POSTtext);

	// Se incrementa y se deja la referencia en la pila.
	if ($ref.codigoIndice.equals(""))
	{
		$salida += "\nldloc " + $ref.posicionLocal;
		$salida += "\nldc.i4.1";
		if ($tipoSimple.equals("double"))
			$salida += "\nconv.r8";
		if (POSTtext.equals("++"))
			$salida += "\nadd";
		else
			$salida += "\nsub";
		$salida += "\nstloc " + $ref.posicionLocal;

		// Calculamos el maxstack (son 3 porque ya estaba el elemento previamente cargado en la pila).
		$maxstack = max($maxstack, 3);
	}
	else
	{
		// Si hay un postincremento en el código que calcula el índice, se va a incrementar 2 veces porque este código ya se ha puesto antes.

		$salida += "\nldloc " + $ref.posicionLocal;
		//$salida += $ref.codigoIndice;
		$salida += "\nldloc 'indice" + indices + "'";                  //añadida

		$salida += "\nldloc " + $ref.posicionLocal;
		//$salida += $ref.codigoIndice;
		$salida += "\nldloc 'indice" + indices++ + "'";                //añadida
		if ($tipoSimple.equals("double"))
			$salida += "\nldelem.r8";
		else
			$salida += "\nldelem.i4";
		
		$salida += "\nldc.i4.1";
		if ($tipoSimple.equals("double"))
			$salida += "\nconv.r8";
		if (POSTtext.equals("++"))
			$salida += "\nadd";
		else
			$salida += "\nsub";

		if ($tipoSimple.equals("double"))
			$salida += "\nstelem.r8";
		else
			$salida += "\nstelem.i4";

		$maxstack = max($maxstack, 5);
	}
}
)?);

//##############################################################################

ref [TablaSimbolos tablaSimbolos] returns [String tipoSimple, int posicionLocal, String codigoIndice, int maxstack] @init{$posicionLocal = -1; $codigoIndice = ""; $maxstack = 0;}:
ID
{
	// Se comprueba que el identificador esté declarado.
	if (tablaSimbolos.getSimbolo($ID.text, true) == null)
		Error.Error2($ID.line, $ID.pos + 1, $ID.text);

	// Se extrae el tipo para comprobar posteriormente que la cantidad de índices coincide.
	int tipo = tablaSimbolos.getSimbolo($ID.text, true).getTipo();

	// Nos guardamos el tipo simple para obtener también qué tipo devuelven las expresiones (se guarda con String).
	$tipoSimple = tablaTipos.getTipoSimpleString(tipo);

	// Marcamos la posición local de la referencia.
	$posicionLocal = tablaSimbolos.getSimbolo($ID.text, true).getPosicionLocal();
}
(
{
	// Se comprueba que la variable sea un array porque si estamos aquí, es que tiene corchetes.
	if (!tablaTipos.getEsArray(tablaSimbolos.getSimbolo($ID.text, true).getTipo()))
		Error.Error11($ID.line, $ID.pos + 1, $ID.text);
}
CORI
{
	// Se comprueba que la cantidad de índices coincide.
	if (tablaTipos.getEsArray(tipo))
	{
		tipo = tablaTipos.getTipoBase(tipo);
	}
	else
	{
		// No hay más índices pero se ha introducido otro.
		Error.Error12($CORI.line, $CORI.pos + 1);
	}
}
expr[tablaSimbolos]
{
	// Comprobamos que el valor de la expresión es entero o real.
	if ($expr.tipoSimple.equals("bool"))
		Error.Error13($CORI.line, $CORI.pos + 1);

	// Se genera el código que calculará el índice en tiempo de ejecución.
	if ($codigoIndice.equals(""))
	{
		$codigoIndice = $expr.salida;
		$codigoIndice += "\nconv.i4";
		$maxstack = max($expr.maxstack, 1);
	}
	else
	{
		$codigoIndice += "\nldc.i4 " + tablaTipos.getDimension(tipo);
		$codigoIndice += "\nmul";
		$codigoIndice += $expr.salida;
		$codigoIndice += "\nconv.i4";
		$codigoIndice += "\nadd";
		$maxstack = max($maxstack, 1 + $expr.maxstack, 2);
	}
}
CORD)*
{
	// Se comprueba que no faltan índices.
	if (tablaTipos.getEsArray(tipo))
	{
		// Hay menos índices de los necesarios porque sigue diciendo que es un array y ya no vamos a encontrar más.
		Error.Error9($ID.line, $ID.pos + 1, $ID.text);
	}
}
;

//##############################################################################

/******************************************************************************
Analizador léxico
******************************************************************************/

CLASS: 'class';
SINGLE: 'Single';
VOID: 'void';
MAIN: 'Main';
INT: 'int';
DOUBLE: 'double';
BOOL: 'bool';
PUBLIC: 'public';
STATIC: 'static';
IF: 'if';
ELSE: 'else';
FOR: 'for';
NEW: 'new';
WRITELINE: 'System.Console.WriteLine';
READLINE: ('int' | 'double' | 'bool') '.Parse(System.Console.ReadLine())';
LLAVEI: '{';
LLAVED: '}';
PARI: '(';
PARD: ')';
CORI: '[';
CORD: ']';
COMA: ',';
PYC: ';';
ASIG: '=';
OR: '|';
AND: '&';
RELOP: '==' | '!=' | '<' | '>' | '<=' | '>=';
ADDOP: '+' | '-';
MULOP: '*' | '/';
POSTINCR: '++';
POSTDECR: '--';
NOT: '!';
PUNTO: '.';
ENTERO: ('0'..'9')+;
REAL: ('0'..'9')+ '.' ('0'..'9')+;
BOOLEANO: 'True' | 'False';
ID: ('a'..'z' | 'A'..'Z') ('a'..'z' | 'A'..'Z' | '0'..'9' | '_')*;


SEPARADOR: (('\r'? '\n') | ' ' | '\t')+ {skip();};
COMENTARIO: (('/*' .* '*/') | ('//' .* '\n')) {skip();};
// consultar el comentario, a ver si finaliza en \r o \n o sólo con \n.
// Es decir, a ver si es obligatorio un \n para que finalice el comentario.

