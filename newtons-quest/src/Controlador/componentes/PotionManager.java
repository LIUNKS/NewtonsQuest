package Controlador.componentes;

import Modelo.Potion;
import Modelo.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase encargada de gestionar las pociones en el juego.
 * Maneja la generación, actualización y efectos de las pociones.
 */
public class PotionManager {
    
    private List<Potion> potions = new ArrayList<>();
    private Random random = new Random();
    
    // Configuración de generación de pociones
    private long lastPotionSpawnTime = 0;
    private final long POTION_SPAWN_INTERVAL;
    private final double MIN_POTION_SPEED;
    private final double MAX_POTION_SPEED;
    
    // Controla si el generador de pociones está en pausa
    private boolean isPaused = false;
    
    // Dimensiones del juego
    private final int GAME_WIDTH;
    private final int GAME_HEIGHT;
    private final int FLOOR_Y;
    
    // Puntuaciones por recoger pociones
    private final int POTION_PICKUP_POINTS = 50;
      // Callbacks
    private java.util.function.Consumer<Integer> onScoreChange;
    private java.util.function.Consumer<String> onPotionEffect; // Para mostrar efectos visuales
    private java.util.function.Function<String, Boolean> onGainLife; // Para ganar vida
    
    /**
     * Constructor del PotionManager
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
        
        System.out.println("PotionManager inicializado");
    }
      /**
     * Establece los callbacks para eventos relacionados con pociones
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
    
    /**
     * Establece el estado de pausa para la generación de pociones
     * @param paused true para pausar la generación de pociones, false para reanudarla
     */
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        
        // Si se reanuda el juego, actualizar el último tiempo de generación
        if (!paused) {
            lastPotionSpawnTime = System.currentTimeMillis();
        }
        
        System.out.println("PotionManager " + (paused ? "pausado" : "reanudado"));
    }
    
    /**
     * Actualiza el estado de las pociones, genera nuevas y comprueba colisiones
     * @param player Jugador para comprobar colisiones
     */
    public void update(Player player) {
        try {
            // Solo generar nuevas pociones si el juego no está pausado
            if (!isPaused) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastPotionSpawnTime > POTION_SPAWN_INTERVAL) {
                    spawnPotion();
                    lastPotionSpawnTime = currentTime;
                }
            }
            
            // Actualizar pociones existentes y comprobar colisiones solo si no está pausado
            if (!isPaused) {
                updatePotions(player);
            }
            
        } catch (Exception e) {
            System.err.println("Error al actualizar pociones: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Genera una nueva poción en una posición aleatoria
     */
    private void spawnPotion() {
        try {
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
            
            System.out.println("Generada poción " + type.name() + " en (" + x + ", " + y + ") con velocidad " + speed);
        } catch (Exception e) {
            System.err.println("Error al generar poción: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Actualiza todas las pociones, comprobando colisiones y si han llegado al suelo
     * @param player Jugador para comprobar colisiones
     */
    private void updatePotions(Player player) {
        try {
            // Lista para guardar las pociones que hay que eliminar
            List<Potion> potionsToRemove = new ArrayList<>();
            
            // Actualizar cada poción
            for (Potion potion : potions) {
                if (!potion.isActive()) {
                    potionsToRemove.add(potion);
                    continue;
                }
                
                // Actualizar posición
                potion.update();
                
                // Comprobar si ha colisionado con el jugador
                if (potion.checkCollision(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
                    // Aplicar efecto según el tipo de poción
                    boolean effectApplied = false;
                    
                    switch (potion.getType()) {
                        case LENTITUD:
                            player.applySlownessEffect();
                            if (onPotionEffect != null) {
                                onPotionEffect.accept("¡Poción de lentitud activada!");
                            }
                            System.out.println("¡Poción de lentitud recogida!");
                            effectApplied = true;
                            break;
                            
                        case PUNTOS:
                            player.applyPointsEffect();
                            if (onPotionEffect != null) {
                                onPotionEffect.accept("¡Puntos dobles por 10 segundos!");
                            }
                            System.out.println("¡Poción de puntos recogida!");
                            effectApplied = true;
                            break;
                              case SALUD:
                            // Aplicar efecto visual en el player
                            player.applyHealthEffect();
                            
                            // Intentar ganar vida a través del ScoreManager
                            if (onGainLife != null && onGainLife.apply("Poción de salud")) {
                                if (onPotionEffect != null) {
                                    onPotionEffect.accept("¡Vida extra obtenida!");
                                }
                                System.out.println("¡Poción de salud recogida - vida extra otorgada!");
                                effectApplied = true;
                            } else {
                                if (onPotionEffect != null) {
                                    onPotionEffect.accept("Ya tienes todas las vidas");
                                }
                                System.out.println("Poción de salud recogida pero no se pudo aplicar - ya tienes todas las vidas");
                                effectApplied = true; // Aún así se recoge la poción
                            }
                            break;
                    }
                    
                    // Las pociones NO dan puntos por ser recogidas
                    // Solo aplican sus efectos correspondientes
                    
                    // Desactivar la poción para que se elimine
                    potion.deactivate();
                    potionsToRemove.add(potion);
                }
                // Comprobar si ha llegado al suelo
                else if (potion.hasReachedFloor(FLOOR_Y)) {
                    // Las pociones simplemente desaparecen al tocar el suelo (no hay penalización)
                    potion.deactivate();
                    potionsToRemove.add(potion);
                    System.out.println("Poción " + potion.getType().name() + " llegó al suelo y desapareció");
                }
            }
            
            // Eliminar las pociones que ya no están activas
            potions.removeAll(potionsToRemove);
            
        } catch (Exception e) {
            System.err.println("Error al actualizar pociones: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene la lista de pociones activas
     * @return Lista de pociones
     */
    public List<Potion> getPotions() {
        return potions;
    }
    
    /**
     * Limpia todas las pociones (útil al reiniciar o pausar)
     */
    public void clearPotions() {
        potions.clear();
    }
}
