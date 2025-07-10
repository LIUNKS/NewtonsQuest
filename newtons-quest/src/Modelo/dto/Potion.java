package Modelo.dto;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Representación de las pociones en el juego.
 * 
 * Las pociones tienen diferentes efectos sobre el jugador: lentitud (reduce velocidad),
 * puntos (otorga puntos extra) y salud (restaura vida). Incluye lógica de movimiento,
 * renderizado y gestión de efectos.
 * 
 * @author Johann
 * @version 1.0
 */
 
public class Potion {
    
    /**
     * Tipos de pociones disponibles en el juego.
     * Cada tipo tiene asociado un archivo de imagen específico.
     */
    public enum PotionType {
        /** Poción que reduce la velocidad del jugador temporalmente */
        LENTITUD("pocion_lentitud.png"),
        /** Poción que otorga puntos extra al jugador */
        PUNTOS("pocion_puntos.png"),
        /** Poción que restaura la salud del jugador */
        SALUD("pocion_salud.png");
        
        private final String filename;
        
        PotionType(String filename) {
            this.filename = filename;
        }
        
        /** @return Nombre del archivo de imagen para este tipo de poción */
        public String getFilename() {
            return filename;
        }
    }
    
    // === POSICIÓN Y MOVIMIENTO ===
    /** Posición X de la poción en el canvas */
    private double x;
    /** Posición Y de la poción en el canvas */
    private double y;
    /** Velocidad de caída vertical */
    private double velocityY;
    
    // === PROPIEDADES ===
    /** Tipo de poción y su efecto */
    private PotionType type;
    /** Estado activo de la poción (false cuando es recolectada) */
    private boolean active = true;
    
    // === DIMENSIONES ===
    /** Ancho del sprite de la poción */
    private final int WIDTH = 32;
    /** Alto del sprite de la poción */
    private final int HEIGHT = 32;
    
    // === GRÁFICOS ===
    /** Sprite de la poción */
    private Image sprite;
    
    /**
     * Constructor de la poción
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param type Tipo de poción
     * @param velocity Velocidad de caída
     */
    public Potion(double x, double y, PotionType type, double velocity) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.velocityY = velocity;
        
        loadSprite();
    }
    
    /**
     * Carga la imagen de la poción según su tipo
     */
    private void loadSprite() {
        try {
            String path = "src/recursos/sprites/pociones/" + type.getFilename();
            File file = new File(path);
            sprite = new Image(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error: No se pudo cargar el sprite de poción: " + type.getFilename(), e);
        }
    }
    
    /**
     * Actualiza la posición de la poción
     */
    public void update() {
        if (!active) return;
        
        // Actualizar posición
        y += velocityY;
    }
    
    /**
     * Dibuja la poción en el canvas
     * @param gc Contexto gráfico donde dibujar
     */
    public void render(GraphicsContext gc) {
        if (!active || sprite == null) return;
        
        gc.drawImage(sprite, x, y, WIDTH, HEIGHT);
    }
    
    /**
     * Comprueba si la poción colisiona con el jugador
     * @param playerX Posición X del jugador
     * @param playerY Posición Y del jugador
     * @param playerWidth Ancho del jugador
     * @param playerHeight Alto del jugador
     * @return true si hay colisión, false en caso contrario
     */
    public boolean checkCollision(double playerX, double playerY, int playerWidth, int playerHeight) {
        // Ajustar el área de colisión para ser más preciso
        double collisionWidth = WIDTH * 0.7;
        double collisionHeight = HEIGHT * 0.7;
        
        // Centrar el área de colisión
        double potionX = x + (WIDTH - collisionWidth) / 2;
        double potionY = y + (HEIGHT - collisionHeight) / 2;
        
        return active && 
               potionX < playerX + playerWidth &&
               potionX + collisionWidth > playerX &&
               potionY < playerY + playerHeight &&
               potionY + collisionHeight > playerY;
    }
    
    /**
     * Comprueba si la poción ha caído al suelo
     * @param floorY Posición Y del suelo
     * @return true si la poción ha llegado al suelo, false en caso contrario
     */
    public boolean hasReachedFloor(double floorY) {
        return active && y + HEIGHT >= floorY;
    }
    
    /**
     * Desactiva la poción (la elimina del juego)
     */
    public void deactivate() {
        active = false;
    }
    
    // Getters
    
    public boolean isActive() {
        return active;
    }
    
    public PotionType getType() {
        return type;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public int getWidth() {
        return WIDTH;
    }
    
    public int getHeight() {
        return HEIGHT;
    }
}
