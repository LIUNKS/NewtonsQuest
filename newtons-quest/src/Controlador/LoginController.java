package Controlador;

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

public class LoginController {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label errorLabel;
    
    // Usuario y contraseña de prueba (en un sistema real, esto estaría en una base de datos)
    private final String TEST_USERNAME = "newton";
    private final String TEST_PASSWORD = "apple123";
    
    public void initialize() {
        // Configurar el botón de inicio de sesión
        loginButton.setOnAction(event -> handleLogin());
        
        // Configurar el botón de registro (por ahora solo muestra un mensaje)
        registerButton.setOnAction(event -> System.out.println("Abrir pantalla de registro"));
        
        // También permitir iniciar sesión presionando Enter en el campo de contraseña
        passwordField.setOnAction(event -> handleLogin());
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        // Verificar credenciales (en un sistema real, esto consultaría una base de datos)
        if (username.equals(TEST_USERNAME) && password.equals(TEST_PASSWORD)) {
            // Credenciales correctas, abrir el menú principal
            try {
                openMainMenu();
            } catch (IOException e) {
                System.err.println("Error al cargar el menú principal: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Credenciales incorrectas, mostrar mensaje de error
            errorLabel.setVisible(true);
            passwordField.clear();
        }
    }
    
    private void openMainMenu() throws IOException {
    // Cargar el menú principal
    FXMLLoader loader = new FXMLLoader(new File("C:/Users/johan/OneDrive/Documentos/NetBeansProjects/AlgoritmosYEstructurasDeDatos/newtons-quest/src/Main/Main.fxml").toURI().toURL());
    Parent root = loader.load();
    
    // Crear una nueva escena
    Scene scene = new Scene(root, 800, 600);
    
    // Añadir el CSS específico para el menú principal
    scene.getStylesheets().add(new File("C:/Users/johan/OneDrive/Documentos/NetBeansProjects/AlgoritmosYEstructurasDeDatos/newtons-quest/src/Vista/main.css").toURI().toURL().toExternalForm());
    
    // Obtener el stage actual
    Stage stage = (Stage) loginButton.getScene().getWindow();
    
    // Cambiar la escena
    stage.setScene(scene);
    stage.setTitle("Newton's Apple Quest - Menú Principal");
}
}