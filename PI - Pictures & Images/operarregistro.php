<?php
	require_once 'minilibreria.php';

	// Se procesan los parámetros que llegan por post.
	$nombre = $_POST["nombre_usuario"];
	$contrasena = $_POST["contrasena"];
	$contrasena2 = $_POST["contrasena2"];
	$email = $_POST["correo_electronico"];
	$sexo = ($_POST["sexo"] == "hombre") ? "hombre" : "mujer";
	$fecha = "";
	if (is_numeric($_POST["dia"]) && is_numeric($_POST["mes"]) && is_numeric($_POST["ano"]))
		$fecha = (checkdate($_POST["mes"], $_POST["dia"], $_POST["ano"])) ? $_POST["ano"]."-".rellenar($_POST["mes"], "0", 2)."-".rellenar($_POST["dia"], "0", 2) : "";
	$pais = $_POST["pais"];
	$ciudad = $_POST["ciudad"];

	// Se comprueban los parámetros.
	if (ENUsuario::existePorNombre($nombre))
	{
		header("location: registrarse.php?error=Ya existe un usuario con el nombre $nombre.");
		exit();
	}
	else
	{
		if ($contrasena != $contrasena2)
		{
			header("location: registrarse.php?error=Las contraseñas no coinciden.");
			exit();
		}
		else
		{
			if ($fecha == "")
			{
				header("location: registrarse.php?error=La fecha no es válida.");
				exit();
			}
			else
			{
				if (ENPais::obtenerPorId($pais) == null)
				{
					header("location: registrarse.php?error=El país introducido no es válido.");
					exit();
				}
			}
		}
	}

	$nuevo = new ENUsuario;
	$nuevo->setNombre($nombre);
	$nuevo->setPais(ENPais::obtenerPorId($pais)->getNombre());
	$nuevo->setCiudad($ciudad);
	$nuevo->setContrasena($contrasena);
	$nuevo->setEmail($email);
	$nuevo->setSexo($sexo);
	$nuevo->setFechaNacimiento($fecha);
	$nuevo->guardar();
	if ($_FILES["foto"] != null)
		$nuevo->setAvatar($_FILES["foto"]);

	$_SESSION["usuario"] = serialize($nuevo);

	header("location: index.php?exito=Usuario registrado correctamente.");
?>
