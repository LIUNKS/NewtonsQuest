package Modelo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Player {
    // Posición del jugador
    private double x;
    private double y;
    
    // Velocidad del jugador
    private double velocityX = 0;
    
    // Constantes de movimiento
    private final double SPEED = 5.0;
    
    // Estado del jugador
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private boolean facingRight = true;
    private boolean hasGreenApple = false; // Estado para manzana verde
    private boolean isDead = false; // Estado para game over
    private long greenAppleEffectStartTime = 0; // Tiempo de inicio del efecto de manzana verde
    private final long GREEN_APPLE_EFFECT_DURATION = 1000; // Duración del efecto en milisegundos
    
    // Dimensiones del jugador
    private final int WIDTH = 64;
    private final int HEIGHT = 96;
    
    // Dimensiones especiales para sprites específicos
    private final int DEAD_WIDTH = 96;  // Más ancho para el sprite de derrota (acostado)
    private final int DEAD_HEIGHT = 64; // Más bajo para el sprite de derrota
    private final int GREEN_APPLE_WIDTH = 72; // Ligeramente más ancho para el sprite de manzana verde
    private final int GREEN_APPLE_HEIGHT = 96; // Misma altura para manzana verde
    
    // Sprites del jugador
    private List<Image> walkRightSprites;
    private List<Image> walkLeftSprites;
    private Image idleRightSprite;
    private Image idleLeftSprite;
    private Image greenAppleSprite; // Sprite para cuando recoge manzana verde
    private Image deadSprite; // Sprite para cuando el jugador pierde
    
    // Animación
    private int currentFrame = 0;
    private final int frameDelay = 5;
    private int frameCounter = 0;
    
    // Constructor
    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        
        loadSprites();
    }
    
    // Cargar los sprites
    private void loadSprites() {
        try {
            // Inicializar las listas de sprites
            walkRightSprites = new ArrayList<>();
            walkLeftSprites = new ArrayList<>();
            
            // Rutas de los sprites
            String[] spritePaths = {
                "src/recursos/sprites/newton/newton_1.png",
                "src/recursos/sprites/newton/newton_2.png",
                "src/recursos/sprites/newton/newton_3.png",
                "src/recursos/sprites/newton/newton_4.png",
                "src/recursos/sprites/newton/newton_5.png",
                "src/recursos/sprites/newton/newton_6.png", // Para manzana verde
                "src/recursos/sprites/newton/newton_7.png"  // Para game over
            };
            
            // Cargar sprites usando FileInputStream para mayor compatibilidad
            idleRightSprite = loadImageSafely(spritePaths[0]);
            idleLeftSprite = idleRightSprite; // Lo invertiremos al dibujar
            
            // Sprites para caminar
            walkRightSprites.add(loadImageSafely(spritePaths[1]));
            walkRightSprites.add(loadImageSafely(spritePaths[2]));
            walkRightSprites.add(loadImageSafely(spritePaths[3]));
            walkRightSprites.add(loadImageSafely(spritePaths[4]));
            
            // Sprites para caminar hacia la izquierda (los mismos sprites pero se dibujarán invertidos)
            walkLeftSprites = new ArrayList<>(walkRightSprites);
            
            // Sprites especiales - usar newton_6.png para manzana verde y newton_7.png para game over
            greenAppleSprite = loadImageSafely(spritePaths[5]); // newton_6.png para manzana verde
            deadSprite = loadImageSafely(spritePaths[6]); // newton_7.png para game over
            
            System.out.println("Sprites cargados correctamente");
            
        } catch (Exception e) {
            System.err.println("Error al cargar los sprites: " + e.getMessage());
            e.printStackTrace();
            
            // Crear sprites de respaldo en caso de error
            createFallbackSprites();
        }
    }
    
    // Método para cargar una imagen de forma segura
    private Image loadImageSafely(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return new Image(new FileInputStream(file));
            } else {
                System.err.println("No se encontró el archivo: " + path);
                return createFallbackImage();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error al cargar la imagen " + path + ": " + e.getMessage());
            return createFallbackImage();
        }
    }
    
    // Crear una imagen de respaldo en caso de error
    private Image createFallbackImage() {
        // Crear una imagen de respaldo (un rectángulo rojo)
        return new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAASElEQVR42u3OMQEAAAgDILV/51nBzwMJSDrZNhEBAgQIECBAgAABAgQIECBAgAABAgQIECBAgAABAgQIECBAgAABAgQIECDwXeAAAeIuAVuYZj0AAAAASUVORK5CYII=");
    }
    
    // Crear sprites de respaldo en caso de error
    private void createFallbackSprites() {
        System.out.println("Creando sprites de respaldo");
        
        // Crear una imagen de respaldo
        Image fallbackImage = createFallbackImage();
        
        // Asignar la imagen de respaldo a todos los sprites
        idleRightSprite = fallbackImage;
        idleLeftSprite = fallbackImage;
        greenAppleSprite = fallbackImage;
        deadSprite = fallbackImage;
        
        // Inicializar las listas de sprites si es necesario
        if (walkRightSprites == null) {
            walkRightSprites = new ArrayList<>();
        }
        if (walkLeftSprites == null) {
            walkLeftSprites = new ArrayList<>();
        }
        
        // Limpiar las listas existentes
        walkRightSprites.clear();
        walkLeftSprites.clear();
        
        // Añadir la imagen de respaldo a las listas
        walkRightSprites.add(fallbackImage);
        walkRightSprites.add(fallbackImage);
        walkRightSprites.add(fallbackImage);
        walkRightSprites.add(fallbackImage);
        
        walkLeftSprites = walkRightSprites;
    }
    
    // Actualizar la posición y estado del jugador
    public void update(double floorY) {
        // El jugador siempre está en el suelo a menos que esté muerto
        if (!isDead) {
            y = floorY - HEIGHT;
        }
        
        // Si el jugador está muerto, no se mueve
        if (isDead) {
            velocityX = 0;
            return;
        }
        
        // Actualizar posición horizontal
        if (isMovingLeft) {
            velocityX = -SPEED;
            facingRight = false;
        } else if (isMovingRight) {
            velocityX = SPEED;
            facingRight = true;
        } else {
            velocityX = 0;
        }
        
        // Actualizar posición (solo horizontal)
        x += velocityX;
        
        // Verificar si ha pasado el tiempo del efecto de manzana verde
        if (hasGreenApple) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - greenAppleEffectStartTime > GREEN_APPLE_EFFECT_DURATION) {
                hasGreenApple = false;
            }
        }
        
        // Actualizar animación
        if (isMovingLeft || isMovingRight) {
            frameCounter++;
            if (frameCounter >= frameDelay) {
                frameCounter = 0;
                currentFrame = (currentFrame + 1) % walkRightSprites.size();
            }
        } else {
            currentFrame = 0;
            frameCounter = 0;
        }
    }
    
    // Dibujar el jugador
    public void render(GraphicsContext gc) {
        if (gc == null) {
            System.err.println("ERROR: GraphicsContext es null en Player.render()");
            return;
        }
        
        Image currentSprite;
        
        // Seleccionar el sprite adecuado según el estado del jugador
        if (isDead) {
            // Si el jugador está muerto, mostrar el sprite de muerto
            currentSprite = deadSprite;
        } else if (hasGreenApple) {
            // Si el jugador ha recogido una manzana verde, mostrar el sprite específico
            currentSprite = greenAppleSprite;
        } else if (isMovingLeft) {
            currentSprite = walkLeftSprites.get(currentFrame);
        } else if (isMovingRight) {
            currentSprite = walkRightSprites.get(currentFrame);
        } else {
            currentSprite = facingRight ? idleRightSprite : idleLeftSprite;
        }
        
        if (currentSprite == null) {
            System.err.println("ERROR: currentSprite es null en Player.render()");
            return;
        }
        
        // Dibujar el sprite
        if ((isMovingLeft || !facingRight) && !isDead && !hasGreenApple) {
            // Dibujar invertido horizontalmente
            gc.save();
            gc.translate(x + WIDTH, y);
            gc.scale(-1, 1);
            gc.drawImage(currentSprite, 0, 0, WIDTH, HEIGHT);
            gc.restore();
        } else if (isDead) {
            // Usar dimensiones especiales para el sprite de derrota
            // Ajustar posición para que el personaje esté en el suelo
            gc.drawImage(currentSprite, x, y + (HEIGHT - DEAD_HEIGHT), DEAD_WIDTH, DEAD_HEIGHT);
        } else if (hasGreenApple) {
            // Usar dimensiones especiales para el sprite de manzana verde
            gc.drawImage(currentSprite, x, y, GREEN_APPLE_WIDTH, GREEN_APPLE_HEIGHT);
        } else {
            gc.drawImage(currentSprite, x, y, WIDTH, HEIGHT);
        }
    }
    
    // Métodos para controlar el movimiento
    public void moveLeft(boolean move) {
        isMovingLeft = move;
        if (move) {
            isMovingRight = false;
        }
    }
    
    public void moveRight(boolean move) {
        isMovingRight = move;
        if (move) {
            isMovingLeft = false;
        }
    }
    
    // Obtener el estado de la manzana verde
    public boolean hasGreenApple() {
        return hasGreenApple;
    }
    
    // Establecer el estado de la manzana verde
    public void setHasGreenApple(boolean hasGreenApple) {
        this.hasGreenApple = hasGreenApple;
        if (hasGreenApple) {
            // Registrar el tiempo actual como el inicio del efecto
            greenAppleEffectStartTime = System.currentTimeMillis();
        }
    }
    
    // Obtener el estado de game over
    public boolean isDead() {
        return isDead;
    }
    
    // Establecer el estado de game over
    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }
    
    // Getters y setters
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
    
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    // Métodos para los nuevos estados
    public void setGreenAppleEffect() {
        hasGreenApple = true;
        greenAppleEffectStartTime = System.currentTimeMillis();
    }
}
