# Paso 2 · Conectividad a la base de datos

**Material:** PPT 3.1.2 · **Clase:** [`PruebaConexion`](src/main/java/cl/duoc/vivero/PruebaConexion.java)

## Objetivo

Lograr que un programa Java abra una conexión con MySQL, ejecute una consulta mínima y cierre todo correctamente.

## Las piezas de JDBC

```
Tu programa Java ──► DriverManager ──► Driver (MySQL Connector/J) ──► Servidor MySQL
                       Connection  ◄──  Statement / ResultSet
```

| Pieza | Para qué sirve |
|---|---|
| **JDBC** (`java.sql`) | API estándar de Java para hablar con bases de datos relacionales |
| **Driver** | Traductor entre JDBC y un motor concreto. Para MySQL: `mysql-connector-j` (ver `pom.xml`) |
| **`DriverManager`** | Fábrica: `DriverManager.getConnection(url, usuario, clave)` |
| **`Connection`** | La «llamada telefónica» abierta con la base. Es un recurso **caro y limitado**: hay que cerrarlo |
| **`Statement`** / **`ResultSet`** | Enviar SQL y recorrer las filas de la respuesta |

### La URL de conexión

```
jdbc:mysql://localhost:3306/vivero
 │    │       │         │     └─ base de datos
 │    │       │         └─────── puerto (3306 es el habitual de MySQL)
 │    │       └───────────────── host (el servidor)
 │    └───────────────────────── motor
 └────────────────────────────── protocolo
```

### `try-with-resources`: cerrar sin olvidarse

```java
try (Connection con = DriverManager.getConnection(URL, USUARIO, CLAVE)) {
    ...
} catch (SQLException e) {
    ...
}
```

Todo lo declarado entre paréntesis se cierra **solo** al salir del bloque, incluso si hay una excepción.
Olvidar el cierre deja conexiones abiertas (*fuga de recursos*) hasta que el servidor se queda sin ellas.

## Cómo ejecutarlo

```bash
scripts/crear-bd.sh                    # desde la raíz, una sola vez
cd paso-02-conexion
mvn -q compile exec:java
```

**Resultado esperado**

```
Conexión exitosa a MySQL 8.4.x
Plantas en la tabla: 8
```

## Errores frecuentes

| Mensaje | Causa probable | Qué revisar |
|---|---|---|
| `Communications link failure` | El servidor no responde | ¿MySQL está encendido? ¿puerto correcto? |
| `Access denied for user 'root'` | Usuario o clave incorrectos | `USUARIO` y `CLAVE` en la clase |
| `Unknown database 'vivero'` | La base no existe | Ejecutar `scripts/crear-bd.sh` |
| `No suitable driver found` | Falta el driver | Dependencia en `pom.xml` y recargar Maven |

## ¿Qué problema tiene este código?

La URL, el usuario y la **clave** están escritos dentro de la clase. Si hay diez clases que se conectan, hay diez
lugares donde cambiarlos, y la clave queda publicada en el repositorio.

➡️ **Siguiente:** [Paso 3 · Configuración de la conexión](../paso-03-configuracion/README.md)
