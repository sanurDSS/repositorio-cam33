<?php
/**
 *
 * @param String $titulo Título (<title>) que tendrá la página.
 */
function baseSuperior($titulo)
{
	if ($titulo == "")
		$titulo = "Pictures &amp; Images";
?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="es" lang="es">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>PI - <?php echo $titulo; ?></title>
		<link type="text/css" rel="stylesheet" href="estilo/estilo.css" media="screen" title="Estilo principal" />
		<link type="text/css" rel="alternate stylesheet" href="estilo/sencillo.css" media="screen" title="Estilo sencillo" />
		<link type="text/css" rel="stylesheet" href="estilo/imprimible.css" media="print" />
		<script src="javascript/formularios.js" type="text/javascript"></script>
		<script src="javascript/ordenar.js" type="text/javascript"></script>
	</head>
	<body>
		<div id="contenedor">
			<div id="contenido">
				<div id="cabecera">
					<div id="titulo">
						<h1><a href="index.php">PI - Pictures &amp; Images</a></h1>
						<h2>Crea tus propios catálogos de fotos</h2>
					</div>

					<div id="busqueda">
						<h3><span>Busca</span></h3>
						<form action="busqueda.php" method="get">
							<div><label>Búsqueda:</label><input type="text" value="" name="busqueda" /></div>
							<div><label></label><input type="submit" value="Buscar" /></div>
							<div><span><a href="buscar.php">Búsqueda avanzada</a></span></div>
						</form>
					</div>
				</div>

				<div id="navegacion">
					<div id="menu">
						<h3><span>Menú</span></h3>
						<ul>
							<li><a href="index.php">Inicio</a></li>
							<li><a href="buscar.php">Búsqueda avanzada</a></li>
							<li><a href="#">Panel de control</a></li>
							<li><a href="#">Crear un álbum</a></li>
							<li><a href="#">Añadir fotografía</a></li>
						</ul>
					</div>

					<div id="identificarse">
						<h3><span>Identifícate</span></h3>
						<form action="index.php" method="post" onsubmit="return validarIdentificacion(this);">
							<div><label>Usuario:</label><input type="text" value="" name="nombre_usuario" /></div>
							<div><label>Contraseña:</label><input type="password" value="" name="contrasena" /></div>
							<div><label></label><input type="submit" value="Entrar" /></div>
							<div><span><a href="registrarse.php">Regístrate</a></span></div>
						</form>
					</div>
				</div>

				<div id="cuerpo">
					<div id="cuerpo_superior"><span></span></div>
					<div id="cuerpo_medio">
<?php
}

function baseInferior()
{
?>
					</div>
					<div id="cuerpo_inferior"><span></span></div>
				</div>

				<div id="pie">
					<div id="pie_superior">
						<div id="creditos">
							<h3><span>Créditos</span></h3>
							<ul>
								<li><a href="mailto:cam33@alu.ua.es">Cristian Aguilera Martínez</a></li>
								<li><a href="http://www.dlsi.ua.es/asignaturas/pi/">Programación en Internet, 2009/2010</a></li>
							</ul>
						</div>
						<div id="rss">
							<h3><span>Subscríbete al canal RSS</span></h3>
							<ul>
								<li><a href="#">Últimas fotos</a></li>
								<li><a href="#">Mejores fotos del día</a></li>
								<li><a href="#">Mejores catálogos</a></li>
							</ul>
						</div>
						<div id="w3">
							<h3><span>W3C</span></h3>
							<p class="w3">
								<a href="http://validator.w3.org"><!--/check?uri=referer-->
									<img src="http://www.w3.org/Icons/valid-xhtml10" alt="Valid XHTML 1.0 Strict" />
								</a>
							</p>
							<p class="w3">
								<a href="http://jigsaw.w3.org/css-validator"><!--/check/referer-->
									<img src="http://jigsaw.w3.org/css-validator/images/vcss" alt="¡CSS Válido!" />
								</a>
							</p>
						</div>
					</div>
					<div id="pie_inferior">
						<div id="estilos">
							<h3><span>Selecciona un estilo:</span></h3>
							<ul>
								<li><a href="#">Estilo principal</a></li>
								<li><a href="#">Estilo sencillo</a></li>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- Estas etiquetas se pueden emplear para añadir imágenes adicionales. -->
		<div id="extraDiv1"><span></span></div>
		<div id="extraDiv2"><span></span></div>
		<div id="extraDiv3"><span></span></div>
		<div id="extraDiv4"><span></span></div>
		<div id="extraDiv5"><span></span></div>
		<div id="extraDiv6"><span></span></div>

	</body>
</html>
<?php
}
?>