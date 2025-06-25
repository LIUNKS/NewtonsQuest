package Controlador.utils;

import Controlador.constants.GameConstants;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;
import java.nio.charset.StandardCharsets;

/**
 * Utilidades para manejo de errores y diálogos del sistema.
 * Centraliza la gestión de excepciones y mensajes al usuario.
 */
public class ErrorHandler {
    
    private static final Logger LOGGER = Logger.getLogger(ErrorHandler.class.getName());
    
    // Configurar el logger una sola vez
    static {
        setupLogger();
    }
    
    // Prevenir instanciación
    private ErrorHandler() {}
    
    /**
     * Configura el logger para manejar correctamente caracteres especiales
     */
    private static void setupLogger() {
        try {
            // Configurar encoding UTF-8 para la consola
            System.setProperty("file.encoding", "UTF-8");
            System.setProperty("console.encoding", "UTF-8");
            
            // Crear un handler personalizado para la consola
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new SimpleFormatter());
            
            // Configurar el logger
            LOGGER.setUseParentHandlers(false);
            LOGGER.addHandler(consoleHandler);
            LOGGER.setLevel(Level.ALL);
            
        } catch (Exception e) {
            // Si falla la configuración, usar el logger por defecto
            System.err.println("Warning: No se pudo configurar el logger correctamente");
        }
    }
    
    /**
     * Maneja errores de navegación entre ventanas
     */
    public static void handleNavigationError(String destination, Exception e, Stage parentStage) {
        String errorMessage = String.format("Error al navegar a %s: %s", destination, e.getMessage());
        LOGGER.log(Level.SEVERE, errorMessage, e);
        
        showErrorDialog(
            "Error de Navegación",
            "No se pudo cargar la pantalla solicitada",
            errorMessage,
            parentStage
        );
    }
    
    /**
     * Maneja errores al mostrar diálogos
     */
    public static void handleDialogError(String dialogName, Exception e, Stage parentStage) {
        String errorMessage = String.format("Error al mostrar %s: %s", dialogName, e.getMessage());
        LOGGER.log(Level.WARNING, errorMessage, e);
        
        showWarningDialog(
            "Error en Diálogo",
            String.format("No se pudo mostrar %s", dialogName),
            "Se mostrará información básica en la consola.",
            parentStage
        );
    }
    
    /**
     * Maneja errores de carga de recursos
     */
    public static void handleResourceError(String resourcePath, Exception e) {
        String errorMessage = String.format("Error al cargar recurso %s: %s", resourcePath, e.getMessage());
        LOGGER.log(Level.SEVERE, errorMessage, e);
        
        // Para errores de recursos, solo log ya que pueden ser críticos
        System.err.println("RECURSO CRÍTICO NO ENCONTRADO: " + resourcePath);
    }
    
    /**
     * Maneja errores de base de datos
     */
    public static void handleDatabaseError(String operation, Exception e, Stage parentStage) {
        String errorMessage = String.format("Error en base de datos (%s): %s", operation, e.getMessage());
        LOGGER.log(Level.SEVERE, errorMessage, e);
        
        showErrorDialog(
            "Error de Base de Datos",
            "Problema de conexión con la base de datos",
            "Verifica tu conexión y vuelve a intentar.",
            parentStage
        );
    }
    
    /**
     * Muestra un diálogo de error al usuario
     */
    public static void showErrorDialog(String title, String header, String content, Stage parentStage) {
        Alert alert = createStyledAlert(AlertType.ERROR, title, header, content);
        if (parentStage != null) {
            alert.initOwner(parentStage);
        }
        alert.showAndWait();
    }
    
    /**
     * Muestra un diálogo de advertencia al usuario
     */
    public static void showWarningDialog(String title, String header, String content, Stage parentStage) {
        Alert alert = createStyledAlert(AlertType.WARNING, title, header, content);
        if (parentStage != null) {
            alert.initOwner(parentStage);
        }
        alert.showAndWait();
    }
    
    /**
     * Muestra un diálogo de información al usuario
     */
    public static void showInfoDialog(String title, String header, String content, Stage parentStage) {
        Alert alert = createStyledAlert(AlertType.INFORMATION, title, header, content);
        if (parentStage != null) {
            alert.initOwner(parentStage);
        }
        alert.showAndWait();
    }
    
    /**
     * Muestra un diálogo de confirmación
     */
    public static boolean showConfirmationDialog(String title, String header, String content, Stage parentStage) {
        Alert alert = createStyledAlert(AlertType.CONFIRMATION, title, header, content);
        if (parentStage != null) {
            alert.initOwner(parentStage);
        }
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Crea un Alert con estilo consistente del juego
     */
    private static Alert createStyledAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(GameConstants.APP_NAME + " - " + title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        // Aplicar estilo del juego al diálogo
        alert.getDialogPane().setStyle(
            "-fx-background-color: " + GameConstants.BACKGROUND_GRADIENT + ";" +
            "-fx-text-fill: white;"
        );
        
        return alert;
    }
      /**
     * Registra información de debug
     */
    public static void logDebug(String message) {
        String formattedMessage = String.format("[DEBUG] %s", message);
        System.out.println(formattedMessage);
        LOGGER.log(Level.INFO, formattedMessage);
    }
    
    /**
     * Registra información general
     */
    public static void logInfo(String message) {
        String formattedMessage = String.format("[INFO] %s", message);
        System.out.println(formattedMessage);
        LOGGER.log(Level.INFO, formattedMessage);
    }
    
    /**
     * Registra advertencias
     */
    public static void logWarning(String message) {
        String formattedMessage = String.format("[WARNING] %s", message);
        System.out.println(formattedMessage);
        LOGGER.log(Level.WARNING, formattedMessage);
    }
    
    /**
     * Registra errores
     */
    public static void logError(String message) {
        String formattedMessage = String.format("[ERROR] %s", message);
        System.err.println(formattedMessage);
        LOGGER.log(Level.SEVERE, formattedMessage);
    }
}
