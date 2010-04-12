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
											<option value="3">3</option>
											<option value="4">4</option>
											<option value="5">5</option>

											<option value="6">6</option>
											<option value="7">7</option>
											<option value="8">8</option>
											<option value="9">9</option>
											<option value="10">10</option>
											<option value="11">11</option>

											<option value="12">12</option>
											<option value="13">13</option>
											<option value="14">14</option>
											<option value="15">15</option>
											<option value="16">16</option>
											<option value="17">17</option>

											<option value="18">18</option>
											<option value="19">19</option>
											<option value="20">20</option>
											<option value="21">21</option>
											<option value="22">22</option>
											<option value="23">23</option>

											<option value="24">24</option>
											<option value="25">25</option>
											<option value="26">26</option>
											<option value="27">27</option>
											<option value="28">28</option>
											<option value="29">29</option>

											<option value="30">30</option>
											<option value="31">31</option>

										</select>
										<select name="mes">
											<option selected="selected" value="1">enero</option>
											<option value="2">febrero</option>
											<option value="3">marzo</option>
											<option value="4">abril</option>
											<option value="5">mayo</option>
											<option value="6">junio</option>
											<option value="7">julio</option>
											<option value="8">agosto</option>
											<option value="9">septiembre</option>
											<option value="10">octubre</option>
											<option value="11">noviembre</option>
											<option value="12">diciembre</option>
										</select>
										<select name="ano">
<?php
for ($i = 1900; $i < date('Y'); $i++)
{
?>
											<option value="<?php echo $i; ?>"><?php echo $i; ?></option>
<?php
}
?>
											<option selected="selected" value="<?php echo date('Y'); ?>"><?php echo date('Y'); ?></option>
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
										<select name="pais">
											<option selected="selected" value="cualquiera">cualquiera</option>
<?php
$paises = ENPais::obtenerTodos();
foreach ($paises as $i)
{
?>
											<option value="<?php echo $i->getId(); ?>"><?php echo $i->getNombre(); ?></option>
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