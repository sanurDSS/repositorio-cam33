grammar plp4;

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
	public TablaSimbolos tablaSimbolosGlobal;

	public String getTipoCIL(String tipoSimple)
	{
		if (tipoSimple.equals("double"))
			return "float64";
		else if (tipoSimple.equals("int"))
			return "int32";
		else if (tipoSimple.equals("bool"))
			return "bool";
		else
			return "class '" + tipoSimple + "'";
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

	public boolean hayMain;
	public boolean enMain;
	public String tipoRetorno;
	public String nombreClase;

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
	// Tabla de símbolos general.
	tablaSimbolosGlobal = new TablaSimbolos(null);

	// Generamos la tabla de tipo que se inicializa, por defecto, con un tipo entero(1), real(2) y booleano(3).
	tablaTipos = new TablaTipos();
	etiquetas = 1;
	indices = 1;
	hayMain = false;
	enMain = false;

	String salida = "";

	salida += ".assembly extern mscorlib {}";
	salida += "\n.assembly '" + Main.nombreFichero.substring(0, Main.nombreFichero.lastIndexOf(".")) + "' {}";
	salida += "\n";
}
(clase[tablaSimbolosGlobal]
{
	salida += $clase.salida;
}
)+
{
	if (!hayMain)
		Error.Error15();

	//System.out.println(tablaTipos.toString());
	System.out.println(salida);
}
;

//##############################################################################

clase [TablaSimbolos tablaSimbolos] returns [String salida]:
CLASS ID LLAVEI
{
	// Se comprueba que el identificador no esté ya en uso en otra clase.
	if (tablaSimbolos.getSimbolo($ID.text, false) != null)
		Error.Error1($ID.line, $ID.pos + 1, $ID.text);

	// Creamos un nuevo ámbito para la clase con el nombre de la clase.
	TablaSimbolos tablaSimbolosNueva = new TablaSimbolos(tablaSimbolos);
	tablaSimbolosNueva.setNombreClase($ID.text);
	nombreClase = $ID.text;

	// Añadimos un nuevo tipo en la tabla de tipos.
	int tipo = tablaTipos.addTipo(0, -1);
	tablaTipos.setEsClase(tipo, true);
	tablaTipos.setTablaSimbolos(tipo, tablaSimbolosNueva);

	// Creamos el símbolo de la clase dentro del ámbito global (para que falle si hay 2 clases con el mismo nombre).
	tablaSimbolos.addSimbolo(new Simbolo(tipo, $ID.text, $ID.line, $ID.pos + 1, -1));

	$salida = "";
	$salida += "\n.class '" + $ID.text + "' extends [mscorlib]System.Object";
	$salida += "\n{";

	$salida += "\n\t.method public specialname rtspecialname instance void .ctor() cil managed";
	$salida += "\n\t{";
	$salida += "\n\t\t.maxstack 1";
	$salida += "\n\t\tldarg.0";
	$salida += "\n\t\tcall instance void [mscorlib]System.Object::.ctor()";
	$salida += "\n\t\tret";
	$salida += "\n\t}";
}
(
miembro[tablaSimbolosNueva]
{
	$salida += "\n";
	$salida += $miembro.salida;
}
)+
LLAVED
{
	$salida += "\n}";
}
;

//##############################################################################

miembro [TablaSimbolos tablaSimbolos] returns [String salida]:
(campo[tablaSimbolos]
{
	$miembro.salida = $campo.salida;
}
)|(
metodo[tablaSimbolos]
{
	$miembro.salida = $metodo.salida;
}
);

//##############################################################################

campo [TablaSimbolos tablaSimbolos] returns [String salida]:
visibilidad decl[tablaSimbolos, true, $visibilidad.text]
{
	$campo.salida = $decl.fields.replaceAll("\n", "\n\t");
};

//##############################################################################

visibilidad returns [String text]: PRIVATE {$text = $PRIVATE.text;} | PUBLIC {$text = $PUBLIC.text;};

//##############################################################################

metodo [TablaSimbolos tablaSimbolos] returns [String salida]:
(PUBLIC tiposimple
{
	tipoRetorno = $tiposimple.text;
	TablaSimbolos tablaSimbolosNueva = new TablaSimbolos(tablaSimbolos);
	tablaSimbolosNueva.conLocales = true;
}
ID PARI args[tablaSimbolosNueva] PARD
{
	// Comprobamos que no exista ningún miembro en la clase con el mismo nombre.
	if (tablaSimbolos.getSimbolo($ID.text, false) != null)
		Error.Error1($ID.line, $ID.pos + 1, $ID.text);

	// Creamos un nuevo tipo de método con la cantidad de parámetros, tabla de símbolos asociada para conocer los parámetros y el tipo de retorno.
	int tipo = tablaTipos.addTipo(0, $args.cantidad);
	tablaTipos.setEsMetodo(tipo, true);
	tablaTipos.setRetorno(tipo, TablaTipos.convertTipoSimple($tiposimple.text));
	tablaTipos.setTablaSimbolos(tipo, tablaSimbolosNueva);

	// Añadimos un símbolo al ámbito de la clase para saber que existe el método.
	tablaSimbolos.addSimbolo(new Simbolo(tipo, $ID.text, $ID.line, $ID.pos + 1, -1));

	$salida = "";
	$salida += "\n.method public " + getTipoCIL($tiposimple.text) + " '" + $ID.text + "' (" + $args.salida + ") cil managed";
	$salida += "\n{";
}
bloque[tablaSimbolosNueva, false]
{
	$salida += "\n\t.locals(" + $bloque.locals + ")";
	$salida += "\n\t.maxstack " + max(1, $bloque.maxstack);

	// Como es el contenido de un método, añadimos un nivel de tabulación.
	$salida += $bloque.salida.replaceAll("\n", "\n\t");

	if ($tiposimple.text.equals("double"))
		$salida += "\n\tldc.r8 0";
	else
		$salida += "\n\tldc.i4 0";
	$salida += "\n\tret";
	$salida += "\n}";

	// Como es un método y está dentro de una clase, añadimos un nivel de tabulación.
	$salida = $salida.replaceAll("\n", "\n\t");
}
)
|
(PUBLIC STATIC VOID MAIN PARI PARD
{
	// Comprobamos que sólo hay un Main en todo el código.
	if (hayMain)
		Error.Error15();
	hayMain = true;
	enMain = true;

	TablaSimbolos tablaSimbolosNueva = new TablaSimbolos(tablaSimbolos);
	tablaSimbolosNueva.conLocales = true;

	$salida = "";
	$salida += "\n.method static public void main () cil managed";
	$salida += "\n{";
	$salida += "\n\t.entrypoint";
}
bloque[tablaSimbolosNueva, false]
{
	$salida += "\n\t.locals(" + $bloque.locals + ")";
	$salida += "\n\t.maxstack " + $bloque.maxstack;

	// Como es el contenido de un método, añadimos un nivel de tabulación.
	$salida += $bloque.salida.replaceAll("\n", "\n\t");

	$salida += "\n\tret";
	$salida += "\n}";

	// Como es un método y está dentro de una clase, añadimos un nivel de tabulación.
	$salida = $salida.replaceAll("\n", "\n\t");

	enMain = false;
}
);

//##############################################################################

args [TablaSimbolos tablaSimbolos] returns [int cantidad, String salida] @init { $cantidad = 0; $salida = ""; }:
(DOUBLE ID1=ID
{
	// Comprobamos que no existe el identificador en este ámbito.
	if (tablaSimbolos.getSimbolo($ID1.text, false) != null)
		Error.Error1($ID1.line, $ID1.pos + 1, $ID1.text);

	$salida += "float64";
	int tipo = tablaTipos.addTipo(TablaTipos.DOUBLE, -1);
	tablaTipos.setEsParametro(tipo, true);
	tablaSimbolos.addSimbolo(new Simbolo(tipo, $ID1.text, $ID1.line, $ID1.pos + 1, ++$cantidad));
}
(COMA DOUBLE ID2=ID
{
	// Comprobamos que no existe el identificador en este ámbito.
	if (tablaSimbolos.getSimbolo($ID2.text, false) != null)
		Error.Error1($ID2.line, $ID2.pos + 1, $ID2.text);

	$salida += ", float64";
	tipo = tablaTipos.addTipo(TablaTipos.DOUBLE, -1);
	tablaTipos.setEsParametro(tipo, true);
	tablaSimbolos.addSimbolo(new Simbolo(tipo, $ID2.text, $ID2.line, $ID2.pos + 1, ++$cantidad));
})*)?;

//##############################################################################

params [TablaSimbolos tablaSimbolos, int line, int pos, int cantidad] returns [String salida, int maxstack, boolean faltan] @init { $salida = ""; $maxstack = 0; $faltan = false;}:
(expr1=expr[tablaSimbolos]
{
	// Decrementamos la cantidad de parámetros y comprobamos que no nos hayamos pasado.
	$cantidad--;
	if (cantidad < 0)
		Error.Error17($line, $pos + 1);

	// Comprobamos que el parámetro es double (o int al menos).
	if (!$expr1.tipoSimple.equals("double") && !$expr1.tipoSimple.equals("int"))
		Error.Error21($line, $pos + 1);

	$salida += $expr1.salida;
	if ($expr1.tipoSimple.equals("int"))
		$salida += "\nconv.r8";
	$maxstack = $expr1.maxstack;

	int contador = 1;
}
(COMA expr2=expr[tablaSimbolos]
{
	// Decrementamos la cantidad de parámetros y comprobamos que no nos hayamos pasado.
	$cantidad--;
	if (cantidad < 0)
		Error.Error17($COMA.line, $COMA.pos + 1);

	// Comprobamos que el parámetro es double (o int al menos).
	if (!$expr2.tipoSimple.equals("double") && !$expr2.tipoSimple.equals("int"))
		Error.Error21($COMA.line, $COMA.pos + 1);

	$salida += $expr2.salida;
	if ($expr2.tipoSimple.equals("int"))
		$salida += "\nconv.r8";
	$maxstack = max($maxstack, contador++ + $expr2.maxstack);
}
)*)?
{
	if ($cantidad > 0)
		$faltan = true;
};

//##############################################################################

tipo [TablaSimbolos tablaSimbolos] returns [int line, int pos, String text]:
ID
{
	// Comprobamos que exista el tipo.
	if (tablaSimbolos.getSimbolo($ID.text, true) == null)
		Error.Error2($ID.line, $ID.pos + 1, $ID.text);

	// Comprobamos que se corresponde con una clase.
	if (!tablaTipos.getEsClase(tablaSimbolos.getSimbolo($ID.text, true).getTipo()))
		Error.Error16($ID.line, $ID.pos + 1, $ID.text);

	$line = $ID.line; $pos = $ID.pos; $text = $ID.text;
}
|
tiposimple {$line = $tiposimple.line; $pos = $tiposimple.pos; $text = $tiposimple.text;};

//##############################################################################

tiposimple returns [int line, int pos, String text]:
(INT {$line = $INT.line; $pos = $INT.pos; $text = $INT.text;}) |
(DOUBLE {$line = $DOUBLE.line; $pos = $DOUBLE.pos; $text = $DOUBLE.text;}) |
(BOOL{$line = $BOOL.line; $pos = $BOOL.pos; $text = $BOOL.text;});

//##############################################################################

decl [TablaSimbolos tablaSimbolos, boolean campo, String visibilidad] returns [String locals, String fields]:
tipo[tablaSimbolos] varid1=varid[tablaSimbolos, $tipo.text, $decl.campo, $decl.visibilidad]
{
	// Establecemos el tipo de la variable para CIL.
	$locals = $varid1.locals;
	$fields = "\n.field " + visibilidad + " " + $varid1.fields;
}
(COMA varid2=varid[tablaSimbolos, $tipo.text, $decl.campo, $decl.visibilidad]
{
	// Concatenamos otra variable más al ".locals".
	$locals += ", " + $varid2.locals;
	$fields += "\n.field " + visibilidad + " " + $varid2.fields;
}
)* PYC;

//##############################################################################

varid [TablaSimbolos tablaSimbolos, String tipoSimple, boolean campo, String visibilidad] returns [String locals, String fields]:
ID
{
	// Se comprueba que el identificador no esté ya en uso.
	if (tablaSimbolos.getSimbolo($ID.text, false) != null)
		Error.Error1($ID.line, $ID.pos + 1, $ID.text);

	// Buscamos el tipo base, que puede ser un tipo primitivo o una clase (llegados a este punto sabemos que es un primitivo o una clase).
	int tipoBase = -1;
	if (tipoSimple.equals("bool") || tipoSimple.equals("int") || tipoSimple.equals("double"))
	{
		tipoBase = TablaTipos.convertTipoSimple(tipoSimple);
	}
	else
	{
		tipoBase = tablaSimbolosGlobal.getSimbolo(tipoSimple, false).getTipo();
	}

	// Se introduce en la tabla el tipo que tendrá el símbolo en la tabla de símbolos.
	int tipo = tablaTipos.addTipo(tipoBase, -1);
	if (tipoBase > 3)
		tablaTipos.setEsObjeto(tipo, true);

	if (!campo)
	{
		Simbolo simbolo = new Simbolo(tipo, $ID.text, $ID.line, $ID.pos + 1, tablaSimbolos.getPosicionLocal());
		tablaSimbolos.addSimbolo(simbolo);
	}
	else
	{
		Simbolo simbolo = new Simbolo(tipo, $ID.text, $ID.line, $ID.pos + 1, -1);
		tablaSimbolos.addSimbolo(simbolo);
		tablaTipos.setEsAtributo(tipo, true);
		tablaTipos.setVisibilidad(tipo, ($visibilidad.equals("public")) ? true : false);
	}

	// Comprobamos si es un array.
	boolean esArray = false;
}
(CORI CORD
{
	// Evitamos que se declaren vectores de objetos.
	if (!tipoSimple.equals("bool") && !tipoSimple.equals("int") && !tipoSimple.equals("double"))
		Error.Error22($CORI.line, $CORI.pos + 1);
	
	// Introduzco una nueva línea para representar que es una nueva dimensión.
	// También hay que modificar la anterior, que ahora apunta a esta nueva línea y además "es array".
	int nuevoTipo = tablaTipos.addTipo(TablaTipos.convertTipoSimple(tipoSimple), -1);
	tablaTipos.setEsArray(tipo, true);
	tablaTipos.setTipoBase(tipo, nuevoTipo);
	//TODO cuando es un campo de una clase y no una declaracion, hay que cambiar algo? el tipo simple seguro que es un primitivo, porque objetos no puede haber arrays

	// Si hubieran más dimensiones en el array, habría que volver a modificar la última línea (es decir, la que acaba de introducirse).
	tipo = nuevoTipo;

	// Indicamos que es un array (porque hay que añadir [] al .locals).
	esArray = true;
}
)*
{
	$locals = getTipoCIL(tipoSimple) + ((esArray) ? "[]" : "");
	$fields = getTipoCIL(tipoSimple) + ((esArray) ? "[]" : "") + " '" + $ID.text + "'";
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
decl[tablaSimbolos, false, ""]
{
	$locals += (($locals.equals("") || $decl.locals.equals("")) ? "" : ", ") + $decl.locals;
})*;

//##############################################################################

bloque [TablaSimbolos tablaSimbolos, boolean crearAmbito] returns [String locals, String salida, int maxstack] @init{$salida="";}:
LLAVEI
{
	// Creamos la nueva tabla de este ámbito.
	if (crearAmbito)
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
bloque[tablaSimbolos, true]
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
	int tipo = tablaTipos.addTipo(1, -1);
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
	$salida = $ref.prefijoCodigo + $cambio.salida + "\nst" + $ref.sufijoCodigo;
	$maxstack = max($ref.maxstack, 2 + $cambio.maxstack);
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

	// Comprobamos si es un atributo o una local (no puede ser otra cosa).
	if (tablaTipos.getEsAtributo(tablaSimbolos.getSimbolo($ID.text, true).getTipo()) && enMain)
		Error.Error24($ID.line, $ID.pos + 1, $ID.text);
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
	if (tablaTipos.getEsAtributo(tablaSimbolos.getSimbolo($ID.text, true).getTipo()))
		$salida += "\nldarg.0";

	$salida += "\nldc.i4 " + tamano;
	if ($tiposimple.text.equals("double"))
		$salida += "\nnewarr [mscorlib]System.Double";
	else
		$salida += "\nnewarr [mscorlib]System.Int32";

	if (!tablaTipos.getEsAtributo(tablaSimbolos.getSimbolo($ID.text, true).getTipo()))
		$salida += "\nstloc " + tablaSimbolos.getSimbolo($ID.text, true).getPosicionLocal();
	else
		$salida += "\nstfld " + getTipoCIL($tiposimple.text) + "[] '" + nombreClase + "'::'" + $ID.text + "'";

	// Calculamos el maxstack.
	$maxstack = 2;
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
	else if ($expr4.tipoSimple.equals("bool"))
		$salida += "\ncall void [mscorlib]System.Console::WriteLine(bool)";
	else
		Error.Error25($WRITELINE.line, $WRITELINE.pos + 1);

	$maxstack = $expr4.maxstack;
}
) |
(RETURN
{
	// Si estamos dentro del método Main, se produce un error.
	if (enMain)
		Error.Error19($RETURN.line, $RETURN.pos + 1);
}
expr5=expr[tablaSimbolos]
{
	// Comprobamos que el tipo de retorno es compatible.
	if ((tipoRetorno.equals("bool") && !$expr5.tipoSimple.equals("bool")) || (!tipoRetorno.equals("bool") && $expr5.tipoSimple.equals("bool")))
		Error.Error20($RETURN.line, $RETURN.pos + 1);
	if ((tipoRetorno.equals("int") || tipoRetorno.equals("double")) && (!$expr5.tipoSimple.equals("int") && !$expr5.tipoSimple.equals("double")))
		Error.Error20($RETURN.line, $RETURN.pos + 1);

	$salida = $expr5.salida;
	if (tipoRetorno.equals("double") && $expr5.tipoSimple.equals("int"))
		$salida += "\nconv.r8";
	else if (tipoRetorno.equals("int") && $expr5.tipoSimple.equals("double"))
		$salida += "\nconv.i4";
	$salida += "\nret";

	$maxstack = $expr5.maxstack;
}
PYC) |
//------------------------------------------------------------------------------
(ID1=ID ASIG NEW ID2=ID PARI PARD PYC
{
	// Se comprueba que el identificador esté declarado.
	if (tablaSimbolos.getSimbolo($ID1.text, true) == null)
		Error.Error2($ID1.line, $ID1.pos + 1, $ID1.text);

	// Se comprueba que el tipo del identificador declarado sea un objeto.
	if (!tablaTipos.getEsClase(tablaTipos.getTipoBase(tablaSimbolos.getSimbolo($ID1.text, true).getTipo())))
		Error.Error16($ID1.line, $ID1.pos + 1, $ID1.text);

	// Comprobamos que sea accesible desde Main.
	boolean atributo = tablaTipos.getEsAtributo(tablaSimbolos.getSimbolo($ID1.text, true).getTipo());
	if (atributo)
	{
		if (enMain)
		{
			Error.Error24($ID1.line, $ID1.pos + 1, $ID1.text);
		}
	}

	// Se comprueba que el identificador 2 esté declarado.
	if (tablaSimbolos.getSimbolo($ID2.text, true) == null)
		Error.Error2($ID2.line, $ID2.pos + 1, $ID2.text);

	// Se comprueba que el tipo del identificador 2 declarado sea una clase.
	if (!tablaTipos.getEsClase(tablaSimbolos.getSimbolo($ID2.text, true).getTipo()))
		Error.Error16($ID2.line, $ID2.pos + 1, $ID2.text);

	// Se comprueba que el tipo del objeto coincida.
	if (tablaTipos.getTipoBase(tablaSimbolos.getSimbolo($ID1.text, true).getTipo()) != tablaSimbolos.getSimbolo($ID2.text, true).getTipo())
		Error.Error23($ID1.line, $ID1.pos + 1, $ID1.text, $ID2.text);

	// Generamos el código según sea una variable local o un atributo (si es atributo, no podemos estar en Main).
	$salida = "";
	if (atributo)
	{
		$salida += "\nldarg.0";
		$salida += "\nnewobj instance void '" + $ID2.text + "'::.ctor()";
		$salida += "\nstfld " + $ID2.text + " '" + nombreClase + "'::'" + $ID1.text + "'";

		$maxstack = 2;
	}
	else
	{
		$salida += "\nnewobj instance void '" + $ID2.text + "'::.ctor()";
		$salida += "\nstloc " + tablaSimbolos.getSimbolo($ID1.text, true).getPosicionLocal();

		$maxstack = 1;
	}
}
) |
//------------------------------------------------------------------------------
(llamada[tablaSimbolos]
{
	$salida = $llamada.salida;
	$salida += "\npop";
	$maxstack = $llamada.maxstack;
}
PYC);

//##############################################################################

llamada [TablaSimbolos tablaSimbolos] returns [String tipoSimple, String salida, int maxstack] @init { $tipoSimple = ""; $salida = ""; $maxstack = 0; }:
(ID1=ID
{
	// En cuanto cambio el ámbito ya no busco hacia arriba, sino que se busca sólo en el ámbito de la clase.
	boolean ambitoGlobal = true;

	// Se comprueba que existe el identificador.
	if (tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal) == null)
		Error.Error2($ID1.line, $ID1.pos + 1, $ID1.text);

	// Comprobamos que, si estamos en el Main, no es ni método ni atributo.
	if ((tablaTipos.getEsAtributo(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo()) || tablaTipos.getEsMetodo(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo())) && enMain)
		Error.Error24($ID1.line, $ID1.pos + 1, $ID1.text);

	boolean primeraAparicion = true;

	// Tabla de símbolos original, porque los argumentos se extraen desde el ámbito original y no desde dentro del método.
	TablaSimbolos tablaSimbolosOriginal = tablaSimbolos;
}
(PUNTO
{
	// Se comprueba que el identificador sea un objeto.
	if (!tablaTipos.getEsObjeto(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo()))
		Error.Error16($ID1.line, $ID1.pos + 1, $ID1.text);

	// Se genera el código según si es local o atributo (sólo podrá ser local la primera aparición, después seguro que es atributo).
	if (primeraAparicion)
	{
		if (tablaTipos.getEsAtributo(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo()))
		{
			$salida += "\nldarg.0";
			$salida += "\nldfld " + getTipoCIL(tablaTipos.getTablaSimbolos(tablaTipos.getTipoBase(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo())).getNombreClase()) + " '" + nombreClase + "'::'" + $ID1.text + "'";
		}
		else
		{
			$salida += "\nldloc " + tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getPosicionLocal();
		}
		primeraAparicion = false;
	}
	else
	{
		$salida += "\nldfld " + getTipoCIL(tablaTipos.getTablaSimbolos(tablaTipos.getTipoBase(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo())).getNombreClase()) + " '" + tablaSimbolos.getNombreClase() + "'::'" + $ID1.text + "'";
	}

	// Cambiamos el ámbito al ámbito de la clase del objeto.
	tablaSimbolos = tablaTipos.getTablaSimbolos(tablaTipos.getTipoBase(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo()));
	ambitoGlobal = false;
}
ID2=ID
{
	// Se comprueba que existe el identificador.
	if (tablaSimbolos.getSimbolo($ID2.text, ambitoGlobal) == null)
		Error.Error2($ID2.line, $ID2.pos + 1, $ID2.text);

	// Comprobamos que tenga visibilidad.
	if (!tablaTipos.getVisibilidad(tablaSimbolos.getSimbolo($ID2.text, ambitoGlobal).getTipo()) && !nombreClase.equals(tablaSimbolos.getNombreClase()))
		Error.Error2($ID2.line, $ID2.pos + 1, $ID2.text);

	$ID1 = $ID2;
}
)*
{
	// Se comprueba que sea un método.
	if (!tablaTipos.getEsMetodo(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo()))
		Error.Error16($ID1.line, $ID1.pos + 1, $ID1.text);

	// Calculamos la cantidad de parámetros.
	int cantidad = tablaTipos.getDimension(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo());
}
PARI params[tablaSimbolosOriginal, $PARI.line, $PARI.pos, cantidad] PARD
{
	// Comprobamos que no falten parámetros.
	if ($params.faltan)
		Error.Error18($PARD.line, $PARD.pos + 1);

	// Generamos la salida.
	if (primeraAparicion)
		$salida += "\nldarg.0";
	$salida += $params.salida;

	String retorno = "";
	switch (tablaTipos.getRetorno(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo()))
	{
		case 1: retorno = "int"; break;
		case 2: retorno = "double"; break;
		case 3: retorno = "bool"; break;
	}
	String parametros = "";
	while (cantidad-- > 0)
	{
		if (parametros.equals(""))
			parametros += "float64";
		else
			parametros += ", float64";
	}

	if (primeraAparicion)
		$salida += "\ncall instance " + getTipoCIL(retorno) + " '" + nombreClase + "'::'" + $ID1.text + "'(" + parametros + ")";
	else
		$salida += "\ncall instance " + getTipoCIL(retorno) + " '" + tablaSimbolos.getNombreClase() + "'::'" + $ID1.text + "'(" + parametros + ")";
	$maxstack = 1 + $params.maxstack;
	$tipoSimple = retorno;
}
);

//##############################################################################

cambio [TablaSimbolos tablaSimbolos, String tipoSimple] returns [String salida, int maxstack] @init{$salida = "";}:
(ASIG expr[tablaSimbolos]
{
	// Comprobamos que los tipos sean compatibles (numéricos, booleanos o de la misma clase).
	boolean numericos = ($tipoSimple.equals("int") || $tipoSimple.equals("double")) && ($expr.tipoSimple.equals("int") || $expr.tipoSimple.equals("double"));
	if ($tipoSimple.equals($expr.tipoSimple) || (numericos))
	{
		// Concatenamos la salida.
		$salida = $expr.salida;

		// Convertimos el tipo si son numéricos.
		if ($tipoSimple.equals("int"))
			$salida += "\nconv.i4";
		else if ($tipoSimple.equals("double"))
			$salida += "\nconv.r8";

		// Calculamos el maxstack.
		$maxstack = $expr.maxstack;
	}
	else
	{
		Error.Error6($ASIG.line, $ASIG.pos + 1);
	}
}
PYC) |
//------------------------------------------------------------------------------
(PUNTO READLINE
{
	// Comprobamos que no sea un objeto.
	if (!$tipoSimple.equals("int") && !$tipoSimple.equals("double") && !$tipoSimple.equals("bool"))
		Error.Error25($READLINE.line, $READLINE.pos + 1);

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
(
RELOP
{
	// Si el tipo simple no es uno de estos 3, es que es un objeto y no se pueden relacionar.
	if (!$tipoSimple.equals("bool") && !$tipoSimple.equals("int") && !$tipoSimple.equals("double"))
		Error.Error26($RELOP.line, $RELOP.pos + 1, $RELOP.text);
}
esum2=esum[tablaSimbolos]
{
	// Si el tipo simple no es uno de estos 3, es que es un objeto y no se pueden relacionar.
	if (!$esum2.tipoSimple.equals("bool") && !$esum2.tipoSimple.equals("int") && !$esum2.tipoSimple.equals("double"))
		Error.Error26($RELOP.line, $RELOP.pos + 1, $RELOP.text);

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
	if (!$tipoSimple.equals("int") && !$tipoSimple.equals("double"))
		Error.Error3($ADDOP.line, $ADDOP.pos + 1, $ADDOP.text);
}
term2=term[tablaSimbolos]
{
	// Comprobamos que la operación pueda realizarse.
	if (!$term2.tipoSimple.equals("int") && !$term2.tipoSimple.equals("double"))
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
	if (!$tipoSimple.equals("int") && !$tipoSimple.equals("double"))
		Error.Error3($MULOP.line, $MULOP.pos + 1, $MULOP.text);
}
factor2=factor[tablaSimbolos]
{
	// Comprobamos que la multiplicación pueda realizarse.
	if (!$factor2.tipoSimple.equals("int") && !$factor2.tipoSimple.equals("double"))
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
	if (!$factor2.tipoSimple.equals("int") && !$factor2.tipoSimple.equals("double"))
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
	$salida = $ref.prefijoCodigo + "\nld" + $ref.sufijoCodigo;
	$maxstack = $ref.maxstack;
}
) |
//------------------------------------------------------------------------------
(llamada[tablaSimbolos]
{
	$tipoSimple = $llamada.tipoSimple;
	$salida = $llamada.salida;
	$maxstack = $llamada.maxstack;
}) |
//------------------------------------------------------------------------------
(ID3=ID (POST=POSTINCR | POST=POSTDECR)
{
	// Se comprueba que el identificador esté declarado.
	if (tablaSimbolos.getSimbolo($ID3.text, true) == null)
		Error.Error2($ID3.line, $ID3.pos + 1, $ID3.text);

	// Comprobamos que sea int o double.
	if (tablaTipos.getTipoBase(tablaSimbolos.getSimbolo($ID3.text, true).getTipo()) == TablaTipos.INT)
		$tipoSimple = "int";
	else if (tablaTipos.getTipoBase(tablaSimbolos.getSimbolo($ID3.text, true).getTipo()) == TablaTipos.DOUBLE)
		$tipoSimple = "double";
	else
		Error.Error3($POST.line, $POST.pos + 1, $POST.text);

	// Comprobamos que, si es un atributo, no es el Main.
	boolean atributo = tablaTipos.getEsAtributo(tablaSimbolos.getSimbolo($ID3.text, true).getTipo());
	if (atributo)
	{
		if (enMain)
			Error.Error24($ID3.line, $ID3.pos + 1, $ID3.text);
	}

	// Comprobamos si es un atributo o una local o un parametro
	// Metemos el valor en la pila 2 veces e incrementamos y guardamos.
	$salida = "";
	if (atributo)
	{
		$salida += "\nldarg.0";
		$salida += "\nldfld " + getTipoCIL($tipoSimple) + " '" + nombreClase + "'::'" + $ID3.text + "'";

		$salida += "\nldarg.0";
		$salida += "\nldarg.0";
		$salida += "\nldfld " + getTipoCIL($tipoSimple) + " '" + nombreClase + "'::'" + $ID3.text + "'";
	
		if ($tipoSimple.equals("int"))
			$salida += "\nldc.i4 1";
		else
			$salida += "\nldc.r8 1";

		if ($POST.text.equals("++"))
			$salida += "\nadd";
		else
			$salida += "\nsub";
		$salida += "\nstfld " + getTipoCIL($tipoSimple) + " '" + nombreClase + "'::'" + $ID3.text + "'";

		$maxstack = 4;
	}
	else
	{
		String tipoVariable = tablaTipos.getEsParametro(tablaSimbolos.getSimbolo($ID3.text, true).getTipo()) ? "arg" : "loc";

		$salida += "\nld" + tipoVariable + " " + tablaSimbolos.getSimbolo($ID3.text, true).getPosicionLocal();
		$salida += "\ndup";

		if ($tipoSimple.equals("int"))
			$salida += "\nldc.i4 1";
		else
			$salida += "\nldc.r8 1";

		if ($POST.text.equals("++"))
			$salida += "\nadd";
		else
			$salida += "\nsub";
		$salida += "\nst" + tipoVariable + " " + tablaSimbolos.getSimbolo($ID3.text, true).getPosicionLocal();

		$maxstack = 3;
	}
}
)
;

//##############################################################################

ref [TablaSimbolos tablaSimbolos] returns [String tipoSimple, String prefijoCodigo, String sufijoCodigo, int maxstack] @init{$prefijoCodigo = $sufijoCodigo = ""; $maxstack = 0;}:
ID1=ID
{
	// En cuanto cambio el ámbito ya no busco hacia arriba, sino que se busca sólo en el ámbito de la clase.
	boolean ambitoGlobal = true;

	// Se comprueba que el identificador esté declarado.
	if (tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal) == null)
		Error.Error2($ID1.line, $ID1.pos + 1, $ID1.text);

	// Comprobamos que sea un parámetro, un atributo o una local.
	if (tablaTipos.getEsClase(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo()) || tablaTipos.getEsMetodo(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo()))
		Error.Error16($ID1.line, $ID1.pos + 1, $ID1.text);

	// Comprobamos que el atributo sea accesible (es decir, que no estamos en el método Main).
	if (tablaTipos.getEsAtributo(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo()) && enMain)
		Error.Error24($ID1.line, $ID1.pos + 1, $ID1.text);

	// Se extrae el tipo para comprobar posteriormente que la cantidad de índices coincide.
	int tipo = tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo();

	// Nos guardamos el tipo simple para obtener también qué tipo devuelven las expresiones (se guarda con String).
	$tipoSimple = tablaTipos.getTipoSimpleString(tipo);
	boolean esArray = tablaTipos.getEsArray(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo());

	// Comprobamos si es local, parámetro o atributo.
	if (tablaTipos.getEsAtributo(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo()))
	{
		$prefijoCodigo = "\nldarg.0";
		$sufijoCodigo = "fld " + getTipoCIL($tipoSimple) + ((esArray) ? "[]" : "") + " '" + nombreClase + "'::'" + $ID1.text + "'";
	}
	else
	{	
		if (tablaTipos.getEsParametro(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo()))
			$sufijoCodigo = "arg " + tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getPosicionLocal();
		else
			$sufijoCodigo = "loc " + tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getPosicionLocal();
	}
	$maxstack = 2;

	// Guardamos la tabla de símbolos original que se utiliza en las expresiones de los corchetes.
	TablaSimbolos tablaSimbolosOriginal = tablaSimbolos;

	String codigoIndice = "";
}
(PUNTO
{
	// Comprobamos que el identificador sea un objeto.
	if (!tablaTipos.getEsObjeto(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo()))
		Error.Error16($ID1.line, $ID1.pos + 1, $ID1.text);

	// Como es una clase, el nuevo ámbito es el ámbito de la clase.
	tablaSimbolos = tablaTipos.getTablaSimbolos(tablaTipos.getTipoBase(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo()));
	ambitoGlobal = false;
}
ID2=ID
{
	// Se comprueba que el identificador esté declarado.
	if (tablaSimbolos.getSimbolo($ID2.text, ambitoGlobal) == null)
		Error.Error2($ID2.line, $ID2.pos + 1, $ID2.text);

	// Comprobamos que el atributo sea accesible (es decir, que es público, o que estamos en esa clase).
	if (!tablaTipos.getVisibilidad(tablaSimbolos.getSimbolo($ID2.text, ambitoGlobal).getTipo()) && !nombreClase.equals(tablaSimbolos.getNombreClase()))
		Error.Error2($ID2.line, $ID2.pos + 1, $ID2.text);

	// Comprobamos que sea un atributo y no un método.
	if (!tablaTipos.getEsAtributo(tablaSimbolos.getSimbolo($ID2.text, ambitoGlobal).getTipo()))
		Error.Error16($ID2.line, $ID2.pos + 1, $ID2.text);

	$tipoSimple = tablaTipos.getTipoSimpleString(tablaSimbolos.getSimbolo($ID2.text, ambitoGlobal).getTipo());
	esArray = tablaTipos.getEsArray(tablaSimbolos.getSimbolo($ID2.text, ambitoGlobal).getTipo());

	// Actualizamos el código de salida.
	$prefijoCodigo = $prefijoCodigo + "\nld" + $sufijoCodigo;
	$sufijoCodigo = "fld " + getTipoCIL($tipoSimple) + ((esArray) ? "[]" : "") + " '" + tablaSimbolos.getNombreClase() + "'::'" + $ID2.text + "'";

	$ID1 = $ID2;
	tipo = tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo();
}
)*
(
{
	// Se comprueba que la variable sea un array porque si estamos aquí, es que tiene corchetes.
	if (!tablaTipos.getEsArray(tablaSimbolos.getSimbolo($ID1.text, ambitoGlobal).getTipo()))
		Error.Error11($ID1.line, $ID1.pos + 1, $ID1.text);
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
expr[tablaSimbolosOriginal]
{
	// Comprobamos que el valor de la expresión es entero o real.
	if (!$expr.tipoSimple.equals("int") && !$expr.tipoSimple.equals("double"))
		Error.Error13($CORI.line, $CORI.pos + 1);

	// Se genera el código que calculará el índice en tiempo de ejecución.
	if (codigoIndice.equals(""))
	{
		codigoIndice = $expr.salida;
		codigoIndice += "\nconv.i4";
		$maxstack = 1 + $expr.maxstack;
	}
	else
	{
		codigoIndice += "\nldc.i4 " + tablaTipos.getDimension(tipo);
		codigoIndice += "\nmul";
		codigoIndice += $expr.salida;
		codigoIndice += "\nconv.i4";
		codigoIndice += "\nadd";
		$maxstack = max($maxstack, 2 + $expr.maxstack);
	}

	// Posible añadido: comprobar que el índice no sale de rango.
	// Hay problemas con las etiquetas porque si se duplica el código (por ejemplo, si pasa a formar parte de una expresión)
	// Es un problema similar al incremento dentro de los corchetes, que ya no nos permite duplicar $expr.salida indiscriminadamente.
	// También falta calcular el maxstack y comprobar que el índice no es negativo.
	// El código funciona, salvo esos problemas mencionados.
	/*int etiqueta = etiquetas++;
	codigoIndice += $expr.salida;
	codigoIndice += "\nconv.i4";
	codigoIndice += "\nldc.i4 " + tablaTipos.getDimension(tipo);
	codigoIndice += "\nblt L" + etiqueta;
	codigoIndice += "\nldstr \"Se ha salido del indice.\"";
	codigoIndice += "\ncall void [mscorlib]System.Console::WriteLine(string)";
	codigoIndice += "\nthrow";
	codigoIndice += "\nL" + etiqueta + ":";
	$maxstack = 999;*/
}
CORD)*
{
	// Se comprueba que no faltan índices.
	if (tablaTipos.getEsArray(tipo))
	{
		// Hay menos índices de los necesarios porque sigue diciendo que es un array y ya no vamos a encontrar más.
		Error.Error9($ID1.line, $ID1.pos + 1, $ID1.text);
	}

	// Si es un array, hay que hacer una última actualización de código.
	if (!codigoIndice.equals(""))
	{
		// Actualizamos el código.
		$prefijoCodigo = $prefijoCodigo + "\nld" + $sufijoCodigo + codigoIndice;

		if ($tipoSimple.equals("double"))
			$sufijoCodigo = "elem.r8";
		else
			$sufijoCodigo = "elem.i4";
	}
}
;

//##############################################################################

/******************************************************************************
Analizador léxico
******************************************************************************/

CLASS: 'class';
VOID: 'void';
MAIN: 'Main';
INT: 'int';
DOUBLE: 'double';
BOOL: 'bool';
PUBLIC: 'public';
PRIVATE: 'private';
STATIC: 'static';
RETURN: 'return';
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
