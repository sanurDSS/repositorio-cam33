<?php
include_once "base.php";

// Extraemos el álbum desde la base de datos.
$id_usuario = $_GET["id"];
$usuario = null;
if (is_numeric($id_usuario))
	$usuario = ENUsuario::obtenerPorId($id_usuario);
else
	$usuario = unserialize($_SESSION["usuario"]);

$propio = false;
if (isset($_SESSION["usuario"]))
{
	if (unserialize($_SESSION["usuario"])->getId() == $usuario->getId())
	{
		$propio = true;
	}
}

$albumes = array();
if ($usuario != null)
{
	$albumes = $usuario->obtenerAlbumes();
}
else
{
	header("location: index.php?aviso=No se pueden ver los álbumes porque el usuario indicado no existe.");
	exit();
}
if (!$propio)
	baseSuperior("Álbumes de ".$usuario->getNombre());
else
	baseSuperior("Mis álbumes de fotos");
?>
					<div id="albumes">
<?php
if (!$propio)
{
?>
						<h3><span>Álbumes de <?php echo $usuario->getNombre() ?></span></h3>
<?php
}
else
{
?>
						<h3><span>Mis álbumes de fotos</span></h3>
<?php
}
?>
						<div>
<?php

// Mostramos los resultados si hay al menos 1.
if (count($albumes) > 0)
{
?>
							<table>
<?php
	foreach ($albumes as $i)
	{
?>
								<tr id="album<?php echo $i->getId(); ?>">
									<td><a href="album.php?id=<?php echo $i->getId(); ?>"><?php echo $i->getTitulo(); ?></a> (con <?php echo count($i->getFotos())." fotos"; ?>)</td>
									<td><?php echo cambiarFormatoFecha($i->getFecha()); ?></td>
									<td><?php echo $i->getPais(); ?></td>
<?php
if ($propio)
{
?>
									<td><input type="button" value="Eliminar" onclick="eliminarAlbum('<?php echo $i->getId(); ?>');"/></td>
<?php
}
?>
								</tr>
<?php
	}
?>
							</table>
<?php
}
else
{
?>
								<p>El usuario no tiene ningún álbum todavía.</p>
<?php
}
?>
						</div>
					</div>
<?php
baseInferior();
?>