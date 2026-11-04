# Cómo trabajar este caso: nube, móvil y Ubuntu

El repositorio es el punto de encuentro. Los cambios nacen en una sesión en la nube, se **revisan desde la app móvil**
y se **ejecutan en el equipo Ubuntu**.

```
Sesión en la nube ──push──► GitHub (donkiwicl/poo_vivero_bd) ──pull──► Ubuntu (PhpStorm / IntelliJ)
                                     │
                                     └── app móvil de GitHub: leer README, navegar código, comentar
```

## Revisar desde el móvil

1. Abre el repositorio en la app de GitHub y entra al [README principal](../README.md): la tabla de pasos es el índice.
2. Cada carpeta `paso-NN-…` tiene un `README.md` que se lee en orden; los fragmentos de código importantes están
   copiados ahí, así que **no hace falta abrir los `.java`** para seguir la explicación.
3. Para comentar un cambio, usa una **Pull Request**: desde el móvil puedes revisar archivos, comentar líneas y aprobar.
4. Qué mirar en cada paso: que el README diga la verdad sobre el código, que la salida esperada coincida con lo que ves
   al ejecutarlo y que la sección *Para pensar* sirva como pregunta de clase.

## Ejecutar y revisar en Ubuntu

```bash
git clone https://github.com/donkiwicl/poo_vivero_bd.git     # la primera vez
cd poo_vivero_bd && git pull                                 # después

scripts/crear-bd.sh                                          # recrea la base
cd paso-07-javafx && mvn javafx:run
```

El detalle de requisitos (JDK 21, Maven, MySQL) está en el [README principal](../README.md#cómo-ejecutarlo-ubuntu).

## Proponer cambios (flujo recomendado)

```bash
git switch -c mejora/paso-05-explicacion
# ... editar ...
git commit -am "Paso 5: aclara la explicación de batch"
git push -u origin mejora/paso-05-explicacion
gh pr create --fill
```

Las Pull Requests ejecutan el **CI** (`.github/workflows/ci.yml`): levanta un MySQL 8.4 limpio, crea la base y
ejecuta los pasos 3 a 6; compila el paso 7 con JavaFX 21.0.6. Si está en verde, lo que dicen los README funciona.

## Convenciones del caso

- Cada paso es **autocontenido**: se abre y ejecuta sin los demás.
- El código que ya apareció en un paso anterior se copia **sin cambios**; lo nuevo de cada paso se explica en su README.
- El modelo (`cl.duoc.vivero.model`) es idéntico en los pasos 4 a 7.
- Si se cambia algo que se repite entre pasos (por ejemplo `DatabaseConnection`), hay que cambiarlo en **todos** los pasos donde aparece.
