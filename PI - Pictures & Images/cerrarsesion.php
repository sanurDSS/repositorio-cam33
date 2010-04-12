<?php
	require_once 'minilibreria.php';
	session_destroy();
	
	// Hay que eliminar la cookie.
	setcookie("nombre");
	setcookie("contrasena");
	setcookie("fecha");

	header("location: index.php");
	exit();
?>