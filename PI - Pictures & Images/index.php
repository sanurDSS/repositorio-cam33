<?php
include_once "base.php";
baseSuperior("");

$fotos = ENFoto::obtenerTodos();
$cantidad = (count($fotos) > 5) ? 5 : count($fotos);

?>
						<div id="portada">
							<h3><span>Últimas <?php echo $cantidad; ?> fotos</span></h3>
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
?>
						</div>
<?php
baseInferior();
?>