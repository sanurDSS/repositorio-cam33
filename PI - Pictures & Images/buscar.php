<?php
include_once "base.php";
baseSuperior("Búsqueda avanzada");
?>
					<div id="buscar">
						<h3><span>Búsqueda avanzada</span></h3>
						<form action="busqueda.php" method="get">
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
									<td class="columna2">
										<select name="dia">
<?php
$paises = ENPais::obtenerTodos();
foreach ($paises as $i)
{
?>
											<option <?php if ($i->getId() == 1) echo 'selected="selected" '; ?>value="<?php echo $i->getId(); ?>"><?php echo $i->getNombre(); ?></option>
<?php
}
?>
										</select>
									</td>
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