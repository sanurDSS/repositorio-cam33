<?php

/**
 * Clase que representa un conector para la base de datos.
 */
class BD
{
	private static $ip;
	private static $usuario;
	private static $contrasena;
	private static $nombreBd;

	/**
	 * Establece los datos de acceso a la base de datos.
	 * @param string $ip Dirección IP del servidor de la base de datos.
	 * @param string $usuario Usuario válido de la base de datos.
	 * @param string $contrasena Contraseña para el usuario.
	 * @param string $nombreBd Nombre de la base de datos que se seleccionará después de conectar.
	 */
	public static function espeficarDatos($ip, $usuario, $contrasena, $nombreBd)
	{
		self::$ip = $ip;
		self::$usuario = $usuario;
		self::$contrasena = $contrasena;
		self::$nombreBd = $nombreBd;
	}

	/**
	 * Realiza la conexión a la base de datos.
	 * @return resource Devuelve el enlace de conexión si se ha conectado correctamente a la base de datos. Devuelve NULL si ha fallado.
	 */
	public static function conectar()
	{
		if (!($conexion=mysql_connect(self::$ip,self::$usuario,self::$contrasena)))
		{
			echo "<BD::conectar> No se pudo conectar al servidor de la base de datos.";
			$conexion = NULL;
		}

		if (!mysql_select_db(self::$nombreBd,$conexion))
		{
			echo "<BD::conectar> No se pudo seleccionar la base de datos.";
			$conexion = NULL;
		}

		return $conexion;
	}

	/**
	 * Realiza la desconexión de la base de datos.
	 * @param link_identifier $conexion Identificador de la conexión a la base de datos.
	 * @return bool Devuelve verdadero si se ha cerrado la conexión correctamente.
	 */
	public static function desconectar($conexion = NULL)
	{
		if ($conexion != NULL)
			return mysql_close($conexion);
		else
			return mysql_close();
	}

	/**
	 * Inicia una nueva transacción en la conexón indicada.
	 * @param link_identifier $conexion Identificador de la conexión a la base de datos.
	 * @return resource Resultado de la sentencia.
	 */
	public static function begin($conexion = NULL)
	{
		if ($conexion != NULL)
			return mysql_query("BEGIN;", $conexion);
		else
			return mysql_query("BEGIN;");
	}

	/**
	 * Finaliza la transacción abierta en la conexón indicada.
	 * @param link_identifier $conexion Identificador de la conexión a la base de datos.
	 * @return resource Resultado de la sentencia.
	 */
	public static function commit($conexion = NULL)
	{
		if ($conexion != NULL)
			return mysql_query("COMMIT;", $conexion);
		else
			return mysql_query("COMMIT;");
	}

	/**
	 * Deshace la transacción abierta en la conexón indicada.
	 * @param link_identifier $conexion Identificador de la conexión a la base de datos.
	 * @return resource Resultado de la sentencia.
	 */
	public static function rollback($conexion = NULL)
	{
		if ($conexion != NULL)
			return mysql_query("ROLLBACK;", $conexion);
		else
			return mysql_query("ROLLBACK;");
	}
}
?>
