package cl.duoc.vivero.dao;

import cl.duoc.vivero.model.Planta;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos de las plantas. Dice QUÉ se puede hacer, no CÓMO.
 * <p>
 * Quien use esta interfaz no sabe si detrás hay MySQL, un archivo JSON o una lista en memoria.
 * Por eso se puede cambiar la implementación sin tocar controladores ni vistas.
 */
public interface PlantaDao {

    List<Planta> listarTodas();

    Optional<Planta> buscarPorId(int id);

    List<Planta> buscarPorNombre(String nombre);

    /** Guarda la planta y le asigna el id generado. */
    void insertar(Planta planta);

    /** @return true si existía una planta con ese id y se modificó */
    boolean actualizar(Planta planta);

    /** @return true si existía una planta con ese id y se eliminó */
    boolean eliminar(int id);
}
