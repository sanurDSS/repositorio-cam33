<?php
include_once "base.php";
baseSuperior("");

$fotos = ENFoto::obtenerTodos();
$cantidad = (count($fotos) > 5) ? 5 : count($fotos);

?>
						<div id="portada">
							<h3><span>Últimas <?php echo $cantidad; ?> fotos añadidas</span></h3>
<?php
for ($i = 0; $i < $cantidad; $i++)
{
	$foto = $fotos[$i];
?>
							<div class="foto">
								<div class="imagen"><a href="fotos/<?php echo $foto->getId(); ?>.jpg"><img src="fotos/m1<?php echo $foto->getId(); ?>.jpg" alt="<?php echo $foto->getTitulo(); ?>" title="<?php echo $foto->getTitulo(); ?>" /></a></div>
								<div class="informacion">
									<h4><a href="foto.php?id=<?php echo $foto->getId(); ?>"><?php echo $foto->getTitulo(); ?></a></h4>
									<p><em>Descripción:</em> <?php echo $foto->getDescripcion(); ?></p>
									<p><em>Fecha:</em> <?php echo cambiarFormatoFecha($foto->getFecha()); ?></p>
									<p><em>País:</em> <?php echo $foto->getPais(); ?></p>
								</div>
							</div>
<?php
}

// Calculamos una foto aleatoria para mostrarla.
$foto = null;

// Leemos el fichero y guardamos los identificadores en un vector.
$id_fotos = array();
$fichero = fopen("seleccionadas.txt", "r");
while (!feof($fichero))
{
	$leido = trim(fgets($fichero, 4096));
	if (is_numeric($leido))
		$id_fotos[] = $leido;
}
fclose($fichero);

// Elegimos al azar uno de los identificadores.
$id_foto = $id_fotos[rand(0, count($id_fotos) - 1)];

echo $id_foto."<br />";

// Obtenemos la foto y la mostramos si realmente existe la foto con ese identificador.
$foto = ENFoto::obtenerPorId($id_foto);
if ($foto != null)
{
?>
							<h3><span>Foto aleatoria seleccionada por el administrador</span></h3>
							<div class="foto">
								<div class="imagen"><a href="fotos/<?php echo $foto->getId(); ?>.jpg"><img src="fotos/m1<?php echo $foto->getId(); ?>.jpg" alt="<?php echo $foto->getTitulo(); ?>" title="<?php echo $foto->getTitulo(); ?>" /></a></div>
								<div class="informacion">
									<h4><a href="foto.php?id=<?php echo $foto->getId(); ?>"><?php echo $foto->getTitulo(); ?></a></h4>
									<p><em>Descripción:</em> <?php echo $foto->getDescripcion(); ?></p>
									<p><em>Fecha:</em> <?php echo cambiarFormatoFecha($foto->getFecha()); ?></p>
									<p><em>País:</em> <?php echo $foto->getPais(); ?></p>
								</div>
							</div>
<?php
}
?>

						</div>
<?php
baseInferior();
?>