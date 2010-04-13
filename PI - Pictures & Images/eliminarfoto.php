<?php
require_once 'minilibreria.php';

$id = $_POST["id"];
$usuario = unserialize($_SESSION["usuario"]);
$foto = (is_numeric($id)) ? ENFoto::obtenerPorId($id) : null;
if ($usuario != null && $foto != null)
{
	if ($usuario->getNombre() == $foto->getUsuario())
	{
		$album = $foto->getIdAlbum();
		$foto->borrarFoto();
		$foto->borrar();

		if ($_POST["ajax"] == "no")
		{
			header("location: album.php?id=$album&exito=La foto ha sido eliminada correctamente.");
			exit();
		}
		else
		{
			echo "OK";
			exit();
		}
	}
}

if ($_POST["ajax"] == "no")
{
	header("location: index.php?exito=No se ha podido eliminar la foto.");
	exit();
}
else
{
	echo "ERROR";
	exit();
}

?>
