<?php
include_once "base.php";
baseSuperior("");
?>
						<div id="portada">
							<h3><span>Últimas 5 fotos</span></h3>
							<?php
							for ($i = 0; $i < 3; $i++)
							{
							?>
							<div class="foto">
								<div class="imagen"><a href="foto.jpg"><img src="miniatura.jpg" alt="Título de la foto" title="Título de la foto<?php echo $i; ?>" /></a></div>
								<div class="informacion">
									<h4><a href="foto.php">Título de la foto</a></h4>
									<p><em>Descripción:</em> Bla bla bla bla bla bla Bla bla bla bla bla bla Bla bla bla bla bla bla Bla bla bla bla bla bla.</p>
									<p><em>Fecha:</em> 20-12-1999</p>
									<p><em>País:</em> España</p>
								</div>
							</div>
							<?php
							}
							?>
							<!--<table border="1px">
								<tr class="foto">
									<td><a href="foto.jpg"><img src="miniatura.jpg" alt="Fotillo" /></a></td>
									<td>
										<h4><a href="foto.php">Título</a></h4>
										<p><em>Descripción</em> Un parrafito</p>
										<p><em>Fecha:</em> 20-12-1999</p>
										<p><em>País:</em> España</p>
									</td>
								</tr>
							</table>-->
						</div>
<?php
baseInferior();
?>