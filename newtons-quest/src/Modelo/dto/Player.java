package Modelo.dto;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa al jugador principal del juego Newton's Quest.
 * 
 * Maneja tanto los datos del usuario (ID, username, email) como el estado del juego
 * (posición, movimiento, efectos, sprites). Incluye lógica de animación y renderizado.
 * 
 * @author Johann
 * @version 1.0
 */
public class Player {
    
    // === DATOS DEL USUARIO ===
    /** ID único del jugador en la base de datos */
    private int id;
    /** Nombre de usuario para login */
    private String username;
    /** Nombre completo del jugador */
    private String fullName;
    /** Correo electrónico del jugador */
    private String email;
    
    // === POSICIÓN Y MOVIMIENTO ===
    /** Posición X del jugador en el canvas */
    private double x;
    /** Posición Y del jugador en el canvas */
    private double y;
    /** Velocidad horizontal actual */
    private double velocityX = 0;
    
    // === CONFIGURACIÓN DE VELOCIDAD ===
    /** Velocidad base de movimiento */
    private final double BASE_SPEED = 5.0;
    /** Velocidad actual (puede ser modificada por efectos) */
    private double currentSpeed = BASE_SPEED;
    
    // === ESTADO DE MOVIMIENTO ===
    /** Indica si el jugador se está moviendo hacia la izquierda */
    private boolean isMovingLeft = false;
    /** Indica si el jugador se está moviendo hacia la derecha */
    private boolean isMovingRight = false;
    /** Indica la dirección que enfrenta el jugador */
    private boolean facingRight = true;
    
    // === EFECTOS ESPECIALES ===
    /** Indica si el jugador tiene el efecto de manzana verde activo */
    private boolean hasGreenApple = false;
    /** Indica si el jugador está en estado de game over */
    private boolean isDead = false;
    /** Tiempo de inicio del efecto de manzana verde */
    private long greenAppleEffectStartTime = 0;
    /** Duración del efecto de manzana verde en milisegundos */
    private final long GREEN_APPLE_EFFECT_DURATION = 1000;
    
    // === EFECTOS DE POCIONES ===
    /** Efecto de reducción de velocidad activo */
    private boolean hasSlownessEffect = false;
    /** Efecto de puntos extra activo */
    private boolean hasPointsEffect = false;
    /** Efecto de curación activo */
    private boolean hasHealthEffect = false;
    /** Tiempo de inicio del efecto de lentitud */
    private long slownessEffectStartTime = 0;
    /** Tiempo de inicio del efecto de puntos */
    private long pointsEffectStartTime = 0;
    /** Tiempo de inicio del efecto de curación */
    private long healthEffectStartTime = 0;
    /** Duración del efecto de lentitud en milisegundos */
    private final long SLOWNESS_EFFECT_DURATION = 8000;
    /** Duración del efecto de puntos en milisegundos */
    private final long POINTS_EFFECT_DURATION = 10000;
    /** Duración del efecto de curación en milisegundos */
    private final long HEALTH_EFFECT_DURATION = 1000;
    
    // === DIMENSIONES DEL SPRITE ===
    /** Ancho estándar del sprite del jugador */
    private final int WIDTH = 64;
    /** Alto estándar del sprite del jugador */
    private final int HEIGHT = 96;
    
    // === DIMENSIONES ESPECIALES ===
    /** Ancho del sprite cuando el jugador está derrotado */
    private final int DEAD_WIDTH = 96;
    /** Alto del sprite cuando el jugador está derrotado */
    private final int DEAD_HEIGHT = 64;
    /** Ancho del sprite durante el efecto de manzana verde */
    private final int GREEN_APPLE_WIDTH = 72;
    /** Alto del sprite durante el efecto de manzana verde */
    private final int GREEN_APPLE_HEIGHT = 96;
    
    // === SPRITES Y ANIMACIÓN ===
    /** Lista de sprites para caminar hacia la derecha */
    private List<Image> walkRightSprites;
    /** Lista de sprites para caminar hacia la izquierda */
    private List<Image> walkLeftSprites;
    /** Sprite en reposo mirando hacia la derecha */
    private Image idleRightSprite;
    /** Sprite en reposo mirando hacia la izquierda */
    private Image idleLeftSprite;
    /** Sprite especial para el efecto de manzana verde */
    private Image greenAppleSprite;
    /** Sprite cuando el jugador es derrotado */
    private Image deadSprite;
    
    // === CONTROL DE ANIMACIÓN ===
    /** Frame actual de la animación */
    private int currentFrame = 0;
    /** Velocidad de cambio de frames */
    private final int frameDelay = 5;
    /** Contador para controlar el cambio de frames */
    private int frameCounter = 0;
    
    /**
     * Constructor principal para crear un jugador en una posición específica.
     * Inicializa la posición y carga todos los sprites necesarios.
     * 
     * @param startX Posición inicial X en el canvas
     * @param startY Posición inicial Y en el canvas
     */
    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        loadSprites();
    }
    
    /**
     * Constructor vacío para uso en operaciones de base de datos.
     * No carga sprites, solo inicializa datos básicos.
     */
    public Player() {
        this.x = 0;
        this.y = 0;
        // Los sprites no se cargan en este constructor
    }
    
    /**
     * Carga todos los sprites necesarios para la animación del jugador.
     * Incluye sprites de caminar, reposo, efectos especiales y game over.
     */
    private void loadSprites() {
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
        
        // Cargar sprites usando FileInputStream
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
    }
    
    /**
     * Carga una imagen de forma segura.
     * @param path Ruta de la imagen
     * @return Imagen cargada
     */
    private Image loadImageSafely(String path) {
        try {
            File file = new File(path);
            return new Image(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error: No se pudo cargar el sprite: " + path, e);
        }
    }
    
    /**
     * Actualiza la posición y estado del jugador en cada frame del juego.
     * Maneja el movimiento, efectos de pociones y lógica de animación.
     * 
     * @param floorY Posición Y del suelo donde debe estar el jugador
     */
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
     * Renderiza el jugador en la pantalla con el sprite apropiado.
     * Selecciona automáticamente el sprite según el estado actual del jugador.
     * 
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
    
    // === MÉTODOS DE CONTROL ===
    
    /** 
     * Controla el movimiento hacia la izquierda.
     * @param move true para iniciar movimiento, false para detenerlo
     */
    public void moveLeft(boolean move) {
        isMovingLeft = move;
        if (move) {
            isMovingRight = false;
        }
    }
    
    /** 
     * Controla el movimiento hacia la derecha.
     * @param move true para iniciar movimiento, false para detenerlo
     */
    public void moveRight(boolean move) {
        isMovingRight = move;
        if (move) {
            isMovingLeft = false;
        }
    }
    
    // === GETTERS Y SETTERS ===
    
    /** @return true si el jugador tiene el efecto de manzana verde activo */
    public boolean hasGreenApple() {
        return hasGreenApple;
    }
    
    /** 
     * Establece el estado del efecto de manzana verde.
     * @param hasGreenApple true para activar el efecto
     */
    public void setHasGreenApple(boolean hasGreenApple) {
        this.hasGreenApple = hasGreenApple;
        if (hasGreenApple) {
            greenAppleEffectStartTime = System.currentTimeMillis();
        }
    }
    
    /** @return true si el jugador está en estado de game over */
    public boolean isDead() {
        return isDead;
    }
    
    /** @param isDead true para establecer el estado de game over */
    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }
    
    /** @return Posición X actual del jugador */
    public double getX() {
        return x;
    }
    
    /** @return Posición Y actual del jugador */
    public double getY() {
        return y;
    }
    
    /** @return Ancho del sprite del jugador */
    public int getWidth() {
        return WIDTH;
    }
    
    /** @return Alto del sprite del jugador */
    public int getHeight() {
        return HEIGHT;
    }
    
    /** 
     * Establece la posición del jugador.
     * @param x Nueva posición X
     * @param y Nueva posición Y
     */
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
