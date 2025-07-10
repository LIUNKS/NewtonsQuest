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
 * Gestor de navegación.
 * 
 * Esta clase centraliza toda la lógica de navegación entre las diferentes pantallas
 * de la aplicación. Proporciona métodos estáticos para cambiar entre vistas,
 * manteniendo consistencia en la carga de recursos FXML y CSS.
 * 
 * Características principales:
 * - Navegación fluida entre pantallas
 * - Carga automática de recursos FXML y CSS
 * - Manejo de sesiones de usuario
 * - Soporte para entorno de desarrollo
 * - Gestión de controladores específicos
 */
public class NavigationManager {
    
    /**
     * Constructor privado para prevenir la instanciación de esta clase de utilidades.
     */
    private NavigationManager() {
        throw new UnsupportedOperationException("Esta es una clase de utilidades y no debe ser instanciada");
    }
    
    // ===============================================
    // === MÉTODOS DE NAVEGACIÓN PRINCIPALES ===
    // ===============================================
    
    /**
     * Navega a la pantalla del mapa de aventuras.
     * 
     * @param currentStage El escenario actual donde se cargará la nueva vista
     * @throws IOException Si ocurre un error al cargar los recursos FXML o CSS
     */
    public static void navigateToMap(Stage currentStage) throws IOException {
        loadScene(currentStage, GameConstants.FXML_PATH + GameConstants.MAP_FXML, 
                 GameConstants.CSS_PATH + GameConstants.MAP_CSS, 
                 GameConstants.MAP_TITLE, 
                 GameConstants.MAIN_WINDOW_WIDTH, GameConstants.MAIN_WINDOW_HEIGHT);
    }
    
    /**
     * Navega a la pantalla del mapa con actualización del estado.
     * 
     * Este método específico se utiliza cuando el usuario regresa del juego
     * para actualizar automáticamente el progreso y el estado de los botones
     * en el mapa de aventuras.
     * 
     * @param currentStage El escenario actual donde se cargará la vista
     * @throws IOException Si ocurre un error al cargar los recursos o al actualizar el estado
     */
    public static void navigateToMapWithRefresh(Stage currentStage) throws IOException {
        // === Configuración de rutas de recursos ===
        String fxmlPath = GameConstants.FXML_PATH + GameConstants.MAP_FXML;
        String cssPath = GameConstants.CSS_PATH + GameConstants.MAP_CSS;
        
        // === Carga de recursos desde directorio de desarrollo ===
        File fxmlFile = new File(fxmlPath);
        File cssFile = new File(cssPath);
        
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        
        // === Actualización del estado del controlador ===
        Object controller = loader.getController();
        if (controller instanceof Controlador.MapController) {
            Controlador.MapController mapController = (Controlador.MapController) controller;
            mapController.refreshMapState();
        }
        
        // === Configuración de la escena ===
        String cssUrl = cssFile.toURI().toURL().toExternalForm();
        Scene scene = new Scene(root, GameConstants.MAIN_WINDOW_WIDTH, GameConstants.MAIN_WINDOW_HEIGHT);
        scene.getStylesheets().add(cssUrl);
        
        // === Aplicación al escenario ===
        currentStage.setScene(scene);
        currentStage.setTitle(GameConstants.MAP_TITLE);
    }
    
    /**
     * Navega a la pantalla de inicio de sesión.
     * 
     * @param currentStage El escenario actual donde se cargará la vista de login
     * @throws IOException Si ocurre un error al cargar los recursos FXML o CSS
     */
    public static void navigateToLogin(Stage currentStage) throws IOException {
        loadScene(currentStage, GameConstants.FXML_PATH + GameConstants.LOGIN_FXML, 
                 GameConstants.CSS_PATH + GameConstants.LOGIN_CSS, 
                 GameConstants.LOGIN_TITLE, 
                 GameConstants.MAIN_WINDOW_WIDTH, GameConstants.MAIN_WINDOW_HEIGHT);
    }
    
    /**
     * Navega a la pantalla del menú principal.
     * 
     * @param currentStage El escenario actual donde se cargará la vista principal
     * @throws IOException Si ocurre un error al cargar los recursos FXML o CSS
     */
    public static void navigateToMain(Stage currentStage) throws IOException {
        loadScene(currentStage, GameConstants.FXML_PATH + GameConstants.MAIN_FXML, 
                 GameConstants.CSS_PATH + GameConstants.MAIN_CSS, 
                 GameConstants.MAIN_TITLE, 
                 GameConstants.MAIN_WINDOW_WIDTH, GameConstants.MAIN_WINDOW_HEIGHT);
    }
    
    /**
     * Navega al menú principal configurando automáticamente el usuario actual.
     * 
     * Este método especializado carga la pantalla principal y configura
     * automáticamente la información del usuario logueado usando SessionManager.
     * Ideal para usar después del login exitoso.
     * 
     * @param currentStage El escenario actual donde se cargará la vista
     * @throws IOException Si ocurre un error al cargar los recursos o configurar el usuario
     */
    public static void navigateToMainWithUser(Stage currentStage) throws IOException {
        // === Configuración de rutas de recursos ===
        String fxmlPath = GameConstants.FXML_PATH + GameConstants.MAIN_FXML;
        String cssPath = GameConstants.CSS_PATH + GameConstants.MAIN_CSS;
        
        // === Carga de recursos desde directorio de desarrollo ===
        File fxmlFile = new File(fxmlPath);
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        
        // === Configuración del controlador con datos de sesión ===
        MainController mainController = loader.getController();
        SessionManager sessionManager = SessionManager.getInstance();
        
        if (sessionManager.isLoggedIn()) {
            mainController.setUsername(sessionManager.getCurrentUsername());
        }
        
        // === Configuración de la escena ===
        File cssFile = new File(cssPath);
        String cssUrl = cssFile.toURI().toURL().toExternalForm();
        Scene scene = new Scene(root, GameConstants.MAIN_WINDOW_WIDTH, GameConstants.MAIN_WINDOW_HEIGHT);
        scene.getStylesheets().add(cssUrl);
        
        // === Configuración del escenario ===
        currentStage.setScene(scene);
        currentStage.setTitle(GameConstants.MAIN_TITLE);
        scene.getRoot().requestFocus();
    }
    
    /**
     * Navega a la pantalla del quiz de conocimientos.
     * 
     * @param currentStage El escenario actual donde se cargará la vista del quiz
     * @throws IOException Si ocurre un error al cargar los recursos FXML o CSS
     */
    public static void navigateToQuiz(Stage currentStage) throws IOException {
        loadScene(currentStage, GameConstants.FXML_PATH + GameConstants.QUIZ_FXML, 
                 GameConstants.CSS_PATH + GameConstants.QUIZ_CSS, 
                 GameConstants.QUIZ_TITLE, 
                 GameConstants.MAIN_WINDOW_WIDTH, GameConstants.MAIN_WINDOW_HEIGHT);
    }
    
    /**
     * Navega a la pantalla de resultados del quiz.
     * 
     * @param currentStage El escenario actual donde se cargará la vista de resultados
     * @throws IOException Si ocurre un error al cargar los recursos FXML o CSS
     */
    public static void navigateToQuizResult(Stage currentStage) throws IOException {
        loadScene(currentStage, GameConstants.FXML_PATH + GameConstants.QUIZ_RESULT_FXML, 
                 GameConstants.CSS_PATH + GameConstants.QUIZ_CSS, 
                 GameConstants.QUIZ_RESULT_TITLE, 
                 GameConstants.MAIN_WINDOW_WIDTH, GameConstants.MAIN_WINDOW_HEIGHT);
    }
    
    /**
     * Navega a la pantalla del mapa usando un método alternativo.
     * 
     * Este método intenta navegar al mapa cuando la referencia al Stage no está disponible.
     * Crea un nuevo Stage como último recurso.
     * 
     * @throws IOException Si ocurre un error al cargar los recursos FXML o CSS
     */
    public static void navigateToMapAlternative() throws IOException {
        // Buscar cualquier Stage existente para reusar
        Stage foundStage = null;
        for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
            if (window instanceof Stage) {
                foundStage = (Stage) window;
                break;
            }
        }
        
        // Si se encontró un Stage, usarlo
        if (foundStage != null) {
            navigateToMap(foundStage);
        } else {
            // Como último recurso, crear un nuevo Stage
            Stage newStage = new Stage();
            navigateToMap(newStage);
            newStage.show();
        }
    }
    
    /**
     * Navega a la pantalla del mapa en caso de emergencia.
     * 
     * Este método es un último recurso cuando todos los demás métodos de navegación han fallado.
     * Intenta cargar directamente la pantalla del mapa usando un enfoque simplificado.
     * 
     * @throws IOException Si ocurre un error al cargar los recursos FXML
     */
    public static void navigateToMapEmergency() throws IOException {
        try {
            // Intentar cargar el FXML directamente
            String fxmlPath = GameConstants.FXML_PATH + GameConstants.MAP_FXML;
            File fxmlFile = new File(fxmlPath);
            
            Parent root;
            if (fxmlFile.exists()) {
                root = FXMLLoader.load(fxmlFile.toURI().toURL());
            } else {
                // Si no se encuentra en la ruta de desarrollo, intentar con el classloader
                root = FXMLLoader.load(NavigationManager.class.getResource("/Vista/Map.fxml"));
            }
            
            // Crear una nueva escena y stage
            Scene scene = new Scene(root, GameConstants.MAIN_WINDOW_WIDTH, GameConstants.MAIN_WINDOW_HEIGHT);
            Stage emergencyStage = new Stage();
            emergencyStage.setScene(scene);
            emergencyStage.setTitle("Newton's Apple Quest - Mapa");
            
            // Cerrar todas las ventanas existentes
            for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                if (window instanceof Stage && window != emergencyStage) {
                    ((Stage) window).close();
                }
            }
            
            // Mostrar la ventana de emergencia
            emergencyStage.show();
            
        } catch (Exception e) {
            System.err.println("Error crítico en navegación de emergencia: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("No se pudo realizar la navegación de emergencia", e);
        }
    }
    
    // ===============================================
    // === MÉTODOS DE UTILIDAD PRIVADOS ===
    // ===============================================
    
    /**
     * Método genérico para cargar una escena con configuración estándar.
     * 
     * Este método centraliza la lógica común de carga de vistas desde el
     * directorio de desarrollo, aplicación de estilos CSS y configuración del escenario.
     * 
     * @param stage El escenario donde se aplicará la nueva escena
     * @param fxmlPath Ruta del archivo FXML a cargar
     * @param cssPath Ruta del archivo CSS a aplicar
     * @param title Título de la ventana
     * @param width Ancho de la ventana
     * @param height Alto de la ventana
     * @throws IOException Si ocurre un error al cargar los recursos
     */
    private static void loadScene(Stage stage, String fxmlPath, String cssPath, 
                                 String title, int width, int height) throws IOException {
        
        // === Carga de recursos desde directorio de desarrollo ===
        File fxmlFile = new File(fxmlPath);
        File cssFile = new File(cssPath);
        
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        
        // === Configuración de la escena ===
        String cssUrl = cssFile.toURI().toURL().toExternalForm();
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(cssUrl);
        
        // === Configuración del escenario ===
        stage.setScene(scene);
        stage.setTitle(title);
        scene.getRoot().requestFocus();
    }
    
    /**
     * Obtiene una instancia del controlador de una vista específica.
     * 
     * Este método utilitario carga un archivo FXML desde el directorio de desarrollo
     * y retorna su controlador asociado, útil para obtener referencias a controladores
     * específicos sin cambiar la escena actual.
     * 
     * @param <T> Tipo del controlador esperado
     * @param stage El escenario actual (no utilizado actualmente)
     * @param fxmlPath Ruta del archivo FXML
     * @param controllerClass Clase del controlador esperado
     * @return Instancia del controlador del tipo especificado
     * @throws IOException Si ocurre un error al cargar el FXML
     */
    public static <T> T getController(Stage stage, String fxmlPath, Class<T> controllerClass) throws IOException {
        File fxmlFile = new File(fxmlPath);
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        loader.load();
        return loader.getController();
    }
}
