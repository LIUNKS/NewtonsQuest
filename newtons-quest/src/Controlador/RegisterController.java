package Controlador;

import Modelo.dao.UsuarioDAO;
import java.io.File;
import java.io.IOException;
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
 * Controlador para la pantalla de registro de nuevos usuarios.
 * 
 * Esta clase gestiona toda la lógica de registro de usuarios incluyendo:
 * - Validación de datos de entrada (nombre, usuario, email, contraseña)
 * - Verificación de disponibilidad de nombre de usuario
 * - Confirmación de contraseña
 * - Registro en la base de datos
 * - Manejo de errores y feedback al usuario
 * - Navegación de regreso al login
 */
public class RegisterController {

    // ===================================
    // COMPONENTES FXML
    // ===================================
    
    /** Campo de texto para el nombre completo */
    @FXML private TextField fullNameField;
    
    /** Campo de texto para el nombre de usuario */
    @FXML private TextField usernameField;
    
    /** Campo de texto para el email */
    @FXML private TextField emailField;
    
    /** Campo de contraseña */
    @FXML private PasswordField passwordField;
    
    /** Campo de confirmación de contraseña */
    @FXML private PasswordField confirmPasswordField;
    
    /** Botón de registro */
    @FXML private Button registerButton;
    
    /** Botón de regreso */
    @FXML private Button backButton;
    
    /** Etiqueta para mostrar errores */
    @FXML private Label errorLabel;

    /**
     * Inicializa el controlador y configura los eventos.
     */
    public void initialize() {
        // Configurar el botón de registro
        registerButton.setOnAction(event -> handleRegister());

        // Configurar el botón de volver
        backButton.setOnAction(event -> {
            try {
                goBackToLogin();
            } catch (IOException e) {
                // Error silencioso al volver a la pantalla de login
            }
        });

        // Ocultar mensaje de error cuando el usuario escribe
        fullNameField.textProperty().addListener((obs, oldText, newText) -> errorLabel.setVisible(false));
        usernameField.textProperty().addListener((obs, oldText, newText) -> errorLabel.setVisible(false));
        emailField.textProperty().addListener((obs, oldText, newText) -> errorLabel.setVisible(false));
        passwordField.textProperty().addListener((obs, oldText, newText) -> errorLabel.setVisible(false));
        confirmPasswordField.textProperty().addListener((obs, oldText, newText) -> errorLabel.setVisible(false));
        
        // Asegurarse de que el errorLabel esté inicialmente oculto
        errorLabel.setVisible(false);
    }

    private void handleRegister() {
        // Obtener los valores de los campos
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validar que todos los campos estén completos
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Por favor, completa todos los campos");
            return;
        }

        // Validar que el nombre de usuario no exista
        if (UsuarioDAO.existeUsuario(username)) {
            showError("El nombre de usuario ya está en uso");
            return;
        }

        // Validar formato de correo electrónico
        if (!email.contains("@") || !email.contains(".")) {
            showError("Por favor, ingresa un correo electrónico válido");
            return;
        }

        // Validar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            showError("Las contraseñas no coinciden");
            return;
        }

        // Validar longitud de la contraseña
        if (password.length() < 6) {
            showError("La contraseña debe tener al menos 6 caracteres");
            return;
        }        // Registrar al usuario en la base de datos con toda la información
        boolean registroExitoso = UsuarioDAO.registrarUsuario(username, password, fullName, email);

        if (registroExitoso) {
            // Mostrar mensaje de éxito y volver a la pantalla de login
            try {
                goBackToLogin();
            } catch (IOException e) {
                // Error silencioso al volver a la pantalla de login
            }
        } else {
            showError("Error al registrar el usuario. Inténtalo de nuevo.");
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
        
        // Mostrando mensaje de error en el registro
    }    private void goBackToLogin() throws IOException {
        try {            // Obtener la ruta del archivo FXML
            String loginFxmlPath = "src/Vista/Login.fxml";
            String loginCssPath = "src/Vista/resources/login.css";

            // Verificar si estamos en desarrollo o en producción
            File loginFxmlFile = new File(loginFxmlPath);
            File loginCssFile = new File(loginCssPath);

            FXMLLoader loader;
            String cssPath;

            if (loginFxmlFile.exists()) {
                // Estamos en desarrollo, usar ruta de archivo
                loader = new FXMLLoader(loginFxmlFile.toURI().toURL());
                cssPath = loginCssFile.toURI().toURL().toExternalForm();            } else {                // Estamos en producción, usar getResource
                loader = new FXMLLoader(getClass().getResource("/Vista/Login.fxml"));
                cssPath = getClass().getResource("/Vista/resources/login.css").toExternalForm();
            }

            // Cargar la pantalla de login
            Parent root = loader.load();

            // Crear una nueva escena
            Scene scene = new Scene(root, 900, 700);

            // Añadir el CSS
            scene.getStylesheets().add(cssPath);

            // Obtener el stage actual
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Cambiar la escena
            stage.setScene(scene);
            stage.setWidth(900);
            stage.setHeight(700);
            stage.setTitle("Newton's Apple Quest - Inicio de Sesión");

        } catch (IOException e) {
            // Error silencioso al cargar la pantalla de login
            throw e;
        }
    }
}