<?php
	$error = $_GET["error"];
	$aviso = $_GET["aviso"];
	$exito = $_GET["exito"];
	$info = $_GET["info"];

	if ($exito != NULL || $info != NULL || $aviso != NULL || $error != NULL)
	{
		echo "<div id=\"mensajes\">\n";
		if ($exito != NULL)
		{
			if (is_string($exito))
			{
				if (strlen($exito)>0)
				{
					echo "<table class=\"exito\">\n";
					echo "<tr>\n";
					echo "<td class=\"columnaizquierda\"><img src=\"estilo/exito.gif\" alt=\"ÉXITO: \" title=\"¡Éxito!\" /></td>";
					echo "<td class=\"columnaderecha\">".$exito."</td>";
					echo "</tr>\n";
					echo "</table>\n";
				}
			}
		}

		if ($info != NULL)
		{
			if (is_string($info))
			{
				if (strlen($info)>0)
				{
					echo "<table class=\"info\">\n";
					echo "<tr>\n";
					echo "<td class=\"columnaizquierda\"><img src=\"estilo/info.gif\" alt=\"INFORMATIVO: \" title=\"Mensaje de información\" /></td>";
					echo "<td class=\"columnaderecha\">".$info."</td>";
					echo "</tr>\n";
					echo "</table>\n";
				}
			}
		}

		if ($aviso != NULL)
		{
			if (is_string($aviso))
			{
				if (strlen($aviso)>0)
				{
					echo "<table class=\"aviso\">\n";
					echo "<tr>\n";
					echo "<td class=\"columnaizquierda\"><img src=\"estilo/aviso.gif\" alt=\"AVISO: \" title=\"¡Cuidado!\" /></td>";
					echo "<td class=\"columnaderecha\">".$aviso."</td>";
					echo "</tr>\n";
					echo "</table>\n";
				}
			}
		}

		if ($error != NULL)
		{
			if (is_string($error))
			{
				if (strlen($error)>0)
				{
					echo "<table class=\"error\">\n";
					echo "<tr>\n";
					echo "<td class=\"columnaizquierda\"><img src=\"estilo/error.gif\" alt=\"ERROR: \" title=\"Ocurrió un error\" /></td>";
					echo "<td class=\"columnaderecha\">".$error."</td>";
					echo "</tr>\n";
					echo "</table>\n";
				}
			}
		}
		echo "</div>\n";
	}

?>
