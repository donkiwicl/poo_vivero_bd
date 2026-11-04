package cl.duoc.vivero.dao;

import cl.duoc.vivero.database.DatabaseConnection;
import cl.duoc.vivero.model.Planta;
import cl.duoc.vivero.model.PlantaExterior;
import cl.duoc.vivero.model.PlantaInterior;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Paso 4 · CRUD completo usando {@link Statement}.
 * <p>
 * Regla para elegir el método JDBC:
 * <ul>
 *   <li>SELECT                    → {@code executeQuery()}  devuelve un ResultSet (filas)</li>
 *   <li>INSERT / UPDATE / DELETE  → {@code executeUpdate()} devuelve cuántas filas afectó</li>
 * </ul>
 * ⚠ Los valores se pegan al texto SQL con "+". Funciona, pero es inseguro: en el paso 5 se corrige.
 */
public class PlantaCrudStatement {

    private static final String COLUMNAS =
            "id, tipo, nombre, altura_cm, cantidad, volumen_maceta_litros, especie, resistente_heladas, lista_despacho";

    /** SELECT: devuelve todas las plantas como objetos del modelo. */
    public List<Planta> listar() throws SQLException {
        List<Planta> plantas = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM planta ORDER BY id";

        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {                       // cada next() avanza una fila; false cuando se acaban
                plantas.add(PlantaMapper.mapear(rs));
            }
        }
        return plantas;
    }

    /** SELECT con JOIN: filas que no son una Planta, así que se leen directo del ResultSet. */
    public List<String> listarConProveedor() throws SQLException {
        List<String> lineas = new ArrayList<>();
        String sql = "SELECT p.nombre AS planta, pr.nombre AS proveedor, pr.ciudad "
                + "FROM planta p INNER JOIN proveedor pr ON pr.id = p.proveedor_id "
                + "ORDER BY pr.nombre, p.nombre";

        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lineas.add(rs.getString("planta") + " ← " + rs.getString("proveedor")
                        + " (" + rs.getString("ciudad") + ")");
            }
        }
        return lineas;
    }

    /** INSERT con valores concatenados. Devuelve las filas insertadas (1 si todo salió bien). */
    public int insertar(Planta planta) throws SQLException {
        String sql;
        if (planta instanceof PlantaInterior interior) {
            sql = "INSERT INTO planta (tipo, nombre, altura_cm, cantidad, volumen_maceta_litros) VALUES ("
                    + "'INTERIOR', '" + interior.getNombre() + "', " + interior.getAlturaCm() + ", "
                    + interior.getCantidad() + ", " + interior.getVolumenMacetaLitros() + ")";
        } else if (planta instanceof PlantaExterior exterior) {
            sql = "INSERT INTO planta (tipo, nombre, altura_cm, cantidad, especie, resistente_heladas, lista_despacho) VALUES ("
                    + "'EXTERIOR', '" + exterior.getNombre() + "', " + exterior.getAlturaCm() + ", "
                    + exterior.getCantidad() + ", '" + exterior.getEspecie() + "', "
                    + exterior.isResistenteHeladas() + ", " + exterior.estaListaParaDespacho() + ")";
        } else {
            throw new IllegalArgumentException("Tipo de planta no soportado");
        }
        return ejecutarCambio(sql);
    }

    /** UPDATE: cambia nombre, altura y cantidad de la planta con ese id. */
    public int actualizarStock(int id, String nombre, int alturaCm, int cantidad) throws SQLException {
        String sql = "UPDATE planta SET nombre = '" + nombre + "', altura_cm = " + alturaCm
                + ", cantidad = " + cantidad + " WHERE id = " + id;
        return ejecutarCambio(sql);
    }

    /** DELETE: elimina la planta con ese id. */
    public int eliminar(int id) throws SQLException {
        return ejecutarCambio("DELETE FROM planta WHERE id = " + id);
    }

    /** Ejecuta cualquier INSERT/UPDATE/DELETE y devuelve cuántas filas afectó. */
    private int ejecutarCambio(String sql) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement()) {
            return st.executeUpdate(sql);
        }
    }

    /** Busca por nombre exacto concatenando el texto. ⚠ VULNERABLE: se explota en el paso 5. */
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
}
