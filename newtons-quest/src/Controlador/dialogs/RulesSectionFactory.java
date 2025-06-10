package Controlador.dialogs;

import Controlador.constants.GameConstants;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Factory class para crear las diferentes secciones del diálogo de reglas.
 * Centraliza la creación de contenido y mantiene consistencia en el estilo.
 */
public class RulesSectionFactory {
    
    /**
     * Crea una sección de reglas con estilo consistente
     */
    public static VBox createRulesSection(String title, String titleColor, String content) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15, 20, 15, 20));
        section.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.03);" +
            "-fx-background-radius: 15px;" +
            "-fx-border-color: rgba(255, 255, 255, 0.1);" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 15px;"
        );
        
        // Título de la sección
        Text sectionTitle = new Text(title);
        sectionTitle.setFont(Font.font("System Bold", FontWeight.BOLD, 18));
        sectionTitle.setStyle(
            "-fx-fill: " + titleColor + ";" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.8), 5, 0, 0, 2);"
        );
        
        // Contenido de la sección
        Text sectionContent = new Text(content);
        sectionContent.setFont(Font.font("System", 14));
        sectionContent.setStyle(
            "-fx-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 3, 0, 0, 1);"
        );
        sectionContent.setWrappingWidth(520);
        
        section.getChildren().addAll(sectionTitle, sectionContent);
        return section;
    }
    
    /**
     * Crea la sección de objetivos del juego
     */
    public static VBox createObjectiveSection() {
        return createRulesSection(
            "🎯 OBJETIVO DEL JUEGO", "#e94560",
            "Controla a Newton para atrapar las manzanas rojas que caen y evitar las verdes.\n" +
            "Acumula puntos para desbloquear las fórmulas físicas famosas de Isaac Newton."
        );
    }
    
    /**
     * Crea la sección de controles del juego
     */
    public static VBox createControlsSection() {
        return createRulesSection(
            "🎮 CONTROLES", "#3498db",
            "• FLECHAS ⬅️ ➡️ o A/D: Mover a Newton\n" +
            "• ESC: Pausar el juego o cerrar ventanas\n" +
            "• NÚMEROS 1️⃣-5️⃣: Ver detalles de fórmulas desbloqueadas"
        );
    }
      /**
     * Crea la sección del sistema de puntos
     */
    public static VBox createPointsSection() {
        return createRulesSection(
            "📊 SISTEMA DE PUNTOS", "#27ae60",
            String.format("🍎 MANZANA ROJA: +%d puntos (¡Atrápala!)\n", GameConstants.RED_APPLE_POINTS) +
            String.format("🍏 MANZANA VERDE: %d puntos + pierdes 1 vida (¡Evítala!)\n", GameConstants.GREEN_APPLE_POINTS) +
            String.format("💔 MANZANA ROJA PERDIDA: %d puntos + pierdes 1 vida\n\n", GameConstants.MISSED_APPLE_POINTS) +
            "💡 Tu puntuación nunca puede ser negativa"
        );
    }
      /**
     * Crea la sección de objetos del juego
     */
    public static VBox createObjectsSection() {
        return createRulesSection(
            "🎁 OBJETOS DEL JUEGO", "#9b59b6",
            String.format("🍎 MANZANAS ROJAS (%.0f%% probabilidad):\n", GameConstants.RED_APPLE_PROBABILITY * 100) +
            "   • Descubrimientos positivos de Newton\n" +
            "   • Otorgan puntos para desbloquear fórmulas\n\n" +
            
            String.format("🍏 MANZANAS VERDES (%.0f%% probabilidad):\n", GameConstants.GREEN_APPLE_PROBABILITY * 100) +
            "   • Obstáculos y distracciones\n" +
            "   • Efecto visual especial en Newton\n" +
            "   • ¡Reducen puntos y vidas!\n\n" +
            
            "❤️ SISTEMA DE VIDAS:\n" +
            String.format("   • Inicias con %d vidas\n", GameConstants.INITIAL_LIVES) +
            "   • Game Over cuando se acaben todas"
        );
    }
      /**
     * Crea la sección de progresión y fórmulas
     */
    public static VBox createProgressSection() {
        return createRulesSection(
            "🔬 FÓRMULAS DE NEWTON", "#f39c12",
            "Desbloquea las famosas fórmulas alcanzando estos puntos:\n\n" +
            String.format("🥇 %d pts: %s\n", GameConstants.FORMULA_1_THRESHOLD, GameConstants.FORMULA_NAMES[0]) +
            String.format("🥈 %d pts: %s\n", GameConstants.FORMULA_2_THRESHOLD, GameConstants.FORMULA_NAMES[1]) +
            String.format("🥉 %d pts: %s\n", GameConstants.FORMULA_3_THRESHOLD, GameConstants.FORMULA_NAMES[2]) +
            String.format("🏆 %d pts: %s\n", GameConstants.FORMULA_4_THRESHOLD, GameConstants.FORMULA_NAMES[3]) +
            String.format("👑 %d pts: %s\n\n", GameConstants.FORMULA_5_THRESHOLD, GameConstants.FORMULA_NAMES[4]) +
            
            "Cada fórmula incluye explicación detallada y ejemplos prácticos."
        );
    }
    
    /**
     * Crea la sección de consejos estratégicos
     */
    public static VBox createTipsSection() {
        return createRulesSection(
            "💡 CONSEJOS ESTRATÉGICOS", "#e67e22",
            "• Mantente en movimiento constante\n" +
            "• Prioriza siempre las manzanas rojas\n" +
            "• Evita las verdes a toda costa\n" +
            "• Aprende las fórmulas presionando números\n" +
            "• La velocidad aumenta progresivamente"
        );
    }
}
