package cl.duoc.vivero.dao;

import cl.duoc.vivero.model.Planta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Segunda implementación de {@link PlantaDao}: guarda todo en una lista, sin base de datos.
 * Sirve para probar la aplicación sin MySQL y para demostrar que el contrato se puede
 * cumplir de muchas formas distintas.
 */
public class PlantaDaoMemoria implements PlantaDao {

    private final List<Planta> plantas = new ArrayList<>();
    private int siguienteId = 1;

    @Override
    public List<Planta> listarTodas() {
        return new ArrayList<>(plantas);
    }

    @Override
    public Optional<Planta> buscarPorId(int id) {
        return plantas.stream().filter(p -> p.getId() == id).findFirst();
    }

    @Override
    public List<Planta> buscarPorNombre(String nombre) {
        return plantas.stream().filter(p -> p.getNombre().equals(nombre)).toList();
    }

    @Override
    public void insertar(Planta planta) {
        planta.setId(siguienteId++);
        plantas.add(planta);
    }

    @Override
    public boolean actualizar(Planta planta) {
        for (int i = 0; i < plantas.size(); i++) {
            if (plantas.get(i).getId() == planta.getId()) {
                plantas.set(i, planta);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        return plantas.removeIf(p -> p.getId() == id);
    }
}
