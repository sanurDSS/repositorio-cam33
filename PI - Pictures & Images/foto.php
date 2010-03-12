<?php
include_once "base.php";
baseSuperior("Foto #1234");
?>
					<div id="foto">
						<h3><span>Título de la foto</span></h3>
						<table>
							<tr>
								<td colspan="2"><a href="foto.jpg"><img src="foto.jpg" alt="Fotillo" /></a></td>
							</tr>
							<tr>
								<td class="columna1">Título:</td>
								<td class="columna2">Título de la foto</td>
							</tr>
							<tr>
								<td class="columna1">Descripción:</td>
								<td class="columna2">Bla bla bla bla bla bla Bla bla bla bla bla bla Bla bla bla bla bla bla Bla bla bla bla bla bla.</td>
							</tr>
							<tr>
								<td class="columna1">Fecha:</td>
								<td class="columna2">18/12/2001</td>
							</tr>
							<tr>
								<td class="columna1">País:</td>
								<td class="columna2">España</td>
							</tr>
							<tr>
								<td class="columna1">Álbum de fotos:</td>
								<td class="columna2"><a href="#">Viaje a Madrid</a></td>
							</tr>
							<tr>
								<td class="columna1">Usuario:</td>
								<td class="columna2"><a href="#">Ernesto</a></td>
							</tr>
						</table>
					</div>
<?php
baseInferior();
?>