# Paso 1 · Nivelación SQL

**Material:** PPT 3.1.1 · **Archivo:** [`consultas.sql`](consultas.sql) · **Esquema:** [`../sql/01_schema.sql`](../sql/01_schema.sql)

## Objetivo

Antes de escribir Java, entender la base de datos que Java va a usar. Este paso no tiene código Java: son consultas
SQL que se ejecutan en el cliente de base de datos (MySQL Workbench, DataGrip, la consola `mysql`).

## El modelo relacional del vivero

```
proveedor                      planta
─────────                      ──────────────────────────────────────────
id  (PK)  ◄──────────────┐     id (PK)
nombre                   └──── proveedor_id (FK, opcional)
ciudad                         tipo  'INTERIOR' | 'EXTERIOR'
                               nombre · altura_cm · cantidad
                               volumen_maceta_litros   ← solo interiores
                               especie · resistente_heladas · lista_despacho ← solo exteriores
```

- **Tabla = clase, fila = objeto, columna = atributo.** `PlantaInterior` y `PlantaExterior` comparten una tabla;
  la columna `tipo` indica de cuál se trata y las columnas que no aplican quedan en `NULL`.
- **`PRIMARY KEY AUTO_INCREMENT`:** MySQL asigna el `id` al insertar. Por eso `Planta` ahora tiene un `id`
  (vale 0 mientras no se ha guardado).
- **`CHECK`:** la base repite las reglas del modelo Java (altura 1–300, cantidad 1–500). Dos defensas son mejor que una.
- **`FOREIGN KEY`:** impide asociar una planta a un proveedor que no existe.

## Qué recorre `consultas.sql`

| Bloque | Sentencias | Idea clave |
|---|---|---|
| 1 | `SELECT`, `WHERE`, `LIKE`, `IS TRUE` | Elegir columnas y filtrar filas |
| 2 | `ORDER BY` | Ordenar, ascendente y descendente |
| 3 | `GROUP BY`, `COUNT`, `SUM`, `AVG`, `HAVING` | `WHERE` filtra **filas**, `HAVING` filtra **grupos** |
| 4 | `INNER JOIN`, `LEFT JOIN` | Combinar tablas a través de la clave foránea |
| 5 | `INSERT`, `UPDATE`, `DELETE` | El resto del CRUD. **Siempre `WHERE`** en `UPDATE` y `DELETE` |
| 6 | Errores comunes (comentados) | `UPDATE` sin `WHERE`, violar `CHECK`, violar `FOREIGN KEY` |

## Cómo ejecutarlo

```bash
scripts/crear-bd.sh                                   # crea la base y los datos (desde la raíz del repo)
mysql -uroot -p1234 -t < paso-01-sql/consultas.sql    # ejecuta todo el archivo
```

Mejor aún: abre `consultas.sql` en tu cliente y ejecuta **un bloque a la vez**, mirando el resultado.

## Para pensar

1. ¿Qué devuelve el `GROUP BY proveedor_id … HAVING SUM(cantidad) > 50`? ¿Por qué solo aparece un proveedor?
2. ¿Qué diferencia hay entre `INNER JOIN` y `LEFT JOIN` si agregas un proveedor sin plantas?
3. ¿Por qué el bloque 6 falla al insertar una planta de 999 cm?

➡️ **Siguiente:** [Paso 2 · Conectividad](../paso-02-conexion/README.md): conseguir que Java ejecute estas mismas sentencias.
