/*************************************************
// Constantes
*************************************************/
var kMinNombreUsuario = 3;
var kMaxNombreUsuario = 15;
var kMinContrasena = 6;
var kMaxContrasena = 15;

function validarIdentificacion(formulario)
{
	// Se extraen las cadenas con el nombre del usuario y la contraseña y se eliminan espacios y tabuladores.
	var nombre_usuario = formulario.nombre_usuario.value.replace(" ", "").replace("\t", "");
	while (nombre_usuario != nombre_usuario.replace(" ", "").replace("\t", ""))
		nombre_usuario = nombre_usuario.replace(" ", "").replace("\t", "");
	var contrasena = formulario.contrasena.value.replace(" ", "").replace("\t", "");
	while (contrasena != contrasena.replace(" ", "").replace("\t", ""))
		contrasena = contrasena.replace(" ", "").replace("\t", "");

	// Se comprueba que todavía existan caracteres en el nombre de usuario y en la contraseña para permitir la consulta al servidor.
	if (nombre_usuario.length > 0 && contrasena.length > 0)
		return true;
	else
		return false;
}

function validarRegistro(formulario)
{
	var alerta = "";

	// Comprobamos que el usuario es correcto.
	var expReg = new RegExp("^[a-zA-Z0-9]{" + kMinNombreUsuario + "," + kMaxNombreUsuario + "}$");
	if (!expReg.test(formulario.nombre_usuario.value))
		alerta = alerta + "- El nombre de usuario sólo puede contener símbolos alfanuméricos y debe tener entre " + kMinNombreUsuario + " y " + kMaxNombreUsuario + " caracteres.\n\n";

	// Comprobamos que la contraseña es válida.
	if (!new RegExp("^[a-zA-Z0-9_]{" + kMinContrasena + "," + kMaxContrasena + "}$").test(formulario.contrasena.value) || !new RegExp("^.*[a-z].*$").test(formulario.contrasena.value) || !new RegExp("^.*[A-Z].*$").test(formulario.contrasena.value) || !new RegExp("^.*[0-9].*$").test(formulario.contrasena.value))
		alerta = alerta + "- La contraseña sólo puede contener símbolos alfanuméricos (al menos una mayúscula, una minúscula y un número) y subrayado (_) y debe tener entre " + kMinContrasena + " y " + kMaxContrasena + " caracteres.\n\n";

	// Comprobamos que las contraseñas coinciden.
	if (formulario.contrasena.value != formulario.contrasena2.value)
		alerta = alerta + "- Las contraseñas no coinciden.\n\n";

	// Comprobamos que se ha marcado algún sexo.
	if (!formulario.sexo[0].checked && !formulario.sexo[1].checked)
		alerta = alerta + "- Es necesario seleccionar algún sexo.\n\n";

	// Comprobamos que la dirección de correo electrónico es correcta.
	expReg = new RegExp("^[a-zA-Z]+([\\.-]?[a-zA-Z0-9]+)*@[a-zA-Z]+([\\.-]?[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]{2,4})+$");
	if (!expReg.test(formulario.correo_electronico.value))
		alerta = alerta + "- La dirección de correo electrónico no es válida.";

	// Comprobamos que la fecha de nacimiento es correcta.
	var fecha = new Date(parseInt(formulario.ano.value), parseInt(formulario.mes.value - 1), parseInt(formulario.dia.value));
	if (fecha.getDate() != parseInt(formulario.dia.value) || parseInt(formulario.mes.value - 1) != fecha.getMonth() || parseInt(formulario.ano.value) != fecha.getFullYear())
		aleta = alerta + "- La fecha introducida no es válida.\n\n";

	// Si se ha concatenado algún error, se muestra el mensaje y se aborta el "submit" del formulario devolviendo falso.
	if (alerta != "")
	{
		alert(alerta);
		return false;
	}
	else
	{
		return true;
	}
}


/*
function validarIdentificacion(formulario)
{
	// Se extraen las cadenas con el nombre del usuario y la contraseña y se eliminan espacios y tabuladores.
	var nombre_usuario = formulario.nombre_usuario.value.replace(" ", "").replace("\t", "");
	while (nombre_usuario != nombre_usuario.replace(" ", "").replace("\t", ""))
		nombre_usuario = nombre_usuario.replace(" ", "").replace("\t", "");
	var contrasena = formulario.contrasena.value.replace(" ", "").replace("\t", "");
	while (contrasena != contrasena.replace(" ", "").replace("\t", ""))
		contrasena = contrasena.replace(" ", "").replace("\t", "");

	// Se comprueba que todavía existan caracteres en el nombre de usuario y en la contraseña para permitir la consulta al servidor.
	if (nombre_usuario.length > 0 && contrasena.length > 0)
		return true;
	else
		return false;
}

function validarRegistro(formulario)
{
	var alerta = "";

	// Comprobamos que el usuario es correcto.
	if (!Array.every(formulario.nombre_usuario.value, esAlfanumerico) || formulario.nombre_usuario.value.length < kMinNombreUsuario || formulario.nombre_usuario.value.length > kMaxNombreUsuario)
		alerta = alerta + "- El nombre de usuario sólo puede contener símbolos alfanuméricos y debe tener entre " + kMinNombreUsuario + " y " + kMaxNombreUsuario + " caracteres.\n\n";

	// Comprobamos que la contraseña es válida.
	var error = false;
	if (formulario.contrasena.value.length < kMinContrasena || formulario.contrasena.value.length > kMaxContrasena)
		error = true;
	var hayMayuscula = false;
	var hayMinuscula = false;
	for (var i = 0; i < formulario.contrasena.value.length && !error; i++)
	{
		if (!esAlfanumerico(formulario.contrasena.value[i]) && formulario.contrasena.value[i] != "_")
		{
			error = true;
		}

		hayMayuscula = hayMayuscula || (formulario.contrasena.value[i] >= "A" && formulario.contrasena.value[i] <= "Z");
		hayMinuscula = hayMinuscula || (formulario.contrasena.value[i] >= "a" && formulario.contrasena.value[i] <= "z");
	}
	if (error == true || !hayMayuscula || !hayMinuscula)
		alerta = alerta + "- La contraseña sólo puede contener símbolos alfanuméricos (al menos una mayúscula y otra minúscula) y subrayado (_) y debe tener entre " + kMinContrasena + " y " + kMaxContrasena + " caracteres.\n\n";

	// Comprobamos que las contraseñas coinciden.
	if (formulario.contrasena.value != formulario.contrasena2.value)
		alerta = alerta + "- Las contraseñas no coinciden.\n\n";

	// Comprobamos que se ha marcado algún sexo.
	if (!formulario.sexo[0].checked && !formulario.sexo[1].checked)
		alerta = alerta + "- Es necesario seleccionar algún sexo.\n\n";

	// Comprobamos que la dirección de correo electrónico es correcta.
	error  = false;
	for (var k = 0; k < formulario.correo_electronico.value.length; k++)
	{
		if (!esAlfanumerico(formulario.correo_electronico.value[k]) && formulario.correo_electronico.value[k] != "-" && formulario.correo_electronico.value[k] != "@" && formulario.correo_electronico.value[k] != ".")
		{
			error = true;
		}
	}
	if (!error)
	{
		// Dividimos la cadena en dos partes para comprobar que sólo hay una arroba.
		var fragmentos = formulario.correo_electronico.value.split("@");
		if (fragmentos.length != 2)
		{
			error = true;
		}
		else
		{
			// Comprobamos que hay un nombre de persona y un nombre de dominio.
			if (fragmentos[0].length < 1 || fragmentos[1].length < 1)
			{
				error = true;
			}
			else
			{
				// Dividimos el dominio en partes.
				var fragmentosdominio = fragmentos[1].split(".");

				// Comprobamos que hay al menos un dominio principal y una extensión.
				if (fragmentosdominio.length < 2)
				{
					error = true;
				}
				else
				{
					// Comprobamos que el dominio principal tiene al menos 2 caracteres.
					if (fragmentosdominio[0].length < 2)
					{
						error = true;
					}

					// Comprobamos que cada extensión del dominio está entre 2 y 4 caracteres.
					for (var j = 1; j < fragmentosdominio.length && !error; j++)
					{
						if (fragmentosdominio[j].length < 2 || fragmentosdominio[j].length > 4)
						{
							error = true;
						}
					}
				}
			}
		}
	}
	if (error)
		alerta = alerta + "- La dirección de correo electrónico no es válida.";

	// Comprobamos que la fecha de nacimiento es correcta.
	var fecha = new Date(parseInt(formulario.ano.value), parseInt(formulario.mes.value - 1), parseInt(formulario.dia.value));
	if (fecha.getDate() != parseInt(formulario.dia.value) || parseInt(formulario.mes.value - 1) != fecha.getMonth() || parseInt(formulario.ano.value) != fecha.getFullYear())
		aleta = alerta + "- La fecha introducida no es válida.\n\n";

	// Si se ha concatenado algún error, se muestra el mensaje y se aborta el "submit" del formulario devolviendo falso.
	if (alerta != "")
	{
		alert(alerta);
		return false;
	}
	else
	{
		return true;
	}
}

// Funciones auxiliares.
function esAlfanumerico(caracter) {return esLetra(caracter) || esNumero(caracter);}
function esLetra(caracter) {return (caracter >= "a" && caracter <= "z") || (caracter >= "A" && caracter <= "Z");}
function esNumero(caracter) {return (caracter >= "0" && caracter <= "9");}
*/