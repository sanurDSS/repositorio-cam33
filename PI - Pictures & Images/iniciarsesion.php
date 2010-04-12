<?php
	require_once 'minilibreria.php';

	// Procesar parametros e intentar identificar al usuario.
	$nombre = $_POST["nombre_usuario"];
	$contrasena = $_POST["contrasena"];
	if (ENUsuario::existePorNombre($nombre))
	{
		$usuario = ENUsuario::obtenerPorNombre($nombre);
		if ($usuario->getContrasena() == $contrasena)
		{
			$_SESSION["usuario"] = serialize($usuario);

			// Comprobamos si hay que recordar el usuario.
			if ($_POST["recordar"] == "on")
			{
				// Guardamos el usuario y la contraseña en una cookie.
				setcookie("nombre", $nombre, time() + (30 * 86400));
				setcookie("contrasena", $contrasena, time() + (30 * 86400));
			}
			header("location: index.php");
			exit();
		}
	}

	header("location: index.php?error=Usuario o contraseña incorrecta");
	exit();

?>
