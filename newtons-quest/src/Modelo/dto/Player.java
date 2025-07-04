package Modelo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Player {
    // Datos del usuario
    private int id;
    private String username;
    private String fullName;
    private String email;
    
    // Posición del jugador
    private double x;
    private double y;
    
    // Velocidad del jugador
    private double velocityX = 0;
    
    // Constantes de movimiento
    private final double BASE_SPEED = 5.0;
    private double currentSpeed = BASE_SPEED;
    
    // Estado del jugador
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private boolean facingRight = true;
    private boolean hasGreenApple = false; // Estado para manzana verde
    private boolean isDead = false; // Estado para game over
    private long greenAppleEffectStartTime = 0; // Tiempo de inicio del efecto de manzana verde
    private final long GREEN_APPLE_EFFECT_DURATION = 1000; // Duración del efecto en milisegundos
    
    // Efectos de pociones
    private boolean hasSlownessEffect = false;
    private boolean hasPointsEffect = false;
    private boolean hasHealthEffect = false;
    private long slownessEffectStartTime = 0;
    private long pointsEffectStartTime = 0;
    private long healthEffectStartTime = 0;
    private final long SLOWNESS_EFFECT_DURATION = 8000; // 8 segundos
    private final long POINTS_EFFECT_DURATION = 10000; // 10 segundos
    private final long HEALTH_EFFECT_DURATION = 1000; // 1 segundo (efecto instantáneo pero visual)
    
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
    
    // Constructor sin parámetros para uso en DAO
    public Player() {
        this.x = 0;
        this.y = 0;
        // No cargamos sprites ya que este constructor es solo para datos
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
            
            greenAppleSprite = loadImageSafely(spritePaths[5]);
            deadSprite = loadImageSafely(spritePaths[6]);
        } catch (Exception e) {
            createFallbackSprites();
        }
    }
    
    /**
     * Carga una imagen de forma segura.
     * @param path Ruta de la imagen
     * @return Imagen cargada o null si hay error
     */
    private Image loadImageSafely(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return new Image(new FileInputStream(file));
            } else {
                return createFallbackImage();
            }
        } catch (FileNotFoundException e) {
            return createFallbackImage();
        }
    }
    
    /**
     * Crea una imagen de respaldo en caso de error.
     * @return Imagen de respaldo simple
     */
    private Image createFallbackImage() {
        return new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAASElEQVR42u3OMQEAAAgDILV/51nBzwMJSDrZNhEBAgQIECBAgAABAgQIECBAgAABAgQIECBAgAABAgQIECBAgAABAgQIECDwXeAAAeIuAVuYZj0AAAAASUVORK5CYII=");
    }
    
    /**
     * Crea sprites de respaldo en caso de error de carga.
     */
    private void createFallbackSprites() {
        
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
        
        // Actualizar posición horizontal con velocidad constante
        currentSpeed = BASE_SPEED;
        
        // Aplicar efecto de lentitud si está activo
        if (hasSlownessEffect) {
            currentSpeed *= 0.6; // Reducir velocidad al 60%
        }
        
        if (isMovingLeft) {
            velocityX = -currentSpeed;
            facingRight = false;
        } else if (isMovingRight) {
            velocityX = currentSpeed;
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
        
        // Verificar efectos de pociones
        long currentTime = System.currentTimeMillis();
        
        if (hasSlownessEffect && currentTime - slownessEffectStartTime > SLOWNESS_EFFECT_DURATION) {
            hasSlownessEffect = false;
        }
        
        if (hasPointsEffect && currentTime - pointsEffectStartTime > POINTS_EFFECT_DURATION) {
            hasPointsEffect = false;
        }
        
        if (hasHealthEffect && currentTime - healthEffectStartTime > HEALTH_EFFECT_DURATION) {
            hasHealthEffect = false;
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
    /**
     * Renderiza el jugador en la pantalla.
     * @param gc Contexto gráfico para el dibujo
     */
    public void render(GraphicsContext gc) {
        if (gc == null) {
            return;
        }
        
        Image currentSprite;
        
        if (isDead) {
            currentSprite = deadSprite;
        } else if (hasGreenApple) {
            currentSprite = greenAppleSprite;
        } else if (isMovingLeft) {
            currentSprite = walkLeftSprites.get(currentFrame);
        } else if (isMovingRight) {
            currentSprite = walkRightSprites.get(currentFrame);
        } else {
            currentSprite = facingRight ? idleRightSprite : idleLeftSprite;
        }
        
        if (currentSprite == null) {
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
    
    // Métodos para efectos de pociones
    public void applySlownessEffect() {
        hasSlownessEffect = true;
        slownessEffectStartTime = System.currentTimeMillis();
    }
    
    public void applyPointsEffect() {
        hasPointsEffect = true;
        pointsEffectStartTime = System.currentTimeMillis();
    }
    
    public boolean applyHealthEffect() {
        // Solo aplicar el efecto visual, la lógica de vidas se maneja en el ScoreManager
        hasHealthEffect = true;
        healthEffectStartTime = System.currentTimeMillis();
        return true; // Siempre aplicar el efecto visual
    }
    
    // Getters para efectos de pociones
    public boolean hasSlownessEffect() {
        return hasSlownessEffect;
    }
    
    public boolean hasPointsEffect() {
        return hasPointsEffect;
    }
    
    public boolean hasHealthEffect() {
        return hasHealthEffect;
    }
    
    // Getters y setters para datos de usuario
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFullName() {
        return fullName != null && !fullName.isEmpty() ? fullName : username;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
