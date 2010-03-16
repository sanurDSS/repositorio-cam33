function cambiarEstiloSimple(titulo)
{
	var estilos = document.getElementsByTagName("link");
	for(var i = 0; i < estilos.length; i++)
	{
		if(estilos[i].getAttribute("title") == titulo)
			estilos[i].disabled = false;
		else
			estilos[i].disabled = true;
	}
}


function cambiarEstilo(titulo)
{
	var arrayLink = document.getElementsByTagName("link");
	for(var i = 0; i < arrayLink.length; i++)
	{
		// Sólo aquellas etiquetas link que hacen referencia a un estilo
		// y que no sea para impresión
		if(arrayLink[i].getAttribute("rel") != null &&
			arrayLink[i].getAttribute("rel").indexOf("stylesheet") != -1 &&
			arrayLink[i].getAttribute("media") != "print")
		{
			// Si tiene título es un estilo preferido o alternativo,
			// si no tiene título es un estilo
			// predeterminado y siempre tiene que utilizarse
			if(arrayLink[i].getAttribute("title") != null &&
				arrayLink[i].getAttribute("title").length > 0)
			{
				if(arrayLink[i].getAttribute("title") == titulo)
				{
					arrayLink[i].disabled = false;
					setCookie("estilo", titulo, 15);
				}
				else
					arrayLink[i].disabled = true;
			}
		}
	}
}
