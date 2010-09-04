#! /bin/bash



clear;
echo -e "\e[45m\e[1m\e[37m                                                                                \e[0m";
echo -e "\e[45m\e[1m\e[37m                                                                                \e[0m";
echo -e "\e[45m\e[1m\e[37m                       PL - Pruebas de la práctica 2                            \e[0m";
echo -e "\e[45m\e[1m\e[37m                                                                                \e[0m";
echo -e "\e[45m\e[1m\e[37m                                                                                \e[0m";
echo;







echo -e "\e[44m\e[1m\e[37m                                                                                \e[0m";
echo -e "\e[44m\e[1m\e[37m  Generando y compilando el traductor                                           \e[0m";
echo -e "\e[44m\e[1m\e[37m                                                                                \e[0m";
echo;

make clean;
make;





echo;
echo;
echo -e "\e[44m\e[1m\e[37m                                                                                \e[0m";
echo -e "\e[44m\e[1m\e[37m  Ejecutando batería de pruebas                                                 \e[0m";
echo -e "\e[44m\e[1m\e[37m                                                                                \e[0m";
echo;


let errores=0;
let correctas=0;
let total=0;
pruebas=$(ls pruebas/*.ent);
for i in $pruebas
{
	let total=$total+1;

	salida=$(echo $i | cut -d '.' -f 1);
	salida=$salida".sal";

	$(rm -f salida);
	$(rm -f diferencias);

	$(java -classpath $ANTLR_CLASSPATH:. Main $i 1>salida 2>&1);
	#sed -n -e "/^Error .*$/p" entrada.txt
	diff salida $salida -b 1>diferencias 2>&1;

	if ([ $? == 0 ]) then
		let correctas=$correctas+1;
		echo -e "Comprobando $i: \e[1m\e[32m[OK]\e[0m";
	else
		let errores=$errores+1;
		echo -e "Comprobando $i: \e[1m\e[31m[ERROR]\e[0m";
		cat diferencias;
		echo;
	fi
}

$(rm -f salida);
$(rm -f diferencias);

#echo ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>";
#java -classpath $ANTLR_CLASSPATH:. Main fichero.txt;
#echo "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<";



echo;
echo;
echo -e "\e[44m\e[1m\e[37m                                                                                \e[0m";
echo -e "\e[44m\e[1m\e[37m  Resumen del autocorrector                                                     \e[0m";
echo -e "\e[44m\e[1m\e[37m                                                                                \e[0m";
echo;

if ([ $errores -gt 0 ]) then
	echo -e "Errores: \e[0;31m$errores\e[0m de $total pruebas";
else
	echo -e "Errores: \e[0;32m$errores\e[0m de $total pruebas";
fi









echo;
echo -e "\e[45m\e[1m\e[37m                                                                                \e[0m";
