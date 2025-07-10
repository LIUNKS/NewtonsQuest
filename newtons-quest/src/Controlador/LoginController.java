package Controlador;

import Modelo.dao.UsuarioDAO;
import Modelo.ConexionDB;
import Controlador.componentes.RankingManager;
import Controlador.utils.SessionManager;
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

/**
 * Controlador para la pantalla de login/autenticación en Newton's Apple Quest.
 * 
 * Esta clase gestiona la autenticación de usuarios y el acceso inicial al juego.
 * Maneja tanto el proceso de login como la navegación hacia el registro de nuevos usuarios.
 * 
 * Funcionalidades principales:
 * - Autenticación de usuarios existentes
 * - Validación de credenciales (usuario y contraseña)
 * - Gestión de sesión de usuario autenticado
 * - Navegación hacia pantalla de registro
 * - Navegación hacia menú principal tras login exitoso
 * - Manejo de errores de autenticación
 * 
 * Flujo de autenticación:
 * 1. Usuario ingresa credenciales (username/password)
 * 2. Validación de datos de entrada
 * 3. Verificación contra base de datos
 * 4. Establecimiento de sesión si la autenticación es exitosa
 * 5. Navegación al menú principal del juego
 * 
 * Gestión de errores:
 * - Credenciales incorrectas
 * - Problemas de conexión a base de datos
 * - Usuarios no existentes
 * - Campos vacíos o inválidos
 * 
 * @author Equipo de desarrollo Newton's Quest
 * @version 1.0
 * @since 2025
 */
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
                    // Error silencioso al cerrar conexión
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
                // Error silencioso al abrir pantalla de registro
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
        
        // Iniciar sesión en el SessionManager
        SessionManager.getInstance().login(username);
        
        // Configurar el usuario actual en el RankingManager
        int userId = UsuarioDAO.obtenerIdUsuario(username);
        if (userId != -1) {
            RankingManager.getInstance().setCurrentUser(userId, username);
        } else {
            showError("Error: No se pudo obtener el ID del usuario");
            return;
        }

        // Abrir el menú principal
        try {
            openMainMenu();
        } catch (IOException e) {
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
    }    private void openRegisterScreen() throws IOException {
        try {
            // Obtener la ruta del archivo FXML
            String registerFxmlPath = "src/Vista/Register.fxml";
            String registerCssPath = "src/Vista/resources/Register.css";

            // Cargar directamente desde archivo en modo desarrollo
            FXMLLoader loader = new FXMLLoader(new File(registerFxmlPath).toURI().toURL());
            String cssPath = new File(registerCssPath).toURI().toURL().toExternalForm();

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
            // Error al cargar pantalla de registro
            throw e;
        }
    }

    private void openMainMenu() throws IOException {
        try {
            // Obtener la ruta del archivo FXML
            String mainFxmlPath = "src/Vista/Main.fxml";
            String mainCssPath = "src/Vista/resources/main.css";

            // Cargar directamente desde archivo en modo desarrollo
            FXMLLoader loader = new FXMLLoader(new File(mainFxmlPath).toURI().toURL());
            String cssPath = new File(mainCssPath).toURI().toURL().toExternalForm();

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
            // Error al cargar menú principal
            throw e;
        }
    }

    // Método para obtener el nombre de usuario currenteal
    public static String getCurrentUsername() {
        return currentUsername;
    }
}