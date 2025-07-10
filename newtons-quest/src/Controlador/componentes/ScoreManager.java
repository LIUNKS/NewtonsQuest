package Controlador.componentes;

/**
 * Gestor de puntuación y sistema de vidas.
 * 
 * Esta clase controla el sistema de puntuación y vidas del juego:
 * 
 *   - Gestión de puntuación actual del jugador
 *   - Sistema de vidas y game over
 *   - Efectos temporales (puntos dobles, etc.)
 *   - Callbacks para eventos de puntuación
 *   - Validación de límites y restricciones
 * 
 * Centraliza toda la lógica relacionada con el puntaje y la supervivencia
 * del jugador, proporcionando una interfaz limpia para el control de
 * estos aspectos fundamentales del juego.
 */
public class ScoreManager {
    
    // ================================================================================================
    // ESTADO DEL JUGADOR
    // ================================================================================================
    
    /** Puntuación actual del jugador */
    private int score = 0;
    
    /** Vidas actuales del jugador */
    private int lives;
    
    /** Número máximo de vidas permitidas */
    private final int MAX_LIVES;
    
    // ================================================================================================
    // CALLBACKS Y EVENTOS
    // ================================================================================================
    
    /** Callback ejecutado cuando el juego termina */
    private Runnable onGameOver;
    
    /** Callback ejecutado cuando cambia la puntuación */
    private java.util.function.Consumer<Integer> onScoreChange;
    
    // ================================================================================================
    // CONSTRUCTORES
    // ================================================================================================
    
    /**
     * Constructor principal del gestor de puntuación.
     * @param maxLives Número máximo de vidas permitidas
     */
    public ScoreManager(int maxLives) {
        this.MAX_LIVES = maxLives;
        this.lives = maxLives;
    }
    
    // ================================================================================================
    // CONFIGURACIÓN DE CALLBACKS
    // ================================================================================================
    
    /**
     * Establece los callbacks para eventos relacionados con la puntuación.
     * @param onGameOver Acción cuando el juego termina (vidas = 0)
     * @param onScoreChange Acción cuando cambia la puntuación
     */
    public void setCallbacks(Runnable onGameOver, java.util.function.Consumer<Integer> onScoreChange) {
        this.onGameOver = onGameOver;
        this.onScoreChange = onScoreChange;
    }
    
    // ================================================================================================
    // GESTIÓN DE PUNTUACIÓN
    // ================================================================================================
    
    /**
     * Añade puntos a la puntuación actual.
     * @param points Puntos a añadir (puede ser negativo para penalizaciones)
     */
    public void addScore(int points) {
        score += points;
        
        if (score < 0) {
            score = 0;
        }
        
        if (onScoreChange != null) {
            onScoreChange.accept(score);
        }
    }
    
    // ================================================================================================
    // GESTIÓN DE VIDAS
    // ================================================================================================
    
    /**
     * Reduce una vida del jugador y verifica si el juego ha terminado.
     * @param reason Motivo por el que perdió la vida
     * @return true si quedan vidas, false si el juego ha terminado
     */
    public boolean loseLife(String reason) {
        if (lives > 0) {
            lives--;
            
            if (lives <= 0) {
                if (onGameOver != null) {
                    onGameOver.run();
                }
                return false;
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Añade una vida extra al jugador si no ha alcanzado el máximo.
     * @param reason Motivo por el que ganó la vida
     * @return true si se pudo añadir la vida, false si ya tenía el máximo
     */
    public boolean gainLife(String reason) {
        if (lives < MAX_LIVES) {
            lives++;
            return true;
        } else {
            return false;
        }
    }
    
    // ================================================================================================
    // REINICIO Y ESTADO
    // ================================================================================================
    
    /**
     * Restablece la puntuación y las vidas a sus valores iniciales.
     */
    public void reset() {
        score = 0;
        lives = MAX_LIVES;
    }
    
    // ================================================================================================
    // MÉTODOS DE ACCESO - GETTERS
    // ================================================================================================
    
    /** @return Puntuación actual del jugador */
    public int getScore() {
        return score;
    }
    
    /** @return Vidas actuales del jugador */
    public int getLives() {
        return lives;
    }
    
    /** @return Número máximo de vidas permitidas */
    public int getMaxLives() {
        return MAX_LIVES;
    }
}
