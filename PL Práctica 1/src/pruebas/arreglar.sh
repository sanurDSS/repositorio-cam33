#! /bin/bash

# Pruebas de entrada y salida.
echo "-------------------------------------------------";
echo "        PL - Pruebas de la práctica 1";
echo "-------------------------------------------------";
echo

# Guarda el contenido de los .err en los .sal.
pruebas=$(ls *.err);
for i in $pruebas
{
	salida="";
	salida=$(echo $i | cut -d '.' -f 1);
	salida=$salida".sal";
	
	cat $i >> $salida;	
}

# Borra los .err.
rm -f *.err;

# Renombra los .fnt a .ent.
pruebas=$(ls *.fnt);
for i in $pruebas
{
	salida="";
	salida=$(echo $i | cut -d '.' -f 1);
	salida=$salida".ent";
	
	mv $i $salida;
}

# Añade "pruebas-" a los .ent.
pruebas=$(ls *.ent);
for i in $pruebas
{
	salida="pruebas-"$i;
	mv $i $salida;
}

# Añade "pruebas-" a los .sal.
pruebas=$(ls *.sal);
for i in $pruebas
{
	salida="pruebas-"$i;
	mv $i $salida;
}

echo;
echo "-------------------------------------------------";
echo "                     FIN";
echo "-------------------------------------------------";
echo;


