# Mapa del material → pasos del caso

Qué parte del material de la EA3 cubre cada paso. Los PPT (3.1.1 a 3.1.5, 3.2.1 y 3.2.2) comparten la misma
estructura de 13 apartados; la columna «Apartados del PPT» indica cuáles trabaja el paso.

## PPT

| Material | Paso | Apartados del PPT que desarrolla el paso |
|---|---|---|
| 3.1.1 Nivelación SQL | [1](../paso-01-sql/README.md) | Tablas, filas y columnas · `CREATE TABLE`, PK y FK · `SELECT`/`WHERE` · `INSERT`/`UPDATE`/`DELETE` · `ORDER BY`/`GROUP BY`/`HAVING` · `JOIN` · flujo CRUD · errores comunes |
| 3.1.2 Conectividad a base de datos | [2](../paso-02-conexion/README.md) | Driver JDBC · URL de conexión · `DriverManager` · `Connection` · cierre de recursos |
| 3.1.3 Configuración de la conexión | [3](../paso-03-configuracion/README.md) | Dependencia Maven · archivo de configuración · clase de conexión única · prueba de conexión · errores comunes |
| 3.1.4 Consultas y manipulación de datos | [4](../paso-04-crud-statement/README.md) | `Statement` · `executeQuery` vs `executeUpdate` · `ResultSet` · mapeo a objetos · CRUD |
| 3.1.5 Sentencias parametrizadas | [5](../paso-05-parametrizadas/README.md) | `Statement` vs `PreparedStatement` · placeholders `?` · `setString`/`setInt` · reutilización · inyección SQL |
| 3.2.1 Refactorizando capa DAO | [6](../paso-06-dao/README.md) | Patrón DAO · interfaz y su implementación · separación de responsabilidades · `Repository` |
| 3.2.2 Integración de BD con interfaz | [7](../paso-07-javafx/README.md) | `TableView` + `ObservableList` · formulario · selección de fila · refresco tras cada operación |

> Nota: en los PPT el contenido de las diapositivas es gráfico; este mapa se apoya en el índice de cada presentación
> y en las guías escritas. Conviene que revises que los apartados coincidan con lo que presentas en clase.

## Guías y ejercicios

| Material | Paso | Relación |
|---|---|---|
| 3.1.6 Guía: conexión y CRUD (`GestorProductosFX`) | 2 → 5 | Es el mismo recorrido (dependencia, URL, clase de conexión, DAO, `PreparedStatement`) con Producto/MySQL. Aquí se hace con el dominio del vivero |
| 3.1.7 Ejercicios generales, bloques 1–6 | 2 → 5, 7 | Bloque 1 (configuración) → pasos 2-3 · Bloque 2 (modelo y SQL) → pasos 1 y 4 · Bloque 3 (consultas) → pasos 4 y 7 · Bloque 4 (INSERT/UPDATE/DELETE) → pasos 4-5 · Bloque 5 (parametrizadas) → paso 5 · Bloque 6 (mini aplicación) → paso 7 |
| 3.2.3 Guía: refactorización e integración (`GestorClientesFX`) | 6 → 7 | Mismo recorrido: controlador con JDBC → `DatabaseConnection` → interfaz DAO → implementación MySQL → controlador limpio |
| 3.2.4 Ejercicios generales, bloques 1–6 | 6 → 7 | Bloque 1 (diagnóstico del acoplamiento) → «Antes y después» del paso 6 · Bloque 2 (conexión) → paso 3 · Bloque 3 (modelo y DAO) → paso 6 · Bloque 4 (CRUD refactorizado) → paso 6 · Bloque 5 (integración) → paso 7 · Bloque 6 (comparación antes/después) → paso 6 |

Los ejercicios 3.1.7 y 3.2.4 **no traen respuestas** a propósito. Este caso resuelve el *mismo recorrido* con otro dominio,
de modo que sirve como modelo de resolución sin entregar la respuesta literal de cada ejercicio.

## Guía docente (3.1.8) → sesiones

| Sesión | Momento de la guía | Paso del caso |
|---|---|---|
| **S13 · 3.1** (4 h) | Apertura: arquitectura cliente-servidor, driver JDBC y parámetros de conexión (20 min) | 2 |
| | Desarrollo 1: dependencias en `pom.xml` y clase de conexión (55 min) | 2 → 3 |
| | Desarrollo 2: `SELECT` y mapeo del `ResultSet` a objetos del dominio, manejo de `SQLException` (70 min) | 4 |
| | Desarrollo 3: `INSERT`/`UPDATE`/`DELETE` con `PreparedStatement` (55 min) | 5 |
| | Cierre: problemas típicos de conexión (25 min) | Tabla de errores de los pasos 2, 3 y 5 |
| **S14 · 3.2** (4 h) | Apertura: persistencia relacional y desacoplamiento mediante interfaces (20 min) | 6 |
| | Desarrollo 1: interfaz de repositorio y refactorización del DAO a JDBC (60 min) | 6 |
| | Desarrollo 2: `TableView` y formularios vinculados al repositorio (80 min) | 7 |
| | Cierre: archivos vs relacional (20 min) | Comparación de la EP2 (JSON) con los pasos 6-7 |
| **EF3 / EP3** | Evaluaciones de laboratorio, sin internet, MySQL local | Los pasos 2 a 7 son el repertorio completo que necesita el estudiante |

Puntos que la guía docente pide enfatizar y dónde aparecen:

- **`try-with-resources`** modelado en vivo → paso 2 (`PruebaConexion`).
- **Sanitizar inputs con `PreparedStatement`** frente a SQL concatenado → paso 5 (`DemoInyeccion`).
- **El controlador FXML no cambia si interactúa con una interfaz bien diseñada** → paso 6 (`PlantaDaoMemoria`) y paso 7 (una sola línea decide el DAO).
- **Capturar `SQLException` y no dejar conexiones abiertas** → todos los DAO usan `try-with-resources` y convierten el error en `PersistenciaException`.
- **Sin conexión o con errores SQL no controlados el CRUD de la EP3 obtiene nota mínima** → tabla de errores comunes en los pasos 3 y 5.
