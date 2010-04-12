<?php
	require_once 'minilibreria.php';

	// Se procesan los parámetros que llegan por post.
	$nombre = $_POST["nombre_usuario"];
	$contrasena = $_POST["contrasena"];
	$contrasena2 = $_POST["contrasena2"];
	$email = $_POST["correo_electronico"];
	$sexo = ($_POST["sexo"] == "hombre") ? "hombre" : "mujer";
	$fecha = (checkdate($_POST["mes"], $_POST["dia"], $_POST["ano"])) ? $_POST["ano"]."-".rellenar($_POST["mes"], "0", 2)."-".rellenar($_POST["dia"], "0", 2) : "";
	$pais = $_POST["pais"];
	$ciudad = $_POST["ciudad"];

	// Se comprueban los parámetros.
	if (ENUsuario::existePorNombre($nombre))
	{
		echo "YA EXISTE EL USUARIO";
		exit();
	}
	else
	{
		if ($contrasena != $contrasena2)
		{
			echo "LAS CONTRASEÑAS NO COINCIDEN";
			exit();
		}
		else
		{
			if ($fecha == "")
			{
				echo "LA FECHA NO ES VÁLIDA";
				exit();
			}
			else
			{
				if (ENPais::obtenerPorId($pais) == null)
				{
					echo "PAÍS INCORRECTO $pais";
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


	echo $nuevo->toString();
	header("location: index.php");
	// si se crea bien, se reenvia al usuairo a algun sitio con EXITO

	// si hay error en algun punto, se redirige con ERROR
?>
