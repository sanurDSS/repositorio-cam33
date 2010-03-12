<?php
include_once "base.php";
baseSuperior("Registrarse");
?>
					<div id="registrarse">
						<h3><span>Datos del nuevo usuario</span></h3>
						<form action="registrarse.php" method="post" enctype="multipart/form-data" onsubmit="return validarRegistro(this);">
							<table>
								<tr>
									<td colspan="2"><h4>Datos personales</h4></td>
								</tr>
								<tr>
									<td class="columna1">Nombre de usuario*:</td>
									<td class="columna2"><input type="text" value="" name="nombre_usuario" /></td>
								</tr>
								<tr>
									<td class="columna1">Contraseña*:</td>
									<td class="columna2"><input type="password" value="" name="contrasena" /></td>
								</tr>
								<tr>
									<td class="columna1">Confirmación de contraseña*:</td>
									<td class="columna2"><input type="password" value="" name="contrasena2" /></td>
								</tr>
								<tr>
									<td class="columna1">Correo electrónico*:</td>
									<td class="columna2"><input type="text" value="" name="correo_electronico" /></td>
								</tr>
								<tr>
									<td class="columna1">Sexo*:</td>
									<td class="columna2">
										<input type="radio" name="sexo" value="hombre" /> Hombre
										<input type="radio" name="sexo" value="mujer" /> Mujer
									</td>
								</tr>
								<tr>
									<td class="columna1">Fecha de nacimiento*:</td>
									<td class="columna2">
										<select name="dia">
											<option selected="selected" value=""></option>
<?php
for ($i = 1; $i <= 31; $i++)
{
?>
											<option value="<?php echo $i; ?>"><?php echo $i; ?></option>
<?php
}
?>
										</select>
										<select name="mes">
											<option selected="selected" value=""></option>
<?php
for ($i = 1; $i <= 12; $i++)
{
?>
											<option value="<?php echo $i; ?>"><?php echo $i; ?></option>
<?php
}
?>
										</select>
										<select name="ano">
											<option selected="selected" value=""></option>
<?php
for ($i = 1901; $i <= 2010; $i++)
{
?>
											<option value="<?php echo $i; ?>"><?php echo $i; ?></option>
<?php
}
?>
										</select>
									</td>
								</tr>
								<tr>
									<td class="columna1">País:</td>
									<td class="columna2"><input type="text" value="¿select con ajax?" name="pais" /></td>
								</tr>
								<tr>
									<td class="columna1">Ciudad:</td>
									<td class="columna2"><input type="text" value="¿select con ajax?" name="ciudad" /></td>
								</tr>
								<tr>
									<td class="columna1">Foto:</td>
									<td class="columna2"><input type="file" value="" name="foto" /></td>
								</tr>
								<tr>
									<td class="columna1">Descripción personal:</td>
									<td class="columna2"><textarea cols="25" rows="4"></textarea></td>
								</tr>
								<tr>
									<td class="columna1">Aficiones:</td>
									<td class="columna2">
										<table id="tablaaficiones">
											<tr>
												<td><input type='checkbox' name='aficiones1' value="1"> Informática</td>
												<td><input type='checkbox' name='aficiones2' value="2"> Deportes</td>
												<td><input type='checkbox' name='aficiones3' value="3"> Economía</td>
											</tr>
											<tr>
												<td><input type='checkbox' name='aficiones4' value="4"> Tecnología</td>
												<td><input type='checkbox' name='aficiones5' value="5"> Cultura</td>
												<td><input type='checkbox' name='aficiones5' value="5"> Política</td>
											</tr>
											<tr>
												<td><input type='checkbox' name='aficiones7' value="7"> Educación</td>
												<td><input type='checkbox' name='aficiones8' value="8"> Ocio</td>
												<td><input type='checkbox' name='aficiones9' value="9"> Amigos</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td colspan="2"><h4>Descripción de la persona que buscas</h4></td>
								</tr>
								<tr>
									<td class="columna1">Sexo:</td>
									<td class="columna2">
										<input type="radio" name="sexo2" value="hombre" /> Hombre
										<input type="radio" name="sexo2" value="mujer" /> Mujer
										<input type="radio" name="sexo2" value="cualquiera" /> Cualquiera
									</td>
								</tr>
								<tr>
									<td class="columna1">Edad:</td>
									<td class="columna2">entre
										<select name="edad1">
											<option selected="selected" value="1">1</option>
<?php
for ($i = 2; $i <= 100; $i++)
{
?>
											<option value="<?php echo $i; ?>"><?php echo $i; ?></option>
<?php
}
?>
										</select> y
										<select name="edad2">
											<option selected="selected" value="1">1</option>
<?php
for ($i = 2; $i <= 100; $i++)
{
?>
											<option value="<?php echo $i; ?>"><?php echo $i; ?></option>
<?php
}
?>
										</select>
									</td>
								</tr>
								<tr>
									<td class="columna1">País:</td>
									<td class="columna2"><input type="text" value="¿select con ajax?" name="pais2" /></td>
								</tr>
								<tr>
									<td class="columna1">Ciudad:</td>
									<td class="columna2"><input type="text" value="¿select con ajax?" name="ciudad2" /></td>
								</tr>
								<tr>
									<td colspan="2"></td>
								</tr>
								<tr>
									<td class="columna1"></td>
									<td class="columna2"><input type="submit" value="Registrarse" /></td>
								</tr>
							</table>
						</form>
					</div>
<?php
baseInferior();
?>