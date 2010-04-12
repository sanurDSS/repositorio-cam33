<?php

require_once 'minilibreria.php';

/**
 * Description of ENUsuario
 *
 * @author cristian
 */
class ENUsuario
{
	/**
	 * Identificador único del usuario. Es clave candidata en la base de datos.
	 * Si tiene valor 0, significa que el usuario no existe en la base de datos.
	 * @var int
	 */
	private $id;

	/**
	 * Nombre del usuario. No puede repetirse en distintos usuarios.
	 * @var string
	 */
	private $nombre;

	/**
	 * Contraseña del usuario.
	 * @var string
	 */
	 private $contrasena;

	 /**
	  * Dirección de correo electrónico.
	  * @var string
	  */
	 private $email;

	 /**
	  * Sexo del usuario ("hombre" o "mujer").
	  * @var string
	  */
	 private $sexo;

	 /**
	  * Fecha de nacimiento del usuario.
	  * AAAA-MM-DD
	  * @var date
	  */
	 private $fecha_nacimiento;

	 /**
	  * Fecha y hora de registro del usuario en la base de datos (con la fecha del servidor).
	  * AAAA-MM-DD HH:MM:SS
	  * @var datetime
	  */
	 private $fecha_registro;

	 /**
	  * Ciudad actual del usuario.
	  * @var string
	  */
	 private $ciudad;

	 /**
	  * País del usuario (cadena de caracteres). Debe existir en la base de datos.
	  * @var string
	  */
	 private $pais;

	 public function getId()
	 {
		 return $this->id;
	 }

	 public function getNombre()
	 {
		 return $this->nombre;
	 }

	 public function setNombre($nombre)
	 {
		 $nombre = filtrarCadena($nombre);
		 $this->nombre = $nombre;
	 }

	 public function getContrasena()
	 {
		 return $this->contrasena;
	 }

	 public function setContrasena($contrasena)
	 {
		 $contrasena = filtrarCadena($contrasena);
		 $this->contrasena = $contrasena;
	 }

	 public function getEmail()
	 {
		 return $this->email;
	 }

	 public function setEmail($email)
	 {
		 $email = filtrarCadena($email);
		 $this->email = $email;
	 }

	 public function getSexo()
	 {
		 return $this->sexo;
	 }

	 public function setSexo($sexo)
	 {
		 if ($sexo != "hombre")
			$sexo = "mujer";
		 $this->sexo = $sexo;
	 }

	 public function getFechaNacimiento()
	 {
		 return $this->fecha_nacimiento;
	 }

	 public function setFechaNacimiento($fecha_nacimiento)
	 {
		 $fecha_nacimiento = filtrarCadena($fecha_nacimiento);
		 $this->fecha_nacimiento = $fecha_nacimiento;
	 }

	 public function getFechaRegistro()
	 {
		 return $this->fecha_registro;
	 }

	 public function getCiudad()
	 {
		 return $this->ciudad;
	 }

	 public function setCiudad($ciudad)
	 {
		 $ciudad = filtrarCadena($ciudad);
		 $this->ciudad = $ciudad;
	 }

	 public function getPais()
	 {
		 return $this->pais;
	 }

	 public function setPais($pais)
	 {
		 $pais = filtrarCadena($pais);
		 $this->pais = $pais;
	 }

	/**
	 * Constructor de la clase.
	 */
	public function  __construct()
	{
		$this->id = 0;
		$this->nombre = "";
		$this->contrasena = "";
		$this->email = "";
		$this->sexo = null;
		$this->fecha_nacimiento = null;
		$this->fecha_registro = null;
		$this->ciudad = "";
		$this->pais = "";
	}

	/**
	 * Obtiene un conjunto de caracteres con los atributos del fabricante. Sobre todo para depuración.
	 * @return string
	 */
	public function toString()
	{
		return "----- USUARIO :: $this->nombre($this->id) :: $this->contrasena :: $this->email :: $this->sexo :: $this->fecha_nacimiento :: $this->fecha_registro :: $this->ciudad :: $this->pais -----";
	}

	/**
	 * Procesa una fila y devuelve un objeto elaborado con el usuario.
	 * @param array $fila Tantas componentes como columnas tiene la tabla "usuarios" de la base de datos.
	 * @return ENUsuario Devuelve el usuario con todos sus atributos.
	 */
	private static function obtenerDatos($fila)
	{
		$usuario = new ENUsuario;
		$usuario->id = $fila[0];
		$usuario->nombre = $fila[1];
		$usuario->contrasena = $fila[2];
		$usuario->email = $fila[3];
		$usuario->sexo = $fila[4];
		$usuario->fecha_nacimiento = $fila[5];
		$usuario->fecha_registro = $fila[6];
		$usuario->ciudad = utf8_encode($fila[7]);
		$usuario->pais = utf8_encode($fila[8]);
		return $usuario;
	}

	/**
	 * Obtiene todos los usuarios que hay en la base de datos.
	 * @return array Devuelve una lista con todos los usuarios de la base de datos. Si hay algun error, devuelve NULL.
	 */
	public static function obtenerTodos()
	{
		$listaUsuarios = NULL;

		try
		{
			$sentencia = "select u.id, u.nombre, u.contrasena, u.email, u.sexo, u.fecha_nacimiento, u.fecha_registro, ciudad, p.nombre from usuarios u, paises p where u.id_pais = p.id order by u.nombre";
			$resultado = mysql_query($sentencia, BD::conectar());

			if ($resultado)
			{
				$listaUsuarios = array();
				$contador = 0;
				while ($fila = mysql_fetch_array($resultado))
				{
					$usuario = self::obtenerDatos($fila);
					if ($usuario != NULL)
					{
						$listaUsuarios[$contador++] = $usuario;
					}
					else
					{
						echo "<ENUsuario::obtenerTodos()> Usuario nulo nº $contador";
					}
				}

				BD::desconectar();
			}
			else
			{
				echo "<ENUsuario::obtenerTodos()>".mysql_error();
			}
		}
		catch (Exception $e)
		{
			$listaUsuarios = NULL;
			echo "<ENUsuario::obtenerTodos() ".$e->getMessage();
		}

		return $listaUsuarios;
	}

	/**
	 * Obtiene un usuario desde la base de datos a partir de su nombre.
	 * @param string $nombre Nombre del usuario que se va a obtener.
	 * @return ENUsuario Devuelve el usuario con todos sus atributos extraidos desde la base de datos. Devuelve NULL si ocurrió algún error.
	 */
	public static function obtenerPorNombre($nombre)
	{
		$nombre = filtrarCadena($nombre);
		$usuario = NULL;

		try
		{
			$sentencia = "select u.id, u.nombre, u.contrasena, u.email, u.sexo, u.fecha_nacimiento, u.fecha_registro, ciudad, p.nombre";
			$sentencia = "$sentencia from usuarios u, paises p";
			$sentencia = "$sentencia where u.id_pais = p.id and u.nombre = '$nombre'";
			$sentencia = "$sentencia order by u.nombre";
			$resultado = mysql_query($sentencia, BD::conectar());

			if ($resultado)
			{
				$fila = mysql_fetch_array($resultado);
				if ($fila)
				{
					$usuario = self::obtenerDatos($fila);
					if ($usuario == NULL)
					{
						echo "<ENUsuario::obtenerPorNombre()> Usuario nulo $nombre";
					}
				}

				BD::desconectar();
			}
			else
			{
				echo "<ENUsuario::obtenerPorNombre()>".mysql_error();
			}
		}
		catch (Exception $e)
		{
			$usuario = NULL;
			echo "<ENUsuario::obtenerPorNombre() ".$e->getMessage();
		}

		return $usuario;
	}

	/**
	 * Obtiene un usuario desde la base de datos a partir de su identificador.
	 * @param int $id Identificador del usuario que se va a obtener.
	 * @return ENUsuario Devuelve el usuario con todos sus atributos extraidos desde la base de datos. Devuelve NULL si ocurrió algún error.
	 */
	public static function obtenerPorId($id)
	{
		$id = filtrarCadena($id);
		$usuario = NULL;

		try
		{
			$sentencia = "select u.id, u.nombre, u.contrasena, u.email, u.sexo, u.fecha_nacimiento, u.fecha_registro, ciudad, p.nombre";
			$sentencia = "$sentencia from usuarios u, paises p";
			$sentencia = "$sentencia where u.id_pais = p.id and u.id = '$id'";
			$sentencia = "$sentencia order by u.nombre";
			$resultado = mysql_query($sentencia, BD::conectar());

			if ($resultado)
			{
				$fila = mysql_fetch_array($resultado);
				if ($fila)
				{
					$usuario = self::obtenerDatos($fila);
					if ($usuario == NULL)
					{
						echo "<ENUsuario::obtenerPorId()> Usuario nulo $nombre";
					}
				}

				BD::desconectar();
			}
			else
			{
				echo "<ENUsuario::obtenerPorId()>".mysql_error();
			}
		}
		catch (Exception $e)
		{
			$usuario = NULL;
			echo "<ENUsuario::obtenerPorId() ".$e->getMessage();
		}

		return $usuario;
	}

	/**
	 * Comprueba si un usuario existe en la base de datos.
	 * @param string $nombre Nombre del usuario.
	 * @return bool Devuelve verdadero si existe el usuario en la base de datos. Falso en caso contrario.
	 */
	public static function existePorNombre($nombre)
	{
		return self::obtenerPorNombre($nombre) != null;
	}

	/**
	 * Comprueba si un usuario existe en la base de datos.
	 * @param int $id Identificador del usuario, que es clave primaria en la tabla "usuarios" de la base de datos.
	 * @return bool Devuelve verdadero si existe el usuario en la base de datos. Falso en caso contrario.
	 */
	public static function existePorId($id)
	{
		return self::obtenerPorId($id) != null;
	}

	/**
	 * Comprueba si el usuario que invoca el método existe en la base de datos.
	 * @return bool Devuelve verdadero si existe el usuario en la base de datos. Falso en caso contrario.
	 */
	public function existe()
	{
		return self::existePorId($this->id);
	}

	/**
	 * Guarda en la base de datos el usuario que invocó el método.
	 * Sólo puede guardarse si no existe en la base de datos. Si ya existe, hay que utilizar el método "actualizar".
	 * Es decir, si el usuario es nuevo, utilizarás "guardar". Si ha sido extraido de la base de datos, se utilizará "actualizar".
	 * @return bool Devuelve verdadero si se ha guardado correctamente. Falso en caso contrario.
	 */
	public function guardar()
	{
		$guardado = false;

		if ($this->id == 0)
		{
			try
			{
				$conexion = BD::conectar();

				// Iniciamos la transacción.
				if (BD::begin($conexion))
				{
					$error = false;

					// Extraemos el identificador del país (debería ser mayor que 0).
					$id_pais = 0;
					$sentencia = "select id from paises where nombre = '".utf8_decode($this->pais)."'";
					$resultado = mysql_query($sentencia, $conexion);

					if ($resultado)
					{
						$fila = mysql_fetch_array($resultado);
						if ($fila)
						{
							$id_pais = $fila[0];
						}

						// Insertamos el usuario.
						$sentencia = "insert into usuarios (nombre, contrasena, email, sexo, fecha_nacimiento, fecha_registro, ciudad, id_pais)";
						$sentencia = "$sentencia values ('".$this->nombre."', '".$this->contrasena."', '".$this->email."', '".$this->sexo."', '".$this->fecha_nacimiento."', now(), '".utf8_decode($this->ciudad)."', ".$id_pais.")";
						$resultado = mysql_query($sentencia, $conexion);

						if ($resultado)
						{
							// Obtenemos el identificador asignado al usuario recién creado.
							$sentencia = "select id, fecha_registro from usuarios where nombre = '".$this->nombre."'";
							$resultado = mysql_query($sentencia, $conexion);

							if ($resultado)
							{
								$fila = mysql_fetch_array($resultado);
								if ($fila)
								{
									// Asignamos el identificador al usuario.
									if ($error == false)
									{
										$this->id = $fila[0];
										$this->fecha_registro = $fila[1];
										$guardado = true;
									}
								}
							}
							else
							{
								$error = true;
							}
						}
						else
						{
							$error = true;
						}
					}
					else
					{
						$error = true;
					}

					// Si hubo error, deshacemos la operación; si no, la cerramos.
					if ($error == true)
					{
						echo "<ENUsuario::guardar()>".mysql_error();
						BD::rollback($conexion);
					}
					else
					{
						BD::commit($conexion);
						BD::desconectar($conexion);
					}
				}
			}
			catch (Exception $e)
			{
				echo "<ENUsuario::guardar() ".$e->getMessage();
			}
		}

		return $guardado;
	}

	/**
	 * Actualiza en la base de datos el usuario que invocó el método.
	 * Ya debe existir el usuario en la base de datos.
	 * @return bool Devuelve verdadero si se ha actualizado correctamente. Falso en caso contrario.
	 */
	public function actualizar()
	{
		$guardado = false;

		if ($this->id > 0)
		{
			try
			{
				$conexion = BD::conectar();

				// Iniciamos la transacción.
				if (BD::begin($conexion))
				{
					$error = false;

					// Extraemos el identificador del país (debería ser mayor que 0).
					$id_pais = 0;
					$sentencia = "select id from paises where nombre = '".utf8_decode($this->pais)."'";
					$resultado = mysql_query($sentencia, $conexion);

					if ($resultado)
					{
						$fila = mysql_fetch_array($resultado);
						if ($fila)
						{
							$id_pais = $fila[0];
						}

						// Actualizamos el usuario.
						$sentencia = "update usuarios set nombre = '".$this->nombre."', contrasena = '".$this->contrasena."', email = '".$this->email."', sexo = '".$this->sexo."', fecha_nacimiento = '".$this->fecha_nacimiento."', ciudad = '".utf8_decode($this->ciudad)."', id_pais = $id_pais";
						$sentencia = "$sentencia where id = $this->id";

						echo "<br/><br/> $sentencia  <br/><br/>";
						$resultado = mysql_query($sentencia, $conexion);

						if ($resultado)
						{
							$guardado = true;
						}
						else
						{
							$error = true;
						}
					}
					else
					{
						$error = true;
					}

					// Si hubo error, deshacemos la operación; si no, la cerramos.
					if ($error == true)
					{
						echo "<ENUsuario::actualizar()>".mysql_error();
						BD::rollback($conexion);
					}
					else
					{
						BD::commit($conexion);
						BD::desconectar($conexion);
					}
				}
			}
			catch (Exception $e)
			{
				echo "<ENUsuario::actualizar() ".$e->getMessage();
			}
		}

		return $guardado;
	}

	/**
	 * Dado un usuario, lo elimina de la base de datos (nombre, teléfonos, ...).
	 * @param ENUsuario $usuario Usuario que va a ser borrado.
	 * @return bool Devuelve verdadero si ha borrado el usuario. Falso en caso contrario.
	 */
	public function borrar()
	{
		return self::borrarPorNombre($this->nombre);
	}

	/**
	 * Dado el nombre de usuario, lo elimina de la base de datos (nombre, teléfonos, ...).
	 * @param string $nombre Nombre del usuario que va a ser borrado.
	 * @return bool Devuelve verdadero si ha borrado el usuario. Falso en caso contrario.
	 */
	public static function borrarPorNombre($nombre)
	{
		$nombre = filtrarCadena($nombre);
		$borrado = false;

		try
		{
			$conexion = BD::conectar();

			$sentencia = "delete from usuarios where nombre = '".$nombre."'";
			$resultado = mysql_query($sentencia, $conexion);
			if ($resultado)
			{
				$borrado = true;
			}
			else
			{
				echo "<ENUsuario::borrarPorNombre(nombre)>".mysql_error();
			}
			
			BD::desconectar($conexion);
		}
		catch (Exception $e)
		{
			echo "<ENUsuario::borrarPorNombre(nombre) ".$e->getMessage();
		}

		return $borrado;
	}

	/**
	 * Dado el identificador de un usuario, lo elimina de la base de datos (nombre, teléfonos, ...).
	 * @param int $id Identificador del usuario que va a ser borrado.
	 * @return bool Devuelve verdadero si ha borrado el usuario. Falso en caso contrario.
	 */
	public static function borrarPorId($id)
	{
		$id = filtrarCadena($id);
		$borrado = false;

		try
		{
			$conexion = BD::conectar();

			$sentencia = "delete from usuarios where id = ".$id;
			$resultado = mysql_query($sentencia, $conexion);
			if ($resultado)
			{
				$borrado = true;
			}
			else
			{
				echo "<ENUsuario::borrarPorId(id)>".mysql_error();
			}

			BD::desconectar($conexion);
		}
		catch (Exception $e)
		{
			echo "<ENUsuario::borrarPorId(id) ".$e->getMessage();
		}

		return $borrado;
	}

	/**
	 * Obtiene los álbumes del usuario. Accede a base de datos.
	 * @return array Devuelve la lista con los álbumes del usuario.
	 */
	public function obtenerAlbumes()
	{
		return ENAlbum::obtenerTodos($this->nombre);
	}

	/**
	 * Dada una foto que acaba de ser enviada por el método post, la guarda en un fichero físico.
	 * Utiliza el identificador del usuario para crear el avatar.
	 * @param resource $httpPostFile Elemento de $HTTP_POST_FILES ($_FILES) que se quiere guardar. Por ejemplo, $_FILES['foto_subida'].
	 * @return bool Devuelve verdadero si ha creado los ficheros correctamente (foto y miniatura).
	 */
	public function setAvatar($httpPostFile)
	{
		//http://emilio.aesinformatica.com/2007/05/03/subir-una-imagen-con-php/
		$creada = false;

		if ($this->id > 0 && is_uploaded_file($httpPostFile['tmp_name']))
		{
			// Establecemos las rutas de las fotos y las miniaturas.
			$rutaFoto = "avatares/$this->id.jpg";
			$rutaMiniatura0 = "avatares/m0$this->id.jpg";
			$rutaMiniatura1 = "avatares/m1$this->id.jpg";
			$rutaMiniatura2 = "avatares/m2$this->id.jpg";
			$rutaMiniatura3 = "avatares/m3$this->id.jpg";
			$rutaMiniatura4 = "avatares/m4$this->id.jpg";
			$rutaMiniatura5 = "avatares/m5$this->id.jpg";

			// Hay que intentar borrar las anteriores. No importa si falla.
			borrarFichero($rutaFoto);
			borrarFichero($rutaMiniatura0);
			borrarFichero($rutaMiniatura1);
			borrarFichero($rutaMiniatura2);
			borrarFichero($rutaMiniatura3);
			borrarFichero($rutaMiniatura4);
			borrarFichero($rutaMiniatura5);

			// Luego hay que copiar el fichero de la imagen a la ruta de la foto.
			if (@move_uploaded_file($httpPostFile['tmp_name'], $rutaFoto))
			{
				if (@chmod($rutaFoto, 0777))
				{
					$miniatura=new thumbnail($rutaFoto);
					//$miniatura->size_width(60);
					//$miniatura->size_height(60);
					$miniatura->size_auto(60);
					$miniatura->jpeg_quality(100);
					$miniatura->save($rutaMiniatura0);

					$miniatura=new thumbnail($rutaFoto);
					//$miniatura->size_width(100);
					//$miniatura->size_height(100);
					$miniatura->size_auto(100);
					$miniatura->jpeg_quality(100);
					$miniatura->save($rutaMiniatura1);

					$miniatura=new thumbnail($rutaFoto);
					//$miniatura->size_width(200);
					//$miniatura->size_height(200);
					$miniatura->size_auto(200);
					$miniatura->jpeg_quality(100);
					$miniatura->save($rutaMiniatura2);

					$miniatura=new thumbnail($rutaFoto);
					//$miniatura->size_width(300);
					//$miniatura->size_height(300);
					$miniatura->size_auto(300);
					$miniatura->jpeg_quality(100);
					$miniatura->save($rutaMiniatura3);

					$miniatura=new thumbnail($rutaFoto);
					//$miniatura->size_width(400);
					//$miniatura->size_height(400);
					$miniatura->size_auto(400);
					$miniatura->jpeg_quality(100);
					$miniatura->save($rutaMiniatura4);

					$miniatura=new thumbnail($rutaFoto);
					//$miniatura->size_width(500);
					//$miniatura->size_height(500);
					$miniatura->size_auto(500);
					$miniatura->jpeg_quality(100);
					$miniatura->save($rutaMiniatura5);

					$creada = true;
				}
			}
		}

		return $creada;
	}

	/**
	 * Obtiene la ruta del avatar del usuario.
	 * @return string Devuevle la ruta del avatar ("" si no tiene avatar).
	 */
	public function getAvatar()
	{
		$ruta = "avatares/m1$this->id.jpg";
		if (file_exists($ruta))
			return $ruta;
		else
			return "";
	}
}
?>
