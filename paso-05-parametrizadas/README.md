# Paso 5 · Sentencias parametrizadas

**Material:** PPT 3.1.5 · Guía 3.1.6 · Ejercicios 3.1.7 · **Clases:**
[`PlantaCrudPreparado`](src/main/java/cl/duoc/vivero/dao/PlantaCrudPreparado.java) ·
[`DemoInyeccion`](src/main/java/cl/duoc/vivero/DemoInyeccion.java)

## Objetivo

Reemplazar la concatenación de texto por **`PreparedStatement`** y comprobar, con una demostración, por qué es la única
forma correcta de enviar datos del usuario a la base.

## Statement vs PreparedStatement

```java
// Paso 4 — el valor se pega al SQL (peligroso)
st.executeQuery("... WHERE nombre = '" + nombre + "'");

// Paso 5 — el SQL va completo, con marcadores; los valores se entregan aparte
PreparedStatement ps = con.prepareStatement("... WHERE nombre = ?");
ps.setString(1, nombre);          // los ? se numeran desde 1
ResultSet rs = ps.executeQuery(); // sin argumentos: el SQL ya está en el ps
```

| | `Statement` | `PreparedStatement` |
|---|---|---|
| Valores | Se pegan al texto SQL | Se envían como **datos** (`setString`, `setInt`, `setDouble`, `setBoolean`…) |
| Comillas, tildes, símbolos | Hay que escaparlos a mano | Se manejan solos |
| Inyección SQL | **Vulnerable** | **Protegido** |
| Reutilización | Se arma de nuevo cada vez | El mismo `ps` se ejecuta muchas veces |

## La demostración (`DemoInyeccion`)

```
== B. Entrada maliciosa SIN parámetros (Statement) ==
   SQL resultante: ... WHERE nombre = '' OR '1'='1'   <- siempre verdadero
   Filas devueltas: 8                                 ← ¡devolvió TODO el inventario!

== C. La misma entrada CON parámetros (PreparedStatement) ==
   Se busca una planta llamada literalmente "' OR '1'='1"
   Filas devueltas: 0                                 ← es solo un texto raro
```

Con `PreparedStatement` la base recibe primero la **estructura** de la consulta y después los **valores**;
un valor jamás puede cambiar la estructura.

## Qué más trae este paso

| Tema | Dónde | Idea |
|---|---|---|
| Parámetros de distinto tipo | `insertar`, `actualizar` | `setString`, `setInt`, `setDouble`, `setBoolean` y `setNull` para lo que no aplica a esa subclase |
| Obtener el `id` generado | `insertar` | `RETURN_GENERATED_KEYS` + `getGeneratedKeys()`: la planta sale de aquí con su `id` |
| UPDATE/DELETE con `WHERE id = ?` | `actualizar`, `eliminar` | Devuelven `true` si la fila existía (`executeUpdate() > 0`) |
| Una sentencia, varias ejecuciones | `insertarVarias` | `addBatch()` + `executeBatch()` |
| Transacción | `insertarVarias` | `setAutoCommit(false)` → `commit()` o `rollback()`: *todo o nada* |

## Cómo ejecutarlo

```bash
cd paso-05-parametrizadas
mvn -q compile exec:java
```

El programa limpia lo que inserta: se puede repetir.

## Errores frecuentes

| Situación | Causa | Revisión |
|---|---|---|
| `Parameter index out of range` | Cantidad de `?` ≠ cantidad de `setXxx` | Contar ambos; los índices parten en 1 |
| `No value specified for parameter 2` | Faltó un `setXxx` | Revisar el orden de los `?` |
| `Data truncated`/`NULL not allowed` | Tipo incompatible o `null` en columna `NOT NULL` | Revisar el `setXxx` usado |
| Sigue habiendo `+` dentro del SQL | Se mezcló concatenación y parámetros | Todo valor variable va con `?` |
| Los `?` no funcionan en `LIKE '%?%'` | El `?` no puede ir dentro de comillas | `LIKE ?` y pasar `"%" + texto + "%"` como parámetro |

## Para pensar

1. ¿Por qué `RETURN_GENERATED_KEYS` es necesario antes de usar el `id` de la planta recién insertada?
2. ¿Qué pasaría en `insertarVarias` si la segunda planta falla? ¿Y sin transacción?
3. `ps.setObject(i + 1, parametros[i])` en `consultar()`: ¿qué ventaja y qué riesgo tiene frente a `setString`?

## ¿Qué problema tiene este código?

Las operaciones funcionan y son seguras, pero **la lógica JDBC sigue siendo una clase suelta** que cualquiera instancia y que
lanza `SQLException`: quien la use (por ejemplo, un controlador gráfico) tendría que conocer JDBC.

➡️ **Siguiente:** [Paso 6 · Refactorización de la capa DAO](../paso-06-dao/README.md)
