<?php

require_once 'minilibreria.php';

/**
 * Description of ENPais
 *
 * @author cristian
 */
class ENPais
{
	private $id;
	private $nombre;

	public function getId()
	{
		return $this->id;
	}

	public function getNombre()
	{
		return $this->nombre;
	}

	/**
	 * Constructor de la clase.
	 */
	public function  __construct()
	{
		$this->id = 0;
		$this->nombre = "";
	}

	/**
	 * Obtiene un conjunto de caracteres con los atributos del fabricante. Sobre todo para depuración.
	 * @return string
	 */
	public function toString()
	{
		return "----- PAIS :: $this->nombre($this->id) -----";
	}

	/**
	 * Procesa una fila y devuelve un objeto elaborado con el país.
	 * @param array $fila Tantas componentes como columnas tiene la tabla "paises" de la base de datos.
	 * @return ENPais Devuelve el país con todos sus atributos.
	 */
	private static function obtenerDatos($fila)
	{
		$pais = new ENPais;
		$pais->id = $fila[0];
		$pais->nombre = utf8_encode($fila[1]);
		return $pais;
	}

	/**
	 * Obtiene todos los paises que hay en la base de datos.
	 * @return array Devuelve una lista con todos los paises de la base de datos. Si hay algun error, devuelve NULL.
	 */
	public static function obtenerTodos()
	{
		$lista = NULL;

		try
		{
			$sentencia = "select id, nombre from paises order by nombre";
			$resultado = mysql_query($sentencia, BD::conectar());

			if ($resultado)
			{
				$lista = array();
				$contador = 0;
				while ($fila = mysql_fetch_array($resultado))
				{
					$pais = self::obtenerDatos($fila);
					if ($pais != NULL)
					{
						$lista[$contador++] = $pais;
					}
					else
					{
						echo "<ENPais::obtenerTodos()> País nulo nº $contador";
					}
				}

				BD::desconectar();
			}
			else
			{
				echo "<ENPais::obtenerTodos()>".mysql_error();
			}
		}
		catch (Exception $e)
		{
			$lista = NULL;
			echo "<ENPais::obtenerTodos() ".$e->getMessage();
		}

		return $lista;
	}

	/**
	 * Obtiene un país desde la base de datos a partir de su nombre.
	 * @param string $nombre Nombre del país que se va a obtener.
	 * @return ENPais Devuelve el país con todos sus atributos extraidos desde la base de datos. Devuelve NULL si ocurrió algún error.
	 */
	public static function obtenerPorNombre($nombre)
	{
		$nombre = filtrarCadena($nombre);
		$pais = NULL;

		try
		{
			$sentencia = "select u.id, u.nombre, u.contrasena, u.email, u.sexo, u.fecha_nacimiento, u.fecha_registro, ciudad, p.nombre";
			$sentencia = "$sentencia from paises u, paises p";
			$sentencia = "$sentencia where u.id_pais = p.id and u.nombre = '$nombre'";
			$sentencia = "$sentencia order by u.nombre";
			$resultado = mysql_query($sentencia, BD::conectar());

			if ($resultado)
			{
				$fila = mysql_fetch_array($resultado);
				if ($fila)
				{
					$pais = self::obtenerDatos($fila);
					if ($pais == NULL)
					{
						echo "<ENPais::obtenerPorNombre()> Usuario nulo $nombre";
					}
				}

				BD::desconectar();
			}
			else
			{
				echo "<ENPais::obtenerPorNombre()>".mysql_error();
			}
		}
		catch (Exception $e)
		{
			$pais = NULL;
			echo "<ENPais::obtenerPorNombre() ".$e->getMessage();
		}

		return $pais;
	}

	/**
	 * Obtiene un país desde la base de datos a partir de su identificador.
	 * @param int $id Identificador del país que se va a obtener.
	 * @return ENPais Devuelve el país con todos sus atributos extraidos desde la base de datos. Devuelve NULL si ocurrió algún error.
	 */
	public static function obtenerPorId($id)
	{
		$id = filtrarCadena($id);
		$pais = NULL;

		try
		{
			$sentencia = "select u.id, u.nombre, u.contrasena, u.email, u.sexo, u.fecha_nacimiento, u.fecha_registro, ciudad, p.nombre";
			$sentencia = "$sentencia from paises u, paises p";
			$sentencia = "$sentencia where u.id_pais = p.id and u.id = '$id'";
			$sentencia = "$sentencia order by u.nombre";
			$resultado = mysql_query($sentencia, BD::conectar());

			if ($resultado)
			{
				$fila = mysql_fetch_array($resultado);
				if ($fila)
				{
					$pais = self::obtenerDatos($fila);
					if ($pais == NULL)
					{
						echo "<ENPais::obtenerPorId()> Usuario nulo $nombre";
					}
				}

				BD::desconectar();
			}
			else
			{
				echo "<ENPais::obtenerPorId()>".mysql_error();
			}
		}
		catch (Exception $e)
		{
			$pais = NULL;
			echo "<ENPais::obtenerPorId() ".$e->getMessage();
		}

		return $pais;
	}
}
?>
