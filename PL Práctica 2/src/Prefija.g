grammar Prefija; 
 
/* Traduce con ANTLR un fichero de expresiones infijas separadas por punto  
   y coma a notación prefija. Usa una gramática EBNF. 
*/  
 
@header { 
import java.lang.String; 
} 
 
@rulecatch { 
  catch (RecognitionException re) { 
    reportError(re); 
    System.exit(1); 
  } 
} 
 
 
/* Analizador sintáctico: */ 
 
prog:   (stat ';')+ ; 
                 
stat:   expr {System.out.println($expr.value);} 
    ; 
 
expr returns [String value] 
    :   e1=multExpr {$value = $e1.value;} 
        (   '+' e2=multExpr {$value = "suma(" + $value + "," + $e2.value + ")";} 
        |   '-' e2=multExpr {$value = "resta(" + $value + "," + $e2.value + ")";} 
        )* 
    ; 
 
multExpr returns [String value] 
    :   e1=atom {$value = $e1.value;} ('*' e2=atom {$value = "mult(" + $value + "," + $e2.value + ")";})* 
    ;  
 
atom returns [String value] 
    :   INT {$value = $INT.text;} 
    |   ID {$value = $ID.text;} 
    |   '(' e=expr ')' {$value = $e.value;} 
    ; 
 
 
/* Analizador léxico: */ 
 
ID      :    ('a'..'z'|'A'..'Z')+ ; 
INT     :    ('0'..'9')+ ; 
NEWLINE    :    ('\r'? '\n'|' '|'\t')+  {skip();}  
    ; 
COMENTARIO 
    :    '/*' .* '*/' {skip();} 
    ; 
COMENTARIO_LINEA 
    : '//' ~('\n'|'\r')* '\r'? '\n' {skip();} 
    ; 
 
