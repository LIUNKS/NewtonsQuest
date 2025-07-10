package Controlador.componentes;

import Modelo.dto.Player;

/**
 * Gestor del jugador y mecánicas de movimiento.
 * 
 * Esta clase controla todas las operaciones relacionadas con el jugador:
 * 
 *   - Inicialización y configuración del jugador
 *   - Actualización de posición y estado
 *   - Restricciones de movimiento en el área de juego
 *   - Activación de efectos especiales (manzana verde, pociones)
 *   - Gestión de eventos de muerte y efectos
 * 
 * Implementa el patrón Singleton para garantizar una única instancia
 * del gestor del jugador durante la ejecución del juego.
 */

public class PlayerManager {
    
    // ================================================================================================
    // INSTANCIA SINGLETON
    // ================================================================================================
    
    /** Instancia única del gestor de jugador */
    private static PlayerManager instance;
    
    // ================================================================================================
    // CONFIGURACIÓN DEL JUGADOR
    // ================================================================================================
    
    /** Objeto jugador actual */
    private Player player;
    
    // ================================================================================================
    // CONFIGURACIÓN DEL ÁREA DE JUEGO
    // ================================================================================================
    
    /** Ancho del área de juego */
    private final int GAME_WIDTH;
    
    /** Alto del área de juego */
    private final int GAME_HEIGHT;
    
    /** Posición Y del suelo del juego */
    private final int FLOOR_Y;
    
    // ================================================================================================
    // CALLBACKS Y EVENTOS
    // ================================================================================================
    
    /** Callback ejecutado cuando el jugador muere */
    private Runnable onPlayerDeath;
    
    /** Callback ejecutado cuando se activa un efecto en el jugador */
    private java.util.function.Consumer<String> onPlayerEffectActivated;
    
    // ================================================================================================
    // CONSTRUCTORES Y PATRÓN SINGLETON
    // ================================================================================================
    
    /**
     * Constructor privado para implementar patrón Singleton.
     * @param gameWidth Ancho del área de juego
     * @param gameHeight Alto del área de juego
     * @param floorY Posición Y del suelo
     */
    private PlayerManager(int gameWidth, int gameHeight, int floorY) {
        this.GAME_WIDTH = gameWidth;
        this.GAME_HEIGHT = gameHeight;
        this.FLOOR_Y = floorY;
    }
    
    /**
     * Obtiene la instancia única del gestor de jugador.
     * @param gameWidth Ancho del área de juego
     * @param gameHeight Alto del área de juego
     * @param floorY Posición Y del suelo
     * @return Instancia única del PlayerManager
     */
    public static PlayerManager getInstance(int gameWidth, int gameHeight, int floorY) {
        if (instance == null) {
            instance = new PlayerManager(gameWidth, gameHeight, floorY);
        }
        return instance;
    }
    
    // ================================================================================================
    // INICIALIZACIÓN DEL JUGADOR
    // ================================================================================================
    
    /**
     * Inicializa un nuevo jugador con datos del usuario.
     * @param userId ID único del usuario
     * @param username Nombre de usuario
     * @param fullName Nombre completo del usuario
     * @param email Correo electrónico del usuario
     */
    public void initializePlayer(int userId, String username, String fullName, String email) {
        double startX = GAME_WIDTH / 2.0;
        double startY = FLOOR_Y;
        
        player = new Player(startX, startY);
        
        // Configurar datos del usuario
        player.setId(userId);
        player.setUsername(username);
        player.setFullName(fullName);
        player.setEmail(email);
    }
    
    // ================================================================================================
    // ACTUALIZACIÓN Y MOVIMIENTO
    // ================================================================================================
    
    /**
     * Actualiza el estado del jugador en cada frame.
     */
    public void update() {
        if (player == null) return;
        
        player.update(FLOOR_Y);
        restrictPlayerMovement();
        
        if (player.isDead() && onPlayerDeath != null) {
            onPlayerDeath.run();
        }
    }
    
    /**
     * Establece la dirección de movimiento del jugador.
     * @param left true para moverse hacia la izquierda
     * @param right true para moverse hacia la derecha
     */
    public void setPlayerMovement(boolean left, boolean right) {
        if (player == null) return;
        
        player.moveLeft(left);
        player.moveRight(right);
    }
    
    /**
     * Restringe el movimiento del jugador a los límites del área de juego.
     */
    private void restrictPlayerMovement() {
        if (player == null) return;
        
        if (player.getX() < 0) {
            player.setPosition(0, player.getY());
        } else if (player.getX() + player.getWidth() > GAME_WIDTH) {
            player.setPosition(GAME_WIDTH - player.getWidth(), player.getY());
        }
    }
    
    // ================================================================================================
    // CONFIGURACIÓN DE CALLBACKS
    // ================================================================================================
    
    /**
     * Establece el callback para cuando el jugador muere.
     * @param callback Función a ejecutar cuando el jugador muere
     */
    public void setOnPlayerDeath(Runnable callback) {
        this.onPlayerDeath = callback;
    }
    
    /**
     * Establece el callback para cuando se activa un efecto en el jugador.
     * @param callback Función a ejecutar cuando se activa un efecto
     */
    public void setOnPlayerEffectActivated(java.util.function.Consumer<String> callback) {
        this.onPlayerEffectActivated = callback;
    }
    
    // ================================================================================================
    // EFECTOS ESPECIALES DEL JUGADOR
    // ================================================================================================
    
    /**
     * Activa el efecto de manzana verde en el jugador.
     */
    public void activateGreenAppleEffect() {
        if (player == null) return;
        
        player.setHasGreenApple(true);
        
        if (onPlayerEffectActivated != null) {
            onPlayerEffectActivated.accept("green_apple");
        }
    }
    
    /**
     * Activa el efecto de lentitud en el jugador.
     */
    public void activateSlownessEffect() {
        if (player == null) return;
        
        player.applySlownessEffect();
        
        if (onPlayerEffectActivated != null) {
            onPlayerEffectActivated.accept("slowness");
        }
    }
    
    /**
     * Activa el efecto de puntos extra en el jugador.
     */
    public void activatePointsEffect() {
        if (player == null) return;
        
        player.applyPointsEffect();
        
        if (onPlayerEffectActivated != null) {
            onPlayerEffectActivated.accept("points");
        }
    }
    
    /**
     * Activa el efecto de regeneración de salud en el jugador.
     */
    public void activateHealthEffect() {
        if (player == null) return;
        
        player.applyHealthEffect();
        
        if (onPlayerEffectActivated != null) {
            onPlayerEffectActivated.accept("health");
        }
    }
    
    // ================================================================================================
    // ESTADO DEL JUGADOR
    // ================================================================================================
    
    /**
     * Establece el estado de muerte del jugador.
     * @param isDead true si el jugador está muerto
     */
    public void setPlayerDead(boolean isDead) {
        if (player == null) return;
        
        player.setDead(isDead);
    }
    
    // ================================================================================================
    // MÉTODOS DE ACCESO - GETTERS
    // ================================================================================================
    
    /**
     * Obtiene el objeto jugador actual.
     * @return Instancia del jugador actual o null si no está inicializado
     */
    public Player getPlayer() {
        return player;
    }
}
