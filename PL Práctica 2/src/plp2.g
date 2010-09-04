/* Embellece código que es un subconjunto de Java. Usa una gramática EBNF. */
grammar plp2;

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

// S -> C* EOF
s:
{
	TablaSimbolos tablaSimbolos = new TablaSimbolos();
}
(c[tablaSimbolos])* EOF
{
	System.out.print(tablaSimbolos.formatear(0));
};

// C -> class id llavei D llaved
c [TablaSimbolos tablaSimbolos]: CLASS ID
{
	Clase clase = new Clase();
	clase.identificador = new TokenP1();
	clase.identificador.lexema = $ID.text;
	clase.identificador.fila = $ID.line;
	clase.identificador.columna = 1 + $ID.pos;
	clase.identificador.tipo = TokenP1.IDENTIFICADOR;
	clase.miembros = new TablaSimbolos($tablaSimbolos);
	$tablaSimbolos.comprobarSimbolo(clase);
	$tablaSimbolos.anadirSimbolo(clase);
}
LLAVEI d[clase.miembros] LLAVED;

// D -> (V | M )*
d [TablaSimbolos tablaSimbolos]: (v[$tablaSimbolos] | m[$tablaSimbolos])*;

// V -> double id pyc
v [TablaSimbolos tablaSimbolos]: DOUBLE ID
{
	Declaracion declaracion = new Declaracion();
	declaracion.identificador = new TokenP1();
	declaracion.identificador.lexema = $ID.text;
	declaracion.identificador.fila = $ID.line;
	declaracion.identificador.columna = 1 + $ID.pos;
	declaracion.identificador.tipo = TokenP1.IDENTIFICADOR;
	$tablaSimbolos.comprobarSimbolo(declaracion);
	$tablaSimbolos.anadirSimbolo(declaracion);
}
PYC;

// M -> void id pari pard llavei Decl Cuerpo llaved
m [TablaSimbolos tablaSimbolos]: VOID ID
{
	Metodo metodo = new Metodo();
	metodo.identificador = new TokenP1();
	metodo.identificador.lexema = $ID.text;
	metodo.identificador.fila = $ID.line;
	metodo.identificador.columna = 1 + $ID.pos;
	metodo.identificador.tipo = TokenP1.IDENTIFICADOR;
	metodo.cuerpo = new TablaSimbolos($tablaSimbolos);
	$tablaSimbolos.comprobarSimbolo(metodo);
	$tablaSimbolos.anadirSimbolo(metodo);
}
PARI PARD LLAVEI decl[metodo.cuerpo] cuerpo[metodo.cuerpo] LLAVED;

// Decl -> V*
decl [TablaSimbolos tablaSimbolos]: (v[$tablaSimbolos])*;

// Cuerpo -> Instr*
cuerpo [TablaSimbolos tablaSimbolos]: (instr[$tablaSimbolos])*;

// Instr -> id asig Factor pyc
instr [TablaSimbolos tablaSimbolos]: ID
{
	Instruccion instruccion = new Instruccion();
	instruccion.identificador = new TokenP1();
	instruccion.identificador.lexema = $ID.text;
	instruccion.identificador.fila = $ID.line;
	instruccion.identificador.columna = 1 + $ID.pos;
	instruccion.identificador.tipo = TokenP1.IDENTIFICADOR;
	$tablaSimbolos.comprobarSimbolo(instruccion);
}
ASIG factor[instruccion]
{
	$tablaSimbolos.comprobarSimbolo(instruccion);
	$tablaSimbolos.anadirSimbolo(instruccion);
}
PYC;

// Factor -> real | id
factor [Instruccion instruccion]: REAL
{
	$instruccion.factor = new TokenP1();
	$instruccion.factor.lexema = $REAL.text;
	$instruccion.factor.fila = $REAL.line;
	$instruccion.factor.columna = 1 + $REAL.pos;
	$instruccion.factor.tipo = TokenP1.REAL;
}
| ID
{
	$instruccion.factor = new TokenP1();
	$instruccion.factor.lexema = $ID.text;
	$instruccion.factor.fila = $ID.line;
	$instruccion.factor.columna = 1 + $ID.pos;
	$instruccion.factor.tipo = TokenP1.IDENTIFICADOR;
};


/******************************************************************************
Analizador léxico
******************************************************************************/

PARI: '(';
PARD: ')';
PYC: ';';
ASIG: '=';
CLASS: 'class';
DOUBLE: 'double';
VOID: 'void';
LLAVEI: '{';
LLAVED: '}';
ID: ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9')*;
REAL: ('0'..'9')+'.'('0'..'9')+;

SEPARADOR: ('\r'? '\n'|' '|'\t')+ {skip();};
COMENTARIO: '/*' .* '*/' {skip();};
