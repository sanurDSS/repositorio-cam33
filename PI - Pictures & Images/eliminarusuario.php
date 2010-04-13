<?php
require_once 'minilibreria.php';

$contrasena = $_POST["contrasena"];
$usuario = unserialize($_SESSION["usuario"]);

if ($usuario != null)
{
	if ($usuario->getContrasena() == $contrasena)
	{
		$usuario->borrarAvatar();

		$albumes = $usuario->obtenerAlbumes();
		foreach ($albumes as $i)
		{
			$fotos = $i->getFotos();
			foreach ($fotos as $j)
			{
				$j->borrarFoto();
			}
		}
		$usuario->borrar();
		$_SESSION["usuario"] = null;
		header("location: index.php?exito=El usuario ha sido eliminado correctamente.");
		exit();
	}
	else
	{
		header("location: darmedebaja.php?error=La contraseña es incorrecta.");
		exit();
	}
}
else
{
	header("location: index.php?error=Para dar de baja un usuario tienes que estar identificado con ese usuario.");
	exit();
}

?>
