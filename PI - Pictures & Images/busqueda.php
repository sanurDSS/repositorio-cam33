<?php
include_once "base.php";
baseSuperior("Resultados de la búsqueda");
?>
					<div id="resultados">
						<h3><span>Resultados de la búsqueda</span></h3>
						<div>
							<table id="tablaresultados">
								<tr class="cabecera">
									<td class="miniatura"><a href="javascript: ordenarTabla('tablaresultados', 0);">Miniatura</a></td>
									<td class="titulo"><a href="javascript: ordenarTabla('tablaresultados', 1);">Título</a></td>
									<td class="fecha"><a href="javascript: ordenarTabla('tablaresultados', 2);">Fecha</a></td>
									<td class="pais"><a href="javascript: ordenarTabla('tablaresultados', 3);">País</a></td>
								</tr>
<?php
$paises = array("España", "Francia", "Venezuela", "Congo", "China", "Japón", "Canadá", "Bolivia", "Corea del Sur");
$fecha = new DateTime();
for ($i = 0; $i < 5; $i++)
{
	$fecha->setDate(rand(2000, 2009), rand(2, 11), rand(2, 27));
?>
								<tr class="foto">
									<td class="miniatura"><a href="foto.jpg"><img src="miniatura.jpg" alt="Fotillo" /></a></td>
									<td class="titulo"><a href="foto.php">Título <?php echo rand(100, 999); ?></a></td>
									<td class="fecha"><?php echo $fecha->format("d/m/Y"); ?></td>
									<td class="pais"><?php echo $paises[rand(0, count($paises) - 1)]; ?></td>
								</tr>
<?php
}
?>
							</table>
						</div>
						<div id="paginacion">
							<a href="#">Anterior</a>
							<a href="#">Siguiente</a>
						</div>
<?php
$usuarios = ENUsuario::obtenerTodos();
foreach ($usuarios as $i)
{
?>
						<p><?php echo $i->toString(); ?></p>
<?php
}
?>
						<hr />
<?php
$albumes = ENAlbum::obtenerTodos();
foreach ($albumes as $i)
{
?>
						<p><?php echo $i->toString(); ?></p>
<?php
}


/*$nuevo = new ENAlbum;
$nuevo->setTitulo("otro album mas que falta filtrarCadena");
$nuevo->setDescripcion("creado desde la EN");
$nuevo->setFecha("2009/10/25");
$nuevo->setPais("Francia");
$nuevo->setUsuario("antonio");
$nuevo->guardar();*/

?>
						<hr />
<?php
$fotos = ENFoto::obtenerTodos("antonio");
foreach ($fotos as $i)
{
?>
						<p><?php echo $i->toString(); ?></p>
<?php
}

$fotico = ENFoto::obtenerPorId(3);
echo $fotico->toString();

$nueva = new ENFoto;
$nueva->setTitulo("me cago en cr.9");
$nueva->setDescripcion("polladongaaa...");
$nueva->setFecha("2007/06/19");
$nueva->setPais("Chile");
$nueva->setIdAlbum(3);
$nueva->guardar();
?>
					</div>
<?php
baseInferior();
?>