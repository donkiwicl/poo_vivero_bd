package cl.duoc.vivero.dao;

/**
 * Error al leer o escribir datos, sin importar dónde se guarden (MySQL, archivo, memoria).
 * <p>
 * Es una excepción no verificada (RuntimeException) para que el controlador no tenga que
 * declarar {@code throws SQLException}: así la interfaz gráfica ni siquiera sabe que existe JDBC.
 */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
