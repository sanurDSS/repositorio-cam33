<?php

require_once 'minilibreria.php';

/**
 * Description of ENFoto
 *
 * @author cristian
 */
class ENFoto
{
	private $id;
	private $titulo;
	private $descripcion;
	private $nombre_original;
	private $fecha;
	private $fecha_registro;
	private $pais;
	private $id_album;
	private $usuario;

	public function getId()
	{
		return $this->id;
	}

	public function getTitulo()
	{
		return $this->titulo;
	}

	public function setTitulo($titulo)
	{
		$titulo = filtrarCadena($titulo);
		$this->titulo = $titulo;
	}

	public function getDescripcion()
	{
		return $this->descripcion;
	}

	public function setDescripcion($descripcion)
	{
		$descripcion = filtrarCadena($descripcion);
		$this->descripcion = $descripcion;
	}

	public function getNombreOriginal()
	{
		return $this->nombre_original;
	}

	public function getFecha()
	{
		return $this->fecha;
	}

	public function setFecha($fecha)
	{
		$this->fecha = $fecha;
	}

	public function getFechaRegistro()
	{
		return $this->fecha_registro;
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

	public function getIdAlbum()
	{
		return $this->id_album;
	}

	public function setIdAlbum($id_album)
	{
		if (is_numeric($id_album))
			$this->id_album = $id_album;
	}

	public function getUsuario()
	{
		return $this->usuario;
	}

	/**
	 * Constructor de la clase.
	 */
	public function  __construct()
	{
		$this->id = 0;
		$this->titulo = "";
		$this->descripcion = "";
		$this->nombre_original = "";
		$this->fecha = null;
		$this->fecha_registro = null;
		$this->pais = "";
		$this->id_album = 0;
		$this->usuario = "";
	}

	/**
	 * Obtiene un conjunto de caracteres con los atributos del fabricante. Sobre todo para depuración.
	 * @return string
	 */
	public function toString()
	{
		return "----- FOTO :: $this->titulo($this->id) :: $this->descripcion :: $this->fecha :: $this->fecha_registro :: $this->pais :: $this->id_album :: $this->usuario -----";
	}

	/**
	 * Procesa una fila y devuelve un objeto elaborado con las fotos.
	 * @param array $fila Tantas componentes como columnas tiene la tabla "fotos" de la base de datos.
	 * @return ENFoto Devuelve las fotos con todos sus atributos.
	 */
	private static function obtenerDatos($fila)
	{
		$foto = new ENFoto;
		$foto->id = $fila[0];
		$foto->titulo = utf8_encode($fila[1]);
		$foto->descripcion = utf8_encode($fila[2]);
		$foto->nombre_original = utf8_encode($fila[3]);
		$foto->fecha = $fila[4];
		$foto->fecha_registro = $fila[5];
		$foto->pais = utf8_encode($fila[6]);
		$foto->id_album = $fila[7];
		$foto->usuario = $fila[8];
		return $foto;
	}

	/**
	 * Obtiene todos las fotos que hay en la base de datos.
	 * @return array Devuelve una lista con todas las fotos de la base de datos. Si hay algun error, devuelve NULL.
	 */
	public static function obtenerTodos($usuario = "")
	{
		$usuario = filtrarCadena($usuario);
		$lista = NULL;

		try
		{
			$sentencia = "select f.id, f.titulo, f.descripcion, f.nombre_original, f.fecha, f.fecha_registro, p.nombre, f.id_album, u.nombre from fotos f, usuarios u, paises p, albumes a";
			$sentencia = "$sentencia where f.id_pais = p.id and f.id_album = a.id and a.id_usuario = u.id";

			if ($usuario != "")
				$sentencia = "$sentencia and u.nombre = '$usuario'";

			$sentencia = "$sentencia order by f.id";
			$resultado = mysql_query($sentencia, BD::conectar());

			if ($resultado)
			{
				$lista = array();
				$contador = 0;
				while ($fila = mysql_fetch_array($resultado))
				{
					$foto = self::obtenerDatos($fila);
					if ($foto != NULL)
					{
						$lista[$contador++] = $foto;
					}
					else
					{
						echo "<ENFoto::obtenerTodos()> Foto nula nº $contador";
					}
				}

				BD::desconectar();
			}
			else
			{
				echo "<ENFoto::obtenerTodos()>".mysql_error();
			}
		}
		catch (Exception $e)
		{
			$lista = NULL;
			echo "<ENFoto::obtenerTodos() ".$e->getMessage();
		}

		return $lista;
	}

	/**
	 * Obtiene una foto desde la base de datos a partir de su identificador.
	 * @param int $id Identificador de la foto que se va a obtener.
	 * @return ENFoto Devuelve las fotos con todos sus atributos extraidos desde la base de datos. Devuelve NULL si ocurrió algún error.
	 */
	public static function obtenerPorId($id)
	{
		$id = filtrarCadena($id);
		$foto = NULL;

		try
		{
			$sentencia = "select f.id, f.titulo, f.descripcion, f.nombre_original, f.fecha, f.fecha_registro, p.nombre, f.id_album, u.nombre from fotos f, usuarios u, paises p, albumes a";
			$sentencia = "$sentencia where f.id_pais = p.id and f.id_album = a.id and a.id_usuario = u.id";
			$sentencia = "$sentencia and f.id = '$id'";
			$resultado = mysql_query($sentencia, BD::conectar());

			if ($resultado)
			{
				$fila = mysql_fetch_array($resultado);
				if ($fila)
				{
					$foto = self::obtenerDatos($fila);
					if ($foto == NULL)
					{
						echo "<ENFoto::obtenerPorId()> Foto nula $id";
					}
				}

				BD::desconectar();
			}
			else
			{
				echo "<ENFoto::obtenerPorId()>".mysql_error();
			}
		}
		catch (Exception $e)
		{
			$foto = NULL;
			echo "<ENFoto::obtenerPorId() ".$e->getMessage();
		}

		return $foto;
	}

	/**
	 * Comprueba si una foto existe en la base de datos.
	 * @param int $id Identificador de la foto, que es clave primaria en la tabla "fotos" de la base de datos.
	 * @return bool Devuelve verdadero si existe las fotos en la base de datos. Falso en caso contrario.
	 */
	public static function existePorId($id)
	{
		return self::obtenerPorId($id) != null;
	}

	/**
	 * Comprueba si las fotos que invoca el método existe en la base de datos.
	 * @return bool Devuelve verdadero si existe las fotos en la base de datos. Falso en caso contrario.
	 */
	public function existe()
	{
		return self::existePorId($this->id);
	}

	/**
	 * Guarda en la base de datos las fotos que invocó el método.
	 * Sólo puede guardarse si no existe en la base de datos. Si ya existe, hay que utilizar el método "actualizar".
	 * Es decir, si las fotos es nuevo, utilizarás "guardar". Si ha sido extraido de la base de datos, se utilizará "actualizar".
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

						// Insertamos las fotos.
						$sentencia = "insert into fotos (titulo, descripcion, nombre_original, fecha, fecha_registro, id_pais, id_album)";
						$sentencia = "$sentencia values ('".utf8_decode($this->titulo)."', '".utf8_decode($this->descripcion)."', '".utf8_decode($this->nombre_original)."', '".$this->fecha."', now(), $id_pais, '$this->id_album')";
						$resultado = mysql_query($sentencia, $conexion);

						if ($resultado)
						{
							// Obtenemos el identificador asignado a la foto recién creado.
							$sentencia = "select id, fecha_registro from usuarios where nombre = '".$this->nombre."'";
							$resultado = mysql_query($sentencia, $conexion);

							if ($resultado)
							{
								$fila = mysql_fetch_array($resultado);
								if ($fila)
								{
									// Asignamos el identificador a la foto.
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
						echo "<ENFoto::guardar()>".mysql_error();
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
				echo "<ENFoto::guardar() ".$e->getMessage();
			}
		}

		return $guardado;
	}

	/**
	 * Dado una foto, la elimina de la base de datos.
	 * @param ENFoto $foto Usuario que va a ser borrado.
	 * @return bool Devuelve verdadero si ha borrado las fotos. Falso en caso contrario.
	 */
	public function borrar()
	{
		return self::borrarPorId($this->id);
	}

	/**
	 * Dado el identificador de una foto, lo elimina de la base de datos (nombre, teléfonos, ...).
	 * @param int $id Identificador de la foto que va a ser borrado.
	 * @return bool Devuelve verdadero si ha borrado las fotos. Falso en caso contrario.
	 */
	public static function borrarPorId($id)
	{
		$id = filtrarCadena($id);
		$borrado = false;

		try
		{
			$conexion = BD::conectar();

			$sentencia = "delete from fotos where id = ".$id;
			$resultado = mysql_query($sentencia, $conexion);
			if ($resultado)
			{
				$borrado = true;
			}
			else
			{
				echo "<ENFoto::borrarPorId(id)>".mysql_error();
			}

			BD::desconectar($conexion);
		}
		catch (Exception $e)
		{
			echo "<ENFoto::borrarPorId(id) ".$e->getMessage();
		}

		return $borrado;
	}

	/**
	 * Dada una foto que acaba de ser enviada por el método post, la guarda en un fichero físico.
	 * Utiliza el identificador de la foto para crearla físicamente.
	 * @param resource $httpPostFile Elemento de $HTTP_POST_FILES ($_FILES) que se quiere guardar. Por ejemplo, $_FILES['foto_subida'].
	 * @return bool Devuelve verdadero si ha creado los ficheros correctamente (foto y miniatura).
	 */
	public function setFoto($httpPostFile)
	{
		//http://emilio.aesinformatica.com/2007/05/03/subir-una-imagen-con-php/
		$creada = false;

		if ($this->id > 0 && is_uploaded_file($httpPostFile['tmp_name']))
		{
			// Establecemos las rutas de las fotos y las miniaturas.
			$rutaFoto = "fotos/$this->id.jpg";
			$rutaMiniatura1 = "fotos/m1$this->id.jpg";
			$rutaMiniatura2 = "fotos/m2$this->id.jpg";
			$rutaMiniatura3 = "fotos/m3$this->id.jpg";
			$rutaMiniatura4 = "fotos/m4$this->id.jpg";

			// Hay que intentar borrar las anteriores. No importa si falla.
			borrarFichero($rutaFoto);
			borrarFichero($rutaMiniatura1);
			borrarFichero($rutaMiniatura2);
			borrarFichero($rutaMiniatura3);
			borrarFichero($rutaMiniatura4);

			// Luego hay que copiar el fichero de la imagen a la ruta de la foto.
			if (@move_uploaded_file($httpPostFile['tmp_name'], $rutaFoto))
			{
				if (@chmod($rutaFoto, 0777))
				{
					$miniatura=new thumbnail($rutaFoto);
					//$miniatura->size_width(100);
					//$miniatura->size_height(100);
					$miniatura->size_auto(150);
					$miniatura->jpeg_quality(100);
					$miniatura->save($rutaMiniatura1);

					$miniatura=new thumbnail($rutaFoto);
					//$miniatura->size_width(200);
					//$miniatura->size_height(200);
					$miniatura->size_auto(250);
					$miniatura->jpeg_quality(100);
					$miniatura->save($rutaMiniatura2);

					$miniatura=new thumbnail($rutaFoto);
					//$miniatura->size_width(300);
					//$miniatura->size_height(300);
					$miniatura->size_auto(350);
					$miniatura->jpeg_quality(100);
					$miniatura->save($rutaMiniatura3);

					$miniatura=new thumbnail($rutaFoto);
					//$miniatura->size_width(400);
					//$miniatura->size_height(400);
					$miniatura->size_auto(450);
					$miniatura->jpeg_quality(100);
					$miniatura->save($rutaMiniatura4);

					$this->nombre_original = $httpPostFile['tmp_name'];
					$creada = true;
				}
			}
		}

		return $creada;
	}
}
?>
