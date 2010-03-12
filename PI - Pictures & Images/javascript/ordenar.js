var orden = false;
function ordenarTabla(idtabla, columna)
{
	// Variables locales.
	var tabla = document.getElementById(idtabla);
	var filas = tabla.getElementsByTagName("TR");
	var filasAux = new Array();

	// Recorremos las filas de la tabla para duplicarlas (la cabecera no).
	for (var i = 1; i < filas.length; i++)
		filasAux.push(filas[i].cloneNode(true));

	// Ordenamos las filas según la columna indicada.
	filasAux = burbuja(filasAux, columna);

	// Eliminamos el contenido de la tabla (excepto la cabecera).
	while (tabla.getElementsByTagName("TBODY")[0].firstChild.nextSibling != null)
		tabla.getElementsByTagName("TBODY")[0].removeChild(tabla.getElementsByTagName("TBODY")[0].firstChild.nextSibling);

	// Añadimos ahora las filas ordenadas anteriormente.
	for (var j = 0; j < filasAux.length; j++)
		tabla.getElementsByTagName("TBODY")[0].appendChild(filasAux[j]);

	// Eliminamos cambiamos el estilo de la columna para que se muestre "ordenada".
	var columnas = filas[0].getElementsByTagName("TD");
	for (var k = 0; k < columnas.length; k++)
		columnas[k].className = "";
	columnas[columna].className = (orden) ? "ascendente" : "descendente";

	// Alternamos el orden en cada petición de ordenación.
	orden = !orden;
}

function burbuja(filas, columna)
{
	for (var i = 0; i < filas.length - 1; i++)
		for (var j = i + 1; j < filas.length; j++)
			if (comparar(filas[i], filas[j], columna, orden))
			{
				var aux = filas[i];
				filas[i] = filas[j];
				filas[j] = aux;
			}
	return filas;
}

function comparar(fila1, fila2, columna, orden)
{
	// Extraemos las cadenas a comparar.
	var cmp1 = fila1.getElementsByTagName("TD")[columna].textContent;
	var cmp2 = fila2.getElementsByTagName("TD")[columna].textContent;

	// Comprobamos si lo que hay que comparar son fechas.
	var fecha1 = Date.parse(cmp1);
	var fecha2 = Date.parse(cmp2);
	if (!isNaN(fecha1) || !isNaN(fecha2))
	{
		cmp1 = fecha1;
		cmp2 = fecha2;
	}

	// Comparamos según un orden ascendente o descendente.
	if (orden)
		return (cmp1 > cmp2) ? true : false;
	else
		return (cmp1 < cmp2) ? true : false;
}
