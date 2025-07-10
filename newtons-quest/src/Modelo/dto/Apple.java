package Modelo.dto;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Representación de las manzanas en el juego.
 * 
 * Las manzanas rojas otorgan puntos positivos al jugador, mientras que las verdes
 * otorgan puntos negativos. Incluye lógica de movimiento, renderizado y colisiones.
 * 
 * @author Johann
 * @version 1.0
 */
public class Apple {
    
    // === POSICIÓN Y MOVIMIENTO ===
    /** Posición X de la manzana en el canvas */
    private double x;
    /** Posición Y de la manzana en el canvas */
    private double y;
    /** Velocidad de caída vertical */
    private double velocityY;
    
    // === PROPIEDADES ===
    /** true para manzana roja (puntos positivos), false para verde (negativos) */
    private boolean isRed;
    /** Estado activo de la manzana (false cuando es recolectada) */
    private boolean active = true;
    
    // === DIMENSIONES ===
    /** Ancho del sprite de la manzana */
    private final int WIDTH = 32;
    /** Alto del sprite de la manzana */
    private final int HEIGHT = 32;
    
    // === GRÁFICOS ===
    /** Sprite de la manzana (roja o verde) */
    private Image sprite;
    
    /**
     * Constructor para crear una manzana con propiedades específicas.
     * 
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param isRed true para manzana roja (puntos +), false para verde (puntos -)
     * @param velocity Velocidad de caída en píxeles por frame
     */
    public Apple(double x, double y, boolean isRed, double velocity) {
        this.x = x;
        this.y = y;
        this.isRed = isRed;
        this.velocityY = velocity;
        loadSprite();
    }
    
    /**
     * Carga el sprite apropiado según el tipo de manzana.
     */
    private void loadSprite() {
        try {
            String path = isRed ? 
                "src/recursos/sprites/manzanas/Apple_Red.png" : 
                "src/recursos/sprites/manzanas/Apple_Green.png";
            
            File file = new File(path);
            sprite = new Image(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error: No se pudo cargar el sprite de manzana: " + 
                (isRed ? "Apple_Red.png" : "Apple_Green.png"), e);
        }
    }
    
    /**
     * Actualiza la posición de la manzana
     */
    public void update() {
        if (!active) return;
        
        // Actualizar posición
        y += velocityY;
    }
    
    /**
     * Dibuja la manzana en el canvas
     * @param gc Contexto gráfico donde dibujar
     */
    public void render(GraphicsContext gc) {
        if (!active || sprite == null) return;
        
        gc.drawImage(sprite, x, y, WIDTH, HEIGHT);
    }
    
    /**
     * Comprueba si la manzana colisiona con el jugador
     * @param playerX Posición X del jugador
     * @param playerY Posición Y del jugador
     * @param playerWidth Ancho del jugador
     * @param playerHeight Alto del jugador
     * @return true si hay colisión, false en caso contrario
     */
    public boolean checkCollision(double playerX, double playerY, int playerWidth, int playerHeight) {
        // Ajustar el área de colisión para ser más preciso (la mitad del tamaño real del sprite)
        double collisionWidth = WIDTH * 0.7;
        double collisionHeight = HEIGHT * 0.7;
        
        // Centrar el área de colisión
        double appleX = x + (WIDTH - collisionWidth) / 2;
        double appleY = y + (HEIGHT - collisionHeight) / 2;
        
        return active && 
               appleX < playerX + playerWidth &&
               appleX + collisionWidth > playerX &&
               appleY < playerY + playerHeight &&
               appleY + collisionHeight > playerY;
    }
    
    /**
     * Comprueba si la manzana ha caído al suelo
     * @param floorY Posición Y del suelo
     * @return true si la manzana ha llegado al suelo, false en caso contrario
     */
    public boolean hasReachedFloor(double floorY) {
        return active && y + HEIGHT >= floorY;
    }
    
    /**
     * Desactiva la manzana (la elimina del juego)
     */
    public void deactivate() {
        active = false;
    }
    
    // Getters y setters
    
    public boolean isActive() {
        return active;
    }
    
    public boolean isRed() {
        return isRed;
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
