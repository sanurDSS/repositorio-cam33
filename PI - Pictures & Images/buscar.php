<?php
include_once "base.php";
baseSuperior("Búsqueda avanzada");
?>
					<div id="buscar">
						<h3><span>Búsqueda avanzada</span></h3>
						<form action="busqueda.php" method="get">
							<!--<div><label for="titulo">Título:</label><input type="text" value="" name="titulo" /></div>
							<div><label for="pais">País:</label>
								<select name="pais">
									<option value="angola">Angola</option>
									<option value="españa">España</option>
									<option value="francia">Francia</option>
								</select>
								<br />
							</div>
							<div>
								<label>Fecha de inicio:</label>
								<select name="dia">
									<option selected="selected" value="1">1</option>
									<option value="2">2</option>
								</select>
								<select name="mes">
									<option selected="selected" value="1">1</option>
									<option value="2">2</option>
								</select>
								<select name="ano">
									<option selected="selected" value="1999">1999</option>
									<option value="2000">2000</option>
								</select>
							</div>
							<div>
								<label>Fecha de fin:</label>
								<select name="dia2">
									<option selected="selected" value="1">1</option>
									<option value="2">2</option>
								</select>
								<select name="mes2">
									<option selected="selected" value="1">1</option>
									<option value="2">2</option>
								</select>
								<select name="ano2">
									<option selected="selected" value="1999">1999</option>
									<option value="2000">2000</option>
								</select>
							</div>
							<div><label></label><input type="submit" value="Buscar" /></div>-->
							<table>
								<tr>
									<td class="columna1">Título:</td>
									<td class="columna2"><input type="text" value="" name="titulo" /></td>
								</tr>
								<tr>
									<td class="columna1">Fecha:</td>
									<td class="columna2">
										<select name="dia">
											<option selected="selected" value="1">1</option>
											<option value="2">2</option>
										</select>
										<select name="mes">
											<option selected="selected" value="1">1</option>
											<option value="2">2</option>
										</select>
										<select name="ano">
											<option selected="selected" value="1999">1999</option>
											<option value="2000">2000</option>
										</select>
										<select name="segmento">
											<option selected="selected" value="antes">(subidas antes de este día)</option>
											<option value="despues">(subidas después de este día)</option>
										</select>
									</td>
								</tr>
								<tr>
									<td class="columna1">País:</td>
									<td class="columna2"><input type="text" value="¿select con ajax?" name="pais" /></td>
								</tr>
								<tr>
									<td class="columna1"></td>
									<td class="columna2"><input type="submit" value="Buscar" /></td>
								</tr>
							</table>
						</form>
					</div>
<?php
baseInferior();
?>