<?php
	require_once 'minilibreria.php';

	if (!isset($_SESSION["usuario"]))
	{
		header("location: index.php?aviso=Debe estar identificado para crear un álbum.");
		exit();
	}

	// Se procesan los parámetros que llegan por post.
	$titulo = $_POST["titulo"];
	$descripcion = $_POST["descripcion"];
	$fecha = "";
	if (is_numeric($_POST["dia"]) && is_numeric($_POST["mes"]) && is_numeric($_POST["ano"]))
		$fecha = (checkdate($_POST["mes"], $_POST["dia"], $_POST["ano"])) ? $_POST["ano"]."-".rellenar($_POST["mes"], "0", 2)."-".rellenar($_POST["dia"], "0", 2) : "";
	$pais = $_POST["pais"];

	// Se comprueban los parámetros.
	if ($titulo == "")
	{
		header("location: crearalbum.php?error=Debe introducir un título y una descripción.");
		exit();
	}
	else
	{
		if ($fecha == "")
		{
				header("location: crearalbum.php?error=La fecha no es válida.");
				exit();
		}
		else
		{
			if (ENPais::obtenerPorId($pais) == null)
			{
				header("location: crearalbum.php?error=El país introducido no es válido.");
				exit();
			}
		}
	}

	$nuevo = new ENAlbum();
	$nuevo->setTitulo($titulo);
	$nuevo->setDescripcion($descripcion);
	$nuevo->setFecha($fecha);
	$nuevo->setPais(ENPais::obtenerPorId($pais)->getNombre());
	$nuevo->setUsuario(unserialize($_SESSION["usuario"])->getNombre());
	$nuevo->guardar();

	header("location: albumes.php?exito=Álbum creado correctamente.");
?>
