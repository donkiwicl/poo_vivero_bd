package cl.duoc.vivero.dao;

import cl.duoc.vivero.model.Planta;
import cl.duoc.vivero.model.PlantaExterior;
import cl.duoc.vivero.model.PlantaInterior;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Convierte UNA fila del {@link ResultSet} en un objeto del modelo.
 * Es el puente entre el mundo relacional (filas) y el mundo orientado a objetos.
 */
final class PlantaMapper {

    private PlantaMapper() {
    }

    /** Usar con el cursor ya posicionado en una fila (después de {@code rs.next()}). */
    static Planta mapear(ResultSet rs) throws SQLException {
        String nombre = rs.getString("nombre");
        int altura = rs.getInt("altura_cm");
        int cantidad = rs.getInt("cantidad");

        Planta planta;
        if ("INTERIOR".equals(rs.getString("tipo"))) {
            planta = new PlantaInterior(nombre, altura, cantidad, rs.getDouble("volumen_maceta_litros"));
        } else {
            PlantaExterior exterior = new PlantaExterior(nombre, altura, cantidad,
                    rs.getString("especie"), rs.getBoolean("resistente_heladas"));
            if (rs.getBoolean("lista_despacho")) {
                exterior.marcarListaParaDespacho();     // el estado solo se cambia con este método
            }
            planta = exterior;
        }
        planta.setId(rs.getInt("id"));
        return planta;
    }
}
