# Paso 4 · Consultas y manipulación de datos

**Material:** PPT 3.1.4 · **Clases:** [`PlantaCrudStatement`](src/main/java/cl/duoc/vivero/dao/PlantaCrudStatement.java) ·
[`PlantaMapper`](src/main/java/cl/duoc/vivero/dao/PlantaMapper.java) · [`Main`](src/main/java/cl/duoc/vivero/Main.java)

## Objetivo

Ejecutar el CRUD completo desde Java: leer filas y convertirlas en **objetos del modelo**, y crear, modificar y eliminar filas.

## Qué se agrega

- El **modelo** de la EP2 (`cl.duoc.vivero.model`). Único cambio: `Planta` tiene `id` (0 = aún no guardada).
- `PlantaMapper`: convierte una fila en una `PlantaInterior` o `PlantaExterior` según la columna `tipo`.
- `PlantaCrudStatement`: un método por operación.

## La regla de oro: qué método llamar

| Sentencia | Método | Devuelve |
|---|---|---|
| `SELECT` | `executeQuery(sql)` | un `ResultSet` (las filas) |
| `INSERT` · `UPDATE` · `DELETE` | `executeUpdate(sql)` | un `int`: cuántas filas afectó |

## Leer: de filas a objetos

```java
try (Connection con = DatabaseConnection.getConnection();
     Statement st = con.createStatement();
     ResultSet rs = st.executeQuery("SELECT ... FROM planta")) {

    while (rs.next()) {                        // avanza una fila; devuelve false cuando se acaban
        plantas.add(PlantaMapper.mapear(rs));  // rs.getInt("altura_cm"), rs.getString("nombre")...
    }
}
```

- El `ResultSet` es un **cursor**: parte *antes* de la primera fila, por eso se llama `next()` antes de leer.
- Las columnas se leen por nombre (`rs.getString("nombre")`) o por posición (`rs.getInt(1)`, que parte en 1).
- El mapeo es el puente entre dos mundos: **filas** (relacional) y **objetos** (orientado a objetos).
  Aquí `tipo` decide qué subclase crear: el polimorfismo del modelo se reconstruye desde la base.
- Un `JOIN` devuelve filas que no son una `Planta`; en `listarConProveedor()` se leen directo del `ResultSet`.

## Escribir

```java
String sql = "UPDATE planta SET cantidad = " + cantidad + " WHERE id = " + id;
int filas = st.executeUpdate(sql);        // 1 = se modificó; 0 = no existía esa fila
```

## ⚠ El problema escondido

Todas las sentencias se construyen **pegando texto** con `+`. Para ver por qué es peligroso, mira
`buscarPorNombreInseguro(nombre)`:

```java
"SELECT ... FROM planta WHERE nombre = '" + nombre + "'"
```

Si `nombre` vale `Monstera`, todo bien. Pero si el usuario escribe `' OR '1'='1`, el SQL queda
`WHERE nombre = '' OR '1'='1'`, que es verdadero para **todas** las filas. Más simple aún: un nombre con una
comilla (`Rosa d'Or`) ya rompe el `INSERT`. Se demuestra en el paso 5.

## Cómo ejecutarlo

```bash
cd paso-04-crud-statement
mvn -q compile exec:java
```

**Resultado esperado** (resumido)

```
== 1. READ: plantas actuales ==
  #1 Interior | Monstera | 80 cm | 15 u. | $10.000
  ...
== 3. CREATE ==
  Filas insertadas: 1
  Quedó con id 9: Interior | Helecho | 35 cm | 25 u. | $8.000
== 4. UPDATE ==
  Filas modificadas: 1
== 5. DELETE ==
  Filas eliminadas: 1
  Plantas que quedan: 8
```

El programa deja la tabla como estaba: se puede ejecutar todas las veces que se quiera.

## Errores frecuentes

| Situación | Causa | Revisión |
|---|---|---|
| `Column 'xyz' not found` | El nombre en `rs.getXxx("xyz")` no coincide con la columna | Comparar con `01_schema.sql` |
| `You have an error in your SQL syntax` | Comillas o comas mal puestas al concatenar | Imprimir el `sql` antes de ejecutarlo |
| La tabla no cambia | No se ejecutó `executeUpdate`, o el `WHERE` no coincide con ninguna fila | Mirar el `int` devuelto |
| `Data truncated` / `CHECK constraint` | Dato fuera de las reglas | Valores permitidos en el esquema |

➡️ **Siguiente:** [Paso 5 · Sentencias parametrizadas](../paso-05-parametrizadas/README.md)
