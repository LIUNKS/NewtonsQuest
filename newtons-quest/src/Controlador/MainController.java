package Controlador;

import Modelo.UsuarioDAO;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainController {
    
    @FXML private Button startButton;
    @FXML private Button rulesButton;
    @FXML private Button settingsButton;
    @FXML private Button logoutButton; // Botón para cerrar sesión
    @FXML private Label welcomeLabel;
    
    private String username;
    private int userId;
    
    public void initialize() {
        // Configurar acciones para los botones
        startButton.setOnAction(event -> startGame());
        rulesButton.setOnAction(event -> showRules());
        settingsButton.setOnAction(event -> showSettings());
        
        // Configurar el botón de cerrar sesión
        logoutButton.setOnAction(event -> handleLogout());
        
        System.out.println("MainController inicializado. Botón de logout configurado: " + (logoutButton != null));
    }
    
    // Método para establecer el nombre de usuario
    public void setUsername(String username) {
        this.username = username;
        this.userId = UsuarioDAO.obtenerIdUsuario(username);
        
        // Actualizar la etiqueta de bienvenida si existe
        if (welcomeLabel != null) {
            welcomeLabel.setText("¡Bienvenido, " + username + "!");
        }
    }
      private void startGame() {
        try {
            System.out.println("Cargando la pantalla del mapa...");
            
            // Verificar si el archivo Map.fxml existe
            File mapFxmlFile = new File("src/Main/Map.fxml");
            if (!mapFxmlFile.exists()) {
                System.err.println("Error: No se encontró el archivo Map.fxml en " + mapFxmlFile.getAbsolutePath());
                return;
            }
            
            // Verificar si el archivo map.css existe
            File mapCssFile = new File("src/Vista/map.css");
            if (!mapCssFile.exists()) {
                System.err.println("Error: No se encontró el archivo map.css en " + mapCssFile.getAbsolutePath());
                return;
            }
            
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(mapFxmlFile.toURI().toURL());
            Parent root = loader.load();
            
            // Obtener el controlador del mapa
            MapController mapController = loader.getController();
            
            // Crear una nueva escena
            Scene scene = new Scene(root, 900, 700);
            
            // Añadir el CSS
            scene.getStylesheets().add(mapCssFile.toURI().toURL().toExternalForm());
            
            // Obtener el stage actual
            Stage stage = (Stage) startButton.getScene().getWindow();
            
            // Cambiar la escena
            stage.setScene(scene);
            stage.setTitle("Newton's Apple Quest - Mapa de Aventuras");
            
            // Asegurarse de que la escena tenga el foco para capturar eventos de teclado
            scene.getRoot().requestFocus();
            
            System.out.println("Pantalla del mapa cargada correctamente");
            
        } catch (IOException e) {
            System.err.println("Error al cargar la pantalla del mapa: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al cargar la pantalla del mapa: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showRules() {
        // Mostrar las reglas del juego en un diálogo
        System.out.println("MOSTRAR REGLAS");
    }
    
    private void showSettings() {
        try {
            // Aquí cargarías la pantalla de configuración
            System.out.println("MOSTRAR CONFIGURACION");
        } catch (Exception e) {
            System.out.println("No se puedo abrir la configuracion");
        }
    }
    
    // Método para manejar el cierre de sesión
    private void handleLogout() {
        try {
            System.out.println("Cerrando sesión...");
            
            // Obtener la ruta del archivo FXML
            String loginFxmlPath = "src/Main/Login.fxml";
            String loginCssPath = "src/Vista/login.css";
            
            // Verificar si estamos en desarrollo o en producción
            File loginFxmlFile = new File(loginFxmlPath);
            File loginCssFile = new File(loginCssPath);
            
            FXMLLoader loader;
            String cssPath;
            
            if (loginFxmlFile.exists()) {
                // Estamos en desarrollo, usar ruta de archivo
                loader = new FXMLLoader(loginFxmlFile.toURI().toURL());
                cssPath = loginCssFile.toURI().toURL().toExternalForm();
            } else {
                // Estamos en producción, usar getResource
                loader = new FXMLLoader(getClass().getResource("/Main/Login.fxml"));
                cssPath = getClass().getResource("/Vista/login.css").toExternalForm();
            }
            
            // Cargar la pantalla de login
            Parent root = loader.load();
            
            // Crear una nueva escena
            Scene scene = new Scene(root, 900, 700);
            
            // Añadir el CSS
            scene.getStylesheets().add(cssPath);
            
            // Obtener el stage actual
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            
            // Cambiar la escena
            stage.setScene(scene);
            stage.setWidth(900);
            stage.setHeight(700);
            stage.setTitle("Newton's Apple Quest - Inicio de Sesión");
            
            System.out.println("Sesión cerrada correctamente");
            
        } catch (IOException e) {
            System.err.println("Error al cargar la pantalla de login: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al cerrar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}