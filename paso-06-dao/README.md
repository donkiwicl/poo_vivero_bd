# Paso 6 · Refactorización de la capa DAO

**Material:** PPT 3.2.1 · Guía 3.2.3 · Ejercicios 3.2.4 · **Paquetes:** `dao` · `repository` · `database` · `model`

## Objetivo

Separar **qué** datos necesita la aplicación (un contrato) de **cómo** se obtienen (JDBC), para que la interfaz gráfica del
paso 7 no sepa que existe una base de datos.

## Antes y después

| Aspecto | Antes (pasos 4-5) | Después (paso 6) |
|---|---|---|
| Lo que ve quien use el CRUD | Una clase concreta con `SQLException` | Una **interfaz** (`PlantaDao`) |
| Dónde vive el SQL | En cualquier clase que lo necesite | **Solo** en `PlantaDaoMySql` |
| Conexiones | Una clase de utilidad | Igual: `DatabaseConnection` como único punto |
| Errores | `SQLException` (obliga a `throws`/`try`) | `PersistenciaException` (no verificada) |
| Cambiar de MySQL a otra fuente | Reescribir todo | Escribir otra clase que implemente la interfaz |

## Las capas

```
Controlador (paso 7)            ← solo conoce PlantaRepository
      │
PlantaRepository                ← recibe un PlantaDao por el constructor
      │
«interface» PlantaDao           ← el contrato: listarTodas, buscarPorId, buscarPorNombre, insertar, actualizar, eliminar
   ├── PlantaDaoMySql           ← JDBC + PreparedStatement   (TODO el SQL está aquí)
   └── PlantaDaoMemoria         ← una lista, sin base de datos
      │
DatabaseConnection ──► MySQL
```

## Las decisiones de diseño

1. **Interfaz `PlantaDao`.** Define las operaciones sin decir cómo se hacen. Es el mismo patrón que `Despachable` en el modelo.
2. **`PlantaDaoMySql`.** Es el código del paso 5, reorganizado. Cambio importante: en lugar de `e.printStackTrace()`
   (el error se pierde en la consola), lanza `PersistenciaException`. Así el error **sube** hasta la interfaz,
   que lo muestra en un `Alert`.
3. **`PersistenciaException` es `RuntimeException`.** Evita que el controlador deba declarar `throws SQLException`,
   que lo ataría a JDBC.
4. **`PlantaDaoMemoria`.** Una segunda implementación del mismo contrato. Demuestra el desacoplamiento y permite probar
   la aplicación sin MySQL (útil si el servicio no está disponible).
5. **`PlantaRepository`.** Es lo que usa el controlador, con la misma forma que el repositorio de la EP2
   (`listar`, `registrar`, `eliminar`), pero que ahora guarda en la base. Recibe el DAO por el constructor
   (**inyección de dependencias**): el repositorio no decide con qué se guarda.

```java
PlantaRepository repo = new PlantaRepository(new PlantaDaoMySql());     // producción
PlantaRepository repo = new PlantaRepository(new PlantaDaoMemoria());   // pruebas — nada más cambia
```

## Cómo ejecutarlo

```bash
cd paso-06-dao
mvn -q compile exec:java
```

`Main` ejecuta **la misma prueba** con los dos DAO. Resultado esperado:

```
=== MySQL (JDBC) ===
  registradas: ids 9 y 10  (total 8 -> 10)
  actualizada: true -> Exterior | Jazmín | 120 cm | 10 u. | $6.600 | despacho: true
  eliminadas: true, true  (total 8)
  eliminar otra vez: false  <- ya no existe
=== Memoria (sin base de datos) ===
  registradas: ids 1 y 2  (total 0 -> 2)
  ...
```

## Errores frecuentes

| Situación | Causa | Revisión |
|---|---|---|
| El controlador vuelve a tener SQL | Se mezclaron responsabilidades otra vez | Mover la consulta a `PlantaDaoMySql` |
| Se repite el código de conexión | No se usa `DatabaseConnection` | Un solo punto de acceso |
| `NullPointerException` en el repositorio | Se pasó `null` como DAO | El constructor lo valida con `requireNonNull` |
| `PersistenciaException` sin pista | Se perdió la causa | Revisar `e.getCause()`: ahí está el `SQLException` original |

## Para pensar

1. ¿Qué tendrías que escribir para guardar las plantas en un archivo JSON otra vez (como la EP2)? ¿Qué clases **no** cambiarían?
2. ¿Por qué `PlantaDao.eliminar` recibe un `int id` y `PlantaRepository.eliminar` recibe una `Planta`?
3. Identifica en los pasos 4-5 el mismo código que ahora está en `PlantaDaoMySql`.

➡️ **Siguiente:** [Paso 7 · Integración con la interfaz JavaFX](../paso-07-javafx/README.md)
