<?php
include_once "base.php";

$usuario = null;
if (isset($_SESSION["usuario"]))
{
	$usuario = unserialize($_SESSION["usuario"]);
}
else
{
	header("location: index.php?aviso=Debe estar identificado para crear un álbum.");
	exit();
}

baseSuperior("Crear un nuevo álbum");
?>
					<div id="registrarse">
						<h3><span>Crear un nuevo álbum</span></h3>
						<form action="operaralbum.php" method="post" onsubmit="return validarAlbum(this);">
							<table>
								<tr>
									<td class="columna1">Título*:</td>
									<td class="columna2"><input type="text" value="" name="titulo" /></td>
								</tr>
								<tr>
									<td class="columna1">Descripción*:</td>
									<td class="columna2"><textarea cols="25" rows="4" name="descripcion"></textarea></td>
								</tr>
								<tr>
									<td class="columna1">Fecha*:</td>
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
											<option value="1">enero</option>
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
									<td class="columna1">País*:</td>
									<td class="columna2">
										<select name="pais">
											<option selected="selected" value=""></option>
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
									<td colspan="2"></td>
								</tr>
								<tr>
									<td class="columna1"></td>
									<td class="columna2"><input type="submit" value="Crear álbum" /></td>
								</tr>
							</table>
						</form>
					</div>
<?php
baseInferior();
?>