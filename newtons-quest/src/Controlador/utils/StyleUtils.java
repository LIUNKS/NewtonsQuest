package Controlador.utils;

import Controlador.constants.GameConstants;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Utilidades de estilo y temas para Newton's Apple Quest.
 * 
 * Esta clase proporciona métodos estáticos para aplicar estilos
 * consistentes en toda la interfaz gráfica del juego.
 * 
 * Funcionalidades principales:
 * - Estilos predefinidos para botones (primario, secundario, peligro)
 * - Configuración de fuentes y tipografías del juego
 * - Aplicación de temas de color consistentes
 * - Estilos para etiquetas y textos
 * - Efectos de hover y animaciones de interfaz
 * 
 * Estilos de botones disponibles:
 * - Botón primario: Para acciones principales (azul)
 * - Botón secundario: Para acciones secundarias (gris)
 * - Botón de peligro: Para acciones destructivas (rojo)
 * - Botón de éxito: Para confirmaciones (verde)
 * 
 * Temas de color:
 * - Colores primarios del juego (azul, dorado)
 * - Paleta de colores complementarios
 * - Esquemas de contraste apropiados para accesibilidad
 * 
 * Todos los estilos siguen las convenciones de diseño del juego
 * y mantienen consistencia visual en toda la aplicación.
 */
public class StyleUtils {
    
    // Prevenir instanciación
    private StyleUtils() {}
    
    /**
     * Aplica el estilo principal de botón del juego
     */
    public static void applyPrimaryButtonStyle(Button button) {
        button.setFont(Font.font("System Bold", FontWeight.BOLD, 16));
        button.setStyle(
            "-fx-background-color: " + GameConstants.PRIMARY_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-padding: 15px 30px;" +
            "-fx-background-radius: 30px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 3);" +
            "-fx-cursor: hand;"
        );
        
        // Agregar efectos hover
        addHoverEffect(button, GameConstants.PRIMARY_COLOR, GameConstants.SECONDARY_COLOR);
    }
    
    /**
     * Aplica el estilo secundario de botón del juego
     */
    public static void applySecondaryButtonStyle(Button button) {
        button.setFont(Font.font("System", FontWeight.NORMAL, 14));
        button.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10px 20px;" +
            "-fx-border-color: " + GameConstants.PRIMARY_COLOR + ";" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 20px;" +
            "-fx-cursor: hand;"
        );
        
        // Agregar efectos hover para botón secundario
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + GameConstants.PRIMARY_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10px 20px;" +
            "-fx-border-color: " + GameConstants.PRIMARY_COLOR + ";" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 20px;" +
            "-fx-cursor: hand;"
        ));
        
        button.setOnMouseExited(e -> applySecondaryButtonStyle(button));
    }
    
    /**
     * Aplica estilo de título principal
     */
    public static void applyMainTitleStyle(Text text) {
        text.setFont(Font.font("System Bold", FontWeight.BOLD, 28));
        text.setStyle(
            "-fx-fill: " + GameConstants.SECONDARY_COLOR + ";" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.8), 8, 0, 0, 3);"
        );
    }
    
    /**
     * Aplica estilo de título secundario
     */
    public static void applySecondaryTitleStyle(Text text) {
        text.setFont(Font.font("System Bold", FontWeight.BOLD, 20));
        text.setStyle(
            "-fx-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 5, 0, 0, 2);"
        );
    }
    
    /**
     * Aplica estilo de texto normal
     */
    public static void applyNormalTextStyle(Text text) {
        text.setFont(Font.font("System", 14));
        text.setStyle(
            "-fx-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 3, 0, 0, 1);"
        );
    }
    
    /**
     * Aplica estilo de label del juego
     */
    public static void applyGameLabelStyle(Label label) {
        label.setFont(Font.font("System", FontWeight.BOLD, 16));
        label.setStyle(
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 5, 0, 0, 2);"
        );
    }
    
    /**
     * Aplica fondo de gradiente del juego a un contenedor
     */
    public static void applyGameBackground(Pane container) {
        container.setStyle("-fx-background-color: " + GameConstants.BACKGROUND_GRADIENT + ";");
    }
    
    /**
     * Aplica estilo de contenedor con transparencia
     */
    public static void applyTransparentContainer(Pane container) {
        container.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.05);" +
            "-fx-background-radius: 20px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0, 0, 5);" +
            "-fx-border-color: rgba(255, 255, 255, 0.08);" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 20px;"
        );
    }
    
    /**
     * Aplica estilo de sección de contenido
     */
    public static void applyContentSectionStyle(Pane section) {
        section.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.03);" +
            "-fx-background-radius: 15px;" +
            "-fx-border-color: rgba(255, 255, 255, 0.1);" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 15px;"
        );
    }
    
    /**
     * Agrega efectos hover a un botón
     */
    public static void addHoverEffect(Button button, String normalColor, String hoverColor) {
        String originalStyle = button.getStyle();
        
        button.setOnMouseEntered(e -> {
            String hoverStyle = originalStyle.replace(normalColor, hoverColor);
            hoverStyle = hoverStyle.replace("rgba(0, 0, 0, 0.4)", "rgba(0, 0, 0, 0.6)");
            hoverStyle += "-fx-scale-x: 1.05; -fx-scale-y: 1.05;";
            button.setStyle(hoverStyle);
        });
        
        button.setOnMouseExited(e -> button.setStyle(originalStyle));
    }
    
    /**
     * Crea un estilo de texto coloreado para secciones temáticas
     */
    public static String createSectionTitleStyle(String color) {
        return "-fx-fill: " + color + ";" +
               "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.8), 5, 0, 0, 2);";
    }
    
    /**
     * Obtiene el estilo CSS para ScrollPane transparente
     */
    public static String getTransparentScrollPaneStyle() {
        return "-fx-background: transparent;" +
               "-fx-background-color: transparent;" +
               "-fx-control-inner-background: transparent;";
    }
}
