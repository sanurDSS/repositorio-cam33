#! /bin/bash

# Pruebas de entrada y salida.
echo "-------------------------------------------------";
echo "        PL - Pruebas de la práctica 1";
echo "-------------------------------------------------";
echo

$(javac *.java)

let errores=0;
let correctas=0;
let total=0;
pruebas=$(ls pruebas/pruebas*.ent);
for i in $pruebas
{
	let total=$total+1;

	# El fichero ejecutable es $i.
	# El fichero con la salida del ejecutable es $i, quitandole el .ent y añadiendole .sal.
	salida="";
	salida=$(echo $i | cut -d '.' -f 1);
	salida=$salida".sal";

	echo "Comprobando $i $salida";

	$(rm -f salida);
	$(rm -f aux);

	$(java Main $i 2>>salida 1>>salida);
	diff salida $salida -b;

	if ([ $? == 0 ]) then
		let correctas=$correctas+1;
		echo -e "\e[0;32m[OK]\e[0m";
	else
		let errores=$errores+1;
		echo -e "\e[0;31m[ERROR]\e[0m";
	fi
	echo;
}

if ([ $errores -gt 0 ]) then
	echo -e "Errores: \e[0;31m$errores\e[0m de $total pruebas";
else
	echo -e "Errores: \e[0;32m$errores\e[0m de $total pruebas";
fi

$(rm -f salida);
$(rm -f aux);

echo;
echo "-------------------------------------------------";
echo "                     FIN";
echo "-------------------------------------------------";
echo;


