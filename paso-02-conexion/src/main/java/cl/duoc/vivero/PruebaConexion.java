package cl.duoc.vivero;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Paso 2 · Primera conexión a la base de datos con JDBC.
 * <p>
 * Aquí los datos de conexión están escritos directamente en la clase. Sirve para ver
 * las piezas una por una; en el paso 3 se mueven a un archivo de configuración.
 */
public class PruebaConexion {

    // URL JDBC:  protocolo : motor :// host : puerto / base_de_datos
    private static final String URL = "jdbc:mysql://localhost:3306/vivero";
    private static final String USUARIO = "root";
    private static final String CLAVE = "1234";

    public static void main(String[] args) {
        // try-with-resources: Connection, Statement y ResultSet se cierran SOLOS al salir del bloque,
        // incluso si ocurre una excepción. Evita "fugas" de conexiones abiertas.
        try (Connection con = DriverManager.getConnection(URL, USUARIO, CLAVE)) {

            System.out.println("Conexión exitosa a " + con.getMetaData().getDatabaseProductName()
                    + " " + con.getMetaData().getDatabaseProductVersion());

            // Una consulta mínima para comprobar que la tabla existe y tiene datos.
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM planta")) {
                rs.next();                                   // mueve el cursor a la primera (y única) fila
                System.out.println("Plantas en la tabla: " + rs.getInt(1));   // columnas parten en 1
            }

        } catch (SQLException e) {
            // Los errores de base de datos siempre llegan como SQLException.
            System.err.println("No se pudo usar la base de datos: " + e.getMessage());
            System.err.println("SQLState = " + e.getSQLState() + " · código de error = " + e.getErrorCode());
        }
    }
}
