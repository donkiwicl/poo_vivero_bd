package cl.duoc.vivero;

import cl.duoc.vivero.dao.PlantaCrudStatement;
import cl.duoc.vivero.model.Planta;
import cl.duoc.vivero.model.PlantaInterior;

import java.sql.SQLException;

/**
 * Paso 4 · Recorre un ciclo CRUD completo desde consola:
 * listar → insertar → actualizar → eliminar. Deja la tabla como estaba.
 */
public class Main {

    public static void main(String[] args) {
        PlantaCrudStatement crud = new PlantaCrudStatement();
        try {
            System.out.println("== 1. READ: plantas actuales ==");
            for (Planta p : crud.listar()) {
                System.out.println("  #" + p.getId() + " " + p);
            }

            System.out.println("\n== 2. READ con JOIN ==");
            crud.listarConProveedor().forEach(l -> System.out.println("  " + l));

            System.out.println("\n== 3. CREATE ==");
            int insertadas = crud.insertar(new PlantaInterior("Helecho", 35, 25, 3.0));
            System.out.println("  Filas insertadas: " + insertadas);
            Planta nueva = crud.buscarPorNombreInseguro("Helecho").get(0);
            System.out.println("  Quedó con id " + nueva.getId() + ": " + nueva);

            System.out.println("\n== 4. UPDATE ==");
            int modificadas = crud.actualizarStock(nueva.getId(), "Helecho", 35, 20);
            System.out.println("  Filas modificadas: " + modificadas);
            System.out.println("  Ahora: " + crud.buscarPorNombreInseguro("Helecho").get(0));

            System.out.println("\n== 5. DELETE ==");
            System.out.println("  Filas eliminadas: " + crud.eliminar(nueva.getId()));
            System.out.println("  Plantas que quedan: " + crud.listar().size());

        } catch (SQLException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
        }
    }
}
