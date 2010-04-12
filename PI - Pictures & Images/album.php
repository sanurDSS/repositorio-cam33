<?php
include_once "base.php";

// Extraemos el álbum desde la base de datos.
$id_album = $_GET["id"];
$album = ENAlbum::obtenerPorId($id_album);
$fotos = array();
if ($album != null)
{
	$fotos = array_reverse($album->getFotos());
}
else
{
	header("location: index.php?aviso=No se puede ver el álbum porque no existe en la base de datos.");
	exit();
}

$usuario = ENUsuario::obtenerPorNombre($album->getUsuario());

$propio = false;
if (isset($_SESSION["usuario"]))
{
	if (unserialize($_SESSION["usuario"])->getNombre() == $usuario->getNombre())
	{
		$propio = true;
	}
}

baseSuperior("Álbum #$id_album");
?>
					<div id="album">
						<h3><span>Álbum: <?php echo $album->getTitulo(); ?></span></h3>
						<table>
							<tr>
								<td class="columna1">Título:</td>
								<td class="columna2"><?php echo $album->getTitulo(); ?></td>
							</tr>
							<tr>
								<td class="columna1">Descripción:</td>
								<td class="columna2"><?php echo $album->getDescripcion(); ?>.</td>
							</tr>
							<tr>
								<td class="columna1">Fecha:</td>
								<td class="columna2"><?php echo cambiarFormatoFecha($album->getFecha()); ?></td>
							</tr>
							<tr>
								<td class="columna1">País:</td>
								<td class="columna2"><?php echo $album->getPais(); ?></td>
							</tr>
							<tr>
								<td class="columna1">Usuario:</td>
								<td class="columna2"><a href="perfil.php?id=<?php echo $usuario->getId(); ?>"><?php echo $usuario->getNombre(); ?></a></td>
							</tr>
							<tr>
								<td class="columna1">Cantidad de fotos:</td>
								<td class="columna2" id="cantidad"><?php echo count($fotos); ?></td>
							</tr>
<?php
if ($propio)
{
?>
							<tr>
								<td class="columna1" colspan="2">
									<form action="insertarfoto.php" method="post">
										<div>
											<input type="submit" value="Añadir foto al álbum"/>
											<input type="hidden" name="id" value="<?php echo $album->getId(); ?>"/>
										</div>
									</form>
									<form action="eliminaralbum.php" method="post" onsubmit="return confirmarEliminarAlbum();">
										<div>
											<input type="submit" value="Eliminar álbum"/>
											<input type="hidden" name="id" value="<?php echo $album->getId(); ?>"/>
											<input type="hidden" name="ajax" value="no"/>
										</div>
									</form>
								</td>
							</tr>
<?php
}
?>
						</table>
						<div id="fotos">
<?php

// Mostramos los resultados si hay al menos 1.
if (count($fotos) > 0)
{
?>

<?php
	foreach ($fotos as $i)
	{
?>
								<div id="foto<?php echo $i->getId(); ?>" class="foto">
									<a href="foto.php?id=<?php echo $i->getId(); ?>"><img src="miniatura.jpg" alt="<?php echo $i->getTitulo(); ?>, <?php echo cambiarFormatoFecha($i->getFecha()); ?>, <?php echo $i->getPais(); ?>" title="<?php echo $i->getTitulo(); ?>, <?php echo cambiarFormatoFecha($i->getFecha()); ?>, <?php echo $i->getPais(); ?>" /></a>
<?php
if ($propio)
{
?>
									<a class="papelera" href="javascript: eliminarFoto('<?php echo $i->getId(); ?>');"><img src="estilo/papelera.png" alt="Eliminar foto" title="Eliminar foto" /></a>
<?php
}
?>
								</div>
<?php
	}
}
?>
						</div>
						<div style="clear: both;"></div>
					</div>
<?php
baseInferior();
?>