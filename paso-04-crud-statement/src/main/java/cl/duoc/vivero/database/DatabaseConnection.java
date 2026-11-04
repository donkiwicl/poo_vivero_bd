package cl.duoc.vivero.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Único punto de la aplicación que sabe CÓMO conectarse a la base de datos.
 * <p>
 * Lee la configuración de {@code db.properties} (dentro de src/main/resources). Si existe una
 * variable de entorno equivalente, esa tiene prioridad: así la clave real nunca necesita
 * escribirse en el código ni en el repositorio.
 */
public final class DatabaseConnection {

    private static final String ARCHIVO_CONFIG = "/db.properties";
    private static final Properties CONFIG = cargarConfiguracion();

    private DatabaseConnection() {
        // Clase de utilidad: no se instancia.
    }

    /**
     * Abre una conexión NUEVA. Quien la pida es responsable de cerrarla,
     * normalmente con try-with-resources.
     */
    public static Connection getConnection() throws SQLException {
        String url = valor("db.url", "VIVERO_DB_URL");
        String usuario = valor("db.usuario", "VIVERO_DB_USUARIO");
        String clave = valor("db.clave", "VIVERO_DB_CLAVE");
        return DriverManager.getConnection(url, usuario, clave);
    }

    private static String valor(String propiedad, String variableEntorno) {
        String deEntorno = System.getenv(variableEntorno);
        if (deEntorno != null && !deEntorno.isBlank()) {
            return deEntorno;
        }
        String deArchivo = CONFIG.getProperty(propiedad);
        if (deArchivo == null) {
            throw new IllegalStateException("Falta '" + propiedad + "' en db.properties");
        }
        return deArchivo;
    }

    private static Properties cargarConfiguracion() {
        try (InputStream in = DatabaseConnection.class.getResourceAsStream(ARCHIVO_CONFIG)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró " + ARCHIVO_CONFIG + " en src/main/resources");
            }
            Properties propiedades = new Properties();
            propiedades.load(in);
            return propiedades;
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + ARCHIVO_CONFIG, e);
        }
    }
}
