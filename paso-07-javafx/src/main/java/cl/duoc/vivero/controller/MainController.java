package cl.duoc.vivero.controller;

import cl.duoc.vivero.dao.PersistenciaException;
import cl.duoc.vivero.dao.PlantaDaoMySql;
import cl.duoc.vivero.model.Despachable;
import cl.duoc.vivero.model.Planta;
import cl.duoc.vivero.model.PlantaExterior;
import cl.duoc.vivero.model.PlantaInterior;
import cl.duoc.vivero.repository.PlantaRepository;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Controlador de la vista principal.
 * <p>
 * Su trabajo es SOLO de interfaz: leer el formulario, llamar al repositorio y refrescar la tabla.
 * No contiene SQL, no abre conexiones y no importa nada de {@code java.sql}.
 */
public class MainController {

    private static final String INTERIOR = "Interior";
    private static final String EXTERIOR = "Exterior";

    // La única línea que sabe que existe MySQL. Para trabajar sin base de datos:  new PlantaDaoMemoria()
    private final PlantaRepository repositorio = new PlantaRepository(new PlantaDaoMySql());

    // TableView observa esta lista: cuando cambia, la tabla se redibuja sola.
    private final ObservableList<Planta> datos = FXCollections.observableArrayList();

    @FXML private TableView<Planta> tabla;
    @FXML private TableColumn<Planta, Number> colId;
    @FXML private TableColumn<Planta, String> colTipo;
    @FXML private TableColumn<Planta, String> colNombre;
    @FXML private TableColumn<Planta, Number> colAltura;
    @FXML private TableColumn<Planta, Number> colCantidad;
    @FXML private TableColumn<Planta, String> colPrecio;
    @FXML private TableColumn<Planta, String> colDespacho;

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtAltura;
    @FXML private TextField txtCantidad;
    @FXML private Label lblVolumen;
    @FXML private TextField txtVolumen;
    @FXML private Label lblEspecie;
    @FXML private TextField txtEspecie;
    @FXML private CheckBox chkHeladas;
    @FXML private Button btnDespacho;
    @FXML private Label lblEstado;

    // ------------------------------------------------------------------ inicio

    @FXML
    public void initialize() {
        // Cada columna recibe una función "fila -> valor a mostrar".
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colTipo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTipo()));
        colNombre.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNombre()));
        colAltura.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getAlturaCm()));
        colCantidad.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCantidad()));
        colPrecio.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(Planta.pesos(c.getValue().calcularPrecio())));
        colDespacho.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(textoDespacho(c.getValue())));
        tabla.setItems(datos);

        cmbTipo.setItems(FXCollections.observableArrayList(INTERIOR, EXTERIOR));
        cmbTipo.getSelectionModel().select(INTERIOR);
        cmbTipo.valueProperty().addListener((obs, anterior, nuevo) -> mostrarCamposDelTipo());
        mostrarCamposDelTipo();

        // Al elegir una fila de la tabla, sus datos pasan al formulario para poder editarlos.
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionada) -> {
            if (seleccionada != null) {
                cargarEnFormulario(seleccionada);
            }
            actualizarBotones();
        });

        cargarDatos();
    }

    // ------------------------------------------------------------------ acciones

    @FXML
    private void guardar() {
        ejecutar("Guardar", () -> {
            Planta planta = construirDesdeFormulario();
            repositorio.registrar(planta);
            cargarDatos();
            limpiar();
            lblEstado.setText("Planta '" + planta.getNombre() + "' guardada con id " + planta.getId() + ".");
        });
    }

    @FXML
    private void actualizar() {
        Planta seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            avisar("Actualizar", "Selecciona una planta de la tabla.");
            return;
        }
        ejecutar("Actualizar", () -> {
            Planta modificada = construirDesdeFormulario();
            modificada.setId(seleccionada.getId());
            conservarDespacho(seleccionada, modificada);
            if (repositorio.actualizar(modificada)) {
                lblEstado.setText("Planta #" + modificada.getId() + " actualizada.");
            } else {
                lblEstado.setText("La planta #" + modificada.getId() + " ya no existe en la base de datos.");
            }
            cargarDatos();
            limpiar();
        });
    }

    @FXML
    private void eliminar() {
        Planta seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            avisar("Eliminar", "Selecciona una planta de la tabla.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar '" + seleccionada.getNombre() + "' del inventario?", ButtonType.OK, ButtonType.CANCEL);
        confirmacion.setHeaderText(null);
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        ejecutar("Eliminar", () -> {
            repositorio.eliminar(seleccionada);
            cargarDatos();
            limpiar();
            lblEstado.setText("Planta '" + seleccionada.getNombre() + "' eliminada.");
        });
    }

    @FXML
    private void marcarDespacho() {
        Planta seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (!(seleccionada instanceof Despachable despachable)) {
            avisar("Despacho", "Solo las plantas de exterior se despachan a domicilio.");
            return;
        }
        ejecutar("Despacho", () -> {
            despachable.marcarListaParaDespacho();
            repositorio.actualizar(seleccionada);
            cargarDatos();
            lblEstado.setText("Planta #" + seleccionada.getId() + " lista para despacho.");
        });
    }

    @FXML
    private void limpiar() {
        tabla.getSelectionModel().clearSelection();
        txtNombre.clear();
        txtAltura.clear();
        txtCantidad.clear();
        txtVolumen.clear();
        txtEspecie.clear();
        chkHeladas.setSelected(false);
        cmbTipo.getSelectionModel().select(INTERIOR);
        actualizarBotones();
    }

    // ------------------------------------------------------------------ apoyo

    private void cargarDatos() {
        datos.setAll(repositorio.listar());          // la tabla se actualiza sola
    }

    /** Lee el formulario y crea el objeto del modelo. El modelo valida; si algo es inválido lanza IllegalArgumentException. */
    private Planta construirDesdeFormulario() {
        String nombre = txtNombre.getText();
        int altura = entero("La altura", txtAltura.getText());
        int cantidad = entero("La cantidad", txtCantidad.getText());

        if (INTERIOR.equals(cmbTipo.getValue())) {
            return new PlantaInterior(nombre, altura, cantidad, decimal("El volumen de la maceta", txtVolumen.getText()));
        }
        String especie = txtEspecie.getText().trim();
        if (especie.isEmpty()) {
            throw new IllegalArgumentException("La especie no puede estar vacía.");
        }
        return new PlantaExterior(nombre, altura, cantidad, especie, chkHeladas.isSelected());
    }

    /** Si la planta ya estaba lista para despacho, editarla no debe perder ese estado. */
    private static void conservarDespacho(Planta original, Planta modificada) {
        if (original instanceof Despachable o && o.estaListaParaDespacho() && modificada instanceof Despachable m) {
            m.marcarListaParaDespacho();
        }
    }

    private void cargarEnFormulario(Planta planta) {
        txtNombre.setText(planta.getNombre());
        txtAltura.setText(String.valueOf(planta.getAlturaCm()));
        txtCantidad.setText(String.valueOf(planta.getCantidad()));
        if (planta instanceof PlantaInterior interior) {
            cmbTipo.getSelectionModel().select(INTERIOR);
            txtVolumen.setText(String.valueOf(interior.getVolumenMacetaLitros()));
        } else if (planta instanceof PlantaExterior exterior) {
            cmbTipo.getSelectionModel().select(EXTERIOR);
            txtEspecie.setText(exterior.getEspecie());
            chkHeladas.setSelected(exterior.isResistenteHeladas());
        }
    }

    private void mostrarCamposDelTipo() {
        boolean interior = INTERIOR.equals(cmbTipo.getValue());
        mostrar(interior, lblVolumen, txtVolumen);
        mostrar(!interior, lblEspecie, txtEspecie, chkHeladas);
    }

    /** Un control oculto también debe dejar de ocupar espacio: visible Y managed. */
    private static void mostrar(boolean visible, javafx.scene.Node... controles) {
        for (javafx.scene.Node control : controles) {
            control.setVisible(visible);
            control.setManaged(visible);
        }
    }

    private void actualizarBotones() {
        btnDespacho.setDisable(!(tabla.getSelectionModel().getSelectedItem() instanceof Despachable));
    }

    private static String textoDespacho(Planta planta) {
        if (planta instanceof Despachable d) {
            return d.estaListaParaDespacho() ? "Listo" : "Pendiente";
        }
        return "—";
    }

    private static int entero(String campo, String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un número entero.");
        }
    }

    private static double decimal(String campo, String texto) {
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un número (ej: 3.5).");
        }
    }

    /** Ejecuta una acción y muestra un Alert si el modelo rechaza los datos o falla la base de datos. */
    private void ejecutar(String titulo, Runnable accion) {
        try {
            accion.run();
        } catch (IllegalArgumentException e) {
            avisar(titulo, e.getMessage());                              // dato inválido (lo dijo el modelo)
        } catch (PersistenciaException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR, e.getMessage()
                    + "\n\nDetalle: " + e.getCause().getMessage());      // falló la base de datos
            alerta.setTitle(titulo);
            alerta.setHeaderText("Error de base de datos");
            alerta.showAndWait();
        }
    }

    private void avisar(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING, mensaje);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
