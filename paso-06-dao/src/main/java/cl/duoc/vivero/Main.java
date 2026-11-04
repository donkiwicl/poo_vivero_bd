package cl.duoc.vivero;

import cl.duoc.vivero.dao.PersistenciaException;
import cl.duoc.vivero.dao.PlantaDao;
import cl.duoc.vivero.dao.PlantaDaoMemoria;
import cl.duoc.vivero.dao.PlantaDaoMySql;
import cl.duoc.vivero.model.PlantaExterior;
import cl.duoc.vivero.model.PlantaInterior;
import cl.duoc.vivero.repository.PlantaRepository;

/**
 * Paso 6 · El MISMO código de prueba se ejecuta con dos DAO distintos.
 * La única línea que cambia es el {@code new PlantaDao...} que se le entrega al repositorio.
 */
public class Main {

    public static void main(String[] args) {
        try {
            probar("MySQL (JDBC)", new PlantaDaoMySql());
            probar("Memoria (sin base de datos)", new PlantaDaoMemoria());
        } catch (PersistenciaException e) {
            System.err.println("Error de persistencia: " + e.getMessage());
            System.err.println("  causa: " + e.getCause().getMessage());
        }
    }

    private static void probar(String titulo, PlantaDao dao) {
        System.out.println("=== " + titulo + " ===");
        PlantaRepository repo = new PlantaRepository(dao);
        int inicial = repo.listar().size();

        PlantaInterior helecho = new PlantaInterior("Helecho", 35, 25, 3.0);
        PlantaExterior jazmin = new PlantaExterior("Jazmín", 120, 14, "Jasminum officinale", true);
        repo.registrar(helecho);
        repo.registrar(jazmin);
        System.out.println("  registradas: ids " + helecho.getId() + " y " + jazmin.getId()
                + "  (total " + inicial + " -> " + repo.listar().size() + ")");

        jazmin.marcarListaParaDespacho();
        jazmin.setCantidad(10);
        System.out.println("  actualizada: " + repo.actualizar(jazmin)
                + " -> " + repo.buscarPorNombre("Jazmín").get(0)
                + " | despacho: " + ((PlantaExterior) repo.buscarPorNombre("Jazmín").get(0)).estaListaParaDespacho());

        System.out.println("  eliminadas: " + repo.eliminar(helecho) + ", " + repo.eliminar(jazmin)
                + "  (total " + repo.listar().size() + ")");
        System.out.println("  eliminar otra vez: " + repo.eliminar(helecho) + "  <- ya no existe");
    }
}
