package Controlador.componentes;

/**
 * Clase encargada de gestionar el sistema de niveles y fórmulas.
 * Separa la lógica de progresión del juego del controlador principal.
 */
public class LevelManager {
    
    private int level = 0;
    private final int MAX_LEVEL = 5;
    
    // Fórmulas y descripciones
    private final String[] FORMULAS = {
        "F = m × g → Fuerza de gravedad (Peso)",
        "v = d / t → Velocidad media",
        "U = m × g × h → Energía potencial gravitatoria",
        "K = ½ × m × v² → Energía cinética",
        "a = (vf - vi) / t → Aceleración"
    };
    
    private final String[] FORMULAS_SHORT = {
        "F = m × g",
        "v = d / t",
        "U = m × g × h",
        "K = ½ × m × v²",
        "a = (vf - vi) / t"
    };    // Descripciones extendidas para cada fórmula
    private final String[] FORMULAS_DESCRIPTIONS = {
        "La fuerza de gravedad (peso) es la que te mantiene en el suelo. Se calcula multiplicando la masa por la gravedad. Ejemplo: Si pesas 50 kg, tu peso en la Tierra es 50 × 9.8 = 490 Newtons. Esta fuerza es la que Newton descubrió observando la caída de las manzanas.",
        "La velocidad media indica lo rápido que viajas. Se calcula dividiendo la distancia entre el tiempo. Ejemplo: Si recorres 100 metros en 20 segundos, tu velocidad es 100 ÷ 20 = 5 metros por segundo. Usamos esta fórmula para calcular qué tan rápido cae una manzana.",
        "La energía potencial es la energía almacenada por la posición de un objeto. Es mayor cuanto más alto esté el objeto. Ejemplo: Una manzana en un árbol de 5 metros tiene más energía potencial que una en el suelo, y es por eso que puede hacer más trabajo al caer.",
        "La energía cinética es la energía que tiene un objeto en movimiento. Es mayor cuanto más rápido se mueve. Ejemplo: Una manzana que cae rápidamente tiene más energía cinética que una que cae lentamente, y por eso puede causar más impacto al golpear el suelo.",
        "La aceleración mide cómo cambia la velocidad en el tiempo. La gravedad acelera los objetos a 9.8 m/s² en la Tierra. Ejemplo: Si una manzana cae desde reposo, después de 1 segundo irá a 9.8 m/s, después de 2 segundos a 19.6 m/s, y así sucesivamente."
    };
    
    // Umbrales de puntuación para desbloquear niveles
    private final int[] LEVEL_THRESHOLDS = {100, 250, 450, 700, 1000};
    
    // Estado de desbloqueo de las fórmulas
    private boolean[] unlockedFormulas;
      // Variables para efectos visuales de desbloqueo
    private boolean showingUnlockEffect = false;
    private int unlockedFormulaIndex = -1;
    private long unlockEffectStartTime = 0;
    private final long UNLOCK_EFFECT_DURATION = 5000; // Duración del efecto en milisegundos (5 segundos)
    
    // Callback para cuando se desbloquea una fórmula
    private Runnable onFormulaUnlocked;
    private Runnable onAllFormulasCompleted; // Nuevo callback para cuando se completan todas las fórmulas

    // Constructores
    public LevelManager() {
        unlockedFormulas = new boolean[MAX_LEVEL];
        for (int i = 0; i < MAX_LEVEL; i++) {
            unlockedFormulas[i] = false;
        }
        System.out.println("LevelManager inicializado");
    }
    
    /**
     * Establece el callback para cuando se desbloquea una fórmula
     * @param onFormulaUnlocked Acción a ejecutar cuando se desbloquea una fórmula
     */
    public void setOnFormulaUnlocked(Runnable onFormulaUnlocked) {
        this.onFormulaUnlocked = onFormulaUnlocked;
    }
    
    /**
     * Establece el callback para cuando se completan todas las fórmulas
     * @param onAllFormulasCompleted Acción a ejecutar cuando se completan todas las fórmulas
     */
    public void setOnAllFormulasCompleted(Runnable onAllFormulasCompleted) {
        this.onAllFormulasCompleted = onAllFormulasCompleted;
    }
    
    /**
     * Verifica si el progreso del jugador ha alcanzado un nuevo nivel
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
                
                // Si hay un callback definido, ejecutarlo
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
        
        // Si ha cambiado el nivel, actualizarlo
        if (newLevel != level) {
            level = newLevel;
            System.out.println("¡Nivel aumentado a " + level + "!");
        }
          // Verificar si se completaron todas las fórmulas
        if (formulaDesbloqueada && areAllFormulasUnlocked()) {
            System.out.println("¡Todas las fórmulas han sido desbloqueadas!");
            if (onAllFormulasCompleted != null) {
                onAllFormulasCompleted.run();
            }
        }
        
        return formulaDesbloqueada;
    }    /**
     * Desbloquea una fórmula específica y muestra el efecto visual
     * @param formulaIndex Índice de la fórmula a desbloquear
     */
    private void unlockFormula(int formulaIndex) {
        unlockedFormulas[formulaIndex] = true;
        
        // Iniciar efecto visual de desbloqueo
        showingUnlockEffect = true;
        unlockedFormulaIndex = formulaIndex;
        unlockEffectStartTime = System.currentTimeMillis();
        
        System.out.println("¡Fórmula desbloqueada: " + FORMULAS_SHORT[formulaIndex] + "!");
        
        // Verificar si se han desbloqueado todas las fórmulas
        if (areAllFormulasUnlocked() && onAllFormulasCompleted != null) {
            System.out.println("¡TODAS LAS FÓRMULAS DESBLOQUEADAS! Ejecutando celebración...");
            onAllFormulasCompleted.run();
        }
    }
    
    /**
     * Actualiza el efecto visual de desbloqueo
     */
    public void updateUnlockEffect() {
        if (showingUnlockEffect) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - unlockEffectStartTime > UNLOCK_EFFECT_DURATION) {
                // Terminar el efecto
                showingUnlockEffect = false;
            }
        }
    }
    
    /**
     * Muestra los detalles de una fórmula específica
     * @param formulaIndex Índice de la fórmula
     * @return true si la fórmula está desbloqueada y se pueden mostrar los detalles
     */
    public boolean showFormulaDetails(int formulaIndex) {
        if (formulaIndex >= 0 && formulaIndex < MAX_LEVEL && unlockedFormulas[formulaIndex]) {
            System.out.println("Mostrando detalles de la fórmula: " + FORMULAS_SHORT[formulaIndex]);
            System.out.println("Descripción: " + FORMULAS_DESCRIPTIONS[formulaIndex]);
            return true;
        }
        return false;
    }
    
    /**
     * Verifica si todas las fórmulas han sido desbloqueadas
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
    
    // Getters
    
    public int getLevel() {
        return level;
    }
    
    public int getMaxLevel() {
        return MAX_LEVEL;
    }
    
    public String[] getFormulas() {
        return FORMULAS;
    }
    
    public String[] getFormulasShort() {
        return FORMULAS_SHORT;
    }
    
    public String[] getFormulasDescriptions() {
        return FORMULAS_DESCRIPTIONS;
    }
    
    public int[] getLevelThresholds() {
        return LEVEL_THRESHOLDS;
    }
    
    public boolean[] getUnlockedFormulas() {
        return unlockedFormulas;
    }
    
    public boolean isShowingUnlockEffect() {
        return showingUnlockEffect;
    }
    
    public int getUnlockedFormulaIndex() {
        return unlockedFormulaIndex;
    }
}
