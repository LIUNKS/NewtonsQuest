package Controlador;

import Modelo.UsuarioDAO;
import Modelo.ConexionDB;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label errorLabel;

    // Variable para almacenar el nombre de usuario actual
    private static String currentUsername;

    public void initialize() {
        // Probar la conexión a la base de datos al iniciar
        Platform.runLater(() -> {
            Connection conn = ConexionDB.getConnection();
            if (conn == null) {
                showError("Error de conexión a la base de datos. Contacta al administrador.");
                loginButton.setDisable(true); // Deshabilitar el botón de login si no hay conexión
            } else {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        });

        // Configurar el botón de inicio de sesión
        loginButton.setOnAction(event -> handleLogin());

        // Configurar el botón de registro
        registerButton.setOnAction(event -> {
            try {
                openRegisterScreen();
            } catch (IOException e) {
                System.err.println("Error al abrir la pantalla de registro: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // También permitir iniciar sesión presionando Enter en el campo de contraseña
        passwordField.setOnAction(event -> handleLogin());

        // Ocultar mensaje de error cuando el usuario escribe
        usernameField.textProperty().addListener((obs, oldText, newText) -> errorLabel.setVisible(false));
        passwordField.textProperty().addListener((obs, oldText, newText) -> errorLabel.setVisible(false));

        // Asegurarse de que el errorLabel esté inicialmente oculto
        errorLabel.setVisible(false);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validar que los campos no estén vacíos
        if (username.isEmpty() || password.isEmpty()) {
            showError("Por favor, completa todos los campos");
            return;
        }

        // Verificar si el usuario existe
        if (!UsuarioDAO.existeUsuario(username)) {
            showError("El usuario no está registrado");
            return;
        }

        // Verificar si la contraseña es correcta
        if (!UsuarioDAO.validarCredenciales(username, password)) {
            passwordField.clear();
            showError("Contraseña incorrecta");
            return;
        }

        // Si llegamos aquí, las credenciales son válidas
        currentUsername = username;

        // Abrir el menú principal
        try {
            openMainMenu();
        } catch (IOException e) {
            System.err.println("Error al cargar el menú principal: " + e.getMessage());
            e.printStackTrace();
            showError("Error al cargar el menú principal");
        }
    }

    // Método para mostrar mensajes de error
    private void showError(String message) {
        // Configurar el texto del mensaje de error
        errorLabel.setText(message);
        
        // Aplicar estilo al mensaje de error
        errorLabel.setStyle("-fx-background-color: rgba(255,107,107,0.15); -fx-padding: 10px; " +
                           "-fx-background-radius: 5px; -fx-border-color: #ff6b6b; " +
                           "-fx-border-radius: 5px; -fx-border-width: 1px; " +
                           "-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
        
        // Hacer visible el mensaje de error
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        
        System.out.println("Mostrando error: " + message);
    }

    private void openRegisterScreen() throws IOException {
        try {
            // Obtener la ruta del archivo FXML
            String registerFxmlPath = "src/Main/Register.fxml";
            String registerCssPath = "src/Vista/register.css";

            // Verificar si estamos en desarrollo o en producción
            File registerFxmlFile = new File(registerFxmlPath);
            File registerCssFile = new File(registerCssPath);

            FXMLLoader loader;
            String cssPath;

            if (registerFxmlFile.exists()) {
                // Estamos en desarrollo, usar ruta de archivo
                loader = new FXMLLoader(registerFxmlFile.toURI().toURL());
                cssPath = registerCssFile.toURI().toURL().toExternalForm();
            } else {
                // Estamos en producción, usar getResource
                loader = new FXMLLoader(getClass().getResource("/Main/Register.fxml"));
                cssPath = getClass().getResource("/Vista/register.css").toExternalForm();
            }

            // Cargar la pantalla de registro
            Parent root = loader.load();

            // Crear una nueva escena con mayor altura para acomodar todos los campos
            Scene scene = new Scene(root, 900, 700);

            // Añadir el CSS
            scene.getStylesheets().add(cssPath);

            // Obtener el stage actual
            Stage stage = (Stage) registerButton.getScene().getWindow();

            // Cambiar la escena y ajustar el tamaño de la ventana
            stage.setScene(scene);
            stage.setWidth(900);
            stage.setHeight(750);
            stage.setTitle("Newton's Apple Quest - Registro");

        } catch (IOException e) {
            System.err.println("Error al cargar la pantalla de registro: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void openMainMenu() throws IOException {
        try {
            // Obtener la ruta del archivo FXML
            String mainFxmlPath = "src/Main/Main.fxml";
            String mainCssPath = "src/Vista/main.css";

            // Verificar si estamos en desarrollo o en producción
            File mainFxmlFile = new File(mainFxmlPath);
            File mainCssFile = new File(mainCssPath);

            FXMLLoader loader;
            String cssPath;

            if (mainFxmlFile.exists()) {
                // Estamos en desarrollo, usar ruta de archivo
                loader = new FXMLLoader(mainFxmlFile.toURI().toURL());
                cssPath = mainCssFile.toURI().toURL().toExternalForm();
            } else {
                // Estamos en producción, usar getResource
                loader = new FXMLLoader(getClass().getResource("/Main/Main.fxml"));
                cssPath = getClass().getResource("/Vista/main.css").toExternalForm();
            }

            // Cargar el menú principal
            Parent root = loader.load();

            // Obtener el controlador del menú principal
            MainController mainController = loader.getController();

            // Pasar el nombre de usuario al controlador del menú principal
            mainController.setUsername(currentUsername);

            // Crear una nueva escena
            Scene scene = new Scene(root, 900, 700);

            // Añadir el CSS específico para el menú principal
            scene.getStylesheets().add(cssPath);

            // Obtener el stage actual
            Stage stage = (Stage) loginButton.getScene().getWindow();

            // Cambiar la escena
            stage.setScene(scene);
            stage.setWidth(900);
            stage.setHeight(700);
            stage.setTitle("Newton's Apple Quest - Menú Principal");

        } catch (IOException e) {
            System.err.println("Error al cargar el menú principal: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Método para obtener el nombre de usuario actual
    public static String getCurrentUsername() {
        return currentUsername;
    }
}