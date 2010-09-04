import java.io.RandomAccessFile;

public class AnalizadorSintacticoSemanticoTraductor
{
	/**
	 * Representa el siguiente token que tiene que analizarse.
	 * Cada regla que se invoque comprobará que este token (también llamado "preanálisis")
	 * pertenece a su conjunto de predicción.
	 */
	private Token token;

	/**
	 * Instancia del analizador léxico.
	 * En una variable miembro para no enviar la referencia en las sucesivas reglas.
	 */
	private AnalizadorLexico analizadorLexico;

	/**
	 * Constructor por defecto del analizador sintáctico.
	 * @param nomFichero Nombre del fichero de texto que se va a analizar.
	 */
	public AnalizadorSintacticoSemanticoTraductor(String nomFichero)
	{
		try
		{
			// Abre el fichero para lectura y crea una instancia del analizador léxico.
			RandomAccessFile flujoEntrada = new RandomAccessFile(nomFichero, "r");
			analizadorLexico = new AnalizadorLexico(flujoEntrada);

			// Obtenemos el primer preanálisis.
			token = analizadorLexico.siguienteToken();

			// Se crea la tabla de símbolos raíz y se realiza la primera llamada a la regla inicial S.
			TablaSimbolos tablaSimbolos = new TablaSimbolos();
			S(tablaSimbolos);

			// Si no ha ocurrido un error, se cierra el fichero de entrada y se imprime por pantalla el código formateado.
			flujoEntrada.close();			
			System.out.print(tablaSimbolos.formatear(0));
		}
		catch (Exception e)
		{
			Error.Error0(e.getMessage());
		}
	}

	/**
	 * Método que representa la regla inicial S.
	 * S → C S | εD → E D | ε
	 * @param tablaSimbolos Tabla de símbolos donde se insertarán los sucesivos símbolos que se encuentren.
	 */
	private void S(TablaSimbolos tablaSimbolos)
	{
		switch (token.tipo)
		{
			case Token.CLASS:
				C(tablaSimbolos);
				S(tablaSimbolos);
				break;
				
			case Token.EOF:
				break;

			default:
				Error.Error3(token, Token.subcadenas[Token.CLASS] + " " + Token.subcadenas[Token.EOF]);
				break;
		}
	}

	/**
	 * Método que representa la regla C.
	 * C → class id llavei D llaved
	 * @param tablaSimbolos Tabla de símbolos donde se insertarán los sucesivos símbolos que se encuentren.
	 */
	private void C(TablaSimbolos tablaSimbolos)
	{
		Clase clase = new Clase();
		emparejar(Token.CLASS);
		clase.identificador = emparejar(Token.IDENTIFICADOR);
		tablaSimbolos.comprobarSimbolo(clase);
		tablaSimbolos.anadirSimbolo(clase);
		emparejar(Token.LLAVE_IZQUIERDA);
		clase.miembros = new TablaSimbolos(tablaSimbolos);
		D(clase.miembros);
		emparejar(Token.LLAVE_DERECHA);
	}

	/**
	 * Método que representa la regla D.
	 * D → E D | ε
	 * @param tablaSimbolos Tabla de símbolos donde se insertarán los sucesivos símbolos que se encuentren.
	 */
	private void D(TablaSimbolos tablaSimbolos)
	{
		switch (token.tipo)
		{
			case Token.DOUBLE:
			case Token.VOID:
				E(tablaSimbolos);
				D(tablaSimbolos);
				break;

			case Token.LLAVE_DERECHA:
				break;

			case Token.EOF:
				Error.Error4(Token.subcadenas[Token.DOUBLE] + " " + Token.subcadenas[Token.VOID] + " " + Token.subcadenas[Token.LLAVE_DERECHA]);
				break;

			default:
				Error.Error3(token, Token.subcadenas[Token.DOUBLE] + " " + Token.subcadenas[Token.VOID] + " " + Token.subcadenas[Token.LLAVE_DERECHA]);
				break;
		}
	}

	/**
	 * Método que representa la regla E.
	 * E → V | M
	 * @param tablaSimbolos Tabla de símbolos donde se insertarán los sucesivos símbolos que se encuentren.
	 */
	private void E(TablaSimbolos tablaSimbolos)
	{
		switch (token.tipo)
		{
			case Token.DOUBLE:
				V(tablaSimbolos);
				break;
			case Token.VOID:
				M(tablaSimbolos);
				break;
				
			case Token.EOF:
				Error.Error4(Token.subcadenas[Token.DOUBLE] + " " + Token.subcadenas[Token.VOID]);
				break;

			default:
				Error.Error3(token, Token.subcadenas[Token.DOUBLE] + " " + Token.subcadenas[Token.VOID]);
				break;
		}
	}

	/**
	 * Método que representa la regla V.
	 * V → double id pyc
	 * @param tablaSimbolos Tabla de símbolos donde se insertarán los sucesivos símbolos que se encuentren.
	 */
	private void V(TablaSimbolos tablaSimbolos)
	{
		Declaracion declaracion = new Declaracion();
		emparejar(Token.DOUBLE);
		declaracion.identificador = emparejar(Token.IDENTIFICADOR);
		tablaSimbolos.comprobarSimbolo(declaracion);
		emparejar(Token.PUNTO_Y_COMA);
		tablaSimbolos.anadirSimbolo(declaracion);
	}

	/**
	 * Método que representa la regla M.
	 * M → void id pari pard llavei Decl Cuerpo llaved
	 * @param tablaSimbolos Tabla de símbolos donde se insertarán los sucesivos símbolos que se encuentren.
	 */
	private void M(TablaSimbolos tablaSimbolos)
	{
		Metodo metodo = new Metodo();
		emparejar(Token.VOID);
		metodo.identificador = emparejar(Token.IDENTIFICADOR);
		tablaSimbolos.comprobarSimbolo(metodo);
		tablaSimbolos.anadirSimbolo(metodo);
		emparejar(Token.PARENTESIS_IZQUIERDO);
		emparejar(Token.PARENTESIS_DERECHO);
		emparejar(Token.LLAVE_IZQUIERDA);
		metodo.cuerpo = new TablaSimbolos(tablaSimbolos);
		Decl(metodo.cuerpo);
		Cuerpo(metodo.cuerpo);
		emparejar(Token.LLAVE_DERECHA);
	}

	/**
	 * Método que representa la regla Decl.
	 * Decl → V Decl | ε
	 * @param tablaSimbolos Tabla de símbolos donde se insertarán los sucesivos símbolos que se encuentren.
	 */
	private void Decl(TablaSimbolos tablaSimbolos)
	{
		switch (token.tipo)
		{
			case Token.DOUBLE:
				V(tablaSimbolos);
				Decl(tablaSimbolos);
				break;
				
			case Token.IDENTIFICADOR:
			case Token.LLAVE_DERECHA:
				break;

			case Token.EOF:
				Error.Error4(Token.subcadenas[Token.DOUBLE] + " " + Token.subcadenas[Token.LLAVE_DERECHA]  + " " + Token.subcadenas[Token.IDENTIFICADOR]);
				break;

			default:
				Error.Error3(token, Token.subcadenas[Token.DOUBLE] + " " + Token.subcadenas[Token.LLAVE_DERECHA]  + " " + Token.subcadenas[Token.IDENTIFICADOR]);
				break;
		}
	}

	/**
	 * Método que representa la regla Cuerpo.
	 * Cuerpo → Instr Cuerpo | ε
	 * @param tablaSimbolos Tabla de símbolos donde se insertarán los sucesivos símbolos que se encuentren.
	 */
	private void Cuerpo(TablaSimbolos tablaSimbolos)
	{
		switch (token.tipo)
		{
			case Token.IDENTIFICADOR:
				Instr(tablaSimbolos);
				Cuerpo(tablaSimbolos);
				break;

			case Token.LLAVE_DERECHA:
				break;

			case Token.EOF:
				Error.Error4(Token.subcadenas[Token.LLAVE_DERECHA] + " " + Token.subcadenas[Token.IDENTIFICADOR]);
				break;

			default:
				Error.Error3(token, Token.subcadenas[Token.LLAVE_DERECHA] + " " + Token.subcadenas[Token.IDENTIFICADOR]);
				break;
		}
	}

	/**
	 * Método que representa la regla Instr.
	 * Instr → id asig Factor pyc
	 * @param tablaSimbolos Tabla de símbolos donde se insertarán los sucesivos símbolos que se encuentren.
	 */
	private void Instr(TablaSimbolos tablaSimbolos)
	{
		Instruccion instruccion = new Instruccion();

		instruccion.identificador = emparejar(Token.IDENTIFICADOR);
		tablaSimbolos.comprobarSimbolo(instruccion);
		emparejar(Token.ASIGNACION);
		Factor(instruccion);
		tablaSimbolos.comprobarSimbolo(instruccion);
		emparejar(Token.PUNTO_Y_COMA);

		tablaSimbolos.anadirSimbolo(instruccion);
	}

	/**
	 * Método que representa la regla Factor.
	 * Factor → real | identificador
	 * @param instruccion Símbolo del tipo "instrucción" donde se insertará el factor encontrado.
	 */
	private void Factor(Instruccion instruccion)
	{
		switch (token.tipo)
		{
			case Token.REAL:
				instruccion.factor = emparejar(Token.REAL);
				break;

			case Token.IDENTIFICADOR:
				instruccion.factor = emparejar(Token.IDENTIFICADOR);
				break;

			case Token.EOF:
				Error.Error4(Token.subcadenas[Token.IDENTIFICADOR] + " " + Token.subcadenas[Token.REAL]);
				break;

			default:
				Error.Error3(token, Token.subcadenas[Token.IDENTIFICADOR] + " " + Token.subcadenas[Token.REAL]);
				break;
		}
	}

	/**
	 * Comprueba que el token pendiente de analizar se corresponde con el tipo de token que debería aparecer
	 * a continuación para que se reconociera correctamente el fichero de entrada.
	 * Si no existe correspondencia, se genera un error; si todo ha ido bien, se lee otro token de la entrada y queda
	 * pendiente de comprobar en el siguiente emparejamiento.
	 * @param tokenEsperado Tipo de token que se debería obtener desde el fichero de entrada.
	 * @return Si no se ha producido un error, devuelve el token que se ha comprobado.
	 */
	private Token emparejar(int tokenEsperado)
	{
		Token tokenActual = token;
		if (token.tipo == tokenEsperado)
		{
			try
			{
				token = analizadorLexico.siguienteToken();
			}
			catch (Exception e)
			{
				Error.Error0(e.getMessage());
			}
		}
		else
		{
			if (token.tipo == Token.EOF)
				Error.Error4(Token.subcadenas[tokenEsperado]);
			else
				Error.Error3(token, Token.subcadenas[tokenEsperado]);				
		}
		return tokenActual;
	}
}



/*
PRED(S → C S) = {class}
PRED(S → ε) = {$}
PRED(C → class id llavei D llaved) = {class}
PRED(D → E D) = {double, void}
PRED(D → ε) = {llaved}
PRED(E → V) = {double}
PRED(E → M) = {void}
PRED(V → double id pyc) = {double}
PRED(M → void id pari pard llavei Decl Cuerpo llaved) = {void}
PRED(Decl → V Decl) = {double}
PRED(Decl → ε) = {id, llaved}
PRED(Cuerpo → Instr Cuerpo) = {id}
PRED(Cuerpo → ε) = {llaved}
PRED(Instr → id asig Factor pyc) = {id}
PRED(Factor → real) = {real}
PRED(Factor → id) = {id}
*/



/*

void S()
{
	if(preanalisis.tipo == "class")
	{
		C();
		S();
	} else if(preanalisis.tipo == "EOF") {
		// E
	} else {
		Error sintactico, esperaba class o EOF
	}
}

void C()
{
	empareja("class");
	empareja("id");
	empareja("llaveid");
	D();
	empareja("llaved");
}

void D() {
	if(preanalisis.tipo == ejA)
		empareja("ejA");
	else if(preanalisis.tipo == ejB)
		empareja("ejB");
	else
		Error, esperaba ejA ejB
 }

*/












/*
---------------------------------------------------------------
Traductor
---------------------------------------------------------------

atributos y variables no usadas
const
espacios y tabuladores

estructura de metodos y mierdacas con todo lo leido
¿desde los S() C()?
Con una tabla de símbolos: ambitos de variables y variables leidas


class C1
{
	dobule x;
	void F()
	{
		double y;
		double x;
	}
	...
}

class C2
{
	double y;
}


Tabla de simbolos
------------------

___C1
______x
______F
_________y;
_________x;
___C2
______y

Clase Simbolo: mismo tipo de los tokens
Se envia la TablaSimbolo como atributo heredado en S(Ts)

Cuando se encuentra una llave, se crea un nuevo ambito, creando una nueva tabla de simbolos y pasando esa a los hijos.
Cuando se encuentre la llave de cierre, se finaliza este ambito, se introduce en el ambito heredado y fuera.

*/

/*
    S → C S {S.s := C.s || S.s}

    S → ε {S.s := ""}

    C → class id llavei D llaved {C.s := "class" || id.lexema || "{" || D.s || "}"}

    D → E D {D.s := E.s || D.s}

    D → ε {D.s := ""}

    E → V {E.s := M.s}

    E → M {E.s := M.s}

    V → double id pyc {V.s := "double" || id.lexema || ";"}

    M → void id pari pard llavei Decl Cuerpo llaved {M.s := "void" || id.lexema || "(){" || Decl.s || Cuerpo.s || "}"}

    Decl → V Decl {Decl.s := V.s || Decl.s}

    Decl → ε {Decl.s := ""}

    Cuerpo → Instr Cuerpo {Cuerpo.s := Instr.s || Cuerpo.s}

    Cuerpo → ε {Cuerpo.s := ""}

    Instr → id asig Factor pyc {Instr.s := Factor.lexema || "=" || Factor.s || ";"}

    Factor → real {Factor.s := Factor.lexema}

    Factor → id {Factor.s := Factor.lexema}

    Falta indicar los espacios, saltos de línea, etc.
*/


// TODO quitar couts de depuracion, ver por que no comprueba que existe un metodo "f".
// No lo encuentra porque "f" todavía no se ha insertado en la tablaSimbolos-padre. Asi que "cuerpo" debe guardar un puntero a su simbolo-padre de alguna forma.
// Es decir, esta saltando desde el cuerpo de f, al cuerpo de la clase, pero sin pasar por "f" porque todavia no esta en el cuerpo de la clase.
// Posibles soluciones: introducir "f" en el cuerpo aunque todavia no se haya evaluado
//                      guardar un puntero al Simbolo propietario del "cuerpo".

