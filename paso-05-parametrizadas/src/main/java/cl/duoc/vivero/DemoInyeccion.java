package cl.duoc.vivero;

import cl.duoc.vivero.dao.PlantaCrudPreparado;
import cl.duoc.vivero.model.Planta;
import cl.duoc.vivero.model.PlantaExterior;
import cl.duoc.vivero.model.PlantaInterior;

import java.sql.SQLException;
import java.util.List;

/**
 * Paso 5 · Demostración de inyección SQL y de cómo la evita PreparedStatement,
 * más un CRUD completo con parámetros. Deja la tabla como estaba.
 */
public class DemoInyeccion {

    // Lo que escribiría un usuario malintencionado en el campo "nombre" de un formulario:
    private static final String ENTRADA_MALICIOSA = "' OR '1'='1";

    public static void main(String[] args) {
        PlantaCrudPreparado crud = new PlantaCrudPreparado();
        try {
            System.out.println("== A. Búsqueda normal: nombre = Monstera ==");
            mostrar(crud.buscarPorNombre("Monstera"));

            System.out.println("\n== B. Entrada maliciosa SIN parámetros (Statement) ==");
            System.out.println("   SQL resultante: ... WHERE nombre = '' OR '1'='1'   <- siempre verdadero");
            mostrar(crud.buscarPorNombreInseguro(ENTRADA_MALICIOSA));

            System.out.println("\n== C. La misma entrada CON parámetros (PreparedStatement) ==");
            System.out.println("   Se busca una planta llamada literalmente \"" + ENTRADA_MALICIOSA + "\"");
            mostrar(crud.buscarPorNombre(ENTRADA_MALICIOSA));

            System.out.println("\n== D. Nombres con comilla ya no rompen el INSERT ==");
            PlantaExterior comilla = new PlantaExterior("Rosa d'Or", 90, 10, "Rosa sp.", false);
            crud.insertar(comilla);
            System.out.println("   Guardada con id " + comilla.getId() + " → " + crud.buscarPorNombre("Rosa d'Or"));

            System.out.println("\n== E. UPDATE y marcar para despacho ==");
            comilla.setCantidad(8);
            comilla.marcarListaParaDespacho();
            System.out.println("   ¿Actualizada? " + crud.actualizar(comilla));
            System.out.println("   → " + crud.buscarPorNombre("Rosa d'Or").get(0).getCantidad() + " unidades");

            System.out.println("\n== F. Una sentencia, varias ejecuciones (batch en transacción) ==");
            int antes = crud.listar().size();
            crud.insertarVarias(List.of(
                    new PlantaInterior("Calathea", 45, 12, 2.5),
                    new PlantaInterior("Sansevieria", 60, 18, 4.0),
                    new PlantaInterior("Zamioculca", 70, 9, 5.5)));
            System.out.println("   Plantas: " + antes + " → " + crud.listar().size());

            System.out.println("\n== Limpieza: se borran las plantas de la demo ==");
            for (String nombre : List.of("Rosa d'Or", "Calathea", "Sansevieria", "Zamioculca")) {
                for (Planta p : crud.buscarPorNombre(nombre)) {
                    crud.eliminar(p.getId());
                }
            }
            System.out.println("   Plantas finales: " + crud.listar().size());

        } catch (SQLException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
        }
    }

    private static void mostrar(List<Planta> plantas) {
        System.out.println("   Filas devueltas: " + plantas.size());
        plantas.stream().limit(3).forEach(p -> System.out.println("   - " + p));
        if (plantas.size() > 3) {
            System.out.println("   - ... (" + (plantas.size() - 3) + " más)");
        }
    }
}
