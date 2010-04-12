<?php
	require_once 'minilibreria.php';
	session_destroy();
	
	// Hay que eliminar la cookie si existe.
	setcookie("nombre");
	setcookie("contrasena");

	header("location: index.php");
	exit();
?>