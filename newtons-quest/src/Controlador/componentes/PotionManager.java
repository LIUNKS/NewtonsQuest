package Controlador.componentes;

import Modelo.dto.Potion;
import Modelo.dto.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gestor de pociones y efectos especiales.
 * 
 * Esta clase controla el sistema de pociones del juego:
 * 
 *   - Generación automática de pociones con tipos aleatorios
 *   - Detección de colisiones entre pociones y jugador
 *   - Aplicación de efectos especiales (lentitud, puntos dobles, salud)
 *   - Control de intervalos de aparición y velocidades
 *   - Gestión del estado de pausa del sistema
 * 
 * Las pociones incluyen tres tipos: LENTITUD (reduce velocidad del jugador),
 * PUNTOS (duplica puntos por tiempo limitado) y SALUD (otorga vida extra).
 */

public class PotionManager {
    
    // ================================================================================================
    // CONFIGURACIÓN DE POCIONES
    // ================================================================================================
    
    /** Lista de pociones activas en el juego */
    private List<Potion> potions = new ArrayList<>();
    
    /** Generador de números aleatorios para posiciones y tipos */
    private Random random = new Random();
    
    // ================================================================================================
    // CONFIGURACIÓN DE GENERACIÓN
    // ================================================================================================
    
    /** Momento de la última generación de poción */
    private long lastPotionSpawnTime = 0;
    
    /** Intervalo entre generaciones de pociones en milisegundos */
    private final long POTION_SPAWN_INTERVAL;
    
    /** Velocidad mínima de caída de pociones */
    private final double MIN_POTION_SPEED;
    
    /** Velocidad máxima de caída de pociones */
    private final double MAX_POTION_SPEED;
    
    /** Estado de pausa del generador de pociones */
    private boolean isPaused = false;
    
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
    // CONFIGURACIÓN DE PUNTUACIÓN
    // ================================================================================================
    
    /** Puntos otorgados por recoger una poción */
    private final int POTION_PICKUP_POINTS = 50;
    
    // ================================================================================================
    // CALLBACKS Y EVENTOS
    // ================================================================================================
    
    /** Callback para cambios en la puntuación */
    private java.util.function.Consumer<Integer> onScoreChange;
    
    /** Callback para mostrar efectos visuales de pociones */
    private java.util.function.Consumer<String> onPotionEffect;
    
    /** Callback para ganar vida (retorna true si tuvo éxito) */
    private java.util.function.Function<String, Boolean> onGainLife;
    
    // ================================================================================================
    // CONSTRUCTORES
    // ================================================================================================
    
    /**
     * Constructor principal del gestor de pociones.
     * @param gameWidth Ancho del área de juego
     * @param gameHeight Alto del área de juego
     * @param floorY Posición Y del suelo
     * @param potionSpawnInterval Intervalo entre generación de pociones (ms)
     * @param minPotionSpeed Velocidad mínima de caída de pociones
     * @param maxPotionSpeed Velocidad máxima de caída de pociones
     */
    public PotionManager(int gameWidth, int gameHeight, int floorY, 
                        long potionSpawnInterval, double minPotionSpeed, double maxPotionSpeed) {
        this.GAME_WIDTH = gameWidth;
        this.GAME_HEIGHT = gameHeight;
        this.FLOOR_Y = floorY;
        this.POTION_SPAWN_INTERVAL = potionSpawnInterval;
        this.MIN_POTION_SPEED = minPotionSpeed;
        this.MAX_POTION_SPEED = maxPotionSpeed;
    }
    
    // ================================================================================================
    // CONFIGURACIÓN DE CALLBACKS
    // ================================================================================================
    
    /**
     * Establece los callbacks para eventos relacionados con pociones.
     * @param onScoreChange Acción cuando cambia la puntuación
     * @param onPotionEffect Acción cuando se activa un efecto de poción
     * @param onGainLife Función para ganar vida (retorna true si tuvo éxito)
     */
    public void setCallbacks(java.util.function.Consumer<Integer> onScoreChange, 
                           java.util.function.Consumer<String> onPotionEffect,
                           java.util.function.Function<String, Boolean> onGainLife) {
        this.onScoreChange = onScoreChange;
        this.onPotionEffect = onPotionEffect;
        this.onGainLife = onGainLife;
    }
    
    // ================================================================================================
    // CONTROL DE ESTADO
    // ================================================================================================
    
    /**
     * Establece el estado de pausa para la generación de pociones.
     * @param paused true para pausar la generación, false para reanudarla
     */
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        
        if (!paused) {
            lastPotionSpawnTime = System.currentTimeMillis();
        }
    }
    
    // ================================================================================================
    // ACTUALIZACIÓN Y GENERACIÓN
    // ================================================================================================
    
    /**
     * Actualiza el estado de las pociones, genera nuevas y comprueba colisiones.
     * @param player Jugador para comprobar colisiones
     */
    public void update(Player player) {
        if (!isPaused) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPotionSpawnTime > POTION_SPAWN_INTERVAL) {
                spawnPotion();
                lastPotionSpawnTime = currentTime;
            }
        }
        
        if (!isPaused) {
            updatePotions(player);
        }
    }
    
    /**
     * Genera una nueva poción en una posición aleatoria.
     */
    private void spawnPotion() {
        // Posición X aleatoria (dejando margen en los bordes)
        double x = 50 + random.nextDouble() * (GAME_WIDTH - 100);
        
        // Posición Y en la parte superior
        double y = -50;
        
        // Velocidad aleatoria
        double speed = MIN_POTION_SPEED + random.nextDouble() * (MAX_POTION_SPEED - MIN_POTION_SPEED);
        
        // Seleccionar tipo de poción aleatoriamente
        Potion.PotionType[] types = Potion.PotionType.values();
        Potion.PotionType type = types[random.nextInt(types.length)];
        
        // Crear la poción y añadirla a la lista
        Potion potion = new Potion(x, y, type, speed);
        potions.add(potion);
    }
    
    // ================================================================================================
    // PROCESAMIENTO DE COLISIONES Y EFECTOS
    // ================================================================================================
    
    /**
     * Actualiza todas las pociones, comprobando colisiones y si han llegado al suelo.
     * @param player Jugador para comprobar colisiones
     */
    private void updatePotions(Player player) {
        List<Potion> potionsToRemove = new ArrayList<>();
        
        for (Potion potion : potions) {
            if (!potion.isActive()) {
                potionsToRemove.add(potion);
                continue;
            }
            
            potion.update();
            
            // Comprobar colisión con el jugador
            if (potion.checkCollision(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
                boolean effectApplied = false;
                
                switch (potion.getType()) {
                    case LENTITUD:
                        player.applySlownessEffect();
                        if (onPotionEffect != null) {
                            onPotionEffect.accept("¡Poción de lentitud activada!");
                        }
                        effectApplied = true;
                        break;
                        
                    case PUNTOS:
                        player.applyPointsEffect();
                        if (onPotionEffect != null) {
                            onPotionEffect.accept("¡Puntos dobles por 10 segundos!");
                        }
                        effectApplied = true;
                        break;
                        
                    case SALUD:
                        player.applyHealthEffect();
                        
                        if (onGainLife != null && onGainLife.apply("Poción de salud")) {
                            if (onPotionEffect != null) {
                                onPotionEffect.accept("¡Vida extra obtenida!");
                            }
                            effectApplied = true;
                        } else {
                            if (onPotionEffect != null) {
                                onPotionEffect.accept("Ya tienes todas las vidas");
                            }
                            effectApplied = true;
                        }
                        break;
                }
                
                potion.deactivate();
                potionsToRemove.add(potion);
            }
            // Comprobar si ha llegado al suelo
            else if (potion.hasReachedFloor(FLOOR_Y)) {
                potion.deactivate();
                potionsToRemove.add(potion);
            }
        }
        
        potions.removeAll(potionsToRemove);
    }
    
    // ================================================================================================
    // MÉTODOS DE ACCESO Y CONTROL
    // ================================================================================================
    
    /**
     * Obtiene la lista de pociones activas.
     * @return Lista de pociones actualmente en el juego
     */
    public List<Potion> getPotions() {
        return potions;
    }
    
    /**
     * Limpia todas las pociones del juego.
     * Útil al reiniciar o pausar el juego.
     */
    public void clearPotions() {
        potions.clear();
    }
}
