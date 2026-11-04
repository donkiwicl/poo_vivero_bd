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

/**
 * Paso 5 · El mismo CRUD del paso 4, pero con {@link PreparedStatement}.
 * <p>
 * El SQL se escribe UNA vez con marcadores {@code ?} y los valores se entregan después con
 * {@code setString}, {@code setInt}, etc. Los valores viajan como DATOS, nunca como parte del
 * código SQL: por eso un texto malicioso no puede cambiar el sentido de la consulta.
 */
public class PlantaCrudPreparado {

    private static final String COLUMNAS =
            "id, tipo, nombre, altura_cm, cantidad, volumen_maceta_litros, especie, resistente_heladas, lista_despacho";

    private static final String SQL_INSERT_INTERIOR =
            "INSERT INTO planta (tipo, nombre, altura_cm, cantidad, volumen_maceta_litros) VALUES ('INTERIOR', ?, ?, ?, ?)";
    private static final String SQL_INSERT_EXTERIOR =
            "INSERT INTO planta (tipo, nombre, altura_cm, cantidad, especie, resistente_heladas, lista_despacho) "
                    + "VALUES ('EXTERIOR', ?, ?, ?, ?, ?, ?)";

    // ---------------------------------------------------------------- READ

    public List<Planta> listar() throws SQLException {
        return consultar("SELECT " + COLUMNAS + " FROM planta ORDER BY id");
    }

    /** Búsqueda segura: el nombre entra como parámetro, no como texto SQL. */
    public List<Planta> buscarPorNombre(String nombre) throws SQLException {
        return consultar("SELECT " + COLUMNAS + " FROM planta WHERE nombre = ?", nombre);
    }

    /** La versión del paso 4, conservada aquí SOLO para compararla en {@link cl.duoc.vivero.DemoInyeccion}. */
    public List<Planta> buscarPorNombreInseguro(String nombre) throws SQLException {
        List<Planta> plantas = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM planta WHERE nombre = '" + nombre + "'";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                plantas.add(PlantaMapper.mapear(rs));
            }
        }
        return plantas;
    }

    private List<Planta> consultar(String sql, Object... parametros) throws SQLException {
        List<Planta> plantas = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);          // los ? se numeran desde 1
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    plantas.add(PlantaMapper.mapear(rs));
                }
            }
        }
        return plantas;
    }

    // -------------------------------------------------------------- CREATE

    /** INSERT. Además de guardar, le asigna a la planta el id que generó la base de datos. */
    public void insertar(Planta planta) throws SQLException {
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
            }
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {   // el AUTO_INCREMENT que eligió MySQL
                if (claves.next()) {
                    planta.setId(claves.getInt(1));
                }
            }
        }
    }

    /**
     * Reutilización: UNA sentencia preparada, VARIAS ejecuciones (batch). Todo en una sola transacción:
     * o se guardan todas o no se guarda ninguna.
     */
    public void insertarVarias(List<PlantaInterior> plantas) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);                         // iniciamos la transacción
            try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_INTERIOR)) {
                for (PlantaInterior p : plantas) {
                    ps.setString(1, p.getNombre());
                    ps.setInt(2, p.getAlturaCm());
                    ps.setInt(3, p.getCantidad());
                    ps.setDouble(4, p.getVolumenMacetaLitros());
                    ps.addBatch();
                }
                ps.executeBatch();
                con.commit();                                 // confirmamos todo junto
            } catch (SQLException e) {
                con.rollback();                               // algo falló: se deshace todo
                throw e;
            }
        }
    }

    // -------------------------------------------------------------- UPDATE

    /** UPDATE de todos los campos de la planta (el WHERE usa su id). Devuelve true si existía. */
    public boolean actualizar(Planta planta) throws SQLException {
        String sql = "UPDATE planta SET tipo = ?, nombre = ?, altura_cm = ?, cantidad = ?, "
                + "volumen_maceta_litros = ?, especie = ?, resistente_heladas = ?, lista_despacho = ? WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(2, planta.getNombre());
            ps.setInt(3, planta.getAlturaCm());
            ps.setInt(4, planta.getCantidad());
            if (planta instanceof PlantaInterior interior) {
                ps.setString(1, "INTERIOR");
                ps.setDouble(5, interior.getVolumenMacetaLitros());
                ps.setNull(6, Types.VARCHAR);                 // valor NULL explícito para lo que no aplica
                ps.setNull(7, Types.BOOLEAN);
                ps.setBoolean(8, false);
            } else if (planta instanceof PlantaExterior exterior) {
                ps.setString(1, "EXTERIOR");
                ps.setNull(5, Types.DECIMAL);
                ps.setString(6, exterior.getEspecie());
                ps.setBoolean(7, exterior.isResistenteHeladas());
                ps.setBoolean(8, exterior.estaListaParaDespacho());
            }
            ps.setInt(9, planta.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------------- DELETE

    public boolean eliminar(int id) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM planta WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
