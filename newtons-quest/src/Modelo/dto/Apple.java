package Modelo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Clase que representa una manzana en el juego
 */
public class Apple {
    // Posición de la manzana
    private double x;
    private double y;
    
    // Velocidad de caída
    private double velocityY;
    
    // Tipo de manzana (roja o verde)
    private boolean isRed;
    
    // Dimensiones de la manzana
    private final int WIDTH = 32;
    private final int HEIGHT = 32;
    
    // Imagen de la manzana
    private Image sprite;
    
    // Estado de la manzana
    private boolean active = true;
    
    /**
     * Constructor de la manzana
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param isRed Si es true, es una manzana roja (puntos positivos), si es false, es verde (puntos negativos)
     * @param velocity Velocidad de caída
     */
    public Apple(double x, double y, boolean isRed, double velocity) {
        this.x = x;
        this.y = y;
        this.isRed = isRed;
        this.velocityY = velocity;
        
        loadSprite();
    }
    
    /**
     * Carga la imagen de la manzana según su tipo
     */
    private void loadSprite() {
        try {
            String path = isRed ? 
                "src/recursos/sprites/manzanas/Apple_Red.png" : 
                "src/recursos/sprites/manzanas/Apple_Green.png";
            
            File file = new File(path);
            if (file.exists()) {
                sprite = new Image(new FileInputStream(file));
                System.out.println("Sprite de manzana " + (isRed ? "roja" : "verde") + " cargado correctamente");
            } else {
                System.err.println("No se encontró el archivo: " + path);
                createFallbackSprite();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error al cargar el sprite de manzana: " + e.getMessage());
            createFallbackSprite();
        }
    }
    
    /**
     * Crea un sprite de respaldo en caso de error
     */
    private void createFallbackSprite() {
        // Crear una imagen de respaldo (rojo o verde según tipo)
        String color = isRed ? "FF0000" : "00FF00";
        sprite = new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAIElEQVR42u3OMQEAAAgDILV/" + color + "nBzwvJJWtgkRAQIECLwXeADnSQFrMO9mowAAAABJRU5ErkJggg==");
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
