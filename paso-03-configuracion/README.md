# Paso 3 · Configuración de la conexión

**Material:** PPT 3.1.3 · **Clases:** [`DatabaseConnection`](src/main/java/cl/duoc/vivero/database/DatabaseConnection.java) ·
[`PruebaConexion`](src/main/java/cl/duoc/vivero/PruebaConexion.java) · **Config:** [`db.properties`](src/main/resources/db.properties)

## Objetivo

Sacar los datos de conexión del código y dejarlos en **un solo lugar**, con **una sola clase** que sabe abrir conexiones.

## Qué cambia respecto al paso 2

```
Paso 2:   PruebaConexion  ──(URL, usuario, clave escritos aquí)──► MySQL

Paso 3:   PruebaConexion ──► DatabaseConnection ──► db.properties
                                      │              (o variables de entorno)
                                      └──────────────► MySQL
```

1. **`db.properties`** guarda `db.url`, `db.usuario` y `db.clave`. Vive en `src/main/resources`, así que Maven lo
   empaqueta dentro del programa.
2. **Variables de entorno** (`VIVERO_DB_URL`, `VIVERO_DB_USUARIO`, `VIVERO_DB_CLAVE`) tienen **prioridad** sobre el archivo.
   Así la clave real nunca necesita estar en el repositorio:
   ```bash
   VIVERO_DB_CLAVE=mi_clave_real mvn -q compile exec:java
   ```
3. **`DatabaseConnection.getConnection()`** es el único método de toda la aplicación que llama a `DriverManager`.
   Cada llamada abre una conexión **nueva**; quien la pide la cierra con `try-with-resources`.

```java
try (Connection con = DatabaseConnection.getConnection();
     Statement st = con.createStatement();
     ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM planta")) {
    ...
}
```

Una clase que no se instancia (`private` constructor, métodos `static`) es una **clase de utilidad**.

## Parámetros útiles en la URL

```
jdbc:mysql://localhost:3306/vivero?sslMode=DISABLED&allowPublicKeyRetrieval=true
```

`sslMode=DISABLED` y `allowPublicKeyRetrieval=true` evitan errores de autenticación en un MySQL **local** de laboratorio.
**No** los uses para conectarte a un servidor real a través de una red.

## Diagnóstico de errores

`PruebaConexion.diagnostico()` convierte el código de error de MySQL en una pista:

| `getErrorCode()` | Significa | Pista |
|:---:|---|---|
| 1045 | Acceso denegado | Revisar `db.usuario` / `db.clave` |
| 1049 | Base inexistente | Ejecutar `scripts/crear-bd.sh` |
| 1146 | Tabla inexistente | Ejecutar `scripts/crear-bd.sh` |
| 0 | Sin respuesta del servidor | ¿MySQL encendido? ¿puerto correcto? |

## Cómo ejecutarlo

```bash
cd paso-03-configuracion
mvn -q compile exec:java
```

Resultado esperado: `Conexión exitosa. Plantas en la tabla: 8`

**Pruébalo mal a propósito:** cambia `db.clave` por otra cosa y ejecuta de nuevo; luego apaga MySQL y repite.
Ver el error en un entorno controlado es la mejor forma de reconocerlo en la evaluación.

➡️ **Siguiente:** [Paso 4 · Consultas y manipulación de datos](../paso-04-crud-statement/README.md)
