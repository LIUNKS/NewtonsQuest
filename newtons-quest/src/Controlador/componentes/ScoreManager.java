package Controlador.componentes;

/**
 * Clase encargada de gestionar el sistema de puntuación y vidas.
 * Separa la lógica de puntuación del controlador principal.
 */
public class ScoreManager {
    
    private int score = 0;
    private int lives;
    private final int MAX_LIVES;
    
    // Callbacks
    private Runnable onGameOver;
    private java.util.function.Consumer<Integer> onScoreChange;
    
    /**
     * Constructor del ScoreManager
     * @param maxLives Número máximo de vidas
     */
    public ScoreManager(int maxLives) {
        this.MAX_LIVES = maxLives;
        this.lives = maxLives;
        System.out.println("ScoreManager inicializado con " + maxLives + " vidas");
    }
    
    /**
     * Establece los callbacks para eventos relacionados con la puntuación
     * @param onGameOver Acción cuando el juego termina
     * @param onScoreChange Acción cuando cambia la puntuación
     */
    public void setCallbacks(Runnable onGameOver, java.util.function.Consumer<Integer> onScoreChange) {
        this.onGameOver = onGameOver;
        this.onScoreChange = onScoreChange;
    }
    
    /**
     * Añade puntos a la puntuación actual
     * @param points Puntos a añadir (puede ser negativo)
     */
    public void addScore(int points) {
        score += points;
        
        // Asegurar que la puntuación no sea negativa
        if (score < 0) {
            score = 0;
        }
        
        System.out.println("Puntuación actualizada: " + score + " (+" + points + ")");
        
        // Notificar el cambio de puntuación
        if (onScoreChange != null) {
            onScoreChange.accept(score);
        }
    }
    
    /**
     * Reduce una vida del jugador y verifica si el juego ha terminado
     * @param reason Motivo por el que perdió la vida
     * @return true si quedan vidas, false si el juego ha terminado
     */
    public boolean loseLife(String reason) {
        if (lives > 0) {
            lives--;
            System.out.println("Vida perdida: " + reason + ". Vidas restantes: " + lives);
            
            // Si no quedan vidas, fin del juego
            if (lives <= 0) {
                System.out.println("¡GAME OVER! No quedan vidas.");
                
                // Notificar fin del juego
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
     * Añade una vida extra al jugador (si no ha alcanzado el máximo)
     * @param reason Motivo por el que ganó la vida
     * @return true si se pudo añadir la vida, false si ya tenía el máximo
     */
    public boolean gainLife(String reason) {
        if (lives < MAX_LIVES) {
            lives++;
            System.out.println("Vida ganada: " + reason + ". Vidas actuales: " + lives);
            return true;
        } else {
            System.out.println("No se puede ganar vida: ya tienes el máximo (" + MAX_LIVES + ")");
            return false;
        }
    }
    
    /**
     * Restablece la puntuación y las vidas a sus valores iniciales
     */
    public void reset() {
        score = 0;
        lives = MAX_LIVES;
        System.out.println("Puntuación y vidas restablecidas");
    }
    
    // Getters
    
    public int getScore() {
        return score;
    }
    
    public int getLives() {
        return lives;
    }
    
    public int getMaxLives() {
        return MAX_LIVES;
    }
}
