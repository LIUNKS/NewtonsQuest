package Controlador.dialogs;

import Controlador.constants.GameConstants;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Factory para la creación de secciones del diálogo de reglas del juego.
 * 
 * Esta clase proporciona métodos estáticos para construir las diferentes
 * secciones del diálogo de reglas de Newton's Quest, asegurando consistencia
 * visual y organizacional en la presentación de información al jugador.
 * 
 * Características principales:
 * - Generación dinámica de secciones con formato consistente
 * - Adaptación automática de contenido a los valores de GameConstants
 * - Estilo visual unificado con la identidad del juego
 * - Organización pedagógica del contenido educativo
 * - Soporte para íconos y formatos visuales avanzados
 * 
 * Secciones disponibles:
 * - Objetivos del juego y mecánicas principales
 * - Controles y comandos disponibles
 * - Sistema de puntuación y progresión
 * - Descripción de objetos y elementos del juego
 * - Información sobre fórmulas de Newton
 * - Consejos estratégicos y mecánicas especiales
 */

public class RulesSectionFactory {
    
    // =====================================
    // MÉTODOS UTILITARIOS
    // =====================================
    
    /**
     * Crea una sección de reglas con formato y estilo consistente.
     * 
     * Este método base construye una sección visual estandarizada para el diálogo
     * de reglas, aplicando el tema visual del juego y asegurando legibilidad y
     * coherencia estética. Cada sección incluye:
     * 
     * - Un título destacado con color personalizable
     * - Contenido principal con formato optimizado para lectura
     * - Efectos visuales como sombras y bordes redondeados
     * - Espaciado y márgenes consistentes
     * - Fondo semitransparente para integración con el tema del juego
     * 
     * @param title Título de la sección a mostrar
     * @param titleColor Color hexadecimal para el título (ej: "#e94560")
     * @param content Contenido textual de la sección
     * @return VBox contenedor de la sección con estilo aplicado
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
        
        // Crear y configurar título de la sección
        Text sectionTitle = new Text(title);
        sectionTitle.setFont(Font.font("System Bold", FontWeight.BOLD, 18));
        sectionTitle.setStyle(
            "-fx-fill: " + titleColor + ";" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.8), 5, 0, 0, 2);"
        );
        
        // Crear y configurar contenido de la sección
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
    
    // =====================================
    // SECCIONES ESPECÍFICAS DEL JUEGO
    // =====================================
    
    /**
     * Crea la sección que explica los objetivos principales del juego.
     * 
     * Esta sección describe la mecánica central de Newton's Quest y el propósito
     * educativo del juego, ayudando al jugador a comprender:
     * 
     * - El concepto básico de juego (atrapar manzanas rojas, evitar verdes)
     * - La relación entre la jugabilidad y el aprendizaje de física
     * - La meta principal: desbloquear las fórmulas de Newton
     * - El contexto histórico de la "manzana de Newton" como inspiración
     * 
     * @return VBox con la sección de objetivos formateada
     */
    public static VBox createObjectiveSection() {
        return createRulesSection(
            "🎯 OBJETIVO DEL JUEGO", "#e94560",
            "Controla a Newton para atrapar las manzanas rojas que caen y evitar las verdes.\n" +
            "Acumula puntos para desbloquear las fórmulas físicas famosas de Isaac Newton."
        );
    }
    
    /**
     * Crea la sección que describe los controles del juego.
     * 
     * Esta sección enumera todos los comandos de teclado disponibles para el jugador,
     * facilitando la interacción con el juego. Incluye información sobre:
     * 
     * - Controles de movimiento (flechas y teclas WASD)
     * - Teclas de acceso a menús y funciones especiales
     * - Comandos para acceder a información sobre fórmulas
     * - Teclas de pausa y salida
     * 
     * Los controles se presentan de forma visual e intuitiva utilizando íconos
     * para mejorar la comprensión del usuario.
     * 
     * @return VBox con la sección de controles formateada
     */
    public static VBox createControlsSection() {
        return createRulesSection(
            "🎮 CONTROLES", "#3498db",
            "• FLECHAS ⬅️ ➡️ o A/D: Mover a Newton\n" +
            "• ESC: Pausar el juego o cerrar ventanas\n" +
            "• S: Abrir configuración del juego\n" +
            "• NÚMEROS 1 - 5: Ver detalles de fórmulas desbloqueadas"
        );
    }
    
    /**
     * Crea la sección que explica el sistema de puntuación.
     * 
     * Esta sección detalla el mecanismo de puntuación del juego, utilizando valores
     * definidos en GameConstants para mantener consistencia con la implementación.
     * Proporciona información sobre:
     * 
     * - Puntos otorgados por atrapar manzanas rojas
     * - Penalizaciones por manzanas verdes y manzanas perdidas
     * - Límites del sistema de puntuación (puntuación no negativa)
     * - Relación entre puntos acumulados y progresión del juego
     * 
     * Los valores numéricos se obtienen dinámicamente de las constantes del juego,
     * asegurando que la documentación siempre refleje el comportamiento actual.
     * 
     * @return VBox con la sección de puntos formateada
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
     * Crea la sección que describe los objetos y elementos del juego.
     * 
     * Esta sección explica en detalle todos los elementos interactivos que el jugador
     * encontrará durante la partida, incluyendo:
     * 
     * - Tipos de manzanas y sus efectos (rojas y verdes)
     * - Probabilidades de aparición de cada tipo de objeto
     * - Sistema de pociones y sus efectos temporales
     * - Mecánica de vidas y condiciones de fin de juego
     * - Efectos visuales asociados a cada elemento
     * 
     * La información incluye datos técnicos como porcentajes de probabilidad
     * tomados directamente de las constantes del juego.
     * 
     * @return VBox con la sección de objetos formateada
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
            
            "🧪 POCIONES MÁGICAS (aparecen ocasionalmente):\n" +
            "   • 🔵 POCIÓN DE PUNTOS: Puntos dobles temporalmente\n" +
            "   • ❤️ POCIÓN DE SALUD: Recupera 1 vida\n" +
            "   • 🐌 POCIÓN DE LENTITUD: Ralentiza al personaje\n\n" +
            
            "❤️ SISTEMA DE VIDAS:\n" +
            String.format("   • Inicias con %d vidas\n", GameConstants.INITIAL_LIVES) +
            "   • El juego termina cuando se acaben todas las vidas\n" +
            "   • Las pociones de salud te permiten recuperar vidas"
        );
    }
    
    /**
     * Crea la sección que describe el sistema de progresión y fórmulas.
     * 
     * Esta sección muestra el aspecto educativo central del juego: las fórmulas
     * de Newton que el jugador puede desbloquear. Incluye información sobre:
     * 
     * - Los cinco niveles de fórmulas disponibles para desbloquear
     * - Umbrales de puntos necesarios para cada nivel
     * - Nombres y descripciones de cada fórmula física
     * - Sistema de recompensas por completar niveles
     * - Contenido educativo disponible tras el desbloqueo
     * 
     * Los umbrales de puntos y nombres de fórmulas se obtienen dinámicamente
     * de GameConstants para asegurar precisión y consistencia.
     * 
     * @return VBox con la sección de progresión formateada
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
     * Crea la sección de consejos estratégicos para el jugador.
     * 
     * Esta sección proporciona recomendaciones y estrategias que ayudan al jugador
     * a mejorar su rendimiento y maximizar su experiencia educativa. Incluye:
     * 
     * - Estrategias básicas de movimiento y toma de decisiones
     * - Consejos para el uso óptimo de las pociones
     * - Recomendaciones para evitar obstáculos y maximizar puntos
     * - Sugerencias para aprovechar el contenido educativo
     * - Técnicas avanzadas para jugadores experimentados
     * 
     * Los consejos están organizados por categorías para facilitar su comprensión
     * y aplicación durante el juego.
     * 
     * @return VBox con la sección de consejos formateada
     */
    public static VBox createTipsSection() {
        return createRulesSection(
            "💡 CONSEJOS ESTRATÉGICOS", "#e67e22",
            "🎯 ESTRATEGIA BÁSICA:\n" +
            "• Mantente en movimiento constante\n" +
            "• Prioriza siempre las manzanas rojas\n" +
            "• Evita las verdes a toda costa\n\n" +
            
            "🧪 POCIONES ESTRATÉGICAS:\n" +
            "• Las pociones son raras pero muy valiosas\n" +
            "• Poción de salud: úsala cuando tengas pocas vidas\n" +
            "• Poción de lentitud: Efecto negativo que ralentiza al personaje evita recoger manzanas\n" +
            "• Poción de puntos: perfecto para desbloquear fórmulas rápido\n\n" +
            
            "📚 APRENDIZAJE:\n" +
            "• Aprende las fórmulas presionando números 1-5\n" +
            "• La velocidad aumenta progresivamente\n" +
            "• Completa todas las fórmulas para la victoria total"
        );
    }
    
    /**
     * Crea la sección que describe mecánicas especiales del juego.
     * 
     * Esta sección describe características avanzadas y elementos adicionales
     * que enriquecen la experiencia de juego. Incluye información sobre:
     * 
     * - Efectos temporales y su impacto en la jugabilidad
     * - Sistema de progresión y guardado de puntuaciones
     * - Ranking competitivo entre jugadores
     * - Elementos audiovisuales (música, efectos sonoros)
     * - Opciones de personalización y configuración
     * - Características inmersivas que conectan el juego con la física
     * 
     * Esta sección complementa el resto de la información, destacando aspectos
     * que mejoran la rejugabilidad y el valor educativo a largo plazo.
     * 
     * @return VBox con la sección de mecánicas especiales formateada
     */
    public static VBox createSpecialMechanicsSection() {
        return createRulesSection(
            "⚡ MECÁNICAS ESPECIALES", "#8e44ad",
            "🎮 EFECTOS TEMPORALES:\n" +
            "• Las pociones crean efectos visuales únicos\n" +
            "• Los efectos negativos de manzanas verdes son temporales\n" +
            "• El juego se vuelve más desafiante con el tiempo\n\n" +
            
            "🏆 SISTEMA DE PROGRESIÓN:\n" +
            "• Tu mejor puntuación se guarda automáticamente\n" +
            "• Compite en el ranking global de jugadores\n" +
            "• Desbloquea contenido educativo sobre física\n\n" +
            
            "🎵 EXPERIENCIA INMERSIVA:\n" +
            "• Música de fondo\n" +
            "• Efectos de sonido\n" +
            "• Configuración personalizable de audio y brillo"
        );
    }
}
