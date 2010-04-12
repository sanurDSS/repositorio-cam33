<?php
require_once 'minilibreria.php';

$id = $_POST["id"];
$usuario = unserialize($_SESSION["usuario"]);
$album = (is_numeric($id)) ? ENAlbum::obtenerPorId($id) : null;
if ($usuario != null && $album != null)
{
	if ($usuario->getNombre() == $album->getUsuario())
	{
		$album->borrar();

		if ($_POST["ajax"] == "no")
		{
			header("location: albumes.php?exito=El álbum ha sido eliminado correctamente.");
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
	header("location: index.php?exito=No se ha podido eliminar el álbum.");
	exit();
}
else
{
	echo "ERROR";
	exit();
}

?>
