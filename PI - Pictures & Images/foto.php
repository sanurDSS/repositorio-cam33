<?php
include_once "base.php";

$id = is_numeric($_GET["id"]) ? $_GET["id"] : -1;
$foto = ENFoto::obtenerPorId($id);
if ($foto == null)
{
	header("location: index.php?error=La foto no existe.");
	exit();
}
$album = ENAlbum::obtenerPorId($foto->getIdAlbum());
$usuario = ENUsuario::obtenerPorNombre($foto->getUsuario());

$propio = false;
if (isset($_SESSION["usuario"]))
{
	if (unserialize($_SESSION["usuario"])->getNombre() == $usuario->getNombre())
	{
		$propio = true;
	}
}

baseSuperior("Foto #".$foto->getId());
?>
					<div id="foto">
						<h3><span>Foto: <?php echo $foto->getTitulo(); ?></span></h3>
						<table>
							<tr>
								<td colspan="2"><a href="foto.jpg"><img src="foto.jpg" alt="<?php echo $foto->getTitulo(); ?>" /></a></td>
							</tr>
							<tr>
								<td class="columna1">Título:</td>
								<td class="columna2"><?php echo $foto->getTitulo(); ?></td>
							</tr>
							<tr>
								<td class="columna1">Descripción:</td>
								<td class="columna2"><?php echo $foto->getDescripcion(); ?></td>
							</tr>
							<tr>
								<td class="columna1">Fecha:</td>
								<td class="columna2">18/12/2001</td>
							</tr>
							<tr>
								<td class="columna1">País:</td>
								<td class="columna2"><?php echo $foto->getPais(); ?></td>
							</tr>
							<tr>
								<td class="columna1">Álbum de fotos:</td>
								<td class="columna2"><a href="album.php?id=<?php echo $album->getId(); ?>"><?php echo $album->getTitulo(); ?></a></td>
							</tr>
							<tr>
								<td class="columna1">Usuario:</td>
								<td class="columna2"><a href="perfil.php?id=<?php echo $usuario->getId(); ?>"><?php echo $usuario->getNombre(); ?></a></td>
							</tr>
<?php
if ($propio)
{
?>
							<tr>
								<td class="columna1" colspan="2">
									<form action="eliminarfoto.php" method="post" onsubmit="return confirmarEliminarFoto();">
										<div>
											<input type="submit" value="Eliminar foto"/>
											<input type="hidden" name="id" value="<?php echo $foto->getId(); ?>"/>
											<input type="hidden" name="ajax" value="no"/>
										</div>
									</form>
								</td>
							</tr>
<?php
}
?>
						</table>
					</div>
<?php
baseInferior();
?>