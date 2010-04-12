<?php
include_once "base.php";
baseSuperior("Resultados de la búsqueda");
?>
					<div id="resultados">
						<h3><span>Resultados de la búsqueda</span></h3>
						<div>
<?php

$titulo = $_GET["titulo"];
$dia = $_GET["dia"];
$mes = $_GET["mes"];
$ano = $_GET["ano"];
$fecha = (checkdate($mes, $dia, $ano)) ? "$ano-".rellenar($mes, "0", 2)."-".rellenar($dia, "0", 2)."" : "";
$segmento = $_GET["segmento"];
$antes = ($segmento == "antes") ? true : false;
$pais = $_GET["pais"];



$fotos = ENFoto::obtenerBusqueda($titulo, $fecha, $antes, $pais);
?>
							<p>
								Se han encontrado <?php echo count($fotos); ?> resultados que contienen la secuencia <em>"<?php echo $titulo; ?>"</em>.
<?php

// Calculamos la cadena según si se especificó el país.
if ($pais != null)
{
	if (is_numeric($pais))
	{
		$pais = ENPais::obtenerPorId($pais);
		if ($pais != null)
			$pais = $pais->getNombre();
		else
			$pais = "";
	}
	else
		$pais = "";
}
else
	$pais = "";

// Comprobamos si se introdujo una fecha.
if ($segmento)
{
	$segmento = ($segmento) ? "antes" : "después";
	switch ($mes)
	{
		case 1: $mes = "enero"; break;
		case 2: $mes = "febrero"; break;
		case 3: $mes = "marzo"; break;
		case 4: $mes = "abril"; break;
		case 5: $mes = "mayo"; break;
		case 6: $mes = "junio"; break;
		case 7: $mes = "julio"; break;
		case 8: $mes = "agosto"; break;
		case 9: $mes = "septiembre"; break;
		case 10: $mes = "octubre"; break;
		case 11: $mes = "noviembre"; break;
		case 12: $mes = "diciembre"; break;
	}
}

if ($pais != "" && fecha != "")
{
	echo "								<br />\n";
	echo "								($pais, $segmento del $dia de $mes del $ano)</p>\n";
}
else
{
	if ($pais != "")
	{
		echo "								<br />\n";
		echo "								($pais)</p>\n";
	}
	else
	{
		if ($fecha != "")
		{
			echo "								<br />\n";
			echo "								($segmento del $dia de $mes del $ano)</p>\n";
		}
	}
}

?>
							</p>
<?php
// Mostramos los resultados si hay al menos 1.
if (count($fotos) > 0)
{
?>
							<table id="tablaresultados">
								<tr class="cabecera">
									<td class="miniatura"><a href="javascript: ordenarTabla('tablaresultados', 0);">Miniatura</a></td>
									<td class="titulo"><a href="javascript: ordenarTabla('tablaresultados', 1);">Título</a></td>
									<td class="fecha"><a href="javascript: ordenarTabla('tablaresultados', 2);">Fecha</a></td>
									<td class="pais"><a href="javascript: ordenarTabla('tablaresultados', 3);">País</a></td>
								</tr>


<?php
	foreach ($fotos as $i)
	{
?>
								<tr class="foto">
									<td class="miniatura"><a href="foto.jpg"><img src="miniatura.jpg" alt="<?php echo $i->getTitulo(); ?>" /></a></td>
									<td class="titulo"><a href="foto.php?id=<?php echo $i->getId(); ?>"><?php echo $i->getTitulo(); ?></a></td>
									<td class="fecha"><?php echo cambiarFormatoFecha($i->getFecha()); ?></td>
									<td class="pais"><?php echo $i->getPais(); ?></td>
								</tr>
<?php
	}
}
else
{
	
}
?>
							</table>
						</div>
						<!--<div id="paginacion">
							<a href="#">Anterior</a>
							<a href="#">Siguiente</a>
						</div>-->
					</div>
<?php
baseInferior();
?>