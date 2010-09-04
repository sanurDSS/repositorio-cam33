#!/bin/sh

# Procesadores de Lenguaje

# Sin parámetros, evalúa la práctica con todos los ficheros de prueba (*.fnt) 
# del directorio actual.
#
# Si se indica un parámetro, como "./corrige.sh 050", evalúa la práctica con
# el fichero 050.fnt.

# Ubicación de ANTLR:
ANTLR_CLASSPATH_AUTOCORRECTOR=antlrworks-1.3.1.jar

# JVM 1.6:
java=java

# Compilador de Java 1.6:
javac=javac

# Nombre del fichero con la especificación de ANTLR:
nom=plp4

ensamb=ilasm2
maqvirt=mono

# Comprueba si existe el fichero para ANTLR:

if test ! -e $nom.g
then
  echo "No encuentro $nom.g"
  exit 1
fi

# Identifica el lenguaje objetivo (por ahora solo Java):

if test -e Main.java
then
   TARGET=java
else
  echo "No encuentro Main.java"
  exit 1            
fi

# Procesa la especificación con ANTLR:

rm -f ${nom}Parser.$TARGET ${nom}Lexer.$TARGET

$java -classpath $ANTLR_CLASSPATH_AUTOCORRECTOR org.antlr.Tool $nom.g

# ANTLR no devuelve un status distinto de 0 si hay algún error :-(

if test $? -ne 0
then
  echo "ANTLR no funciona correctamente"
  exit 1
fi

if test ! -e ${nom}Parser.$TARGET
then
  echo "Error en la especificacion de ANTLR"
  exit 1
elif test ! -e ${nom}Lexer.$TARGET
then
  echo "Error en la especificacion de ANTLR"
  exit 1
fi

# Compila todas las clases:

if test $TARGET = "java"
then
  $javac -classpath $ANTLR_CLASSPATH_AUTOCORRECTOR:. *.java
  if test $? -ne 0
  then
     echo "No compila"
     exit 1
  fi  
fi

# Ficheros a corregir:

mostrardiff="no"
if test ! -z $1
then  
  archivosentrada=$1.fnt
  mostrardiff="si"
else
  archivosentrada=$(ls *.fnt) 
fi

# Ficheros a corregir:
for i in $archivosentrada 
do
  n=$(basename $i .fnt)  
  rm -f $n.il.tmp $n.err.tmp $n.tmp.exe	
  if test $TARGET = "java"
  then
    $java  -classpath $ANTLR_CLASSPATH_AUTOCORRECTOR:. Main $n.fnt >$n.il.tmp 2> $n.err.tmp
  fi
      
  if [ -s $n.il.tmp ]
  then
   $ensamb -output:$n.tmp.exe $n.il.tmp >$n.erase 2>&1
   rm $n.erase
   $maqvirt $n.tmp.exe <$n.ent  > $n.sal.tmp
  else
   rm -f $n.sal.tmp
   touch $n.sal.tmp
  fi

  if test $mostrardiff = "si"
  then
    diff -B $n.sal $n.sal.tmp
  fi
  diff -B --brief $n.sal $n.sal.tmp >/dev/null
  if test $? -eq 0 
  then
    if test $mostrardiff = "si"
    then
      diff -B $n.err $n.err.tmp
    fi
    diff -B --brief $n.err $n.err.tmp >/dev/null
    if test $? -eq 0
    then
      echo "$n: ok"  
    else 
      echo "$n: #error#"   
    fi  
  else 
   echo "$n: #Error#"   
  fi
done

exit 0

