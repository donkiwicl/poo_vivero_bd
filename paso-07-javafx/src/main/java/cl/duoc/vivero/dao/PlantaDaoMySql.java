package cl.duoc.vivero.dao;

import cl.duoc.vivero.database.DatabaseConnection;
import cl.duoc.vivero.model.Planta;
import cl.duoc.vivero.model.PlantaExterior;
import cl.duoc.vivero.model.PlantaInterior;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de {@link PlantaDao} para MySQL con JDBC. TODO el SQL de la aplicación vive aquí.
 * <p>
 * Diferencia con el paso 5: los {@link SQLException} se convierten en {@link PersistenciaException}
 * en vez de imprimirse con {@code printStackTrace()}. Así el error SUBE hasta la interfaz,
 * que puede mostrarlo en un Alert, en lugar de perderse en la consola.
 */
public class PlantaDaoMySql implements PlantaDao {

    private static final String COLUMNAS =
            "id, tipo, nombre, altura_cm, cantidad, volumen_maceta_litros, especie, resistente_heladas, lista_despacho";

    private static final String SQL_INSERT_INTERIOR =
            "INSERT INTO planta (tipo, nombre, altura_cm, cantidad, volumen_maceta_litros) VALUES ('INTERIOR', ?, ?, ?, ?)";
    private static final String SQL_INSERT_EXTERIOR =
            "INSERT INTO planta (tipo, nombre, altura_cm, cantidad, especie, resistente_heladas, lista_despacho) "
                    + "VALUES ('EXTERIOR', ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE planta SET tipo = ?, nombre = ?, altura_cm = ?, cantidad = ?, volumen_maceta_litros = ?, "
                    + "especie = ?, resistente_heladas = ?, lista_despacho = ? WHERE id = ?";

    @Override
    public List<Planta> listarTodas() {
        return consultar("SELECT " + COLUMNAS + " FROM planta ORDER BY id");
    }

    @Override
    public Optional<Planta> buscarPorId(int id) {
        return consultar("SELECT " + COLUMNAS + " FROM planta WHERE id = ?", id).stream().findFirst();
    }

    @Override
    public List<Planta> buscarPorNombre(String nombre) {
        return consultar("SELECT " + COLUMNAS + " FROM planta WHERE nombre = ? ORDER BY id", nombre);
    }

    @Override
    public void insertar(Planta planta) {
        String sql = (planta instanceof PlantaInterior) ? SQL_INSERT_INTERIOR : SQL_INSERT_EXTERIOR;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, planta.getNombre());
            ps.setInt(2, planta.getAlturaCm());
            ps.setInt(3, planta.getCantidad());
            if (planta instanceof PlantaInterior interior) {
                ps.setDouble(4, interior.getVolumenMacetaLitros());
            } else if (planta instanceof PlantaExterior exterior) {
                ps.setString(4, exterior.getEspecie());
                ps.setBoolean(5, exterior.isResistenteHeladas());
                ps.setBoolean(6, exterior.estaListaParaDespacho());
            } else {
                throw new IllegalArgumentException("Tipo de planta no soportado");
            }
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    planta.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo guardar la planta '" + planta.getNombre() + "'", e);
        }
    }

    @Override
    public boolean actualizar(Planta planta) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setString(2, planta.getNombre());
            ps.setInt(3, planta.getAlturaCm());
            ps.setInt(4, planta.getCantidad());
            if (planta instanceof PlantaInterior interior) {
                ps.setString(1, "INTERIOR");
                ps.setDouble(5, interior.getVolumenMacetaLitros());
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.BOOLEAN);
                ps.setBoolean(8, false);
            } else if (planta instanceof PlantaExterior exterior) {
                ps.setString(1, "EXTERIOR");
                ps.setNull(5, Types.DECIMAL);
                ps.setString(6, exterior.getEspecie());
                ps.setBoolean(7, exterior.isResistenteHeladas());
                ps.setBoolean(8, exterior.estaListaParaDespacho());
            } else {
                throw new IllegalArgumentException("Tipo de planta no soportado");
            }
            ps.setInt(9, planta.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo actualizar la planta #" + planta.getId(), e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM planta WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar la planta #" + id, e);
        }
    }

    /** Método de apoyo: ejecuta cualquier SELECT de plantas, con o sin parámetros. */
    private List<Planta> consultar(String sql, Object... parametros) {
        List<Planta> plantas = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    plantas.add(PlantaMapper.mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudieron leer las plantas", e);
        }
        return plantas;
    }
}
