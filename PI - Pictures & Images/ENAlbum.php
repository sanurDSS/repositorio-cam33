<?php

require_once 'minilibreria.php';

/**
 * Description of ENAlbum
 *
 * @author cristian
 */
class ENAlbum
{
	private $id;
	private $titulo;
	private $descripcion;
	private $fecha;
	private $pais;
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

	public function getFecha()
	{
		return $this->fecha;
	}

	public function setFecha($fecha)
	{
		$fecha = filtrarCadena($fecha);
		$this->fecha = $fecha;
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

	public function getUsuario()
	{
		return $this->usuario;
	}

	public function setUsuario($usuario)
	{
		$usuario = filtrarCadena($usuario);
		$this->usuario = $usuario;
	}

	/**
	 * Obtiene un conjunto de caracteres con los atributos del fabricante. Sobre todo para depuración.
	 * @return string
	 */
	public function toString()
	{
		return "----- ALBUM :: $this->titulo($this->id) :: $this->descripcion :: $this->fecha :: $this->pais :: $this->usuario -----";
	}

	/**
	 * Procesa una fila y devuelve un objeto elaborado con el album.
	 * @param array $fila Tantas componentes como columnas tiene la tabla "albumes" de la base de datos.
	 * @return ENAlbum Devuelve el album con todos sus atributos.
	 */
	private static function obtenerDatos($fila)
	{
		$album = new ENAlbum;
		$album->id = $fila[0];
		$album->titulo = utf8_encode($fila[1]);
		$album->descripcion = utf8_encode($fila[2]);
		$album->fecha = $fila[3];
		$album->pais = utf8_encode($fila[4]);
		$album->usuario = $fila[5];
		return $album;
	}

	/**
	 * Obtiene todos los albumes que hay en la base de datos.
	 * @return array Devuelve una lista con todos los albumes de la base de datos. Si hay algun error, devuelve NULL.
	 */
	public static function obtenerTodos($usuario="")
	{
		$usuario = filtrarCadena($usuario);
		$lista = NULL;

		try
		{
			$sentencia = "select a.id, a.titulo, a.descripcion, a.fecha, p.nombre, u.nombre from albumes a, usuarios u, paises p";
			$sentencia = "$sentencia where a.id_pais = p.id and a.id_usuario = u.id";

			if ($usuario != "")
				$sentencia = "$sentencia and u.nombre = '$usuario'";

			$sentencia = "$sentencia order by a.id";
			$resultado = mysql_query($sentencia, BD::conectar());

			if ($resultado)
			{
				$lista = array();
				$contador = 0;
				while ($fila = mysql_fetch_array($resultado))
				{
					$album = self::obtenerDatos($fila);
					if ($album != NULL)
					{
						$lista[$contador++] = $album;
					}
					else
					{
						echo "<ENAlbum::obtenerTodos()> Álbum nulo nº $contador";
					}
				}

				BD::desconectar();
			}
			else
			{
				echo "<ENAlbum::obtenerTodos()>".mysql_error();
			}
		}
		catch (Exception $e)
		{
			$lista = NULL;
			echo "<ENAlbum::obtenerTodos() ".$e->getMessage();
		}

		return $lista;
	}

	/**
	 * Obtiene un album desde la base de datos a partir de su identificador.
	 * @param int $id Identificador del album que se va a obtener.
	 * @return ENAlbum Devuelve el album con todos sus atributos extraidos desde la base de datos. Devuelve NULL si ocurrió algún error.
	 */
	public static function obtenerPorId($id)
	{
		$id = filtrarCadena($id);
		$album = NULL;

		try
		{
			$sentencia = "select a.id, a.titulo, a.descripcion, a.fecha, p.nombre, u.nombre from albumes a, usuarios u, paises p";
			$sentencia = "$sentencia where a.id_pais = p.id and a.id_usuario = u.id and a.id = $id";
			$resultado = mysql_query($sentencia, BD::conectar());

			if ($resultado)
			{
				$fila = mysql_fetch_array($resultado);
				if ($fila)
				{
					$album = self::obtenerDatos($fila);
					if ($album == NULL)
					{
						echo "<ENAlbum::obtenerPorId()> Álbum nulo $id";
					}
				}

				BD::desconectar();
			}
			else
			{
				echo "<ENAlbum::obtenerPorId()>".mysql_error();
			}
		}
		catch (Exception $e)
		{
			$album = NULL;
			echo "<ENAlbum::obtenerPorId() ".$e->getMessage();
		}

		return $album;
	}

	/**
	 * Comprueba si un album existe en la base de datos.
	 * @param int $id Identificador del album, que es clave primaria en la tabla "albumes" de la base de datos.
	 * @return bool Devuelve verdadero si existe el album en la base de datos. Falso en caso contrario.
	 */
	public static function existePorId($id)
	{
		return self::obtenerPorId($id) != null;
	}

	/**
	 * Comprueba si el album que invoca el método existe en la base de datos.
	 * @return bool Devuelve verdadero si existe el album en la base de datos. Falso en caso contrario.
	 */
	public function existe()
	{
		return self::existePorId($this->id);
	}

	/**
	 * Guarda en la base de datos el album que invocó el método.
	 * Sólo puede guardarse si no existe en la base de datos. Si ya existe, hay que utilizar el método "actualizar".
	 * Es decir, si el album es nuevo, utilizarás "guardar". Si ha sido extraido de la base de datos, se utilizará "actualizar".
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
						else
						{
							$error = true;
						}
					}
					else
					{
						$error = true;
					}

					// Extraemos el identificador del usuario (debería ser mayor que 0).
					$id_usuario = 0;
					if ($error == false)
					{
						$sentencia = "select id from usuarios where nombre = '".$this->usuario."'";
						$resultado = mysql_query($sentencia, $conexion);
						if ($resultado)
						{
							$fila = mysql_fetch_array($resultado);
							if ($fila)
							{
								$id_usuario = $fila[0];
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

					if ($error == false)
					{
						// Insertamos el album.
						$sentencia = "insert into albumes (titulo, descripcion, fecha, id_pais, id_usuario)";
						$sentencia = "$sentencia values ('".utf8_decode($this->titulo)."', '".utf8_decode($this->descripcion)."', '".$this->fecha."', '".$id_pais."', '".$id_usuario."')";
						$resultado = mysql_query($sentencia, $conexion);

						if ($resultado)
						{
							// Obtenemos el identificador asignado al album recién creado.
							$sentencia = "select max(id) from albumes where id_usuario = '".$id_usuario."'";
							$resultado = mysql_query($sentencia, $conexion);
							if ($resultado)
							{
								$fila = mysql_fetch_array($resultado);
								if ($fila)
								{
									// Asignamos el identificador al album.
									if ($error == false)
									{
										$this->id = $fila[0];
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

					// Si hubo error, deshacemos la operación; si no, la cerramos.
					if ($error == true)
					{
						echo "<ENAlbum::guardar()>".mysql_error();
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
				echo "<ENAlbum::guardar() ".$e->getMessage();
			}
		}

		return $guardado;
	}

	/**
	 * Dado un album, lo elimina de la base de datos (nombre, teléfonos, ...).
	 * @param ENAlbum $album Álbum que va a ser borrado.
	 * @return bool Devuelve verdadero si ha borrado el album. Falso en caso contrario.
	 */
	public function borrar()
	{
		return self::borrarPorId($this->id);
	}

	/**
	 * Dado el identificador de un album, lo elimina de la base de datos (nombre, teléfonos, ...).
	 * @param int $id Identificador del album que va a ser borrado.
	 * @return bool Devuelve verdadero si ha borrado el album. Falso en caso contrario.
	 */
	public static function borrarPorId($id)
	{
		$id = filtrarCadena($id);
		$borrado = false;

		try
		{
			$conexion = BD::conectar();
			$sentencia = "delete from albumes where id = ".$id;
			$resultado = mysql_query($sentencia, $conexion);
			if ($resultado)
			{
				$borrado = true;
			}
			else
			{
				echo "<ENAlbum::borrarPorId(id)>".mysql_error();
			}

			BD::desconectar($conexion);
		}
		catch (Exception $e)
		{
			echo "<ENAlbum::borrarPorId(id) ".$e->getMessage();
		}

		return $borrado;
	}

	public function getFotos()
	{
		$fotosUsuario = ENFoto::obtenerTodos($this->usuario);
		$fotosAlbum = array();
		$contador = 0;
		foreach ($fotosUsuario as $i)
		{
			if ($i->getIdAlbum() == $this->id)
			{
				$fotosAlbum[$contador++] = $i;
			}
		}
		return $fotosAlbum;
	}
}
?>
