package Controlador.componentes;

import Modelo.Apple;
import Modelo.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase encargada de gestionar las manzanas en el juego.
 * Separa la lógica de generación y control de manzanas del controlador principal.
 */
public class AppleManager {
    
    private List<Apple> apples = new ArrayList<>();
    private Random random = new Random();
    
    // Configuración de generación de manzanas
    private long lastAppleSpawnTime = 0;
    private final long APPLE_SPAWN_INTERVAL;
    private final double MIN_APPLE_SPEED;
    private final double MAX_APPLE_SPEED;
    
    // Controla si el generador de manzanas está en pausa
    private boolean isPaused = false;
    
    // Dimensiones del juego
    private final int GAME_WIDTH;
    private final int GAME_HEIGHT;
    private final int FLOOR_Y;
    
    // Puntuaciones
    private final int RED_APPLE_POINTS = 10;
    private final int GREEN_APPLE_POINTS = -5;
    private final int MISSED_APPLE_POINTS = -3;
    
    // Callbacks
    private java.util.function.Consumer<Integer> onScoreChange;
    private Runnable onLifeLost;
    
    /**
     * Constructor del AppleManager
     * @param gameWidth Ancho del área de juego
     * @param gameHeight Alto del área de juego
     * @param floorY Posición Y del suelo
     * @param appleSpawnInterval Intervalo entre generación de manzanas (ms)
     * @param minAppleSpeed Velocidad mínima de caída de manzanas
     * @param maxAppleSpeed Velocidad máxima de caída de manzanas
     */
    public AppleManager(int gameWidth, int gameHeight, int floorY, 
                       long appleSpawnInterval, double minAppleSpeed, double maxAppleSpeed) {
        this.GAME_WIDTH = gameWidth;
        this.GAME_HEIGHT = gameHeight;
        this.FLOOR_Y = floorY;
        this.APPLE_SPAWN_INTERVAL = appleSpawnInterval;
        this.MIN_APPLE_SPEED = minAppleSpeed;
        this.MAX_APPLE_SPEED = maxAppleSpeed;
        
        System.out.println("AppleManager inicializado");
    }
    
    /**
     * Establece los callbacks para eventos relacionados con manzanas
     * @param onScoreChange Acción cuando cambia la puntuación
     * @param onLifeLost Acción cuando se pierde una vida
     */    public void setCallbacks(java.util.function.Consumer<Integer> onScoreChange, Runnable onLifeLost) {
        this.onScoreChange = onScoreChange;
        this.onLifeLost = onLifeLost;
    }
    
    /**
     * Establece el estado de pausa para la generación de manzanas
     * @param paused true para pausar la generación de manzanas, false para reanudarla
     */
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        
        // Si se reanuda el juego, actualizar el último tiempo de generación
        // para evitar que se generen muchas manzanas de golpe
        if (!paused) {
            lastAppleSpawnTime = System.currentTimeMillis();
        }
        
        System.out.println("AppleManager " + (paused ? "pausado" : "reanudado"));
    }
    
    /**
     * Actualiza el estado de las manzanas, genera nuevas y comprueba colisiones
     * @param player Jugador para comprobar colisiones
     */    public void update(Player player) {
        try {
            // Solo generar nuevas manzanas si el juego no está pausado
            if (!isPaused) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastAppleSpawnTime > APPLE_SPAWN_INTERVAL) {
                    spawnApple();
                    lastAppleSpawnTime = currentTime;
                }
            }
            
            // Actualizar manzanas existentes y comprobar colisiones solo si no está pausado
            if (!isPaused) {
                updateApples(player);
            }
            
        } catch (Exception e) {
            System.err.println("Error al actualizar manzanas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Genera una nueva manzana en una posición aleatoria
     */
    private void spawnApple() {
        try {
            // Posición X aleatoria (dejando margen en los bordes)
            double x = 50 + random.nextDouble() * (GAME_WIDTH - 100);
            
            // Posición Y en la parte superior
            double y = -50;
            
            // Velocidad aleatoria
            double speed = MIN_APPLE_SPEED + random.nextDouble() * (MAX_APPLE_SPEED - MIN_APPLE_SPEED);
            
            // 70% de probabilidad de que sea manzana roja, 30% de que sea verde
            boolean isRed = random.nextDouble() < 0.7;
            
            // Crear la manzana y añadirla a la lista
            Apple apple = new Apple(x, y, isRed, speed);
            apples.add(apple);
            
            System.out.println("Generada manzana " + (isRed ? "roja" : "verde") + " en (" + x + ", " + y + ") con velocidad " + speed);
        } catch (Exception e) {
            System.err.println("Error al generar manzana: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Actualiza todas las manzanas, comprobando colisiones y si han llegado al suelo
     * @param player Jugador para comprobar colisiones
     */
    private void updateApples(Player player) {
        try {
            // Lista para guardar las manzanas que hay que eliminar
            List<Apple> applesToRemove = new ArrayList<>();
            
            // Actualizar cada manzana
            for (Apple apple : apples) {
                if (!apple.isActive()) {
                    applesToRemove.add(apple);
                    continue;
                }
                
                // Actualizar posición
                apple.update();
                
                // Comprobar si ha colisionado con el jugador
                if (apple.checkCollision(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
                    // Actualizar puntuación según el tipo de manzana
                    if (apple.isRed()) {
                        // Manzana roja recogida: puntos positivos
                        if (onScoreChange != null) {
                            onScoreChange.accept(RED_APPLE_POINTS);
                        }
                        System.out.println("¡Manzana roja recogida! Puntos: +" + RED_APPLE_POINTS);
                    } else {
                        // Manzana verde recogida: puntos negativos y perder vida
                        if (onScoreChange != null) {
                            onScoreChange.accept(GREEN_APPLE_POINTS);
                        }
                        System.out.println("¡Manzana verde recogida! Puntos: " + GREEN_APPLE_POINTS);
                        
                        // Activar el sprite de manzana verde
                        player.setGreenAppleEffect();
                        
                        // Perder una vida por atrapar manzana verde
                        if (onLifeLost != null) {
                            onLifeLost.run();
                        }
                    }
                    
                    // Desactivar la manzana para que se elimine
                    apple.deactivate();
                    applesToRemove.add(apple);
                }
                // Comprobar si ha llegado al suelo
                else if (apple.hasReachedFloor(FLOOR_Y)) {
                    // Si era roja y cayó al suelo, penalizar
                    if (apple.isRed()) {
                        // Manzana roja perdida: puntos negativos y perder vida
                        if (onScoreChange != null) {
                            onScoreChange.accept(MISSED_APPLE_POINTS);
                        }
                        System.out.println("Manzana roja perdida. Puntos: " + MISSED_APPLE_POINTS);
                        
                        // Perder una vida por dejar caer manzana roja
                        if (onLifeLost != null) {
                            onLifeLost.run();
                        }
                    }
                    
                    // Desactivar la manzana para que se elimine
                    apple.deactivate();
                    applesToRemove.add(apple);
                }
            }
            
            // Eliminar las manzanas que ya no están activas
            apples.removeAll(applesToRemove);
            
        } catch (Exception e) {
            System.err.println("Error al actualizar manzanas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene la lista de manzanas activas
     * @return Lista de manzanas
     */
    public List<Apple> getApples() {
        return apples;
    }
    
    /**
     * Limpia todas las manzanas (útil al reiniciar o pausar)
     */
    public void clearApples() {
        apples.clear();
    }
}
