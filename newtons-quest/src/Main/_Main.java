package Main;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación
 * 
 * Esta clase extiende Application de JavaFX y es responsable de inicializar
 * la aplicación cargando la pantalla de inicio de sesión desde el entorno
 * de desarrollo.
 */
public class _Main extends Application {
    
    // === Constantes de configuración ===
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 700;
    private static final String WINDOW_TITLE = "Newton's Apple Quest - Inicio de Sesión";
    
    // === Rutas de recursos (entorno de desarrollo) ===
    private static final String LOGIN_FXML_PATH = "src/Vista/Login.fxml";
    private static final String LOGIN_CSS_PATH = "src/Vista/resources/login.css";
    
    /**
     * Método principal que inicializa el escenario primario de JavaFX.
     * 
     * Carga la vista de login y su CSS desde el directorio de fuentes del proyecto.
     * Configura la ventana principal con un tamaño fijo y aplica los estilos CSS.
     * 
     * @param primaryStage El escenario principal de la aplicación
     * @throws Exception Si ocurre un error durante la carga de la vista o recursos
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // === Carga de recursos desde directorio de desarrollo ===
        File loginFxmlFile = new File(LOGIN_FXML_PATH);
        File loginCssFile = new File(LOGIN_CSS_PATH);
        
        // Cargar la vista FXML
        FXMLLoader loader = new FXMLLoader(loginFxmlFile.toURI().toURL());
        Parent root = loader.load();
        
        // === Configuración de la escena ===
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Aplicar estilos CSS
        String cssPath = loginCssFile.toURI().toURL().toExternalForm();
        scene.getStylesheets().add(cssPath);

        // === Configuración de la ventana principal ===
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Método principal de la aplicación.
     * 
     * Punto de entrada de la aplicación que inicia el ciclo de vida de JavaFX.
     * Este método es llamado automáticamente por la JVM al ejecutar la aplicación.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
