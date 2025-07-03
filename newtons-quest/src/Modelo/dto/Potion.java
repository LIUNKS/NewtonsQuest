package Modelo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Clase que representa una poción en el juego
 */
public class Potion {
    // Tipos de poción
    public enum PotionType {
        LENTITUD("pocion_lentitud.png"),
        PUNTOS("pocion_puntos.png"),
        SALUD("pocion_salud.png");
        
        private final String filename;
        
        PotionType(String filename) {
            this.filename = filename;
        }
        
        public String getFilename() {
            return filename;
        }
    }
    
    // Posición de la poción
    private double x;
    private double y;
    
    // Velocidad de caída
    private double velocityY;
    
    // Tipo de poción
    private PotionType type;
    
    // Dimensiones de la poción
    private final int WIDTH = 32;
    private final int HEIGHT = 32;
    
    // Imagen de la poción
    private Image sprite;
    
    // Estado de la poción
    private boolean active = true;
    
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
            if (file.exists()) {
                sprite = new Image(new FileInputStream(file));
                System.out.println("Sprite de poción " + type.name() + " cargado correctamente");
            } else {
                System.err.println("No se encontró el archivo: " + path);
                createFallbackSprite();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error al cargar el sprite de poción: " + e.getMessage());
            createFallbackSprite();
        }
    }
    
    /**
     * Crea un sprite de respaldo en caso de error
     */
    private void createFallbackSprite() {
        // Crear una imagen de respaldo según el tipo de poción
        String color = switch (type) {
            case LENTITUD -> "0000FF"; // Azul
            case PUNTOS -> "FFD700";   // Dorado
            case SALUD -> "FF69B4";    // Rosa
        };
        sprite = new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAIElEQVR42u3OMQEAAAgDILV/" + color + "nBzwvJJWtgkRAQIECLwXeADnSQFrMO9mowAAAABJRU5ErkJggg==");
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
