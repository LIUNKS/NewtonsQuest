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

/**
 * Gestor centralizado de errores y diálogos en Newton's Apple Quest.
 * 
 * Esta clase proporciona utilidades para el manejo consistente de errores
 * y la presentación de mensajes al usuario en toda la aplicación.
 * 
 * Funcionalidades principales:
 * - Gestión centralizada de excepciones y errores
 * - Diálogos de error personalizados y user-friendly
 * - Sistema de logging configurado para desarrollo y producción
 * - Mensajes de confirmación y alertas al usuario
 * - Manejo de diferentes tipos de alertas (error, advertencia, información)
 * 
 * Tipos de diálogos soportados:
 * - Alertas de error con detalles técnicos
 * - Diálogos de confirmación (Sí/No)
 * - Mensajes informativos
 * - Advertencias al usuario
 * 
 * El sistema de logging está configurado para:
 * - Registro de errores en consola durante desarrollo
 * - Formato consistente de mensajes de error
 * - Niveles apropiados de logging según criticidad
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
            // Forzar el uso de UTF-8 para todos los procesos de Java
            System.setProperty("file.encoding", "UTF-8");
            // Establecer encoding específico para la consola
            System.setProperty("sun.stdout.encoding", "UTF-8");
            System.setProperty("sun.stderr.encoding", "UTF-8");
            
            // Crear un handler personalizado para la consola con formato UTF-8
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            
            // Crear un formateador personalizado para mejor legibilidad
            SimpleFormatter formatter = new SimpleFormatter() {
                @Override
                public String format(java.util.logging.LogRecord record) {
                    return String.format("[%1$tF %1$tT] %2$s: %3$s%n",
                            new java.util.Date(record.getMillis()),
                            record.getLevel().getName(),
                            record.getMessage());
                }
            };
            
            consoleHandler.setFormatter(formatter);
            
            // Configurar el logger
            LOGGER.setUseParentHandlers(false);
            LOGGER.addHandler(consoleHandler);
            LOGGER.setLevel(Level.ALL);
            
            // Registrar la configuración exitosa
            LOGGER.info("Logger configurado correctamente con soporte UTF-8");
            
        } catch (Exception e) {
            // Si falla la configuración, usar el logger por defecto
            System.err.println("Warning: No se pudo configurar el logger correctamente: " + e.getMessage());
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
     * 
     * @param message Mensaje de depuración para registrar
     */
    public static void logDebug(String message) {
        LOGGER.log(Level.INFO, "[DEBUG] " + message);
    }
    
    /**
     * Registra información general
     * 
     * @param message Mensaje informativo para registrar
     */
    public static void logInfo(String message) {
        try {
            // Asegurarnos de que el mensaje se registre como UTF-8
            String normalizedMessage = new String(message.getBytes("UTF-8"), "UTF-8");
            LOGGER.log(Level.INFO, "[INFO] " + normalizedMessage);
        } catch (Exception e) {
            // Si hay error de codificación, usar el mensaje original
            LOGGER.log(Level.INFO, "[INFO] " + message);
        }
    }
    
    /**
     * Registra advertencias
     * 
     * @param message Mensaje de advertencia para registrar
     */
    public static void logWarning(String message) {
        try {
            // Asegurarnos de que el mensaje se registre como UTF-8
            String normalizedMessage = new String(message.getBytes("UTF-8"), "UTF-8");
            LOGGER.log(Level.WARNING, "[WARNING] " + normalizedMessage);
        } catch (Exception e) {
            // Si hay error de codificación, usar el mensaje original
            LOGGER.log(Level.WARNING, "[WARNING] " + message);
        }
    }
    
    /**
     * Registra errores
     * 
     * @param message Mensaje de error para registrar
     */
    public static void logError(String message) {
        try {
            // Asegurarnos de que el mensaje se registre como UTF-8
            String normalizedMessage = new String(message.getBytes("UTF-8"), "UTF-8");
            LOGGER.log(Level.SEVERE, "[ERROR] " + normalizedMessage);
        } catch (Exception e) {
            // Si hay error de codificación, usar el mensaje original
            LOGGER.log(Level.SEVERE, "[ERROR] " + message);
        }
    }
}
