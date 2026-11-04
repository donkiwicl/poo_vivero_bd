package cl.duoc.vivero;

import javafx.application.Application;

/**
 * Punto de entrada. Existe separado de {@link App} para que la aplicación JavaFX
 * arranque desde IntelliJ IDEA / PhpStorm sin configuración adicional.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
