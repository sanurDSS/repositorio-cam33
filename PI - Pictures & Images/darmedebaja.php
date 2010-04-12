<?php
include_once "base.php";

if (!isset($_SESSION["usuario"]))
{
	header("location: index.php?error=Para dar de baja un usuario tienes que estar identificado con ese usuario.");
	exit();
}

baseSuperior("Darme de baja");
?>
						<div id="portada">
							<h3><span>Darme de baja</span></h3>
							<div>
								<p>Se eliminarán todos sus álbumes y fotos.<br />Introduzca su contraseña de usuario para darse de baja.</p>
								<form action="eliminarusuario.php" method="post" onsubmit="return confirm('¿Está seguro?');">
									<div><input type="password" value="" name="contrasena" /><input type="submit" value="Darme de baja" /></div>
								</form>
							</div>
						</div>
<?php
baseInferior();
?>