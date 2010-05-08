<?php
include_once "base.php";

$id_usuario = $_GET["id"];
$usuario = null;
if (is_numeric($id_usuario))
	$usuario = ENUsuario::obtenerPorId($id_usuario);
else
	$usuario = unserialize($_SESSION["usuario"]);

if ($usuario == null)
{
	header("location: index.php?aviso=No existe el usuario seleccionado para ver el perfil.");
	exit();
}

$propio = false;
if (isset($_SESSION["usuario"]))
{
	if (unserialize($_SESSION["usuario"])->getNombre() == $usuario->getNombre())
	{
		$propio = true;
	}
}

$cabeza = "Mi perfil";
if (!$propio)
	$cabeza = "Perfil de ".$usuario->getNombre();
baseSuperior($cabeza);

?>
					<div id="perfil">
						<h3><span><?php echo $cabeza; ?></span></h3>
						<table>
							<tr>
								<td colspan="2"><a href="avatares/<?php echo $usuario->getId(); ?>.jpg"><img src="avatares/m2<?php echo $usuario->getId(); ?>.jpg" alt="<?php echo $usuario->getNombre(); ?>" /></a></td>
							</tr>
							<tr>
								<td class="columna1">Nombre:</td>
								<td class="columna2"><?php echo $usuario->getNombre(); ?></td>
							</tr>
							<tr>
								<td class="columna1">Correo electrónico:</td>
								<td class="columna2"><?php echo $usuario->getEmail(); ?></td>
							</tr>
							<tr>
								<td class="columna1">Sexo:</td>
								<td class="columna2"><?php echo $usuario->getSexo(); ?></td>
							</tr>
							<tr>
								<td class="columna1">Fecha de nacimiento:</td>
								<td class="columna2"><?php echo cambiarFormatoFecha($usuario->getFechaNacimiento()); ?></td>
							</tr>
							<tr>
								<td class="columna1">Fecha de registro:</td>
								<td class="columna2"><?php echo $usuario->getFechaRegistro(); ?></td>
							</tr>
							<tr>
								<td class="columna1">País:</td>
								<td class="columna2"><?php echo $usuario->getPais(); ?></td>
							</tr>
							<tr>
								<td class="columna1">Ciudad:</td>
								<td class="columna2"><?php echo $usuario->getCiudad(); ?></td>
							</tr>
							<tr>
								<td class="columna1">Álbumes de fotos:</td>
								<td class="columna2"><?php echo count($usuario->obtenerAlbumes()); ?> (<a href="albumes.php?id=<?php echo $usuario->getId(); ?>">ver álbumes</a>)</td>
							</tr>
<?php
if ($propio)
{
?>
							<!--<tr>
								<td class="columna1" colspan="2">
									<form action="modificarperfil.php" method="get">
										<div>
											<input type="submit" value="Modificar perfil"/>
										</div>
									</form>
								</td>
							</tr>-->
<?php
}
?>
						</table>
					</div>
<?php
baseInferior();
?>