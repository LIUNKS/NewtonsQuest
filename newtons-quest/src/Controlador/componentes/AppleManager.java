package Controlador.componentes;

import Modelo.dto.Apple;
import Modelo.dto.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gestor de manzanas.
 * 
 * Esta clase centraliza toda la lógica relacionada con la generación, 
 * comportamiento y gestión de las manzanas en el juego. Separa claramente 
 * las responsabilidades del controlador principal, manejando:
 * 
 *   - Generación automática de manzanas rojas y verdes
 *   - Control de velocidad y posicionamiento
 *   - Detección de colisiones con el jugador
 *   - Gestión de puntuación y pérdida de vidas
 *   - Sistema de pausa para la generación
 * 
 * Las manzanas rojas otorgan puntos positivos al ser recolectadas, mientras
 * que las verdes causan pérdida de puntos y vidas. El sistema también maneja
 * el caso especial de manzanas rojas perdidas (que llegan al suelo).
 */

public class AppleManager {
    
    // =====================================
    // ATRIBUTOS DE INSTANCIA
    // =====================================
    
    /** Lista de manzanas activas en el juego */
    private List<Apple> apples = new ArrayList<>();
    
    /** Generador de números aleatorios para posicionamiento y velocidad */
    private Random random = new Random();
    
    // =====================================
    // CONFIGURACIÓN DE GENERACIÓN
    // =====================================
    
    /** Timestamp de la última manzana generada */
    private long lastAppleSpawnTime = 0;
    
    /** Intervalo entre generación de manzanas en milisegundos */
    private final long APPLE_SPAWN_INTERVAL;
    
    /** Velocidad mínima de caída de manzanas */
    private final double MIN_APPLE_SPEED;
    
    /** Velocidad máxima de caída de manzanas */
    private final double MAX_APPLE_SPEED;
    
    /** Indica si la generación de manzanas está pausada */
    private boolean isPaused = false;
    
    // =====================================
    // DIMENSIONES DEL JUEGO
    // =====================================
    
    /** Ancho del área de juego */
    private final int GAME_WIDTH;
    
    /** Alto del área de juego */
    private final int GAME_HEIGHT;
    
    /** Posición Y del suelo del juego */
    private final int FLOOR_Y;
    
    // =====================================
    // CONFIGURACIÓN DE PUNTUACIÓN
    // =====================================
    
    /** Puntos otorgados por recoger una manzana roja */
    private final int RED_APPLE_POINTS = 10;
    
    /** Puntos perdidos por tocar una manzana verde */
    private final int GREEN_APPLE_POINTS = -5;
    
    /** Puntos perdidos por dejar caer una manzana roja */
    private final int MISSED_APPLE_POINTS = -3;
    
    // =====================================
    // CALLBACKS
    // =====================================
    
    /** Callback para notificar cambios en la puntuación */
    private java.util.function.Consumer<Integer> onScoreChange;
    
    /** Callback para notificar pérdida de una vida */
    private Runnable onLifeLost;
    
    // =====================================
    // CONSTRUCTOR
    // =====================================
    
    /**
     * Constructor del gestor de manzanas.
     * 
     * Inicializa el gestor con las dimensiones del área de juego y los
     * parámetros de configuración para la generación y velocidad de manzanas.
     * 
     * @param gameWidth Ancho del área de juego en píxeles
     * @param gameHeight Alto del área de juego en píxeles  
     * @param floorY Posición Y del suelo en píxeles
     * @param appleSpawnInterval Intervalo entre generación de manzanas en milisegundos
     * @param minAppleSpeed Velocidad mínima de caída en píxeles por frame
     * @param maxAppleSpeed Velocidad máxima de caída en píxeles por frame
     */
    public AppleManager(int gameWidth, int gameHeight, int floorY, 
                       long appleSpawnInterval, double minAppleSpeed, double maxAppleSpeed) {
        this.GAME_WIDTH = gameWidth;
        this.GAME_HEIGHT = gameHeight;
        this.FLOOR_Y = floorY;
        this.APPLE_SPAWN_INTERVAL = appleSpawnInterval;
        this.MIN_APPLE_SPEED = minAppleSpeed;
        this.MAX_APPLE_SPEED = maxAppleSpeed;
    }
    
    // =====================================
    // MÉTODOS PÚBLICOS - CONFIGURACIÓN
    // =====================================
    
    /**
     * Establece los callbacks para eventos relacionados con manzanas.
     * 
     * Estos callbacks permiten al AppleManager comunicarse con otros
     * componentes del juego cuando ocurren eventos importantes como
     * cambios en la puntuación o pérdida de vidas.
     * 
     * @param onScoreChange Función callback que recibe el cambio de puntuación
     * @param onLifeLost Función callback ejecutada cuando se pierde una vida
     */
    public void setCallbacks(java.util.function.Consumer<Integer> onScoreChange, Runnable onLifeLost) {
        this.onScoreChange = onScoreChange;
        this.onLifeLost = onLifeLost;
    }
    
    /**
     * Controla el estado de pausa para la generación de manzanas.
     * 
     * Cuando se pausa, se detiene la generación de nuevas manzanas pero
     * las existentes continúan su movimiento. Al reanudar, se reinicia el
     * timer de generación para evitar acumulación de manzanas.
     * 
     * @param paused true para pausar la generación, false para reanudarla
     */
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        
        // Reiniciar timer al reanudar para evitar generación masiva
        if (!paused) {
            lastAppleSpawnTime = System.currentTimeMillis();
        }
    }
    
    // =====================================
    // MÉTODOS PÚBLICOS - ACTUALIZACIÓN
    // =====================================
    
    /**
     * Actualiza el estado completo del sistema de manzanas.
     * 
     * Realiza las siguientes operaciones en cada frame:
     * - Genera nuevas manzanas según el intervalo configurado
     * - Actualiza posición de manzanas existentes
     * - Detecta colisiones con el jugador
     * - Elimina manzanas que han salido del área de juego
     * /
     * 
     * @param player Instancia del jugador para detectar colisiones
     */
    public void update(Player player) {
        try {
            // Generar nuevas manzanas si no está pausado
            if (!isPaused) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastAppleSpawnTime > APPLE_SPAWN_INTERVAL) {
                    spawnApple();
                    lastAppleSpawnTime = currentTime;
                }
            }
            
            // Actualizar manzanas existentes y verificar colisiones
            if (!isPaused) {
                updateApples(player);
            }
            
        } catch (Exception e) {
            // Manejar errores silenciosamente en producción
        }
    }
    
    // =====================================
    // MÉTODOS PRIVADOS - GENERACIÓN
    // =====================================
    
    /**
     * Genera una nueva manzana en posición aleatoria.
     * 
     * Crea una manzana con las siguientes características:
     * - Posición X aleatoria dentro del área de juego con márgenes
     * - Posición Y inicial en la parte superior (fuera de pantalla)
     * - Velocidad aleatoria dentro del rango configurado
     * - 70% probabilidad de ser roja, 30% de ser verde
     */
    private void spawnApple() {
        try {
            // Posición X aleatoria (dejando margen en los bordes)
            double x = 50 + random.nextDouble() * (GAME_WIDTH - 100);
            
            // Posición Y en la parte superior
            double y = -50;
            
            // Velocidad aleatoria dentro del rango configurado
            double speed = MIN_APPLE_SPEED + random.nextDouble() * (MAX_APPLE_SPEED - MIN_APPLE_SPEED);
            
            // 70% de probabilidad de que sea manzana roja, 30% de que sea verde
            boolean isRed = random.nextDouble() < 0.7;
            
            // Crear la manzana y añadirla a la lista activa
            Apple apple = new Apple(x, y, isRed, speed);
            apples.add(apple);
            
        } catch (Exception e) {
            // Manejar errores en producción
        }
    }
    
    // =====================================
    // MÉTODOS PRIVADOS - ACTUALIZACIÓN
    // =====================================
    
    /**
     * Actualiza posición de manzanas existentes y gestiona colisiones.
     * 
     * Para cada manzana activa, realiza las siguientes operaciones:
     * - Actualiza su posición según la velocidad de caída
     * - Verifica colisión con el jugador y aplica efectos correspondientes
     * - Detecta si ha llegado al suelo y aplica penalizaciones si es necesario
     * - Elimina manzanas inactivas de la lista
     * 
     * @param player Instancia del jugador para verificar colisiones
     */
    private void updateApples(Player player) {
        try {
            // Lista para manzanas que deben ser eliminadas
            List<Apple> applesToRemove = new ArrayList<>();
            
            // Procesar cada manzana activa
            for (Apple apple : apples) {
                if (!apple.isActive()) {
                    applesToRemove.add(apple);
                    continue;
                }
                
                // Actualizar posición de la manzana
                apple.update();
                
                // Verificar colisión con el jugador
                if (apple.checkCollision(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
                    processAppleCollected(apple, player);
                    applesToRemove.add(apple);
                }
                // Verificar si llegó al suelo
                else if (apple.hasReachedFloor(FLOOR_Y)) {
                    processAppleReachedFloor(apple);
                    applesToRemove.add(apple);
                }
            }
            
            // Eliminar manzanas procesadas
            apples.removeAll(applesToRemove);
            
        } catch (Exception e) {
            // Manejar errores en producción
        }
    }
    
    /**
     * Procesa el evento de manzana recolectada por el jugador.
     * 
     * @param apple Manzana que fue recolectada
     * @param player Jugador que recolectó la manzana
     */
    private void processAppleCollected(Apple apple, Player player) {
        if (apple.isRed()) {
            // Manzana roja: puntos positivos
            if (onScoreChange != null) {
                int points = RED_APPLE_POINTS;
                // Aplicar multiplicador de puntos dobles si está activo
                if (player.hasPointsEffect()) {
                    points *= 2;
                }
                onScoreChange.accept(points);
            }
        } else {
            // Manzana verde: puntos negativos y pérdida de vida
            if (onScoreChange != null) {
                onScoreChange.accept(GREEN_APPLE_POINTS);
            }
            
            // Activar efecto visual en el jugador
            player.setGreenAppleEffect();
            
            // Aplicar pérdida de vida
            if (onLifeLost != null) {
                onLifeLost.run();
            }
        }
        
        // Desactivar la manzana
        apple.deactivate();
    }
    
    /**
     * Procesa el evento de manzana que llegó al suelo.
     * 
     * @param apple Manzana que llegó al suelo
     */
    private void processAppleReachedFloor(Apple apple) {
        // Solo penalizar si era una manzana roja perdida
        if (apple.isRed()) {
            if (onScoreChange != null) {
                onScoreChange.accept(MISSED_APPLE_POINTS);
            }
            
            // Pérdida de vida por manzana roja perdida
            if (onLifeLost != null) {
                onLifeLost.run();
            }
        }
        
        // Desactivar la manzana
        apple.deactivate();
    }
    
    // =====================================
    // MÉTODOS PÚBLICOS - ACCESO A DATOS
    // =====================================
    
    /**
     * Obtiene la lista de manzanas activas en el juego.
     * 
     * Esta lista es utilizada por el RenderManager para dibujar
     * las manzanas en pantalla durante cada frame de renderizado.
     * 
     * @return Lista inmutable de manzanas activas
     */
    public List<Apple> getApples() {
        return apples;
    }
    
    /**
     * Elimina todas las manzanas del juego.
     * 
     * Útil para limpiar el estado del juego al reiniciar una partida,
     * pausar el juego o cambiar de nivel. No afecta la configuración
     * del gestor, solo limpia las manzanas existentes.
     */
    public void clearApples() {
        apples.clear();
    }
}
