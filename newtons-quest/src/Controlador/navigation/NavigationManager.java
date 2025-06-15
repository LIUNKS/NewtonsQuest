package Controlador.navigation;

import Controlador.constants.GameConstants;
import Controlador.MainController;
import Controlador.utils.SessionManager;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase utilitaria para manejar la navegación entre ventanas del juego.
 * Centraliza la lógica de carga de FXML y CSS para mantener consistencia.
 */
public class NavigationManager {
      /**
     * Navega a la pantalla del mapa
     */
    public static void navigateToMap(Stage currentStage) throws IOException {
        loadScene(currentStage, GameConstants.FXML_PATH + GameConstants.MAP_FXML, 
                 GameConstants.CSS_PATH + GameConstants.MAP_CSS, 
                 GameConstants.MAP_TITLE, 
                 GameConstants.MAIN_WINDOW_WIDTH, GameConstants.MAIN_WINDOW_HEIGHT);
    }
    
    /**
     * Navega a la pantalla de login
     */
    public static void navigateToLogin(Stage currentStage) throws IOException {
        loadScene(currentStage, GameConstants.FXML_PATH + GameConstants.LOGIN_FXML, 
                 GameConstants.CSS_PATH + GameConstants.LOGIN_CSS, 
                 GameConstants.LOGIN_TITLE, 
                 GameConstants.MAIN_WINDOW_WIDTH, GameConstants.MAIN_WINDOW_HEIGHT);
    }
      /**
     * Navega a la pantalla principal
     */
    public static void navigateToMain(Stage currentStage) throws IOException {
        loadScene(currentStage, GameConstants.FXML_PATH + GameConstants.MAIN_FXML, 
                 GameConstants.CSS_PATH + GameConstants.MAIN_CSS, 
                 GameConstants.MAIN_TITLE, 
                 GameConstants.MAIN_WINDOW_WIDTH, GameConstants.MAIN_WINDOW_HEIGHT);
    }
    
    /**
     * Navega a la pantalla principal configurando el usuario actual
     */
    public static void navigateToMainWithUser(Stage currentStage) throws IOException {
        // Cargar el FXML y obtener el controlador
        String fxmlPath = GameConstants.FXML_PATH + GameConstants.MAIN_FXML;
        String cssPath = GameConstants.CSS_PATH + GameConstants.MAIN_CSS;
        
        // Verificar si los archivos existen
        File fxmlFile = new File(fxmlPath);
        File cssFile = new File(cssPath);
        
        if (!fxmlFile.exists()) {
            throw new IOException("No se encontró el archivo FXML: " + fxmlFile.getAbsolutePath());
        }
        
        if (!cssFile.exists()) {
            throw new IOException("No se encontró el archivo CSS: " + cssFile.getAbsolutePath());
        }
        
        // Cargar FXML
        FXMLLoader loader;
        String cssUrl;
        
        try {
            // Intentar cargar desde archivo (desarrollo)
            loader = new FXMLLoader(fxmlFile.toURI().toURL());
            cssUrl = cssFile.toURI().toURL().toExternalForm();
        } catch (Exception e) {
            // Fallback para producción
            String resourcePath = fxmlPath.replace("src/", "/");
            String cssResourcePath = cssPath.replace("src/", "/");
            
            loader = new FXMLLoader(NavigationManager.class.getResource(resourcePath));
            cssUrl = NavigationManager.class.getResource(cssResourcePath).toExternalForm();
        }
        
        // Cargar la vista
        Parent root = loader.load();
        
        // Obtener el controlador y configurar el usuario
        MainController mainController = loader.getController();
        SessionManager sessionManager = SessionManager.getInstance();
        
        if (sessionManager.isLoggedIn()) {
            mainController.setUsername(sessionManager.getCurrentUsername());
        }
        
        // Crear nueva escena
        Scene scene = new Scene(root, GameConstants.MAIN_WINDOW_WIDTH, GameConstants.MAIN_WINDOW_HEIGHT);
        
        // Aplicar CSS
        scene.getStylesheets().add(cssUrl);
        
        // Configurar stage
        currentStage.setScene(scene);
        currentStage.setWidth(GameConstants.MAIN_WINDOW_WIDTH);
        currentStage.setHeight(GameConstants.MAIN_WINDOW_HEIGHT);
        currentStage.setTitle(GameConstants.MAIN_TITLE);
        
        // Asegurar que la escena tenga foco para eventos de teclado
        scene.getRoot().requestFocus();
    }
    
    /**
     * Método genérico para cargar una escena
     */
    private static void loadScene(Stage stage, String fxmlPath, String cssPath, 
                                 String title, int width, int height) throws IOException {
        
        // Verificar si los archivos existen
        File fxmlFile = new File(fxmlPath);
        File cssFile = new File(cssPath);
        
        if (!fxmlFile.exists()) {
            throw new IOException("No se encontró el archivo FXML: " + fxmlFile.getAbsolutePath());
        }
        
        if (!cssFile.exists()) {
            throw new IOException("No se encontró el archivo CSS: " + cssFile.getAbsolutePath());
        }
        
        // Cargar FXML
        FXMLLoader loader;
        String cssUrl;
        
        try {
            // Intentar cargar desde archivo (desarrollo)
            loader = new FXMLLoader(fxmlFile.toURI().toURL());
            cssUrl = cssFile.toURI().toURL().toExternalForm();
        } catch (Exception e) {
            // Fallback para producción
            String resourcePath = fxmlPath.replace("src/", "/");
            String cssResourcePath = cssPath.replace("src/", "/");
            
            loader = new FXMLLoader(NavigationManager.class.getResource(resourcePath));
            cssUrl = NavigationManager.class.getResource(cssResourcePath).toExternalForm();
        }
        
        // Cargar la vista
        Parent root = loader.load();
        
        // Crear nueva escena
        Scene scene = new Scene(root, width, height);
        
        // Aplicar CSS
        scene.getStylesheets().add(cssUrl);
        
        // Configurar stage
        stage.setScene(scene);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setTitle(title);
        
        // Asegurar que la escena tenga foco para eventos de teclado
        scene.getRoot().requestFocus();
    }
    
    /**
     * Obtiene el controlador de la escena cargada
     */
    public static <T> T getController(Stage stage, String fxmlPath, Class<T> controllerClass) throws IOException {
        File fxmlFile = new File(fxmlPath);
        
        FXMLLoader loader;
        
        if (fxmlFile.exists()) {
            loader = new FXMLLoader(fxmlFile.toURI().toURL());
        } else {
            String resourcePath = fxmlPath.replace("src/", "/");
            loader = new FXMLLoader(NavigationManager.class.getResource(resourcePath));
        }
        
        loader.load();
        return loader.getController();
    }
}
