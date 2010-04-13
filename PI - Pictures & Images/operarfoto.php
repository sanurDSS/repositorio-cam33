<?php
	require_once 'minilibreria.php';

	if (!isset($_SESSION["usuario"]))
	{
		header("location: index.php?aviso=Debe estar identificado para crear un álbum.");
		exit();
	}

	// Se procesan los parámetros que llegan por post.
	$id = $_POST["id"];
	$titulo = $_POST["titulo"];
	$descripcion = $_POST["descripcion"];
	$fecha = "";
	if (is_numeric($_POST["dia"]) && is_numeric($_POST["mes"]) && is_numeric($_POST["ano"]))
		$fecha = (checkdate($_POST["mes"], $_POST["dia"], $_POST["ano"])) ? $_POST["ano"]."-".rellenar($_POST["mes"], "0", 2)."-".rellenar($_POST["dia"], "0", 2) : "";
	$pais = $_POST["pais"];
	$album = (is_numeric($id)) ? ENAlbum::obtenerPorId($id) : null;

	// Se comprueban los parámetros.
	if ($album == null)
	{
		header("location: index.php?aviso=No se encuentra el álbum para añadir la foto.");
		exit();
	}
	else
	{
		if ($album->getUsuario() != unserialize($_SESSION["usuario"])->getNombre())
		{
			header("location: index.php?error=No puedes insertar una foto en un álbum ajeno.");
			exit();
		}
		else
		{
			if ($titulo == "")
			{
				header("location: anadirfoto.php?id=$id&error=Debe introducir un título y una descripción.");
				exit();
			}
			else
			{
				if ($fecha == "")
				{
						header("location: anadirfoto.php?id=$id&error=La fecha no es válida.");
						exit();
				}
				else
				{
					if (ENPais::obtenerPorId($pais) == null)
					{
						header("location: anadirfoto.php?id=$id&error=El país introducido no es válido.");
						exit();
					}
					else
					{
						if (!isset($_FILES["foto"]))
						{
							header("location: anadirfoto.php?id=$id&error=No se ha introducido una imagen.");
							exit();
						}
						else
						{
							if ($_FILES["foto"]["tmp_name"] == "")
							{
								header("location: anadirfoto.php?id=$id&error=No se ha introducido una imagen.");
								exit();
							}
						}
					}
				}
			}
		}
	}

	$nuevo = new ENFoto();
	$nuevo->setTitulo($titulo);
	$nuevo->setDescripcion($descripcion);
	$nuevo->setFecha($fecha);
	$nuevo->setPais(ENPais::obtenerPorId($pais)->getNombre());
	$nuevo->setIdAlbum($album->getId());
	$nuevo->guardar();
	if (!$nuevo->setFoto($_FILES["foto"]))
	{
		$nuevo->borrarFoto();
		$nuevo->borrar();
		header("location: anadirfoto.php?id=".$album->getId()."&error=No se pudo crear la foto porque tiene un formato incorrecto. Debe ser JPG.");
		exit();
	}

	header("location: album.php?id=".$album->getId()."&exito=Foto subida correctamente.");
?>
