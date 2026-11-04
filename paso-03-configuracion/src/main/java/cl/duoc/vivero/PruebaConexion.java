package cl.duoc.vivero;

import cl.duoc.vivero.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Paso 3 · La misma prueba del paso 2, pero la conexión ahora sale de {@link DatabaseConnection}.
 * Si cambia el puerto, el usuario o la clave, esta clase NO se toca.
 */
public class PruebaConexion {

    public static void main(String[] args) {
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM planta")) {

            rs.next();
            System.out.println("Conexión exitosa. Plantas en la tabla: " + rs.getInt(1));

        } catch (SQLException e) {
            System.err.println("No se pudo usar la base de datos: " + diagnostico(e));
        }
    }

    /** Traduce los errores más comunes a una pista para el estudiante. */
    static String diagnostico(SQLException e) {
        String pista = switch (e.getErrorCode()) {
            case 1045 -> "usuario o clave incorrectos (revisa db.usuario / db.clave)";
            case 1049 -> "la base de datos no existe (ejecuta scripts/crear-bd.sh)";
            case 1146 -> "falta la tabla (ejecuta scripts/crear-bd.sh)";
            case 0 -> "el servidor no responde: ¿MySQL está encendido y el puerto es el correcto?";
            default -> "revisa el mensaje del servidor";
        };
        return e.getMessage() + "\n  -> Pista: " + pista;
    }
}
