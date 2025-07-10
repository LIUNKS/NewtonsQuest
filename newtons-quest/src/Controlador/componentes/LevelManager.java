package Controlador.componentes;

/**
 * Gestor de niveles y progresión.
 * 
 * Esta clase controla el sistema de progresión del juego, incluyendo:
 * 
 *   - Desbloqueo de fórmulas de Newton basado en puntuación
 *   - Gestión de niveles y umbrales de progreso
 *   - Efectos visuales de desbloqueo de contenido
 *   - Información detallada de las fórmulas físicas
 *   - Sistema de completación y logros
 * 
 * Maneja cinco fórmulas fundamentales de la física que el jugador
 * debe desbloquear progresivamente alcanzando ciertos puntajes.
 */

public class LevelManager {
    
    // =====================================
    // CONFIGURACIÓN DE NIVELES
    // =====================================
    
    /** Nivel actual del jugador */
    private int level = 0;
    
    /** Número máximo de niveles disponibles */
    private final int MAX_LEVEL = 5;
    
    // =====================================
    // FÓRMULAS Y CONTENIDO EDUCATIVO
    // =====================================
    
    /** Fórmulas completas con descripción */
    private final String[] FORMULAS = {
        "F = m × g → Fuerza de gravedad (Peso)",
        "v = d / t → Velocidad media",
        "U = m × g × h → Energía potencial gravitatoria",
        "K = ½ × m × v² → Energía cinética",
        "a = (vf - vi) / t → Aceleración"
    };
    
    /** Fórmulas en formato corto para la interfaz */
    private final String[] FORMULAS_SHORT = {
        "F = m × g",
        "v = d / t",
        "U = m × g × h",
        "K = ½ × m × v²",
        "a = (vf - vi) / t"
    };    
    /** Descripciones educativas detalladas para cada fórmula */
    private final String[] FORMULAS_DESCRIPTIONS = {
        "La fuerza de gravedad (peso) es la que te mantiene en el suelo. Se calcula multiplicando la masa por la gravedad. Ejemplo: Si pesas 50 kg, tu peso en la Tierra es 50 × 9.8 = 490 Newtons. Esta fuerza es la que Newton descubrió observando la caída de las manzanas.",
        "La velocidad media indica lo rápido que viajas. Se calcula dividiendo la distancia entre el tiempo. Ejemplo: Si recorres 100 metros en 20 segundos, tu velocidad es 100 ÷ 20 = 5 metros por segundo. Usamos esta fórmula para calcular qué tan rápido cae una manzana.",
        "La energía potencial es la energía almacenada por la posición de un objeto. Es mayor cuanto más alto esté el objeto. Ejemplo: Una manzana en un árbol de 5 metros tiene más energía potencial que una en el suelo, y es por eso que puede hacer más trabajo al caer.",
        "La energía cinética es la energía que tiene un objeto en movimiento. Es mayor cuanto más rápido se mueve. Ejemplo: Una manzana que cae rápidamente tiene más energía cinética que una que cae lentamente, y por eso puede causar más impacto al golpear el suelo.",
        "La aceleración mide cómo cambia la velocidad en el tiempo. La gravedad acelera los objetos a 9.8 m/s² en la Tierra. Ejemplo: Si una manzana cae desde reposo, después de 1 segundo irá a 9.8 m/s, después de 2 segundos a 19.6 m/s, y así sucesivamente."
    };
    
    // =====================================
    // CONFIGURACIÓN DE PROGRESIÓN
    // =====================================
    
    /** Umbrales de puntuación necesarios para desbloquear cada fórmula */
    private final int[] LEVEL_THRESHOLDS = {100, 250, 450, 700, 1000};
    
    /** Estado de desbloqueo de cada fórmula */
    private boolean[] unlockedFormulas;
    
    // =====================================
    // SISTEMA DE EFECTOS VISUALES
    // =====================================
    
    /** Indica si se está mostrando un efecto de desbloqueo */
    private boolean showingUnlockEffect = false;
    
    /** Índice de la fórmula que se acaba de desbloquear */
    private int unlockedFormulaIndex = -1;
    
    /** Momento en que inició el efecto de desbloqueo */
    private long unlockEffectStartTime = 0;
    
    /** Duración del efecto visual en milisegundos */
    private final long UNLOCK_EFFECT_DURATION = 5000;
    
    // =====================================
    // CALLBACKS
    // =====================================
    
    /** Callback ejecutado cuando se desbloquea una fórmula */
    private Runnable onFormulaUnlocked;
    
    /** Callback ejecutado cuando se completan todas las fórmulas */
    private Runnable onAllFormulasCompleted;
    
    // ================================================================================================
    // CONSTRUCTORES
    // ================================================================================================
    
    /**
     * Constructor principal del gestor de niveles.
     * Inicializa todas las fórmulas como bloqueadas.
     */
    public LevelManager() {
        unlockedFormulas = new boolean[MAX_LEVEL];
        for (int i = 0; i < MAX_LEVEL; i++) {
            unlockedFormulas[i] = false;
        }
    }
    
    // ================================================================================================
    // CONFIGURACIÓN DE CALLBACKS
    // ================================================================================================
    
    /**
     * Establece el callback para cuando se desbloquea una fórmula.
     * @param onFormulaUnlocked Acción a ejecutar cuando se desbloquea una fórmula
     */
    public void setOnFormulaUnlocked(Runnable onFormulaUnlocked) {
        this.onFormulaUnlocked = onFormulaUnlocked;
    }
    
    /**
     * Establece el callback para cuando se completan todas las fórmulas.
     * @param onAllFormulasCompleted Acción a ejecutar cuando se completan todas las fórmulas
     */
    public void setOnAllFormulasCompleted(Runnable onAllFormulasCompleted) {
        this.onAllFormulasCompleted = onAllFormulasCompleted;
    }
    
    // ================================================================================================
    // GESTIÓN DE PROGRESIÓN DE NIVELES
    // ================================================================================================
    
    /**
     * Verifica si el progreso del jugador ha alcanzado un nuevo nivel.
     * @param score Puntuación actual del jugador
     * @return true si se ha desbloqueado una nueva fórmula
     */
    public boolean checkLevelProgress(int score) {
        boolean formulaDesbloqueada = false;
        
        // Verificar si ha alcanzado la puntuación para un nuevo nivel
        for (int i = 0; i < MAX_LEVEL; i++) {
            if (!unlockedFormulas[i] && score >= LEVEL_THRESHOLDS[i]) {
                unlockFormula(i);
                formulaDesbloqueada = true;
                
                if (onFormulaUnlocked != null) {
                    onFormulaUnlocked.run();
                }
                
                break; // Solo desbloquear una fórmula a la vez
            }
        }
        
        // Actualizar el nivel basado en las fórmulas desbloqueadas
        int newLevel = 0;
        for (int i = 0; i < MAX_LEVEL; i++) {
            if (unlockedFormulas[i]) {
                newLevel = i + 1;
            }
        }
        
        if (newLevel != level) {
            level = newLevel;
        }
        
        // Verificar si se completaron todas las fórmulas
        if (formulaDesbloqueada && areAllFormulasUnlocked()) {
            if (onAllFormulasCompleted != null) {
                onAllFormulasCompleted.run();
            }
        }
        
        return formulaDesbloqueada;
    }    /**
     * Desbloquea una fórmula específica y muestra el efecto visual.
     * @param formulaIndex Índice de la fórmula a desbloquear
     */
    private void unlockFormula(int formulaIndex) {
        unlockedFormulas[formulaIndex] = true;
        
        // Iniciar efecto visual de desbloqueo
        showingUnlockEffect = true;
        unlockedFormulaIndex = formulaIndex;
        unlockEffectStartTime = System.currentTimeMillis();
        
        // Verificar si se han desbloqueado todas las fórmulas
        if (areAllFormulasUnlocked() && onAllFormulasCompleted != null) {
            onAllFormulasCompleted.run();
        }
    }
    
    // ================================================================================================
    // EFECTOS VISUALES DE DESBLOQUEO
    // ================================================================================================
    
    /**
     * Actualiza el efecto visual de desbloqueo.
     */
    public void updateUnlockEffect() {
        if (showingUnlockEffect) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - unlockEffectStartTime > UNLOCK_EFFECT_DURATION) {
                showingUnlockEffect = false;
            }
        }
    }
    
    /**
     * Muestra los detalles de una fórmula específica.
     * @param formulaIndex Índice de la fórmula
     * @return true si la fórmula está desbloqueada y se pueden mostrar los detalles
     */
    public boolean showFormulaDetails(int formulaIndex) {
        if (formulaIndex >= 0 && formulaIndex < MAX_LEVEL && unlockedFormulas[formulaIndex]) {
            return true;
        }
        return false;
    }
    
    // ================================================================================================
    // VALIDACIÓN Y ESTADO
    // ================================================================================================
    
    /**
     * Verifica si todas las fórmulas han sido desbloqueadas.
     * @return true si todas las fórmulas están desbloqueadas
     */
    public boolean areAllFormulasUnlocked() {
        for (boolean unlocked : unlockedFormulas) {
            if (!unlocked) {
                return false;
            }
        }
        return true;
    }
    
    // ================================================================================================
    // MÉTODOS DE ACCESO - GETTERS
    // ================================================================================================
    
    /** @return Nivel actual del jugador */
    public int getLevel() {
        return level;
    }
    
    /** @return Número máximo de niveles disponibles */
    public int getMaxLevel() {
        return MAX_LEVEL;
    }
    
    /** @return Array con todas las fórmulas completas */
    public String[] getFormulas() {
        return FORMULAS;
    }
    
    /** @return Array con las fórmulas en formato corto */
    public String[] getFormulasShort() {
        return FORMULAS_SHORT;
    }
    
    /** @return Array con las descripciones detalladas de las fórmulas */
    public String[] getFormulasDescriptions() {
        return FORMULAS_DESCRIPTIONS;
    }
    
    /** @return Array con los umbrales de puntuación para cada nivel */
    public int[] getLevelThresholds() {
        return LEVEL_THRESHOLDS;
    }
    
    /** @return Array que indica qué fórmulas están desbloqueadas */
    public boolean[] getUnlockedFormulas() {
        return unlockedFormulas;
    }
    
    /** @return true si se está mostrando un efecto de desbloqueo */
    public boolean isShowingUnlockEffect() {
        return showingUnlockEffect;
    }
    
    /** @return Índice de la fórmula que se acaba de desbloquear */
    public int getUnlockedFormulaIndex() {
        return unlockedFormulaIndex;
    }
}
