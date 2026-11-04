package cl.duoc.vivero.repository;

import cl.duoc.vivero.dao.PlantaDao;
import cl.duoc.vivero.model.Planta;

import java.util.List;
import java.util.Objects;

/**
 * Lo único que ve el controlador. Reemplaza al repositorio de la EP2, que trabajaba con listas
 * en memoria y archivos JSON: ahora las operaciones tienen los mismos nombres de uso
 * (listar, registrar, eliminar), pero guardan en la base de datos a través de un {@link PlantaDao}.
 * <p>
 * El DAO se recibe por el constructor (inyección de dependencias): el repositorio no decide
 * si trabaja con MySQL o con memoria.
 */
public class PlantaRepository {

    private final PlantaDao dao;

    public PlantaRepository(PlantaDao dao) {
        this.dao = Objects.requireNonNull(dao, "El DAO es obligatorio");
    }

    public List<Planta> listar() {
        return dao.listarTodas();
    }

    public List<Planta> buscarPorNombre(String nombre) {
        return dao.buscarPorNombre(nombre);
    }

    public void registrar(Planta planta) {
        Objects.requireNonNull(planta, "La planta es obligatoria");
        dao.insertar(planta);
    }

    /** @return true si la planta existía y se modificó */
    public boolean actualizar(Planta planta) {
        Objects.requireNonNull(planta, "La planta es obligatoria");
        return dao.actualizar(planta);
    }

    /** @return true si la planta existía y se eliminó */
    public boolean eliminar(Planta planta) {
        Objects.requireNonNull(planta, "La planta es obligatoria");
        return dao.eliminar(planta.getId());
    }
}
